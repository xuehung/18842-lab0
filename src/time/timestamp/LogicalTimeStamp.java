package time.timestamp;

public class LogicalTimeStamp extends TimeStamp {
	private int time;
	
	public LogicalTimeStamp(int counter, String localName) {
		this.time = counter;
		this.localName = localName;
	}
	
	/*
	
	private TimeStamp logicalTimeStamp;
	public void LogicalTimeStamp(int counter) {
	this.logicalTimeStamp = super(counter);
	
	}
	*/
	
	@Override
	public boolean compareTo(TimeStamp t) {
		return false;
				
	}
	
}
