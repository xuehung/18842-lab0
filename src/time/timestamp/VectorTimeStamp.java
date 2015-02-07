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
