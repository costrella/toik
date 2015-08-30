package pl.edu.agh.sna.util;

import java.util.Map;

public class BucketUtil {
	public static Map<String, Integer> assignToBuckets(int[] buckets, Map<Integer, Integer> map) {
		BucketInt bucketInt = new BucketInt(buckets);
		for (Integer key : map.keySet()) {
			bucketInt.add(key, map.get(key));
		}
		return bucketInt.getMap();
	}

	public static Map<String, Integer> assignToBuckets(double[] buckets, Map<Double, Integer> map) {
		BucketDouble bucketDouble = new BucketDouble(buckets);
		for (Double key : map.keySet()) {
			bucketDouble.add(key, map.get(key));
		}
		return bucketDouble.getMap();
	}

	public static Map<String, Integer> assignToBuckets(double[] buckets, Double[] values) {
		BucketDouble bucketDouble = new BucketDouble(buckets);
		for (Double value : values) {
			bucketDouble.add(value, 1);
		}
		return bucketDouble.getMap();
	}
}
