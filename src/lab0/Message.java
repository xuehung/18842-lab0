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
	 public void setSource(String source) {
		 this.src = source;
	 }
	 public void setSeqNum(int sequenceNumber) {
		 this.seqNum = sequenceNumber;
	 }
	 public void setDuplicate(Boolean dupe) {
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
	public String getSrc() {
		return src;
	}
	public int getSeqNum() {
		return seqNum;
	}
	public boolean getDup(){
		return isDuplicate;
	}
	
	public Message clone() {
		Message clonedMsg = new Message(dest, kind, data);
		clonedMsg.setSource(src);
		clonedMsg.setDuplicate(true);
		clonedMsg.setSeqNum(seqNum);
		return clonedMsg;
	}
	 
	 
	 // other accessors, toString, etc as needed
}
