package time.clock;

import time.timestamp.LogicalTimeStamp;
import time.timestamp.TimeStamp;

public class LogicalClock extends ClockService {
	
	private int counter = 0;
	
	protected LogicalClock(String localName) {
		this.localName = localName;
	}
	
	@Override
	public synchronized TimeStamp getTime() {
		return new LogicalTimeStamp(++counter, localName);
	}

	@Override
	public synchronized TimeStamp getTime(TimeStamp t) {
		if (t instanceof LogicalTimeStamp) {
			LogicalTimeStamp lts = (LogicalTimeStamp)(t);
			counter = Math.max(counter, (Integer)lts.getTime());
			return new LogicalTimeStamp(++counter, localName);
		}
		return null;
	}

	@Override
	public String showTime() {
		return counter + "";
	}
	
}
