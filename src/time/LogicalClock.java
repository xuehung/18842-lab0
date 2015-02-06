package time;

public class LogicalClock extends ClockService {
	
	private int counter = 0;
	
	public TimeStamp getTime() {
		return new LogicalTimeStamp(counter++);
	}

	@Override
	public TimeStamp getTime(TimeStamp t) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
