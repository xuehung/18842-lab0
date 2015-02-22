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

import time.clock.ClockType;
import datatype.Action;
import datatype.Groups;
import datatype.Node;
import datatype.Rule;

public class ConfigLoader {
	
	public final static String LOGGER_NAME = "logger";

	private Map<String, List<LinkedHashMap<String, Object>>> config = null;
	private Map<String, Node> nodeMap = null;
	private List<Rule> sendRules = null;
	private List<Rule> receiveRules = null;
	private File configFile = null;
	Object yamlObject = null;
	private long lastModified = 0;
	private Map<String, Groups> groupMap = null;
	@SuppressWarnings("unchecked")
	public ConfigLoader(String filename) throws FileNotFoundException {
		this.configFile = new File(filename);
		this.lastModified = this.configFile.lastModified();
		InputStream input = null;
		input = new FileInputStream(this.configFile);
	    Yaml yaml = new Yaml();
	    yamlObject = yaml.load(input);
	    
	    
		this.config = (Map<String, List<LinkedHashMap<String, Object>>>) yamlObject;
	}
	
	/*
	 * check whether we should reload the configuration file
	 * once this method is called, the lastModified time will
	 * be updated and will return false when called again
	 */
	@SuppressWarnings("unchecked")
	public boolean needUpdate() {
		if (this.lastModified == this.configFile.lastModified()) {
			return false;
		}
		InputStream input = null;
		try {
			input = new FileInputStream(this.configFile);
		} catch (FileNotFoundException e) {
			return false;
		}
	    Yaml yaml = new Yaml();
		this.config = (Map<String, List<LinkedHashMap<String, Object>>>) yaml.load(input);
		this.lastModified = this.configFile.lastModified();
		return true;
	}
	public Map<String, Groups> getGroups() {
		
		this.groupMap = new HashMap<String, Groups>();
		
		List<LinkedHashMap<String, Object>> groupInfo = this.config.get("groups");
		for (LinkedHashMap<String, Object> group : groupInfo) {
			String name = (String) group.get("name");
			ArrayList<String> members =((ArrayList<String>)(group.get("members")));
			Groups theGroup = new Groups(members);
			groupMap.put(name, theGroup);
			}
		return this.groupMap;
	}
	public Map<String, Node> getNodeMap() {
		this.nodeMap = new HashMap<String, Node>();
		
		List<LinkedHashMap<String, Object>> nodeList = this.config.get("configuration");
		for (LinkedHashMap<String, Object> node : nodeList) {
			String name = (String) node.get("name");
			String ip = (String) node.get("ip");
			int port = (Integer) node.get("port") + 8;
			this.nodeMap.put(name, new Node(name, ip, port));
		}
		return this.nodeMap;
	}
	public List<String> getNodeList() {
		List<String> nodeList = new ArrayList<String>();
		for (String key: this.nodeMap.keySet()) {
			nodeList.add(key);
		}
		java.util.Collections.sort(nodeList);
		return nodeList;
	}
	
	public List<String> getGroupList() {
		List<String> groupList = new ArrayList<String>();
		for (String key: this.groupMap.keySet()) {
			groupList.add(key);
		}
		java.util.Collections.sort(groupList);
		return groupList;
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
	
//	private void loadGroups(List<Node> nodes, String fieldName) {
//		List<List<Node>> groups = this.groups.get(fieldName);
//	}
	private void loadRule(List<Rule> ruleList, String fieldName) {
		List<LinkedHashMap<String, Object>> sendRuleList = this.config.get(fieldName);
		if (sendRuleList != null) {
			for (LinkedHashMap<String, Object> sendRule : sendRuleList) {
				String actionStr = (String) sendRule.get("action");
				String src = (String) sendRule.get("src");
				String dest = (String) sendRule.get("dest");
				String kind = (String) sendRule.get("kind");
				Integer seqNum = (Integer) sendRule.get("seqNum");
				Boolean duplicate = (Boolean) sendRule.get("duplicate");
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
				} else {
					System.err.println("action field is missing or illegal");
				}
			}
		}
	}
	
	public ClockType getClockType() {
		@SuppressWarnings("unchecked")
		String clock = ((Map<String, String>)(this.yamlObject)).get("clock");
		return (ClockType.LOGICAL.toString().toLowerCase().equals(clock)) ? 
				ClockType.LOGICAL : ClockType.VECTOR;
	}
}
