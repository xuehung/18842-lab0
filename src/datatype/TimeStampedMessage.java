package datatype;

import time.timestamp.TimeStamp;

@SuppressWarnings("serial")
public class TimeStampedMessage extends Message {

	private TimeStamp timestamp = null;

	public TimeStampedMessage(String dest, String kind, Object data) {
		super(dest, kind, data);
	}

	public TimeStamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(TimeStamp timestamp) {
		this.timestamp = timestamp;
	}
}
