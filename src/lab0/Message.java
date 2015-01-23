package lab0;

import java.io.Serializable;

public class Message implements Serializable {
	
	/**
	 * 	serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	private String src = null;
	private String dest = null;
	private String kind = null;
	private Object data = null;
	private int seqNum = -1;
	private boolean isDuplicate = false;
	
	
	public Message(String dest, String kind, Object data) {
		 this.dest = dest;
		 this.kind = kind;
		 this.data = data;
	 }
	 // These settors are used by MessagePasser.send, not your app
	 public void set_source(String source) {
		 this.src = source;
	 }
	 public void set_seqNum(int sequenceNumber) {
		 this.seqNum = sequenceNumber;
	 }
	 public void set_duplicate(Boolean dupe) {
		 this.isDuplicate = dupe;
	 }
	 public String getDest() {
			return dest;
	}
	public String getKind() {
		return kind;
	}
	public Object getData() {
		return data;
	}
	 
	 
	 // other accessors, toString, etc as needed
}
