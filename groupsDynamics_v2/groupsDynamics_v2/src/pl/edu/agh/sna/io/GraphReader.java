package pl.edu.agh.sna.io;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import pl.edu.agh.sna.model.DirectedWeightedGraph;

public class GraphReader {

	public DirectedWeightedGraph readGraph(String filePath) throws IOException {
		DirectedWeightedGraph graph = new DirectedWeightedGraph();

		List<String> lines = FileUtils.readLines(new File(filePath));
		for (String line : lines) {
			if (line.length() == 0) {
				continue;
			}
			if (line.equals("Source Target Weight")) {
				continue;
			}
			String[] parts = line.split(" ");
			if (parts.length == 3) {
				graph.addEdge(parts[0], parts[1], Double.valueOf(parts[2]));
			} else {
				throw new IllegalArgumentException("Not supported infput format for graph");
			}

		}

		return graph;
	}

}
