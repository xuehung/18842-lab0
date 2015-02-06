package time;

public class LogicalTimeStamp extends TimeStamp {
	private int time;
	
	public LogicalTimeStamp(int counter) {
		this.time = counter;
	}
	
	/*
	
	private TimeStamp logicalTimeStamp;
	public void LogicalTimeStamp(int counter) {
	this.logicalTimeStamp = super(counter);
	
	}
	*/
	public int[] getTime() {
		return null;
	}
	@Override
	public boolean compareTo(TimeStamp t) {
		return false;
				
	}
	
}
