package lab0;

import java.util.List;

import datatype.Message;
import datatype.Rule;

public class RuleFilter {
	private String src = null;
	private String dest = null;
	private String kind = null;
	private Integer seqNum = null;
	private Boolean duplicate = null;

	
	public RuleFilter(Message message) {
		this.src=message.getSrc();
		this.dest=message.getDest();
		this.kind=message.getKind();
		this.seqNum=message.getSeqNum();
		this.duplicate=message.getDup();
	}
	public boolean compareWithRule(Rule rule) {
		if(rule.getSrc()!=null && !src.equals(rule.getSrc())) 
				return false;
		else if (rule.getDest()!=null && !dest.equals(rule.getDest()))
				return false;
		else if (rule.getKind() !=null && !kind.equals(rule.getKind()))
				return false;
		else if (rule.getSeqNum()!=null && !seqNum.equals(rule.getSeqNum()))
				return false;
		else if (rule.getDuplicate()!= null && !duplicate.equals(rule.getDuplicate()))
				return false;
		else 
				return true;

	}
	public Rule getRule(List<Rule> rules) {
		for(int i=0;i<rules.size();i++) {
			Rule everyRule=rules.get(i);
			if ( compareWithRule(everyRule)){
				return everyRule;
			}
		}
		return null;
	}
}
