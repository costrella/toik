package pl.edu.agh.sna.model;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class DirectedWeightedGraph {
	private Map<String, Edge> edgeMap = new HashMap<String, Edge>();

	public void buildGraphBasedOnEdgeMap(Map<String, Edge> edgeMap) {
		this.edgeMap = edgeMap;
	}

	public void addEdge(String from, String to) {
		String tmp = from + "-" + to;

		if (edgeMap.containsKey(tmp)) {
			Edge edge = edgeMap.get(tmp);
			edge.setWeight(edge.getWeight() + 1);
		} else {
			edgeMap.put(tmp, new Edge(from, to));
		}
	}

	public int getEdgeSize() {
		return edgeMap.size();
	}

	public int getNodeSize() {
		return getNodes().size();
	}

	public Set<String> getNodes() {
		Set<String> nodes = new TreeSet<>();
		for (Edge edge : edgeMap.values()) {
			nodes.add(edge.getFrom());
			nodes.add(edge.getTo());
		}

		return nodes;
	}

	public Map<String, Edge> filterGraphToVertices(Set<String> vertices) {
		Map<String, Edge> subgraphMap = new HashMap<>();

		for (Edge edge : edgeMap.values()) {
			if (vertices.contains(edge.getFrom()) && vertices.contains(edge.getTo())) {
				subgraphMap.put(edge.getFrom() + "-" + edge.getTo(), edge);
			}
		}
		return subgraphMap;
	}

	public DirectedWeightedGraph filterGraphToVerticesAsNewGraph(Set<String> vertices) {
		Map<String, Edge> edgeMap = filterGraphToVertices(vertices);
		DirectedWeightedGraph g = new DirectedWeightedGraph();
		g.buildGraphBasedOnEdgeMap(edgeMap);
		return g;
	}

	public void addEdge(int from, int to, double weight) {
		String tmp = from + "-" + to;
		edgeMap.put(tmp, new Edge(String.valueOf(from), String.valueOf(to), weight));
	}

	public void addEdge(String from, String to, double weight) {
		String tmp = from + "-" + to;
		edgeMap.put(tmp, new Edge(from, to, weight));
	}

	public Collection<Edge> getEdges() {
		return edgeMap.values();
	}

	public Collection<Edge> getEdgesNoLoops() {
		Collection<Edge> edges = edgeMap.values();
		List<Edge> edgesNoLoops = new ArrayList<>();
		for (Edge edge : edges) {
			if (!edge.getFrom().equals(edge.getTo())) {
				edgesNoLoops.add(edge);
			}
		}
		return edgesNoLoops;
	}

	public double getDensity() {
		int nodes = getNodeSize();
		int edges = getEdgesNoLoops().size();

		double density = 1.0 * edges / (nodes * (nodes - 1));
		return density;
	}

	public void addEdges(Collection<Edge> edges) {
		for (Edge edge : edges) {
			addEdge(edge.getFrom(), edge.getTo(), edge.getWeight());
		}
	}

	public void saveGraph(String path) throws IOException {
		FileWriter writer = new FileWriter(path);
		PrintWriter out = new PrintWriter(writer);
		for (Edge edge : edgeMap.values()) {
			out.println(edge.getFrom() + " " + edge.getTo() + " " + edge.getWeight());
		}

		out.close();
	}

	public Set<String> getEdgesAsStrings() {
		return edgeMap.keySet();
	}

	public int getEdgeSizeWithWeighs() {
		int no = 0;
		for (Edge edge : edgeMap.values()) {
			no += edge.getWeight();
		}

		return no;
	}

	public void removeEdgeBelowThreshold(double threshold) {
		Set<String> toRemoveSet = new HashSet<>();
		for (String key : edgeMap.keySet()) {
			Edge edge = edgeMap.get(key);
			if (edge.getWeight() < threshold) {
				toRemoveSet.add(key);
			}
		}

		for (String toRemove : toRemoveSet) {
			edgeMap.remove(toRemove);
		}
	}

}
