package pl.edu.agh.sna.model;

import java.util.Date;
import java.util.Map;

import org.joda.time.DateTime;

import pl.edu.agh.sna.util.DateUtil;

public class TimeSlot implements ISimpleTimeSlot {
	private Map<String, Group> groupMap;
	private int number;
	private Date startDate;
	private Date endDate;

	public TimeSlot() {
	}

	public TimeSlot(int number) {
		this.number = number;
	}

	public Map<String, Group> getGroupMap() {
		return groupMap;
	}

	public void setGroupMap(Map<String, Group> groupMap) {
		this.groupMap = groupMap;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public Date getStartDate() {
		return startDate;
	}

	public DateTime getStartDateAsDateTime() {
		return DateUtil.toJodaTime(startDate);
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public DateTime getEndDateAsDateTime() {
		return DateUtil.toJodaTime(endDate);
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public boolean isInRange(Date date) {
		return date.after(startDate) && date.before(endDate);
	}

	public String getSimpleStartDate() {
		return DateUtil.formatDateToDays(startDate);
	}

	public String getSimpleEndDate() {
		return DateUtil.formatDateToDays(endDate);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (startDate != null && endDate != null) {
			sb.append(number).append(":");
			sb.append("(");
			sb.append(DateUtil.formatDateToDays(startDate));
			sb.append("-");
			sb.append(DateUtil.formatDateToDays(endDate));
			sb.append(")");
		}
		return sb.toString();
	}

}
