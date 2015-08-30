package pl.edu.agh.sna.dynamics.gevi.jgraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.view.mxGraph;

public class mxCustomHierarchicalLayout extends mxHierarchicalLayout {
	private List<Object> predefinedRoots;

	public mxCustomHierarchicalLayout(mxGraph graph) {
		super(graph);
		// layoutFromSinks = false;
	}

	public mxCustomHierarchicalLayout(mxGraph graph, int orientation) {
		super(graph, orientation);
		// layoutFromSinks = false;
	}

	public void setPredefinedRoots(List<Object> predefinedRoots) {
		this.predefinedRoots = predefinedRoots;
	}

	@Override
	public void execute(Object parent, List<Object> roots) {
		if (roots == null) {
			roots = graph.findTreeRoots(parent);
			if (predefinedRoots != null)
				roots.addAll(predefinedRoots);
		}

		this.roots = roots;
		mxIGraphModel model = graph.getModel();

		model.beginUpdate();
		try {
			run(parent);

			if (isResizeParent() && !graph.isCellCollapsed(parent)) {
				graph.updateGroupBounds(new Object[] { parent }, getParentBorder(), isMoveParent());
			}
		} finally {
			model.endUpdate();
		}
	}

	/**
	 * The API method used to exercise the layout upon the graph description and
	 * produce a separate description of the vertex position and edge routing
	 * changes made.
	 */
	public void run(Object parent) {
		// Separate out unconnected hierarchies
		List<Set<Object>> hierarchyVertices = new ArrayList<Set<Object>>();

		// Keep track of one root in each hierarchy in case it's fixed position
		// List<Object> fixedRoots = null;
		// List<Point2D> rootLocations = null;
		List<Set<Object>> affectedEdges = null;

		// if (fixRoots) {
		// fixedRoots = new ArrayList<Object>();
		// rootLocations = new ArrayList<Point2D>();
		// }
		affectedEdges = new ArrayList<Set<Object>>();

		for (int i = 0; i < roots.size(); i++) {
			// First check if this root appears in any of the previous vertex
			// sets
			boolean newHierarchy = true;
			Iterator<Set<Object>> iter = hierarchyVertices.iterator();

			while (newHierarchy && iter.hasNext()) {
				if (iter.next().contains(roots.get(i))) {
					newHierarchy = false;
				}
			}

			if (newHierarchy) {
				// Obtains set of vertices connected to this root
				Stack<Object> cellsStack = new Stack<Object>();
				cellsStack.push(roots.get(i));
				Set<Object> edgeSet = null;

				// if (fixRoots) {
				// fixedRoots.add(roots.get(i));
				// Point2D location = getVertexBounds(roots.get(i)).getPoint();
				// rootLocations.add(location);
				// }
				edgeSet = new HashSet<Object>();

				Set<Object> vertexSet = new HashSet<Object>();

				while (!cellsStack.isEmpty()) {
					Object cell = cellsStack.pop();

					if (!vertexSet.contains(cell)) {
						vertexSet.add(cell);

						// if (fixRoots) {
						// edgeSet.addAll(Arrays.asList(graph.getIncomingEdges(cell,
						// parent)));
						// }

						Object[] conns = graph.getConnections(cell, parent);
						Object[] cells = graph.getOpposites(conns, cell);

						for (int j = 0; j < cells.length; j++) {
							if (!vertexSet.contains(cells[j])) {
								cellsStack.push(cells[j]);
							}
						}
					}
				}

				hierarchyVertices.add(vertexSet);
				affectedEdges.add(edgeSet);

				// if (fixRoots) {
				// affectedEdges.add(edgeSet);
				// }
			}
		}

		// Perform a layout for each seperate hierarchy
		// Track initial coordinate x-positioning
		double initialX = 0;
		Iterator<Set<Object>> iter = hierarchyVertices.iterator();
		int i = 0;

		while (iter.hasNext()) {
			Set<Object> vertexSet = iter.next();

			model = new mxCustomGraphHierarchyModel(this, vertexSet.toArray(), roots, parent);

			cycleStage(parent);
			layeringStage();
			crossingStage(parent);
			initialX = placementStage(initialX, parent);

			int diffX = 0;
			int diffY = 0;
			graph.moveCells(vertexSet.toArray(), diffX, diffY);
			Set<Object> connectedEdges = affectedEdges.get(i);
			graph.moveCells(connectedEdges.toArray(), diffX, diffY);

			i++;
		}
	}

	@Override
	public void layeringStage() {
		model.fixRanks();
	}

}
