package lab0;

import java.io.IOException;

public class Test {

	public static void main(String[] argv) throws IOException {
		// it is test
		MessagePasser mp = new MessagePasser("config.yml", "alice");
		MessagePasser mp1 = new MessagePasser("config.yml", "bob");
		
		mp.send(new Message("bob", "kindxxxx", "hi"));
		mp.send(new Message("bob", "kindoooo", "hi"));
		mp.send(new Message("bob", "a", "hi"));
		mp.send(new Message("bob", "b", "hi"));
		mp.send(new Message("bob", "c", "hi"));
		mp.send(new Message("bob", "d", "hi"));
		
		mp1.send(new Message("bob", "a", "hi"));
		mp1.send(new Message("bob", "b", "hi"));
		mp1.send(new Message("bob", "c", "hi"));
		mp1.send(new Message("bob", "d", "hi"));
	}
}
