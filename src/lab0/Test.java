package lab0;

import java.io.IOException;

public class Test {

	public static void main(String[] argv) throws IOException {
		// it is test
		MessagePasser mp = new MessagePasser("config.yml", "alice");
		MessagePasser mp1 = new MessagePasser("config.yml", "bob");
		System.out.println("buffer size = "+mp1.messageBuffer.size());
		mp.send(new Message("bob", "kind", "hi"));
				
		mp.send(new Message("bob", "kind", "hi"));
		
		System.out.println("buffer size = "+mp1.messageBuffer.size());
		System.out.println("last line");

	}
}
