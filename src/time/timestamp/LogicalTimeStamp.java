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
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int compareTo(TimeStamp t) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}