package pl.edu.agh.sna.measures;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

public class JaccardIndex {

	@SuppressWarnings("unchecked")
	public double calculate(Set<String> set1, Set<String> set2) {
		Set<String> intersection = new HashSet<>(CollectionUtils.intersection(set1, set2));
		Set<String> union = new HashSet<>(CollectionUtils.union(set1, set2));

		if (set1.size() > 0 && set2.size() > 0) {
			return 1.0 * intersection.size() / union.size();
		} else {
			return 0;
		}
	}
}
