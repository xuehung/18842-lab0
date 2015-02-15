package datatype;

public class MulticastMessage extends TimeStampedMessage {
	
	private String groupName = null;
	private int[] vector = null;

	public MulticastMessage(String groupName, String dest, String kind, Object data) {
		super(dest, kind, data);
		this.groupName = groupName;
	}

	public String getGroupName() {
		return groupName;
	}

	public int[] getVector() {
		return vector;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public void setVector(int[] vector) {
		this.vector = vector;
	}
}
