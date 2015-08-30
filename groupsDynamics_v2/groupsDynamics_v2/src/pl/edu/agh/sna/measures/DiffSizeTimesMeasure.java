package pl.edu.agh.sna.measures;

import java.util.Set;

public class DiffSizeTimesMeasure {

	public int calculate(Set<String> set1, Set<String> set2) {
		int size1 = set1.size();
		int size2 = set2.size();

		if (size1 == 0 || size2 == 0) {
			return 0;
		} else if (size1 < size2) {
			return size2 / size1;
		} else {
			return size1 / size2;
		}
	}

}
