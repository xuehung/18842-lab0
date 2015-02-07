package time.timestamp;

public abstract class TimeStamp implements Comparable<TimeStamp> {
	protected String localName = null;
	public abstract int compareTo(TimeStamp t);
	public abstract String toString();
	public abstract Object getTime();
}
