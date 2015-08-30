package pl.edu.agh.sna.dynamics;

import static pl.edu.agh.sna.util.StringConstants.SPACE;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import pl.edu.agh.sna.measures.DiffSizeTimesMeasure;
import pl.edu.agh.sna.measures.ModifiedJaccardIndex;
import pl.edu.agh.sna.model.Group;
import pl.edu.agh.sna.model.GroupTransition;
import pl.edu.agh.sna.model.TimeSlot;
import pl.edu.agh.sna.util.GroupTransitionsUtil;
import pl.edu.agh.sna.util.MapUtil;

import com.google.common.base.Preconditions;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

public class GroupEvolutionTracker {
	private static final int MIN_GROUP_SIZE = 3;
	private List<TimeSlot> timeslots;
	private Set<Group> stableGroups = new HashSet<>();
	private Multimap<Group, GroupTransition> groupTransitions = LinkedListMultimap.create();
	private Multimap<Group, GroupTransition> decayGroupTransitions = LinkedListMultimap.create();

	private double evolutionThreshold = 0.5;
	private int ratioBetweenGroupsForMatching = 50;
	private int ratioBetweenGroupsForStrongMatching = 10;
	private double ratioForConstancy = 0.05;
	private int minDurationTimeForStableGroups = 3;

	private int lastSlotNo = -1;

	public void setTimeslots(List<TimeSlot> timeslots) {
		this.timeslots = timeslots;
	}

	public void evaluateSgci() {
		makeTransitions(evolutionThreshold);
		extractStableGroups();
		assignEventsToTransitions();
		appendDecayEvents();
	}

	private void makeTransitions(double threshold) {
		Preconditions.checkNotNull(timeslots);
		Preconditions.checkArgument(timeslots.size() > 1);

		ModifiedJaccardIndex mJaccard = new ModifiedJaccardIndex();
		DiffSizeTimesMeasure diffSizeTimesMeasure = new DiffSizeTimesMeasure();

		TimeSlot currentSlot = timeslots.get(0);
		TimeSlot nextSlot;

		for (int slotNo = 1; slotNo < timeslots.size(); slotNo++) {
			nextSlot = timeslots.get(slotNo);
			lastSlotNo = nextSlot.getNumber();

			for (Group currentGroup : currentSlot.getGroupMap().values()) {
				if (currentGroup.getMembers().size() < MIN_GROUP_SIZE)
					continue;

				for (Group nextGroup : nextSlot.getGroupMap().values()) {
					if (nextGroup.getMembers().size() < MIN_GROUP_SIZE)
						continue;
					double diffSizeTimesValue = diffSizeTimesMeasure.calculate(currentGroup.getMembers(),
							nextGroup.getMembers());

					if (diffSizeTimesValue < ratioBetweenGroupsForMatching) {
						double match = mJaccard.calculate(currentGroup.getMembers(), nextGroup.getMembers());

						if (match >= threshold) {
							GroupTransition transition = new GroupTransition(currentGroup, nextGroup, match);
							groupTransitions.put(currentGroup, transition);
						}
					}
				}

			}
			currentSlot = nextSlot;
		}
	}

	private void assignEventsToTransitions() {
		int RATIO = ratioBetweenGroupsForStrongMatching;
		for (GroupTransition transition : groupTransitions.values()) {
			// System.out.println("slott: " + transition.getFrom().getName() +
			// "-" + transition.getTo().getName());
			Group fromGroup = transition.getFrom();
			Group toGroup = transition.getTo();

			int fromSize = fromGroup.getMembers().size();
			int toSize = toGroup.getMembers().size();

			Collection<Group> successors = GroupTransitionsUtil.getGroupSuccessors(fromGroup, groupTransitions);
			Collection<Group> predecessors = GroupTransitionsUtil.getGroupPredecessors(toGroup, groupTransitions);

			if (fromSize / toSize >= RATIO) {
				transition.setEventType(GroupEvents.DELETION);
			} else if (toSize / fromSize >= RATIO) {
				transition.setEventType(GroupEvents.ADDITION);
			} else if (fromSize - toSize > 0) {
				boolean similarSucc = false;
				for (Group succ : successors) {
					if (!toGroup.getName().equals(succ.getName())) {
						int succSize = succ.getMembers().size();
						if (fromSize / succSize < RATIO && succSize / fromSize < RATIO) {
							similarSucc = true;
							break;
						}
					}
				}
				boolean similarPred = false;
				for (Group pred : predecessors) {
					if (!fromGroup.getName().equals(pred.getName())) {
						int predSize = pred.getMembers().size();
						if (predSize / toSize < RATIO && toSize / predSize < RATIO) {
							similarPred = true;
							break;
						}
					}
				}
				if (similarSucc) {
					if (similarPred) {
						transition.setEventType(GroupEvents.SPLIT_MERGE);
					} else {
						transition.setEventType(GroupEvents.SPLIT);
					}
				} else {
					if (1.0 * (fromSize - toSize) / fromSize <= ratioForConstancy) {
						transition.setEventType(GroupEvents.CONSTANCY);
					} else {
						transition.setEventType(GroupEvents.CHANGE_SIZE);
					}
				}
			} else {
				boolean similar = false;
				for (Group pred : predecessors) {
					if (!fromGroup.getName().equals(pred.getName())) {
						int predSize = pred.getMembers().size();
						if (predSize / toSize < RATIO && toSize / predSize < RATIO) {
							similar = true;
							break;
						}
					}
				}
				if (similar) {
					transition.setEventType(GroupEvents.MERGE);
				} else {
					if (1.0 * (toSize - fromSize) / fromSize <= ratioForConstancy) {
						transition.setEventType(GroupEvents.CONSTANCY);
					} else {
						transition.setEventType(GroupEvents.CHANGE_SIZE);
					}
				}
			}
		}
	}

