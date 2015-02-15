package lab0;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import datatype.MulticastMessage;

public class MulticaseService {
	private MessagePasser mp = null;
	private BufferManager bm = null;
	private int N;
	private int Npos;
	private int G;
	private int[][] vectors = null;
	private List<List<MulticastMessage>> holdQueues = null;
	private Map<String, Groups> groupMap = null;
	
	List<String> nodeList = null;
	List<String> groupList = null;
	
	public MulticaseService(ConfigLoader config, String localName, MessagePasser mp, BufferManager bm) {
		this.nodeList = config.getNodeList();
		this.groupList = config.getGroupList();
		this.groupMap = config.getGroups();
		
		this.N = nodeList.size();
		this.Npos = nodeList.indexOf(localName);
		this.G = groupList.size();
		vectors = new int[G][N];
		holdQueues = new ArrayList<List<MulticastMessage>>(G);
		for (int i = 0 ; i < G ; i++) {
			holdQueues.set(i, new ArrayList<MulticastMessage>(N));
		}
		this.bm = bm;
		this.mp = mp;
	}
	
	public void RCOMulticast(MulticastMessage message) {
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
		this.BMulticast(message);
	}
	
	private void RCODeliver(MulticastMessage message) {
		bm.addToIncomingBuffer(message);
	}
	
	private void BMulticast(MulticastMessage message) {
		String groupName = message.getGroupName();
		for (String dest : this.groupMap.get(groupName).getMembers()) {
			MulticastMessage messageToSend = new MulticastMessage(groupName,
					dest, message.getKind(), message.getData());
			mp.send(messageToSend);
		}
	}
	
	/*
	 * Called by Receiving client or buffermanager
	 */
	public void BDeliver(MulticastMessage message) {
		if (message == null) {
			return;
		}
		// place <Vgj , m> in hold-back queue;
		int groupIndex = groupList.indexOf(message.getGroupName());
		int processIndex = this.nodeList.indexOf(message.getSrc());
		holdQueues.get(groupIndex).add(message);
		
		
		MulticastMessage messageToDeliver = this.nextAvailable(groupIndex, processIndex);
		if (messageToDeliver != null) {
			this.RCODeliver(messageToDeliver);
		}
	}
	
	private MulticastMessage nextAvailable(int groupIndex, int processIndex) {
		List<MulticastMessage> queue = holdQueues.get(groupIndex);
		for (MulticastMessage message : queue) {
			boolean match = true;
			int[] vector = message.getVector();
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
	
}
