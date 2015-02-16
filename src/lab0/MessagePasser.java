package lab0;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import time.clock.ClockFactory;
import time.clock.ClockService;
import time.timestamp.TimeStamp;
import datatype.LogEvent;
import datatype.Message;
import datatype.MulticastMessage;
import datatype.Node;
import datatype.Rule;
import datatype.TimeStampedMessage;

public class MessagePasser {
		
	private String configFilename = null;
	private String localName = null;
	private Map<String, Node> nodeMap = null;
	private Map<String, Socket> socketMap = null;
	
	private BufferManager bufferManager = null;
	private RuleManager ruleManager = null;
	private ConfigLoader configLoader = null;
	
	private ServerSocket listener = null;
	private ClockService clockService = null;
	private int seqNumCounter = 0;
	private Map<String, Groups> groupMap = null;
	private MulticastService ms = null;
	public MessagePasser(String configFilename, String localName) throws IOException {
		
		System.out.printf("##### MessagePasser(name: %s) is initialized #####\n\n", localName);
		
		/* set instance variables */
		this.configFilename = configFilename;
		this.localName = localName;
		this.nodeMap = new HashMap<String, Node>();
		this.socketMap = new HashMap<String, Socket>();
		this.bufferManager = new BufferManager();
		this.groupMap = new HashMap<String, Groups>();
		
		/* parse configuration */
		this.loadConfig();
		this.ms = MulticastService.getInstance(configLoader, localName, this, bufferManager); 
		if (configLoader.getClockType() != null) {
			clockService = ClockFactory.getClockInstance(configLoader.getClockType(), this.configLoader.getNodeList(), this.localName);
			System.out.println(configLoader.getClockType());
		}
		
		
		
		/* create server socket for listening*/
		if (this.nodeMap.containsKey(this.localName)) {
			this.createServerSocket(this.nodeMap.get(this.localName).getPort());
		} else {
			System.err.printf("local name: %s does not exist in configurarion\n", this.localName);
		}
		this.createClientSocket();
		
		/* create the thread responsible for sending messages */
		this.createSendingThread();
		
	}
	private void createClientSocket() throws IOException {
		for (String name : this.nodeMap.keySet()) {
			if (name.compareTo(this.localName) >= 0) {				
				Node destNode = this.nodeMap.get(name);
				Thread client = new Thread(new MessageClient(this, destNode, bufferManager, socketMap, localName, ruleManager, configLoader));
				client.start();
			}
		}
		
	}
	private void loadConfig() throws FileNotFoundException {
		this.configLoader = new ConfigLoader(this.configFilename);
		configLoader.getClockType();
	    /* configuration */
		this.nodeMap = configLoader.getNodeMap();
		this.groupMap = configLoader.getGroups();
		this.ruleManager = new RuleManager(configLoader,localName);
	  
	}
	
	
	/**
	 * create the server socket to accept requests from other nodes
	 * @param port
	 * @throws IOException
	 */
	private void createServerSocket(int port) throws IOException {
		this.listener = new ServerSocket(port);
		Thread serverThread = new Thread(new MessageServer(this, this.listener,
				bufferManager, this.socketMap, this.nodeMap,
				this.ruleManager, localName, configLoader));
		serverThread.start();
	}
	
	
	private void createSendingThread() {
		Thread sendingThread = new Thread() {
			public void run() {
				while (true) {
					Message message = bufferManager.takeFromOutgoingBuffer(socketMap);
					if (message != null) {
						
						Socket socket = socketMap.get(message.getDest());
						
						try {
							ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
							//message.setSource(localName);
							oos.writeObject(message);
						} catch (IOException e) {
							e.printStackTrace();
							bufferManager.addToOutgoingBuffer(message);
						}		
					}
				}
			}
		};
		sendingThread.start();
	}

	/**
	 * Send a message
	 * @param message
	 * @return indicates whether the message is dropped
	 */
	public boolean send(Message message) {
		if (message == null || !this.nodeMap.containsKey(message.getDest())) {
			return false;
		}
		
		message.setSource(this.localName);
		message.setSeqNum(seqNumCounter++);
		if (clockService != null && message instanceof TimeStampedMessage) {
			TimeStamp ts = clockService.getTime();
			((TimeStampedMessage)message).setTimestamp(ts);
			String logtext = String.format("%s sent a message to %s", 
					message.getSrc(), message.getDest());
			System.err.println(logtext);
			if (((TimeStampedMessage)message).isRequireLog()) {
				this.logEvent(ts, logtext);
			}
		}
		
		Rule matchRule = ruleManager.matchSendRule(message);
		System.out.println("matchRule = "+matchRule);
		if (matchRule != null) {
			System.out.println("matchRule = "+matchRule.getAction());
			switch (matchRule.getAction()) {
			case drop:
				return false;
			case duplicate:
				/* duplicate field is already set in clone */
				Message duplicateMsg = message.clone();
				bufferManager.addToOutgoingBuffer(message);
				bufferManager.addToOutgoingBuffer(duplicateMsg);
				bufferManager.clearDelayOutgoingMessage();
				break;
			case delay:
				bufferManager.delayOutgoingMessage(message);
				break;
			}
		} else {
			bufferManager.addToOutgoingBuffer(message);
			bufferManager.clearDelayOutgoingMessage();
		}
		return true;
	}

	/**
	 * If no message is available in the buffer, this method blocks
	 */
	public Message receive() {
		return bufferManager.takeFromIncomingBuffer();
	}
	
	public void logEvent(TimeStamp ts, String text) {
		if (nodeMap.containsKey(ConfigLoader.LOGGER_NAME)) {
			if (clockService != null) {
				TimeStampedMessage message = new TimeStampedMessage(ConfigLoader.LOGGER_NAME, 
						null, new LogEvent(ts, text));
				this.send(message);
			}
		}
	}
	
	public void logEvent(String text, boolean toLogger) {
		if (nodeMap.containsKey(ConfigLoader.LOGGER_NAME)) {
			if (clockService != null) {
				// TODO should increase
				TimeStamp ts = clockService.getNonIncreTime();
				if (toLogger) {
					this.logEvent(ts, text);
				}
			}
		}
	}
	
	public String showTime() {
		return clockService.showTime();
	}
	
	
	public void multicast(MulticastMessage message) {
		if (message != null && this.groupMap.containsKey(message.getGroupName())) {
			ms.RCOMulticast(message);
		}
	}
}