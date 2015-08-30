package pl.edu.agh.sna.model;

import pl.edu.agh.sna.dynamics.GroupEvents;

public class GroupTransition {
	private Group from;
	private Group to;
	private double measure;
	private GroupEvents eventType;

	public GroupTransition(Group from, Group to, double measure) {
		this.from = from;
		this.to = to;
		this.measure = measure;
	}

	public Group getFrom() {
		return from;
	}

	public void setFrom(Group from) {
		this.from = from;
	}

	public Group getTo() {
		return to;
	}

	public void setTo(Group to) {
		this.to = to;
	}

	public double getMeasure() {
		return measure;
	}

	public void setMeasure(double measure) {
		this.measure = measure;
	}

	public GroupEvents getEventType() {
		return eventType;
	}

	public void setEventType(GroupEvents eventType) {
		this.eventType = eventType;
	}

	@Override
	public String toString() {
		return getFrom().getName() + " -> " + getTo().getName() + "[" + getMeasure() + "]";
	}

}
