package pl.edu.agh.sna.model;

import java.util.Date;

public interface ISimpleTimeSlot {
	Date getStartDate();

	Date getEndDate();

	boolean isInRange(Date date);
}
