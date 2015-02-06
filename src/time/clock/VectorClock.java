package time.clock;

import java.util.List;

import time.timestamp.TimeStamp;
import time.timestamp.VectorTimeStamp;

public class VectorClock extends ClockService {
	private int size = 0;
	private int localPos = 0;
	private int[] vector = null;
	
	protected VectorClock(List<String> list, String localName) {
		this.size = list.size();
		this.localName = localName;
		this.localPos = list.indexOf(localName);
		this.vector = new int[size];
	}

	@Override
	public TimeStamp getTime() {
		VectorTimeStamp ts = new VectorTimeStamp(vector);
		vector[localPos]++;
		return ts;
	}

	@Override
	public TimeStamp getTime(TimeStamp t) {
		// TODO Auto-generated method stub
		return null;
	}
}
