package pl.edu.agh.sna.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class BucketDouble {
	private double[] list;
	private int[] leThanList;
	private int rest;

	public BucketDouble(double[] list) {
		this.list = list;
		leThanList = new int[list.length];
	}

	public void add(double value, int count) {
		for (int i = 0; i < list.length; i++) {
			double refValue = list[i];
			if (value <= refValue) {
				leThanList[i] += count;
				return;
			}
		}
		rest++;
	}

	public Map<String, Integer> getMap() {
		Map<String, Integer> map = new LinkedHashMap<String, Integer>();
		if (list.length > 0) {
			map.put("<=" + list[0], leThanList[0]);

			double prev = list[0];
			for (int i = 1; i < list.length; i++) {
				map.put(prev + "-" + list[i], leThanList[i]);
				prev = list[i];
			}
			map.put(">" + prev, rest);
		}
		return map;
	}
}