	private void extractStableGroups() {
		if (minDurationTimeForStableGroups == 1) {
			extractFugitiveGroups();
			return;
		}
		if (minDurationTimeForStableGroups > 2) {
			Set<String> notStableGroupNames = new HashSet<>();

			Set<String> startGroupNames = new HashSet<>();
			for (Group group : groupTransitions.keySet()) {
				startGroupNames.add(group.getName());
			}

			for (GroupTransition transition : groupTransitions.values()) {
				if (transition.getTo() != null && startGroupNames.contains(transition.getTo().getName())) {
					startGroupNames.remove(transition.getTo().getName());
				}
			}

			Map<String, Group> groupMap = GroupTransitionsUtil.extractGroups(groupTransitions);

			for (String startGroupName : startGroupNames) {
				Collection<GroupTransition> transitions = groupTransitions.get(groupMap.get(startGroupName));

				boolean stable = false;
				for (GroupTransition t : transitions) {
					Group group = t.getTo();
					if (group != null && groupTransitions.containsKey(group)) {
						stable = true;
					}
				}
				if (!stable) {
					notStableGroupNames.add(startGroupName);
				}

			}

			for (String notStableGroupName : notStableGroupNames) {
				groupTransitions.removeAll(groupMap.get(notStableGroupName));
				startGroupNames.remove(notStableGroupName);
			}
		}

		stableGroups.clear();
		for (GroupTransition transition : groupTransitions.values()) {
			if (transition.getTo() != null) {
				stableGroups.add(transition.getFrom());
				stableGroups.add(transition.getTo());
			}
		}

	}

	private void extractFugitiveGroups() {
		stableGroups.clear();
		for (TimeSlot timeslot : timeslots) {
			stableGroups.addAll(timeslot.getGroupMap().values());
		}
	}

