package pl.edu.agh.sna.measures;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

public class DiffSizeMeasure {

	@SuppressWarnings("unchecked")
	public int calculate(Set<String> set1, Set<String> set2, boolean left) {
		Set<String> intersection = new HashSet<>(CollectionUtils.intersection(set1, set2));

		if (set1.size() > 0 && set2.size() > 0) {
			if (left) {
				return set1.size() - intersection.size();
			} else {
				return set2.size() - intersection.size();
			}
		} else {
			return 0;
		}
	}
}
