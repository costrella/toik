package pl.edu.agh.sna.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;

import pl.edu.agh.sna.model.Group;
import pl.edu.agh.sna.model.TimeSlot;

public class GroupAnalyzerUtil {
	public static Map<Integer, Integer> countGroupSizes(LinkedHashMap<Integer, TimeSlot> timeslotMap, int year) {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (TimeSlot timeslot : timeslotMap.values()) {
			if (new DateTime(timeslot.getEndDate()).getYear() == year) {
				for (Group group : timeslot.getGroupMap().values()) {
					int size = group.getMembers().size();
					if (map.containsKey(size)) {
						map.put(size, map.get(size) + 1);
					} else {
						map.put(size, 1);
					}
				}
			}
		}
		return map;
	}

	public static Map<Integer, Integer> countGroupSizes(LinkedHashMap<Integer, TimeSlot> timeslotMap) {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (TimeSlot timeslot : timeslotMap.values()) {
			for (Group group : timeslot.getGroupMap().values()) {
				int size = group.getMembers().size();
				if (map.containsKey(size)) {
					map.put(size, map.get(size) + 1);
				} else {
					map.put(size, 1);
				}
			}
		}
		return map;
	}

	public static Map<Integer, Integer> countGroupSizes(LinkedHashMap<Integer, TimeSlot> timeslotMap, int year,
			Set<Group> stableGroups) {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (TimeSlot timeslot : timeslotMap.values()) {
			if (new DateTime(timeslot.getEndDate()).getYear() == year) {
				for (Group group : timeslot.getGroupMap().values()) {
					if (stableGroups.contains(group)) {
						int size = group.getMembers().size();
						if (map.containsKey(size)) {
							map.put(size, map.get(size) + 1);
						} else {
							map.put(size, 1);
						}
					}
				}
			}
		}
		return map;
	}

	public static Map<Integer, Integer> countGroupSizes(Set<Group> groups) {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (Group group : groups) {
			int size = group.getMembers().size();
			if (map.containsKey(size)) {
				map.put(size, map.get(size) + 1);
			} else {
				map.put(size, 1);
			}
		}

		return map;
	}

	public static Map<Integer, Integer> countGroupSizes(LinkedHashMap<Integer, TimeSlot> timeslotMap,
			Set<Group> stableGroups) {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (Group group : stableGroups) {
			int size = group.getMembers().size();
			if (map.containsKey(size)) {
				map.put(size, map.get(size) + 1);
			} else {
				map.put(size, 1);
			}
		}

		// Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		// for (TimeSlot timeslot : timeslotMap.values()) {
		// for (Group group : timeslot.getGroupMap().values()) {
		// if (stableGroups.contains(group)) {
		// int size = group.getMembers().size();
		// if (map.containsKey(size)) {
		// map.put(size, map.get(size) + 1);
		// } else {
		// map.put(size, 1);
		// }
		// }
		// }
		// }
		return map;
	}

}
