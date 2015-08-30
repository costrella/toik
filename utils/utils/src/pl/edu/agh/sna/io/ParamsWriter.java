package pl.edu.agh.sna.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import pl.edu.agh.sna.util.StdParams;

import com.google.common.base.Joiner;
import com.google.common.collect.Multimap;

public class ParamsWriter {
	public static final String SPACE = " ";

	public static void writeMap(Map map, String path) throws IOException {
		writeMap(map, path, true);
	}

	public static <T> void writeMapList(List<Map<T, Integer>> list, T[] mapKeys, String path) throws IOException {
		List<String> lines = new ArrayList<>();
		lines.add("number " + Joiner.on(" ").join(mapKeys));

		int i = 0;
		for (Map<?, ?> map : list) {
			StringBuilder sb = new StringBuilder();
			sb.append(i);
			for (T key : mapKeys) {
				sb.append(" " + map.get(key));
			}
			lines.add(sb.toString());
			i++;
		}
		FileUtils.writeLines(new File(path), lines);
	}

	public static void writeMap(Map map, String path, boolean order) throws IOException {
		List keyList = new ArrayList<>(map.keySet());

		if (order) {
			Collections.sort(keyList);
		}

		List<String> lines = new ArrayList<>();
		for (Object key : keyList) {
			Object value = map.get(key);
			if (value instanceof Collection) {
				Collection values = (Collection) value;
				for (Object v : values) {
					lines.add(key + SPACE + v);
				}
			} else if (value instanceof StdParams) {
				StdParams p = (StdParams) value;
				lines.add(key + SPACE + p.getMeanAndStdDev()[0] + SPACE + p.getMeanAndStdDev()[1]);
			} else {
				lines.add(key + SPACE + value);
			}

		}

		FileUtils.writeLines(new File(path), lines);
	}

	public static void writeCollection(Collection<?> collection, String path) throws IOException {
		List<String> lines = new ArrayList<>();
		for (Object o : collection) {
			lines.add(o.toString());
		}
		FileUtils.writeLines(new File(path), lines);
	}

	public static void writeCollectionMap(Map map, String path, boolean order) throws IOException {
		List keyList = new ArrayList<>(map.keySet());

		if (order) {
			Collections.sort(keyList);
		}

		List<String> lines = new ArrayList<>();
		for (Object key : keyList) {
			Object value = map.get(key);
			if (value instanceof Collection) {
				StringBuilder sb = new StringBuilder();
				sb.append(key);
				Collection values = (Collection) value;
				for (Object v : values) {
					sb.append(SPACE + v);
				}
				lines.add(sb.toString());
			} else if (value instanceof Integer[]) {
				StringBuilder sb = new StringBuilder();
				sb.append(key);
				Integer[] values = (Integer[]) value;
				for (Object v : values) {
					sb.append(SPACE + v);
				}
				lines.add(sb.toString());
			} else {
				lines.add(key + SPACE + value);
			}

		}

		FileUtils.writeLines(new File(path), lines);
	}

	public static void writeMultimap(Multimap multimap, String path) throws IOException {
		writeMultimap(multimap, path, true);
	}

	public static void writeMultimap(Multimap multimap, String path, boolean order) throws IOException {
		List keyList = new ArrayList<>(multimap.keySet());
		if (order) {
			Collections.sort(keyList);
		}

		List<String> lines = new ArrayList<>();
		for (Object key : keyList) {
			Object value = multimap.get(key);
			if (value instanceof Collection) {
				Collection values = (Collection) value;
				for (Object v : values) {
					lines.add(key + SPACE + v);
				}
			} else {
				lines.add(key + SPACE + value);
			}

		}

		FileUtils.writeLines(new File(path), lines);
	}

}
