package lab0;

import java.util.List;

import datatype.Rule;

public class RuleManager {

	private ConfigLoader configLoader = null;
	private List<Rule> sendRules = null;
	private List<Rule> receiveRules = null;
	
	public RuleManager(ConfigLoader configLoader) {
		this.configLoader = configLoader;
		this.sendRules = configLoader.getSendRules();
		this.receiveRules = configLoader.getReceiveRules();
	}
	
	public Rule matchSendRule(Message message) {
		this.checkConfigReload();
		RuleFilter ruleFilter = new RuleFilter(message);
		return ruleFilter.getRule(sendRules);
	}
	
	public Rule matchReceiveRule(Message message) {
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
