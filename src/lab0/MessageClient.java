package lab0;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;

import time.clock.ClockFactory;
import time.clock.ClockService;
import time.timestamp.TimeStamp;
import datatype.Message;
import datatype.MulticastMessage;
import datatype.Node;
import datatype.Rule;
import datatype.TimeStampedMessage;

public class MessageClient implements Runnable {
	private BufferManager bufferManager = null;
	private Map<String, Socket> socketMap = null;
	private Socket socket = null;
	private Node destNode = null;
	private String localName = null;
	private RuleManager ruleManager = null;
	private ClockService clockService = null;
	private MessagePasser mp = null;
	private MulticastService ms = null;
	public MessageClient(MessagePasser mp,
			Node destNode,
			BufferManager bufferManager,
			Map<String, Socket> socketMap, String localName,
			RuleManager ruleManager, ConfigLoader configLoader) {
		this.mp = mp;
		this.bufferManager = bufferManager;
		this.socketMap = socketMap;
		this.destNode = destNode;
		this.localName = localName;
		this.socket = this.socketMap.get(destNode.getName());
		this.ruleManager = ruleManager;
		this.clockService = ClockFactory.getClockInstance();
		this.ms = MulticastService.getInstance(configLoader, localName, mp, bufferManager);
		//new MulticastService(configLoader, localName, mp, bufferManager); 
	}

	@Override
	public void run() {
		/*
		 * When run() is called, there are two situations 
		 * 1) socket already exists 
		 * 2) we need to create the socket
		 */
		while (true) {
			if (destNode.getName().compareTo(this.localName) < 0 && socket == null) {
				break;
			}
			/* create the socket by small name */
			if (destNode.getName().compareTo(this.localName) >= 0) {
				while (!socketMap.containsKey(destNode.getName())) {
					try {
						socket = new Socket(destNode.getIp(),
								destNode.getPort());
						try {
							ObjectOutputStream oos = new ObjectOutputStream(
									socket.getOutputStream());
							Message message = new Message(destNode.getName(),
									null, null);
							message.setSource(localName);
							oos.writeObject(message);
						} catch (IOException e) {
							e.printStackTrace();
						}
					} catch (Exception e) {
						continue;
					}
					if (!socketMap.containsKey(destNode.getName())) {
						socketMap.put(destNode.getName(), socket);
					}
				}
			}

			/* listen for incoming messages */
			while (true) {
				ObjectInputStream ois;
				try {
					ois = new ObjectInputStream(socket.getInputStream());
					Message message = (Message) ois.readObject();
					if (clockService != null && message instanceof TimeStampedMessage) {
						TimeStamp ts = clockService.getTime(((TimeStampedMessage) message).getTimestamp());
						String logtext = String.format("%s received a message from %s at time %s", 
								message.getDest(), message.getSrc(), ((TimeStampedMessage) message).getTimestamp());
						System.err.println(logtext);
						if (((TimeStampedMessage) message).isRequireLog()) {
							mp.logEvent(ts, logtext);
						}
					}
					Rule matchRule = ruleManager.matchReceiveRule(message);

					if (matchRule != null) {
						System.out.println("matchRule = "
								+ matchRule.getAction());
						switch (matchRule.getAction()) {
						case drop:
							break;
						case duplicate:
							/* duplicate field is already set in clone */
							Message duplicateMsg = message.clone();
							
							if (message instanceof MulticastMessage) {
								ms.BDeliver((MulticastMessage)message);
							} 
							if (duplicateMsg instanceof MulticastMessage) {
								ms.BDeliver((MulticastMessage)duplicateMsg);
							} 
							
							bufferManager.addToIncomingBuffer(message);
							bufferManager.addToIncomingBuffer(duplicateMsg);
							bufferManager.clearDelayIncomingMessage(ms);
							break;
						case delay:
							bufferManager.delayIncomingMessage(message);
							break;
						}
					} else {
						if (message instanceof MulticastMessage) {
							ms.BDeliver((MulticastMessage)message);
						} else {
							bufferManager.addToIncomingBuffer(message);
						}
						bufferManager.clearDelayIncomingMessage(ms);
					}

				} catch (IOException e) {
					System.err.println("socket dropped!");
					this.socketMap.remove(destNode.getName());
					socket = null;
					break;
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}
}