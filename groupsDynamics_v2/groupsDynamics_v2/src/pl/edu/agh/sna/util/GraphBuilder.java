package pl.edu.agh.sna.util;

import java.io.IOException;
import java.util.List;

import pl.edu.agh.sna.model.BasicComment;
import pl.edu.agh.sna.model.DirectedWeightedGraph;

public class GraphBuilder {

	public static DirectedWeightedGraph buildGraphInCommentsModel(List<BasicComment> commentList) throws IOException {

		DirectedWeightedGraph graph = new DirectedWeightedGraph();

		for (BasicComment comment : commentList) {

			if (comment.getParentCommentId() != null) {
				int parentCommentAuthorId = comment.getParentCommentAuthorId();
				graph.addEdge(comment.getAuthorId().toString(), String.valueOf(parentCommentAuthorId));
			} else {
				graph.addEdge(comment.getAuthorId().toString(), comment.getPostAuthorId().toString());
			}
		}

		return graph;
	}

}
