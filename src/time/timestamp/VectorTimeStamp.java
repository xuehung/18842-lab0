package time.timestamp;

import java.util.Arrays;

public class VectorTimeStamp extends TimeStamp {
	
	int[] vector = null;
	
	public VectorTimeStamp() {
		
	}
	public VectorTimeStamp(int[] vector) {
		this.vector = Arrays.copyOf(vector, vector.length);
	}
	
	public int[] getTime() {
		return vector;
	}

	@Override
	public boolean compareTo(TimeStamp t) {
		// TODO Auto-generated method stub
		return false;
	}

}
