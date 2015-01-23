package lab0;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageServer implements Runnable{

	private ServerSocket listener = null;
	private LinkedBlockingQueue<Message> messageBuffer = null;
	
	
	
	public MessageServer(ServerSocket listener, LinkedBlockingQueue<Message> messageBuffer) {
		this.listener = listener;
		this.messageBuffer = messageBuffer;
	}
	
	@Override
	public void run() {
		while (true) {
			Socket socket = null;
			try {
				socket = this.listener.accept();
				System.out.println(socket.getInputStream().toString());
				this.messageBuffer.add(new Message("1", "2", new String("TEST")));
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		} 
		
	}

}
