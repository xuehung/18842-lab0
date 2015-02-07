package datatype;

import java.io.Serializable;

import time.timestamp.TimeStamp;

public class LogEvent implements Serializable {

	/**
	 * default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	private TimeStamp timestamp = null;
	private String text = null;
	public LogEvent(TimeStamp timestamp, String text) {
		this.timestamp = timestamp;
		this.text = text;
	}
	public TimeStamp getTimestamp() {
		return timestamp;
	}
	public String getText() {
		return text;
	}
}
