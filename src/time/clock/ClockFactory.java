package time.clock;

import java.util.List;

public abstract class ClockFactory {
	
	private static ClockService clock = null;
	
	int nodeNum = 0;
	
	/*
	 * Must specify the number of nodes in this system
	 */
	public ClockFactory(int nodeNum) {
		nodeNum = Math.max(this.nodeNum, nodeNum);
	}
	
	public static ClockService getClockInstance(ClockType type, List<String> list, String localName) {
		if (clock == null) {
			if (ClockType.LOGICAL.equals(type)) {
				clock = new LogicalClock(localName); 
			} else if (ClockType.VECTOR.equals(type)) {
				clock = new VectorClock(list, localName); 
			}
		}
		return clock;
	}
}
