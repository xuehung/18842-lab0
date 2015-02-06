package lab0;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;

import datatype.Message;
import datatype.Node;
import datatype.Rule;

public class MessageClient implements Runnable {
	private BufferManager bufferManager = null;
	private Map<String, Socket> socketMap = null;
	private Socket socket = null;
	private Node destNode = null;
	private String localName = null;
	private RuleManager ruleManager = null;

	public MessageClient(Node destNode,
			BufferManager bufferManager,
			Map<String, Socket> socketMap, String localName,
			RuleManager ruleManager) {
		this.bufferManager = bufferManager;
		this.socketMap = socketMap;
		this.destNode = destNode;
		this.localName = localName;
		this.socket = this.socketMap.get(destNode.getName());
		this.ruleManager = ruleManager;
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
			if (destNode.getName().compareTo(this.localName) > 0) {
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
					socketMap.put(destNode.getName(), socket);
				}
			}

			/* listen for incoming messages */
			while (true) {
				ObjectInputStream ois;
				try {
					ois = new ObjectInputStream(socket.getInputStream());
					Message message = (Message) ois.readObject();
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
							bufferManager.addToIncomingBuffer(message);
							bufferManager.addToIncomingBuffer(duplicateMsg);
							bufferManager.clearDelayIncomingMessage();
							break;
						case delay:
							bufferManager.delayIncomingMessage(message);
							break;
						}
					} else {
						bufferManager.addToIncomingBuffer(message);
						bufferManager.clearDelayIncomingMessage();
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