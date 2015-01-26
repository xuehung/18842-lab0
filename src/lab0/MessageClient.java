package lab0;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import datatype.Node;

public class MessageClient implements Runnable {
	private LinkedBlockingQueue<Message> incomingBuffer = null;
	private Map<String, Socket> socketMap = null;
	private Socket socket = null;
	private Node destNode = null;
	private String localName = null;
	
	public MessageClient(Node destNode, 
			LinkedBlockingQueue<Message> messageBuffer, 
			Map<String, Socket> socketMap,
			String localName) {
		this.incomingBuffer = messageBuffer;
		this.socketMap = socketMap;
		this.destNode = destNode;
		this.localName = localName;
		this.socket = this.socketMap.get(destNode.getName());
	}
	
	@Override
	public void run() {
		/*
		 * When run() is called, there are two situations
		 * 	1) socket already exists
		 * 	2) we need to create the socket
		 */
		/* create the socket */
		while (!socketMap.containsKey(destNode.getName())) {
			try {
				socket = new Socket(destNode.getIp(), destNode.getPort());
				try {
					ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
					Message message = new Message(destNode.getName(), null, null);
					message.set_source(localName);
					oos.writeObject(message);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				continue;
			}
			socketMap.put(destNode.getName(), socket);
		}
		
		
		/* listen for incoming messages */
		while (true) {
			ObjectInputStream ois;
			try {
				System.out.println("socket = "+socket);
				ois = new ObjectInputStream(socket.getInputStream());
				Message msg = (Message)ois.readObject();
				this.incomingBuffer.add(msg);
				System.out.println("kind = " + msg.getKind());
			} catch (IOException e) {
				e.printStackTrace();
				break;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}
