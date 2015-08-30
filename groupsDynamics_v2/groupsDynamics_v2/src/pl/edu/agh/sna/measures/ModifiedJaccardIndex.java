package pl.edu.agh.sna.measures;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

public class ModifiedJaccardIndex {

	@SuppressWarnings("unchecked")
	public double calculate(Set<String> set1, Set<String> set2) {
		Set<String> intersection = new HashSet<>(CollectionUtils.intersection(set1, set2));

		if (set1.size() > 0 && set2.size() > 0) {
			return Math.max(1.0 * intersection.size() / set1.size(), 1.0 * intersection.size() / set2.size());
		} else {
			return 0;
		}
	}
}
