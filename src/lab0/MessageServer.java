package lab0;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

import datatype.Message;
import datatype.Node;

public class MessageServer implements Runnable {

	private String localName = null;
	private ServerSocket listener = null;
	private BufferManager bufferManager = null;
	private Map<String, Node> nodeMap = null;
	private Map<String, Socket> socketMap = null;
	private RuleManager ruleManager = null;
	private MessagePasser mp = null;
	
	public MessageServer(MessagePasser mp,
			ServerSocket listener, 
			BufferManager bufferManager,
			Map<String, Socket> socketMap,
			Map<String, Node> nodeMap,
			RuleManager ruleManager,
			String localName) {
		this.listener = listener;
		this.bufferManager = bufferManager;
		this.socketMap = socketMap;
		this.nodeMap = nodeMap;
		this.ruleManager = ruleManager;
		this.localName = localName;
		this.mp = mp;
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
				System.out.println("connection from "+ src +" has been established");
				Node node = this.nodeMap.get(src);
				socketMap.put(src, socket);
				Thread client = new Thread(new MessageClient(mp, node, bufferManager, socketMap, localName, ruleManager));
				client.start();
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
		} 
		
	}

}