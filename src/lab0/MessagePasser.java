package lab0;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.yaml.snakeyaml.Yaml;

import datatype.Action;
import datatype.Node;
import datatype.Rule;

public class MessagePasser {
	
	final private int BUFFER_LEN = 1000; 
	
	private String configFilename = null;
	private String localName = null;
	public LinkedBlockingQueue<Message> messageBuffer = null; 
	private Map<String, Node> nodeMap = null;
	private List<Rule> sendRules = null;
	private List<Rule> receiveRules = null;
	
	private ServerSocket listener = null;
	private Map<String, Socket> socketMap = null;
	
	public MessagePasser(String configFilename, String localName) throws IOException {
		
		System.out.printf("##### MessagePasser(name: %s) is initialized #####\n\n", localName);
		
		/* set instance variables */
		this.configFilename = configFilename;
		this.localName = localName;
		this.messageBuffer = new LinkedBlockingQueue<Message>(BUFFER_LEN);
		this.nodeMap = new HashMap<String, Node>();
		this.sendRules = new ArrayList<Rule>();
		this.receiveRules = new ArrayList<Rule>();
		this.socketMap = new HashMap<String, Socket>();
		
		/* parse configuration */
		this.loadConfig();
		
		/* create server socket */
		
		if (this.nodeMap.containsKey(this.localName)) {
			this.createServerSocket(this.nodeMap.get(this.localName).getPort());
		} else {
			System.err.printf("local name: %s does not exist in configurarion\n", this.localName);
		}
		
	}
	
	private void loadConfig() {
		InputStream input = null;
		try {
			input = new FileInputStream(new File(this.configFilename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
	    Yaml yaml = new Yaml();
	    
	    @SuppressWarnings("unchecked")
		Map<String, List<LinkedHashMap<String, Object>>> object = (Map<String, List<LinkedHashMap<String, Object>>>) yaml.load(input);
	    
	    /* configuration */
	    List<LinkedHashMap<String, Object>> nodeList = object.get("configuration");
	    for (LinkedHashMap<String, Object> node : nodeList) {
	    		String name = (String)node.get("name");
	    		String ip = (String)node.get("ip");
	    		int port = (Integer)node.get("port") + 4;
	    		this.nodeMap.put(name, new Node(name, ip, port));
	    		System.out.printf("%s(ip: %s, port = %d) is added\n", name, ip, port);
	    }
	    
	    /* send and receive rules */
	    this.loadRule(object, this.sendRules, "sendRules");
	    this.loadRule(object, this.receiveRules, "receiveRules");
	    
	}
	
	private void loadRule(Map<String, List<LinkedHashMap<String, Object>>> yamlObject, 
			List<Rule> ruleList, String fieldName) {
		System.out.printf("\nloading rules: %s\n", fieldName);
		List<LinkedHashMap<String, Object>> sendRuleList = yamlObject.get(fieldName);
	    for (LinkedHashMap<String, Object> sendRule : sendRuleList) {
	    		String actionStr = (String)sendRule.get("action");
	    		String src = (String)sendRule.get("src");
	    		String dest = (String)sendRule.get("dest");
	    		String kind = (String)sendRule.get("kind");
	    		Integer seqNum = (Integer)sendRule.get("seqNum");
	    		Boolean duplicate = (Boolean)sendRule.get("duplicate");
	    		Action action = null;
	    		for (Action ac : Action.values()) {
	    			if (ac.toString().equals(actionStr)) {
	    				action = ac;
	    			}
	    		}
	    		if (action != null) {
	    			Rule rule = new Rule(action);
	    			ruleList.add(rule);
	    			rule.setSrc(src);
	    			rule.setDest(dest);
	    			rule.setKind(kind);
	    			rule.setSeqNum(seqNum);
	    			rule.setDuplicate(duplicate);
	    			System.out.printf("rule (%s: %s -> %s, %s, %s, %s)\n", 
	    					rule.getAction(), rule.getSrc(), rule.getDest(), 
	    					rule.getKind(), rule.getSeqNum(), rule.getDuplicate());
	    		} else {
	    			System.err.println("action field is missing or illegal");
	    		}
	    }
	    System.out.printf("%d rules are loaded\n", ruleList.size());
	}
	
	private void createServerSocket(int port) throws IOException {
		this.listener = new ServerSocket(port);
		Thread serverThread = new Thread(new MessageServer(this.listener, this.messageBuffer, this.socketMap));
		serverThread.start();
	}
	
	private Socket createSocket(Node destNode) {
		Socket client = null;
		try {
			client = new Socket(destNode.getIp(), destNode.getPort());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return client;
	}

	void send(Message message) {
		message.set_source(this.localName);
		String dest = message.getDest();
		if (!this.nodeMap.containsKey(dest)) {
			return;
		}
		Node destNode = this.nodeMap.get(dest);
		if (!this.socketMap.containsKey(dest)) {
			Socket socket = createSocket(destNode);
			if (socket == null) {
				return;
			}
			this.socketMap.put(dest, socket);
		}
		Socket socket = this.socketMap.get(dest);
		try {
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/*
		
		BufferedOutputStream out = null;
		try {
			@SuppressWarnings("resource")
			Socket client = new Socket(destNode.getIp(), destNode.getPort());
			out = new BufferedOutputStream(client.getOutputStream());
			out.write(message.getData().toString().getBytes());
			out.flush();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			out = null;
		}
		System.out.println("data was sent");
		*/
	}

	// may block. Doesn't have to.
	Message receive() {
		return this.messageBuffer.poll();
	}
	
}
