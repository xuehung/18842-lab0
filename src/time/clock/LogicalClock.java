package time.clock;

import time.timestamp.LogicalTimeStamp;
import time.timestamp.TimeStamp;

public class LogicalClock extends ClockService {
	
	private int counter = 0;
	
	protected LogicalClock(String localName) {
		this.localName = localName;
	}
	
	public TimeStamp getTime() {
		return new LogicalTimeStamp(counter++, localName);
	}

	@Override
	public TimeStamp getTime(TimeStamp t) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
