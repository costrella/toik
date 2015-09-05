package pl.edu.agh.sna.demo;

import java.util.HashSet;
import java.util.Set;

import pl.edu.agh.sna.model.Group;

public class Member {
	String id;
	private Set<Group> stableGroups = new HashSet<>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Set<Group> getStableGroups() {
		return stableGroups;
	}

	public void setStableGroups(Set<Group> stableGroups) {
		this.stableGroups = stableGroups;
	}

}
