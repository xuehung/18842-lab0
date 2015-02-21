package datatype;

import java.util.ArrayList;
import java.util.List;

public class Groups {
	
	private ArrayList<String> members = null;

	public Groups(ArrayList<String> members){
		this.members = members;
	}
	public ArrayList<String> getMembers(){
		return this.members;
	}
}
