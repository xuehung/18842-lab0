package time.clock;

import java.util.ArrayList;
import java.util.Collections;
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
		if (t instanceof VectorTimeStamp) {
			int[] outVector = ((VectorTimeStamp)t).getTime();
			for(int i = 0; i < size; i++) {
				if (i == localPos) 
						this.vector[i]++;
				else if (outVector[i] > this.vector[i]) 
					this.vector[i] = outVector[i];
			}
			return new VectorTimeStamp(this.vector);
		}
		return null;
	}
	
	public static void main(String[] argv) {
		int[] v1 = {1, 2, 5};
		int[] v2 = {3, 6, 6};
		int[] v3 = {6, 7, 7};
		int[] v4 = {1, 3, 7};
		int[] v5 = {2, 4, 5};
		ArrayList<VectorTimeStamp> list = new ArrayList<VectorTimeStamp>();
		list.add(new VectorTimeStamp(v1));
		list.add(new VectorTimeStamp(v2));
		list.add(new VectorTimeStamp(v3));
		list.add(new VectorTimeStamp(v4));
		list.add(new VectorTimeStamp(v5));
		
		System.out.println("Concurrent process: ");
		for(int j=0;j<list.size()-1;j++) {
			for(int i=j+1; i<list.size(); i++) {
				if (list.get(j).compareTo(list.get(i)) == 0) {
					System.out.println(list.get(j).toString()+"||"+list.get(i).toString());
				}
			}
		}
		System.out.println("Sequential process: ");
		for(int j=0;j<list.size()-1;j++) {
			for(int i=j+1; i<list.size(); i++) {
				if (list.get(j).compareTo(list.get(i)) < 0) {
					System.out.println(list.get(j).toString()+"->"+list.get(i).toString());
				}
			}
		}
		for(int j=0;j<list.size()-1;j++) {
			for(int i=j+1; i<list.size(); i++) {
				if (list.get(j).compareTo(list.get(i)) > 0) {
					System.out.println(list.get(i).toString()+"->"+list.get(j).toString());
				}
			}
		}
		
//		for (int i = 0 ; i < list.size() ; i++) {
//			VectorTimeStamp max = list.get(i);
//			int maxIndex = i;
//			for (int j = 1 ; j < list.size() - i ; j++) {
//				if (list.get(j).compareTo(t))
//			}
//		}
//		
	}

}
