package pl.edu.agh.sna.io;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

public class ResourcesReader {
	private static BufferedImage bottomRightArrowImg;
	private static String bottomRightArrowImgPath = "resources/arrow_bottom_right.png";
	private static BufferedImage topRightArrowImg;
	private static String topRightArrowImgPath = "resources/arrow_top_right.png";
	public static int ARROW_SIZE = 48;

	private static Logger log = Logger.getLogger(ResourcesReader.class);

	static {
		bottomRightArrowImg = readResource(bottomRightArrowImgPath);
		topRightArrowImg = readResource(topRightArrowImgPath);
	}

	public static BufferedImage getBottomRightArrowImg() {
		return bottomRightArrowImg;
	}

	public static BufferedImage getTopRightArrowImg() {
		return topRightArrowImg;
	}

	private static BufferedImage readResource(String path) {
		try {
			return ImageIO.read(new File(ResourcesReader.class.getClassLoader().getResource(path).getFile()));
		} catch (Exception e) {
			log.error("Cannot read image from file", e);
			return null;
		}
	}

}
