package lab0;

import java.util.ArrayList;
import java.util.List;

import datatype.Node;

public class Groups {
	
	private ArrayList<String> members = null;

	public Groups(ArrayList<String> members){
		this.members = members;
	}
	public ArrayList<String> getMembers(){
		return this.members;
	}
}
