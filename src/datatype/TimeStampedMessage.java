package datatype;

import time.timestamp.TimeStamp;

@SuppressWarnings("serial")
public class TimeStampedMessage extends Message implements Comparable<TimeStampedMessage> {

	private TimeStamp timestamp = null;
	private boolean requireLog = false;

	public TimeStampedMessage(String dest, String kind, Object data) {
		super(dest, kind, data);
	}

	public TimeStamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(TimeStamp timestamp) {
		this.timestamp = timestamp;
	}
	
	public boolean isRequireLog() {
		return requireLog;
	}

	public void setRequireLog(boolean requireLog) {
		this.requireLog = requireLog;
	}

	@Override
	public int compareTo(TimeStampedMessage message) {
		if (this.timestamp != null) {
			return this.timestamp.compareTo(message.getTimestamp());
		}
		return -1;
	}
}
