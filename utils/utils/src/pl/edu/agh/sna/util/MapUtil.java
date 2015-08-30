package pl.edu.agh.sna.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MapUtil {
	public static <T> void addOccurenceToMap(Map<T, Integer> map, T id) {
		if (map.containsKey(id)) {
			map.put(id, map.get(id) + 1);
		} else {
			map.put(id, 1);
		}
	}

	public static <T> void addOccurenceToMap(Map<T, Integer> map, T id, Integer value) {
		if (map.containsKey(id)) {
			map.put(id, map.get(id) + value);
		} else {
			map.put(id, value);
		}
	}

	public static <T> void addOccurenceMapToMap(Map<T, Integer> mainMap, Map<T, Integer> partialMap) {
		for (Entry<T, Integer> entry : partialMap.entrySet()) {
			addOccurenceToMap(mainMap, entry.getKey(), entry.getValue());
		}
	}

	public static <T> List<Entry<T, Integer>> getSortedEntriesByValues(Map<T, Integer> map) {
		return getSortedEntriesByValues(map, false);
	}

	public static <T> List<Entry<T, Integer>> getSortedEntriesByValues(Map<T, Integer> map, final boolean descending) {
		List<Entry<T, Integer>> entryList = new ArrayList<>(map.entrySet());
		Collections.sort(entryList, new Comparator<Entry<T, Integer>>() {

			@Override
			public int compare(Entry<T, Integer> o1, Entry<T, Integer> o2) {
				int result = o1.getValue().compareTo(o2.getValue());
				if (descending) {
					result *= -1;
				}
				return result;
			}

		});

		return entryList;
	}

	public static <T, S extends Number> void printMap(Map<T, S> map) {
		for (Entry<T, S> entry : map.entrySet()) {
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}
	}

	public static <T, S, W> void printMapOfMapsAsTable(Map<T, Map<S, W>> map) {
		for (Entry<T, Map<S, W>> entry : map.entrySet()) {
			StringBuilder sb = new StringBuilder(entry.getKey() + ": ");
			for (Entry<S, W> intEntry : entry.getValue().entrySet()) {
				sb.append(intEntry.getValue() + ";");
			}
			System.out.println(sb.toString());
		}
	}
}
