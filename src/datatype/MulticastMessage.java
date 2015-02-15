package datatype;

public class MulticastMessage {
	private String groupName = null;
	private String kind = null;
	private Object data = null;

	public MulticastMessage(String groupName, String kind, Object data) {
		this.groupName = groupName;
		this.kind = kind; 
		this.data = data;
	}
	
}
