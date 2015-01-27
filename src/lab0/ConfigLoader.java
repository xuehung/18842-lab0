package lab0;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import datatype.Action;
import datatype.Node;
import datatype.Rule;

public class ConfigLoader {

	private Map<String, List<LinkedHashMap<String, Object>>> config = null;
	private Map<String, Node> nodeMap = null;
	private List<Rule> sendRules = null;
	private List<Rule> receiveRules = null;
	private File configFile = null;
	private long lastModified = 0;
	
	@SuppressWarnings("unchecked")
	public ConfigLoader(String filename) throws FileNotFoundException {
		this.configFile = new File(filename);
		this.lastModified = this.configFile.lastModified();
		InputStream input = null;
		input = new FileInputStream(this.configFile);
	    Yaml yaml = new Yaml();
	    
		this.config = (Map<String, List<LinkedHashMap<String, Object>>>) yaml.load(input);
	}
	
	/*
	 * check whether we should reload the configuration file
	 * once this method is called, the lastModified time will
	 * be updated and will return false when called again
	 */
	public boolean needUpdate() {
		if (this.lastModified == this.configFile.lastModified()) {
			return false;
		}
		this.lastModified = this.configFile.lastModified();
		System.out.println("Configuration reloaded!");
		return true;
	}
	
	public Map<String, Node> getNodeMap() {
		this.nodeMap = new HashMap<String, Node>();
		
		List<LinkedHashMap<String, Object>> nodeList = this.config.get("configuration");
		for (LinkedHashMap<String, Object> node : nodeList) {
			String name = (String) node.get("name");
			String ip = (String) node.get("ip");
			int port = (Integer) node.get("port") + 8;
			this.nodeMap.put(name, new Node(name, ip, port));
			System.out.printf("%s(ip: %s, port = %d) is added\n", name, ip, port);
		}
		return this.nodeMap;
	}
	
	public List<Rule> getSendRules() {
		this.sendRules = new ArrayList<Rule>();
		
		this.loadRule(this.sendRules, "sendRules");
		return this.sendRules;
	}
	
	public List<Rule> getReceiveRules() {
		this.receiveRules = new ArrayList<Rule>();
		
		this.loadRule(this.receiveRules, "receiveRules");
		return this.receiveRules;
	}
	
	private void loadRule(List<Rule> ruleList, String fieldName) {
		System.out.printf("\nloading rules: %s\n", fieldName);
		List<LinkedHashMap<String, Object>> sendRuleList = this.config.get(fieldName);
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
	
}