package lab0;

import java.util.ArrayList;
import java.util.List;

import datatype.MulticastMessage;
import datatype.Node;

public class MulticaseService {
	
	private BufferManager bm = null;
	private int N;
	private int Npos;
	private int G;
	private String localName = null;
	private int[][] vectors = null;
	private List<List<MulticastMessage>> holdQueues = null;
	
	List<String> nodeList = null;
	List<String> groupList = null;
	
	public MulticaseService(ConfigLoader config, String localName, BufferManager bm) {
		this.nodeList = config.getNodeList();
		this.groupList = config.getGroupList();
		
		this.N = nodeList.size();
		this.Npos = nodeList.indexOf(localName);
		this.G = groupList.size();
		vectors = new int[G][N];
		holdQueues = new ArrayList<List<MulticastMessage>>(G);
		for (int i = 0 ; i < G ; i++) {
			holdQueues.set(i, new ArrayList<MulticastMessage>(N));
		}
		this.bm = bm;
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
		
	}
	
	private void BMulticast(MulticastMessage message) {
		if (message == null) {
			return;
		}
		// TODO
	}
	
	public void BDeliver(MulticastMessage message) {
		if (message == null) {
			return;
		}
		// place <Vgj , m> in hold-back queue;
		int groupIndex = groupList.indexOf(message.getGroupName());
		int processIndex = this.nodeList.indexOf(message.getSrc());
		holdQueues.get(groupIndex).add(message);
		if () {
			
		}
	}
	
	private boolean nextAvailable(int groupIndex, int processIndex) {
		List<MulticastMessage> queue = holdQueues.get(groupIndex);
		for (MulticastMessage message : queue) {
			boolean 
			int[] vector = message.getVector();
			if (vector[processIndex] == this.vectors[groupIndex][processIndex] + 1) {
				
			}
		}
	}
	
}
