package lab0;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import datatype.Node;

public class MessageServer implements Runnable {

	private ServerSocket listener = null;
	private LinkedBlockingQueue<Message> incomingBuffer = null;
	private Map<String, Node> nodeMap = null;
	private Map<String, Socket> socketMap = null;
	
	
	public MessageServer(ServerSocket listener, 
			LinkedBlockingQueue<Message> messageBuffer,
			Map<String, Socket> socketMap,
			Map<String, Node> nodeMap) {
		this.listener = listener;
		this.incomingBuffer = messageBuffer;
		this.socketMap = socketMap;
		this.nodeMap = nodeMap;
	}
	
	@Override
	public void run() {
		Socket socket = null;
		while (true) {
			try {
				socket = this.listener.accept();
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				//
				Message msg = (Message)ois.readObject();
				String src = msg.getSrc();
				if (!nodeMap.containsKey(src)) {
					System.err.println("cannot recognize src: "+src);
					continue;
				}
				if (socketMap.containsKey(src)) {
					System.err.println("socket already exists: "+src);
					continue;
				}
				if (msg.getKind() != null || msg.getData() != null) {
					System.err.println("The kind and data of the first message should be null");
					continue;
				}
				
				Node node = this.nodeMap.get(src);
				socketMap.put(src, socket);
				Thread client = new Thread(new MessageClient(node, incomingBuffer, socketMap, null));
				client.start();
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
		} 
		
	}

}
