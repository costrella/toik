package pl.edu.agh.sna.measures;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

public class FlowMeasure {

	@SuppressWarnings("unchecked")
	public double calculate(Set<String> set1, Set<String> set2) {
		Set<String> intersection = new HashSet<>(CollectionUtils.intersection(set1, set2));
		double first = set1.size() > 0 ? 1.0 * intersection.size() / set1.size() : 0;
		double second = set2.size() > 0 ? 1.0 * intersection.size() / set2.size() : 0;

		if (first > second) {
			return -first;
		} else if (first < second) {
			return second;
		} else {
			return 0;
		}
	}

}
