package time;

public abstract class ClockService {

	public abstract TimeStamp getTime();
	/* 
	 * this updates the time based on given timestamp, it is usually called
	 * when a message is received
	 */
	public abstract TimeStamp getTime(TimeStamp t);
	
		
}
