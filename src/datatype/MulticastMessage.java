package datatype;

public class MulticastMessage extends TimeStampedMessage {
	
	
	private static final long serialVersionUID = 1L;
	private String groupName = null;
	private String originator = null;
	public String getOriginator() {
		return originator;
	}

	public void setOriginator(String originator) {
		this.originator = originator;
	}

	private Integer[] vector = null;

	public MulticastMessage(String groupName, String dest, String kind, Object data) {
		super(dest, kind, data);
		this.groupName = groupName;
	}

	public String getGroupName() {
		return groupName;
	}

	public Integer[] getVector() {
		return vector;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public void setVector(Integer[] vector) {
		this.vector = vector;
	}
}
