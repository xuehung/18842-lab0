package lab0;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageClient implements Runnable {
	private LinkedBlockingQueue<Message> messageBuffer = null;
	private Socket socket = null;
	
	public MessageClient(LinkedBlockingQueue<Message> messageBuffer, Socket socket) {
		this.messageBuffer = messageBuffer;
		this.socket = socket;
	}
	
	@Override
	public void run() {
		while (true) {
			ObjectInputStream ois;
			try {
				ois = new ObjectInputStream(socket.getInputStream());
				Message msg = (Message)ois.readObject();
				this.messageBuffer.add(msg);
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
