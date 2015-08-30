package pl.edu.agh.sna.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Group {
	private LinkedHashMap<Integer, Double> params;
	private Set<String> members;
	private String globalName;
	private String localName;
	private int slotNo;
	private Map<String, Map<String, Double>> contextMap;

	public Group() {
		members = new HashSet<>();
		params = new LinkedHashMap<Integer, Double>();
		contextMap = new HashMap<String, Map<String, Double>>();
	}

	public Group(String name) {
		this();
		setName(name);
	}

	public Group(String name, String member) {
		this(name);
		addMember(member);
	}

	public Group(String name, String member, int slotNo) {
		this(name, member);
		this.slotNo = slotNo;
	}

	public void addMember(String member) {
		members.add(member);
	}

	public void addMembers(Collection<String> membersCollection) {
		members.addAll(membersCollection);
	}

	public Set<String> getMembers() {
		return members;
	}

	public void removeMember(String member) {
		members.remove(member);
	}

	public String getName() {
		return globalName;
	}

	public void setName(String name) {
		this.globalName = name;
	}

	public Map<Integer, Double> getParams() {
		return params;
	}

	public int getSlotNo() {
		return slotNo;
	}

	public void setSlotNo(int slotNo) {
		this.slotNo = slotNo;
	}

	public String getLocalName() {
		return localName;
	}

	public void setLocalName(String localName) {
		this.localName = localName;
	}

	public void addContext(String contextName) {
		if (!contextMap.containsKey(contextName)) {
			contextMap.put(contextName, new HashMap<String, Double>());
		}
	}

	public void addContext(String contextName, Map<String, Double> map) {
		contextMap.put(contextName, map);
	}

	public Map<String, Double> getContext(String contextName) {
		if (contextMap.containsKey(contextName)) {
			return contextMap.get(contextName);
		} else {
			return new HashMap<String, Double>();
		}
	}

	public boolean containsContext(String contextName) {
		return contextMap.containsKey(contextName);
	}

	public Map<String, Map<String, Double>> getContextMap() {
		return contextMap;
	}

	@Override
	public String toString() {
		return getName();// + " - " + members.toString();
	}

}
