package time.timestamp;

public abstract class TimeStamp {
	protected String localName = null;
	public abstract boolean compareTo(TimeStamp t);
}
