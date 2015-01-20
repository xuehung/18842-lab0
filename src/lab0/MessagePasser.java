package lab0;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;

import org.yaml.snakeyaml.Yaml;

import datatype.Action;
import datatype.Node;
import datatype.Rule;

public class MessagePasser {
	private String configFilename = null;
	private String localName = null;
	private Map<String, Node> nodeMap = null;
	private List<Rule> sendRules = null;
	private List<Rule> receiveRules = null;
	
	public MessagePasser(String configFilename, String localName) {
		
		System.out.printf("##### MessagePasser(name: %s) is initialized #####\n\n", localName);
		
		/* set instance variables */
		this.configFilename = configFilename;
		this.localName = localName;
		this.nodeMap = new HashMap<String, Node>();
		this.sendRules = new ArrayList<Rule>();
		this.receiveRules = new ArrayList<Rule>();
		
		/* parse configuration */
		this.loadConfig();
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
	    		int port = (Integer)node.get("port");
	    		this.nodeMap.put(name, new Node(name, ip, port));
	    		System.out.printf("%s(ip: %s, port = %d) is added\n", name, ip, port);
	    }
	    
	    /* send rules */
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

	void send(Message message) {

	}

	// may block. Doesn't have to.
	Message receive() {
		return null;
	}
}