	public void saveGroupEvolutionsWithEvents(String path) throws IOException {
		Map<GroupEvents, Integer> eventsMap = new HashMap<>();

		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path)));

		for (TimeSlot timeslot : timeslots) {

			for (Group group : timeslot.getGroupMap().values()) {
				int timeslotNo = timeslot.getNumber();

				if (stableGroups.contains(group)) {
					Collection<GroupTransition> successors = GroupTransitionsUtil.getGroupSuccessorTransitions(group,
							groupTransitions);
					if (successors.size() > 0) {
						List<Integer> groupSuccIds = new ArrayList<>();
						Map<Integer, GroupTransition> tmp = new HashMap<>();
						for (GroupTransition succTrans : successors) {
							Integer localId = Integer.valueOf(succTrans.getTo().getLocalName());
							groupSuccIds.add(localId);
							tmp.put(localId, succTrans);
						}
						Collections.sort(groupSuccIds);
						for (Integer succKey : groupSuccIds) {
							GroupTransition succTrans = tmp.get(succKey);
							StringBuilder sb = new StringBuilder();
							sb.append(timeslotNo + SPACE);
							sb.append(group.getLocalName()).append(SPACE);
							sb.append(succTrans.getEventType() + SPACE);
							sb.append((timeslotNo + 1) + SPACE);
							sb.append(succTrans.getTo().getLocalName());

							writer.write(sb.toString());
							writer.newLine();

							MapUtil.addOccurenceToMap(eventsMap, succTrans.getEventType());
						}
					} else if (timeslot.getNumber() != lastSlotNo) {
						StringBuilder sb = new StringBuilder();
						sb.append(timeslotNo + SPACE);
						sb.append(group.getLocalName()).append(SPACE);
						sb.append(GroupEvents.DECAY).append(SPACE);
						sb.append((timeslotNo + 1) + SPACE);
						sb.append("NULL");

						writer.write(sb.toString());
						writer.newLine();

						MapUtil.addOccurenceToMap(eventsMap, GroupEvents.DECAY);
					}
				}
			}
		}

		writer.newLine();
		writer.write("summary\n");
		for (Entry<GroupEvents, Integer> entry : eventsMap.entrySet()) {
			writer.write(entry.getKey() + SPACE + entry.getValue() + "\n");
		}

		writer.close();
	}

	public Collection<GroupTransition> getGroupTransitionsFromTimeslot(TimeSlot timeslot) {
		Preconditions.checkNotNull(timeslot);

		Set<GroupTransition> setOfTransitions = new HashSet<>();
		for (Group group : timeslot.getGroupMap().values()) {
			Collection<GroupTransition> successors = GroupTransitionsUtil.getGroupSuccessorTransitions(group,
					groupTransitions);
			if (successors.size() > 0) {
				setOfTransitions.addAll(successors);
			}
		}

		return setOfTransitions;
	}

	public Multimap<Group, GroupTransition> getGroupTransitions() {
		return Multimaps.unmodifiableMultimap(groupTransitions);
	}

	public Multimap<Group, GroupTransition> getGroupDecays() {
		return Multimaps.unmodifiableMultimap(decayGroupTransitions);
	}

	public Multimap<Group, GroupTransition> getAllGroupTransitions() {
		Multimap<Group, GroupTransition> allTransitions = LinkedListMultimap.create();
		allTransitions.putAll(groupTransitions);
		allTransitions.putAll(decayGroupTransitions);
		return Multimaps.unmodifiableMultimap(allTransitions);
	}

	public Collection<GroupTransition> getAllGroupTransitionsAsCollection() {
		return getAllGroupTransitions().values();
	}

	public double getEvolutionThreshold() {
		return evolutionThreshold;
	}

	public void setEvolutionThreshold(double evolutionThreshold) {
		Preconditions.checkArgument(evolutionThreshold > 0 && evolutionThreshold < 1,
				"Evolution threshold should be between 0 and 1");
		this.evolutionThreshold = evolutionThreshold;
	}

	public int getRatioBetweenGroupsForMatching() {
		return ratioBetweenGroupsForMatching;
	}

	public void setRatioBetweenGroupsForMatching(int ratioBetweenGroupsForMatching) {
		this.ratioBetweenGroupsForMatching = ratioBetweenGroupsForMatching;
	}

	public int getRatioBetweenGroupsForStrongMatching() {
		return ratioBetweenGroupsForStrongMatching;
	}

	public void setRatioBetweenGroupsForStrongMatching(int ratioBetweenGroupsForStrongMatching) {
		this.ratioBetweenGroupsForStrongMatching = ratioBetweenGroupsForStrongMatching;
	}

	public double getRatioForConstancy() {
		return ratioForConstancy;
	}

	public void setRatioForConstancy(double ratioForConstancy) {
		Preconditions.checkArgument(ratioForConstancy >= 0 && ratioForConstancy < 0.2,
				"Ratio for constancy event should be between 0 and 0.2");
		this.ratioForConstancy = ratioForConstancy;
	}

	public int getMinDurationTimeForStableGroups() {
		return minDurationTimeForStableGroups;
	}

	public void setMinDurationTimeForStableGroups(int minDurationTimeForStableGroups) {
		Preconditions.checkArgument(minDurationTimeForStableGroups > 0 && minDurationTimeForStableGroups <= 3,
				"Min duration time for stable groups should be no more than 3");
		this.minDurationTimeForStableGroups = minDurationTimeForStableGroups;
	}

	public Set<Group> getStableGroups() {
		return Collections.unmodifiableSet(stableGroups);
	}

	private void appendDecayEvents() {

		for (TimeSlot timeslot : timeslots) {
			for (Group group : timeslot.getGroupMap().values()) {
				if (stableGroups.contains(group)) {
					Collection<GroupTransition> successors = GroupTransitionsUtil.getGroupSuccessorTransitions(group,
							groupTransitions);
					if (successors.size() == 0 && (timeslot.getNumber() != lastSlotNo)) {
						GroupTransition transition = new GroupTransition(group, null, 0);
						transition.setEventType(GroupEvents.DECAY);
						decayGroupTransitions.put(group, transition);
					}
				}
			}
		}
	}

}
