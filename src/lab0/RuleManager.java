package lab0;

import java.util.List;

import datatype.Message;
import datatype.Rule;

public class RuleManager {

	private ConfigLoader configLoader = null;
	private List<Rule> sendRules = null;
	private List<Rule> receiveRules = null;
	private String localName = null;
	public RuleManager(ConfigLoader configLoader, String localName) {
		this.configLoader = configLoader;
		this.sendRules = configLoader.getSendRules();
		this.receiveRules = configLoader.getReceiveRules();
		this.localName = localName;
	}
	
	public Rule matchSendRule(Message message) {
		if(localName.equals(message.getDest()) && localName.equals(message.getSrc()))
			return null;
		this.checkConfigReload();
		RuleFilter ruleFilter = new RuleFilter(message);
		return ruleFilter.getRule(sendRules);
	}
	
	public Rule matchReceiveRule(Message message) {
		if(localName.equals(message.getDest()) && localName.equals(message.getSrc()))
			return null;
		this.checkConfigReload();
		RuleFilter ruleFilter = new RuleFilter(message);
		return ruleFilter.getRule(receiveRules);
	}
	
	private void checkConfigReload() {
		if (this.configLoader.needUpdate()) {
			this.sendRules = configLoader.getSendRules();
			this.receiveRules = configLoader.getReceiveRules();
		}
	}
	
	
}
