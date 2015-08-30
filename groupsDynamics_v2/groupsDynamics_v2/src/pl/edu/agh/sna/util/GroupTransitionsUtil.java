package pl.edu.agh.sna.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.edu.agh.sna.model.Group;
import pl.edu.agh.sna.model.GroupTransition;

import com.google.common.collect.Multimap;

public class GroupTransitionsUtil {
	public static Collection<Group> getGroupPredecessors(Group group, Multimap<Group, GroupTransition> groupTransitions) {
		List<Group> list = new ArrayList<>();
		for (GroupTransition transition : groupTransitions.values()) {
			if (transition.getTo().equals(group)) {
				list.add(transition.getFrom());
			}
		}

		return list;
	}

	public static Collection<Group> getGroupSuccessors(Group group, Multimap<Group, GroupTransition> groupTransitions) {
		List<Group> list = new ArrayList<>();
		for (GroupTransition transition : groupTransitions.values()) {
			if (transition.getFrom().equals(group)) {
				list.add(transition.getTo());
			}
		}

		return list;
	}

	public static Map<String, Group> extractGroups(Multimap<Group, GroupTransition> groupTransitions) {
		Map<String, Group> map = new HashMap<>();
		for (GroupTransition transition : groupTransitions.values()) {
			Group fromGroup = transition.getFrom();
			Group toGroup = transition.getTo();
			map.put(fromGroup.getName(), fromGroup);
			map.put(toGroup.getName(), toGroup);
		}
		return map;
	}

	public static Collection<GroupTransition> getGroupSuccessorTransitions(Group group,
			Multimap<Group, GroupTransition> groupTransitions) {
		List<GroupTransition> list = new ArrayList<>();
		for (GroupTransition transition : groupTransitions.values()) {
			if (transition.getFrom().equals(group)) {
				list.add(transition);
			}
		}

		return list;
	}

	public static Collection<GroupTransition> getGroupPredecessorTransitions(Group group,
			Multimap<Group, GroupTransition> groupTransitions) {
		List<GroupTransition> list = new ArrayList<>();
		for (GroupTransition transition : groupTransitions.values()) {
			if (transition.getTo().equals(group)) {
				list.add(transition);
			}
		}

		return list;
	}
}
