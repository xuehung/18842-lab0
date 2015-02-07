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
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < vector.length; i++) {
		   result.append( vector[i] );
		}
		String mynewstring = result.toString();
		return mynewstring;
	}
	
	@Override
	public int compareTo(TimeStamp t) {
		int[] v1 = vector;
		int[] v2 = (int[]) t.getTime();
		boolean equlity = true, less = false, more = false;
		for(int i = 0; i< this.vector.length; i++) {
			if (v1[i] != v2[i] )	
				equlity = false;
			if (v1[i] < v2[i])
				less = true;
			else if (v1[i] > v2[i])
				more = true;
		}
		if (equlity == true || (less == true && more == true)) 
			return 0;
		else if (less = true && more == false) 
			return -1;
		else 
			return 1;
				
	}

}
