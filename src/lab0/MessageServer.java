package lab0;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageServer implements Runnable {

	private ServerSocket listener = null;
	private LinkedBlockingQueue<Message> messageBuffer = null;
	private Map<String, Socket> socketMap = null;
	
	
	public MessageServer(ServerSocket listener, 
			LinkedBlockingQueue<Message> messageBuffer,
			Map<String, Socket> socketMap) {
		this.listener = listener;
		this.messageBuffer = messageBuffer;
		this.socketMap = socketMap;
	}
	
	@Override
	public void run() {
		Socket socket = null;
		while (true) {
			try {
				socket = this.listener.accept();
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				Message msg = (Message)ois.readObject();
				this.socketMap.put(msg.getSrc(), socket);
				Thread client = new Thread(new MessageClient(this.messageBuffer, socket));
				client.start();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
		} 
		
	}

}
