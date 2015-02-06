package time;

import java.util.List;

import datatype.Node;

public abstract class ClockFactory {
	
	private static ClockService logical = null;
	private static ClockService vector = null;
	
	int nodeNum = 0;
	
	/*
	 * Must specify the number of nodes in this system
	 */
	public ClockFactory(int nodeNum) {
		nodeNum = Math.max(this.nodeNum, nodeNum);
	}
	
	public static ClockService getClockInstance(ClockType type, List<String> list, String localName) {
		if (ClockType.LOGICAL.equals(type)) {
			if (logical == null) {
				logical = new LogicalClock(); 
			}
			return logical;
		} else if (ClockType.VECTOR.equals(type)) {
			if (vector == null) {
				vector = new VectorClock(list, localName); 
			}
			return vector;
		}
		return null;
	}
}
