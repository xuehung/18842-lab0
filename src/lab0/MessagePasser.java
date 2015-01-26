package lab0;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import datatype.Action;
import datatype.Node;
import datatype.Rule;

public class MessagePasser {
	
	final private int BUFFER_LEN = 1000; 
	
	private String configFilename = null;
	private String localName = null;
	private LinkedBlockingQueue<Message> incomingBuffer = null;
	private LinkedBlockingQueue<Message> outgoingBuffer = null;
	private Map<String, Node> nodeMap = null;
	private List<Rule> sendRules = null;
	private List<Rule> receiveRules = null;
	private ServerSocket listener = null;
	private Map<String, Socket> socketMap = null;
	private ConfigLoader configLoader = null;
	
	public MessagePasser(String configFilename, String localName) throws IOException {
		
		System.out.printf("##### MessagePasser(name: %s) is initialized #####\n\n", localName);
		
		/* set instance variables */
		this.configFilename = configFilename;
		this.localName = localName;
		this.incomingBuffer = new LinkedBlockingQueue<Message>(BUFFER_LEN);
		this.outgoingBuffer = new LinkedBlockingQueue<Message>(BUFFER_LEN);
		this.nodeMap = new HashMap<String, Node>();
		this.sendRules = new ArrayList<Rule>();
		this.receiveRules = new ArrayList<Rule>();
		this.socketMap = new HashMap<String, Socket>();
		
		/* parse configuration */
		this.loadConfig();
		
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
			if (name.compareTo(this.localName) > 0) {				
				Node destNode = this.nodeMap.get(name);
				Thread client = new Thread(new MessageClient(destNode, incomingBuffer, socketMap, localName));
				client.start();
			}
		}
		
	}
	private void loadConfig() throws FileNotFoundException {
		this.configLoader = new ConfigLoader(this.configFilename);
		
	    /* configuration */
		this.nodeMap = configLoader.getNodeMap();
	   	
		/* send and receive rules */
		this.sendRules = configLoader.getSendRules();
		this.receiveRules = configLoader.getReceiveRules();
	}
	
	private void checkConfigReload() {
		if (this.configLoader.needUpdate()) {
			this.sendRules = configLoader.getSendRules();
			this.receiveRules = configLoader.getReceiveRules();
		}
	}
	
	
	
	/**
	 * create the server socket to accept requests from other nodes
	 * @param port
	 * @throws IOException
	 */
	private void createServerSocket(int port) throws IOException {
		this.listener = new ServerSocket(port);
		Thread serverThread = new Thread(new MessageServer(this.listener, this.incomingBuffer, this.socketMap, this.nodeMap));
		serverThread.start();
	}
	
	
	private void createSendingThread() {
		Thread sendingThread = new Thread() {
			public void run() {
				while (true) {
					Iterator<Message> it = outgoingBuffer.iterator();
					while (it.hasNext()) {
						Message message = it.next();
						if (socketMap.containsKey(message.getDest())) {
							outgoingBuffer.remove(message);
							// send
							Socket socket = socketMap.get(message.getDest());
							try {
								ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
								message.set_source(localName);
								oos.writeObject(message);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					
				}
			}
		};
		sendingThread.start();
	}

	public void send(Message message) {
		if (message == null) {
			return;
		}
		message.set_source(this.localName);
		this.checkConfigReload();
		this.outgoingBuffer.add(message);
	}

	/**
	 * If no message is available in the buffer, this method blocks
	 */
	public Message receive() {
		try {
			return this.incomingBuffer.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}