package pl.edu.agh.sna.util;

import java.awt.Color;

public class ColorsUtil {
	private static final Color initialColor = new Color(0, 255, 0);

	private static int[][] jetAlikePalette = new int[][] {
			// { 0, 0, 155 },
			// { 0, 0, 169 }, { 0, 0, 183 }, { 0, 0, 198 }, { 0, 0, 212 },
			// { 0, 0, 226 },
			// { 0, 0, 240 },
			{ 0, 0, 255 },// { 0, 14, 255 },
			// { 0, 28, 255 },// { 0, 42, 255 },
			// { 0, 56, 255 },// { 0, 70, 255 },
			// { 0, 84, 255 }, //{ 0, 98, 255 },
			{ 0, 112, 255 }, // { 0, 127, 255 },
			// { 0, 141, 255 }, //{ 0, 155, 255 },
			{ 0, 169, 255 },// { 0, 183, 255 },
			// { 0, 198, 255 },// { 0, 212, 255 },
			{ 0, 226, 255 }, // { 0, 240, 255 },
			// { 0, 255, 255 }, { 14, 255, 240 }, { 28, 255, 226 },
			// { 42, 255, 212 }, { 56, 255, 198 }, //{ 70, 255, 183 },
			// { 84, 255, 169 }, { 98, 255, 155 }, { 112, 255, 141 },
			// { 127, 255, 127 }, { 141, 255, 112 }, { 155, 255, 98 },
			// { 169, 255, 84 }, { 183, 255, 70 }, { 198, 255, 56 },
			// { 212, 255, 42 }, { 226, 255, 28 },
			// { 240, 255, 14 },
			{ 255, 255, 0 }, // { 255, 240, 0 },
			{ 255, 226, 0 }, // { 255, 212, 0 },
			{ 255, 198, 0 }, // { 255, 183, 0 },
			{ 255, 169, 0 }, // { 255, 155, 0 },
			{ 255, 141, 0 }, // { 255, 127, 0 },
			{ 255, 112, 0 }, // { 255, 98, 0 },
			{ 255, 84, 0 }, // { 255, 70, 0 },
			{ 255, 56, 0 }, // { 255, 42, 0 },
			{ 255, 28, 0 }, // { 255, 14, 0 },
			{ 255, 0, 0 }, // { 240, 0, 0 },
			{ 226, 0, 0 } // ,{ 212, 0, 0 }
	};

	public static String getColorSaturation(double value, double maxThreshold) {
		int partition = (int) Math.round(value * jetAlikePalette.length / maxThreshold);
		if (partition >= jetAlikePalette.length) {
			partition = jetAlikePalette.length - 1;
		}
		int[] colorParts = jetAlikePalette[partition];
		Color color = new Color(colorParts[0], colorParts[1], colorParts[2]);
		String rgb = Integer.toHexString(color.getRGB());
		rgb = rgb.substring(2, rgb.length());
		return "fillColor=" + rgb;
	}

}
