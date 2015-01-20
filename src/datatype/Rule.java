package datatype;

public class Rule {

	/* required field */
	private Action action = null;
	
	/* optional fields where null means not specified */
	private String src = null;
	private String dest = null;
	private String kind = null;
	private Integer seqNum = null;
	private Boolean duplicate = null;
	
	public Rule(Action action) {
		/* 
		 * once action is set, it cannot be modified 
		 * and there is no setter for action
		 */
		this.action = action;
	}

	public Action getAction() {
		return action;
	}

	public String getSrc() {
		return src;
	}

	public String getDest() {
		return dest;
	}

	public String getKind() {
		return kind;
	}

	public Integer getSeqNum() {
		return seqNum;
	}

	public Boolean getDuplicate() {
		return duplicate;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public void setSeqNum(Integer seqNum) {
		this.seqNum = seqNum;
	}

	public void setDuplicate(Boolean duplicate) {
		this.duplicate = duplicate;
	}
	
	
}
