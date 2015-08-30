package pl.edu.agh.sna.model;

public class Edge {
	private String from;
	private String to;
	private double weight;

	public Edge(String from, String to) {
		this(from, to, 1.0);
	}

	public Edge(String from, String to, double weight) {
		this.from = from;
		this.to = to;
		this.weight = weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getWeight() {
		return weight;
	}

	public String getFrom() {
		return from;
	}

	public String getTo() {
		return to;
	}

}
