package pl.edu.agh.sna.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class BucketInt {
	private int[] list;
	private int[] leThanList;
	private int rest;

	public BucketInt(int[] list) {
		this.list = list;
		leThanList = new int[list.length];
	}

	public void add(int value, int count) {
		for (int i = 0; i < list.length; i++) {
			int refValue = list[i];
			if (value <= refValue) {
				leThanList[i] += count;
				return;
			}
		}
		rest += count;
	}

	public Map<String, Integer> getMap() {
		Map<String, Integer> map = new LinkedHashMap<String, Integer>();
		if (list.length > 0) {
			map.put("<=" + list[0], leThanList[0]);

			int prev = list[0];
			for (int i = 1; i < list.length; i++) {
				map.put(prev + "-" + list[i], leThanList[i]);
				prev = list[i];
			}
			map.put(">" + prev, rest);
		}
		return map;
	}
}
