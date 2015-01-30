package lab0;

import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class BufferManager {
	final private int BUFFER_LEN = 1000;
	private LinkedBlockingQueue<Message> incomingBuffer = null;
	private LinkedBlockingQueue<Message> outgoingBuffer = null;
	private Queue<Message> incomingDelayQueue = null;
	private Queue<Message> outgoingDelayQueue = null;
	
	public BufferManager() {	
		this.incomingBuffer = new LinkedBlockingQueue<Message>(BUFFER_LEN);
		this.outgoingBuffer = new LinkedBlockingQueue<Message>(BUFFER_LEN);
		this.incomingDelayQueue = new ArrayDeque<Message>();
		this.outgoingDelayQueue = new ArrayDeque<Message>();
	}
	
	public void addToOutgoingBuffer(Message message) {
		outgoingBuffer.add(message);
	}
	
	public void addToIncomingBuffer(Message message) {
		incomingBuffer.add(message);
	}
	
	/**
	 * Return the first message whose destination has established the 
	 * connection and the socket to which has existed in the socketMap
	 * @param socketMap
	 * @return message or null if no such exists
	 */
	public Message takeFromOutgoingBuffer(Map<String, Socket> socketMap) {
		Iterator<Message> it = outgoingBuffer.iterator();
		while (it.hasNext()) {
			Message message = it.next();
			if (socketMap.containsKey(message.getDest())) {
				outgoingBuffer.remove(message);
				return message;
			}
		}
		return null;
	}
	/**
	 * will block if the stack is empty or null if interrupted
	 * @return
	 */
	public Message takeFromIncomingBuffer() {
		try {
			return incomingBuffer.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Add the message in thread-safe way
	 * @param message
	 */
	public synchronized void delayOutgoingMessage(Message message) {
		outgoingDelayQueue.add(message);
	}
	
	/**
	 * Add the message in thread-safe way
	 * @param message
	 */
	public synchronized void delayIncomingMessage(Message message) {
		incomingDelayQueue.add(message);
	}
	
	public synchronized void clearDelayOutgoingMessage() {
		outgoingBuffer.addAll(outgoingDelayQueue);
		outgoingDelayQueue.clear();
	}
	
	public synchronized void clearDelayIncomingMessage() {
		incomingBuffer.addAll(incomingDelayQueue);
		incomingDelayQueue.clear();
	}
}
