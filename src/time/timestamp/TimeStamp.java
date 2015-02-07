package time.timestamp;

import java.io.Serializable;

public abstract class TimeStamp implements Comparable<TimeStamp>, Serializable {
	/**
	 * 	serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	protected String localName = null;
	public abstract int compareTo(TimeStamp t);
	public abstract String toString();
	public abstract Object getTime();
}
