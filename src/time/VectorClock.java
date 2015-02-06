package time;

import java.util.List;

public class VectorClock extends ClockService {
	private int size = 0;
	private int localPos = 0;
	private int[] vector = null;
	
	public VectorClock(List<String> list, String myName) {
		this.size = list.size();
		this.localPos = list.indexOf(myName);
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
