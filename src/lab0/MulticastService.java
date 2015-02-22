package lab0;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import datatype.Groups;
import datatype.MulticastMessage;

public class MulticastService {
	private static MulticastService ms = null; 
	private MessagePasser mp = null;
	private BufferManager bm = null;
	private String localName = null;
	private int N;
	private int Npos;
	private int G;
	private Integer[][] vectors = null;
	private List<List<MulticastMessage>> holdQueues = null;
	private Map<String, Groups> groupMap = null;
	private Set<String> received = null;
	
	List<String> nodeList = null;
	List<String> groupList = null;
	
	public static MulticastService getInstance(ConfigLoader config, String localName, MessagePasser mp, BufferManager bm) {
		if (ms == null) {
			ms = new MulticastService(config, localName, mp, bm);
		}
		return ms;
	}
	
	private MulticastService(ConfigLoader config, String localName, MessagePasser mp, BufferManager bm) {
		this.nodeList = config.getNodeList();
		this.groupList = config.getGroupList();
		this.groupMap = config.getGroups();
		this.localName = localName;
		
		this.N = nodeList.size();
		this.Npos = nodeList.indexOf(localName);
		this.G = groupList.size();
		vectors = new Integer[G][N];
		for (int i = 0 ; i < G ; i++) {
			for (int j = 0 ; j < N ; j++) {
				vectors[i][j] = 0;
			}
		}
		holdQueues = new ArrayList<List<MulticastMessage>>(G);
		for (int i = 0 ; i < G ; i++) {
			holdQueues.add(new ArrayList<MulticastMessage>());
		}
		this.bm = bm;
		this.mp = mp;
		this.received = new HashSet<String>();
	}
	
	public void RCOMulticast(MulticastMessage message) {
		System.out.println("RCOMulticast is called");
		if (message == null) {
			System.err.println("Multicase null message");
			return;
		}
		String groupName = message.getGroupName();
		if (groupName == null || this.groupList.indexOf(groupName) == -1) {
			System.err.println("Invalid groupName: " + groupName);
			return;
		}
		vectors[this.groupList.indexOf(groupName)][Npos] += 1;
		this.BMulticast(message, vectors[this.groupList.indexOf(groupName)]);
	}
	
	private void RCODeliver(MulticastMessage message) {
		System.out.println("RCODeliver is called");
		bm.addToIncomingBuffer(message);
	}
	
	/*
	 * Don't set vector, which means the message already has the vector
	 */
	private void BMulticast(MulticastMessage message) {
		BMulticast(message, null);
	}
	
	/*
	 * If vector is null, it is for reliable multicast.
	 * So don't send to itself and the receiver
	 */
	private void BMulticast(MulticastMessage message, Integer[] vector) {
		System.out.println("BMulticast is called");
		String groupName = message.getGroupName();
		for (String dest : this.groupMap.get(groupName).getMembers()) {
			MulticastMessage messageToSend = new MulticastMessage(groupName,
					dest, message.getKind(), message.getData());
			/* This node is the originator */
			if (vector != null) {
				messageToSend.setVector(vector.clone());
				messageToSend.setOriginator(localName);
			} else {
				/* This node help forward for reliable multicast */
				if (dest.equals(localName)) {
					continue;
				}
				messageToSend.setVector(message.getVector().clone());
				messageToSend.setOriginator(message.getOriginator());
			}
			
			/*
			 * send() returns false means the message is dropped because
			 * of the send rule. In this case, the process of multicast is
			 * regarded fail and stop sending the remaining messages.
			 */
			if (mp.send(messageToSend) == false) {
				break;
			}
		}
	}
	
	/*
	 * Called by Receiving client or bufferManager
	 */
	public void BDeliver(MulticastMessage message) {
		System.out.println("BDeliver is called");
		if (message == null) {
			return;
		}
		
		String key = getKey(message);
		if (this.received.contains(key)) {
			return;
		}
		// Received := Received union   m  ;
		this.received.add(key);
		
		
		if (message.getSrc().equals(localName)) {
			this.RCODeliver(message);
			return;
		}
		
		// B-multicast message
		this.BMulticast(message);
		
		
		// place <Vgj , m> in hold-back queue;
		int groupIndex = groupList.indexOf(message.getGroupName());
		int processIndex = this.nodeList.indexOf(message.getOriginator());
		System.out.println("add to holdQueues");
		holdQueues.get(groupIndex).add(message);
		
		MulticastMessage messageToDeliver = this.nextAvailable(groupIndex, processIndex);;
		while (messageToDeliver != null) {
			this.RCODeliver(messageToDeliver);
			messageToDeliver = this.nextAvailable(groupIndex, processIndex);;
		}
	}
	
	private MulticastMessage nextAvailable(int groupIndex, int processIndex) {
		System.out.println("nextAvailable() is called");
		List<MulticastMessage> queue = holdQueues.get(groupIndex);
		//System.out.println("queue size = "+queue);
		//System.out.println("this vector" + getIntArrayToString(this.vectors[groupIndex]));
		for (MulticastMessage message : queue) {
			boolean match = true;
			Integer[] vector = message.getVector();
			//System.out.println("message vector" + getIntArrayToString(vector));
			if (vector[processIndex] == this.vectors[groupIndex][processIndex] + 1) {
				for (int k = 0 ; k < N ; k++) {
					if (k != processIndex) {
						if (vector[k] > vectors[groupIndex][k]) {
							match = false;
							break;
						}
					}
				}
			} else {
				match = false;
			}
			if (match) {
				queue.remove(message);
				// Vgi j  := Vgi j +1
				vectors[groupIndex][processIndex]++;
				System.out.println("CO-deliver");
				return message;
			}
		}
		System.out.println("wait");
		return null;
	}
	
	private String getKey(MulticastMessage message) {
		return message.getGroupName() + ":" + getIntArrayToString(message.getVector());
	}
	
	private String getIntArrayToString(Integer[] vector) {
		StringBuilder sb = new StringBuilder();
		for (int i : vector) {
			sb.append(i);
			sb.append("|");
		}
		return sb.toString();
	}
	
}
