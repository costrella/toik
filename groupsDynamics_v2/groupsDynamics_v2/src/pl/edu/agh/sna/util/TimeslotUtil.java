package pl.edu.agh.sna.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import pl.edu.agh.sna.model.TimeSlot;

public class TimeslotUtil {
	private static Logger log = Logger.getLogger(TimeslotUtil.class);
	private static boolean startsFromOne = false;

	public static void setStartsFromOne(boolean flag) {
		startsFromOne = flag;
	}

	public static boolean isStartFromOne() {
		return startsFromOne;
	}

	public static List<TimeSlot> createTimeslotsForPeriods(DateTime startDate, DateTime endDate, int slotLength,
			int offset) {
		List<TimeSlot> timeslots = new ArrayList<>();

		DateTime slotStartDate = startDate;
		DateTime slotEndDate;
		int counter = 0;
		if (startsFromOne) {
			counter++;
		}
		while (slotStartDate.isBefore(endDate)) {
			slotEndDate = slotStartDate.plusDays(slotLength);

			TimeSlot timeslot = new TimeSlot(counter);
			timeslot.setStartDate(slotStartDate.toDate());
			timeslot.setEndDate(slotEndDate.toDate());
			timeslots.add(timeslot);

			slotStartDate = slotStartDate.plusDays(offset);
			counter++;
		}

		log.debug("timeslots: " + timeslots);
		return timeslots;
	}

	public static LinkedHashMap<Integer, TimeSlot> createTimeslotsForPeriodsAsMap(DateTime startDate, DateTime endDate,
			int slotLength, int offset) {
		List<TimeSlot> timeslots = createTimeslotsForPeriods(startDate, endDate, slotLength, offset);
		return convertTimeslotListToMap(timeslots);
	}

	private static LinkedHashMap<Integer, TimeSlot> convertTimeslotListToMap(List<TimeSlot> timeslots) {
		LinkedHashMap<Integer, TimeSlot> timeslotMap = new LinkedHashMap<>();

		for (TimeSlot timeslot : timeslots) {
			timeslotMap.put(timeslot.getNumber(), timeslot);
		}
		return timeslotMap;
	}

	public static List<TimeSlot> createTimeslots(int number) {
		List<TimeSlot> timeslots = new ArrayList<>();
		int modifier = startsFromOne ? 1 : 0;

		for (int i = 0; i < number; i++) {
			timeslots.add(new TimeSlot(i + modifier));
		}
		return timeslots;
	}

	public static LinkedHashMap<Integer, TimeSlot> createTimeslotsAsMap(int number) {
		List<TimeSlot> timeslots = createTimeslots(number);
		return convertTimeslotListToMap(timeslots);
	}

	public static void removeFirstSlots(List<TimeSlot> slots, int number) {
		for (int i = 0; i < number; i++) {
			slots.remove(0);
		}
	}

	public static void removeLastSlots(List<TimeSlot> slots, int number) {
		for (int i = 0; i < number; i++) {
			slots.remove(slots.size() - 1);
		}
	}

	public static void removeFirstSlots(LinkedHashMap<Integer, TimeSlot> slotMap, int number) {
		TreeSet<Integer> keys = new TreeSet<>(slotMap.keySet());
		Iterator<Integer> iter = keys.iterator();
		int i = 0;
		while (iter.hasNext() && i < number) {
			Integer key = iter.next();
			slotMap.remove(key);
			i++;
		}
	}

	public static void removeLastSlots(LinkedHashMap<Integer, TimeSlot> slotMap, int number) {
		TreeSet<Integer> keys = new TreeSet<>(slotMap.keySet());
		Iterator<Integer> iter = keys.descendingIterator();
		int i = 0;
		while (iter.hasNext() && i < number) {
			Integer key = iter.next();
			slotMap.remove(key);
			i++;
		}
	}

}
