package pl.edu.agh.sna.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math.util.MathUtils;

public class StdParams {
	private List<Double> list = new ArrayList<>();
	private boolean sorted = false;

	public void add(Double value) {
		if (!value.isNaN()) {
			list.add(value);
			sorted = false;
		}
	}

	public List<Double> getValues() {
		return list;
	}

	private double calculateMean() {
		if (list.size() == 0) {
			return 0;
		}

		double sum = 0;
		for (double val : list) {
			sum += val;
		}

		return 1.0 * sum / list.size();
	}

	private double calculateStdDev() {
		if (list.size() == 0) {
			return 0;
		}
		double mean = calculateMean();

		double partSum = 0;
		for (double val : list) {
			double part = val - mean;
			part = part * part;
			partSum += part;
		}

		return Math.sqrt(1.0 * partSum / list.size());
	}

	public Double[] getMeanAndStdDev() {
		return new Double[] { calculateMean(), calculateStdDev() };
	}

	public Double getMean() {
		return calculateMean();
	}

	public double getMedian() {
		checkSort();
		// int halfIdx = list.size() / 2;
		// if (list.size() == 0) {
		// return 0;
		// } else if (list.size() % 2 == 1) {
		// return list.get(halfIdx);
		// } else {
		// return (list.get(halfIdx) + list.get(halfIdx - 1)) / 2.0;
		// }
		if (list.size() == 0) {
			return 0;
		}
		return getMedian(0, list.size());
	}

	private double getMedian(int start, int end) {
		final int size = end - start;

		if (size % 2 == 1) {
			return list.get(start + (size + 1) / 2 - 1);
		} else {
			return (list.get(start + size / 2 - 1) + list.get(start + size / 2)) / 2.0;
		}
	}

	public void print() {
		System.out.println(list.toString());
	}

	private void checkSort() {
		if (!sorted) {
			Collections.sort(list);
			sorted = true;
		}
	}

	public double get1Quartile() {
		checkSort();
		final int size = list.size();

		if (list.size() == 0) {
			return 0;
		}
		if (size % 2 == 1) {
			if (size > 1) {
				return getMedian(0, size / 2 + 1);
			} else {
				return getMedian(0, 1);
			}
		} else {
			return getMedian(0, size / 2);
		}
	}

	public double get3Quartile() {
		checkSort();
		final int size = list.size();
		if (list.size() == 0) {
			return 0;
		}
		if (size % 2 == 1) {
			if (size > 1) {
				return getMedian(size / 2, size);
			} else {
				return getMedian(0, 1);
			}
		} else {
			return getMedian(size / 2, size);
		}
	}

	public Map<String, Integer> makeHistogram(double step) {
		if (list.size() > 0) {
			double from;
			double to;
			if (step < 1) {
				from = MathUtils.round(list.get(0), 1, java.math.BigDecimal.ROUND_DOWN);
				to = MathUtils.round(list.get(list.size() - 1), 1, java.math.BigDecimal.ROUND_UP);
			} else {
				from = MathUtils.round(list.get(0), 0, java.math.BigDecimal.ROUND_DOWN);
				to = MathUtils.round(list.get(list.size() - 1), 0, java.math.BigDecimal.ROUND_UP);
			}

			double[] buckets = genBuckets(from, to, step);
			return BucketUtil.assignToBuckets(buckets, list.toArray(new Double[0]));
		}
		return null;
	}

	private double[] genBuckets(double minValue, double maxValue, double step) {
		List<Double> bucketList = new ArrayList<>();
		for (double d = minValue; d <= maxValue; d += step) {
			bucketList.add(MathUtils.round(d, 1));
		}
		return ArrayUtils.toPrimitive(bucketList.toArray(new Double[0]));
	}

	public double getInterquartile() {
		return get3Quartile() - get1Quartile();
	}

	public int countValues() {
		return list.size();
	}

	public double getMin() {
		checkSort();
		return list.size() > 0 ? list.get(0) : 0;
	}

	public double getMax() {
		checkSort();
		return list.size() > 0 ? list.get(list.size() - 1) : 0;
	}

	public void printStatistics() {
		System.out.println("mean, stdev: " + Arrays.toString(getMeanAndStdDev()));
		System.out.println("median: " + getMedian());
		System.out.println("1q: " + get1Quartile());
		System.out.println("3q: " + get3Quartile());
		System.out.println("IQ: " + getInterquartile());
	}

	public static void main(String[] args) {
		StdParams stdParams = new StdParams();
		stdParams.add(2.0);
		stdParams.add(5.0);
		stdParams.add(3.0);
		System.out.println("mean, stdev: " + Arrays.toString(stdParams.getMeanAndStdDev()));
		System.out.println("median: " + stdParams.getMedian());
		System.out.println("1q: " + stdParams.get1Quartile());
		System.out.println("3q: " + stdParams.get3Quartile());
		System.out.println("IQ: " + stdParams.getInterquartile());
		System.out.println("min: " + stdParams.getMin());
		System.out.println("max: " + stdParams.getMax());
		stdParams.print();
	}
}
