package pl.edu.agh.sna.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;

public class DateUtil {
	private static SimpleDateFormat dateFormatDMYHM = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	private static SimpleDateFormat dateFormatDMY = new SimpleDateFormat("dd.MM.yyyy");

	public static String formatDate(Date date) {
		return dateFormatDMYHM.format(date);
	}

	public static String formatDateToDays(Date date) {
		return dateFormatDMY.format(date);
	}

	public static DateTime toJodaTime(Date date) {
		return new DateTime(date.getTime());
	}
}
