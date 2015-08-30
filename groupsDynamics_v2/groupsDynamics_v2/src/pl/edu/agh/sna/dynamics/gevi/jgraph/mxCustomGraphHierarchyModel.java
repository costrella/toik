package pl.edu.agh.sna.dynamics.gevi.jgraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.hierarchical.model.mxGraphHierarchyEdge;
import com.mxgraph.layout.hierarchical.model.mxGraphHierarchyModel;
import com.mxgraph.layout.hierarchical.model.mxGraphHierarchyNode;
import com.mxgraph.layout.hierarchical.model.mxGraphHierarchyRank;

public class mxCustomGraphHierarchyModel extends mxGraphHierarchyModel {

	private List<Object> verticesList;
	private static Logger log = Logger.getLogger(mxCustomGraphHierarchyModel.class);

	public mxCustomGraphHierarchyModel(mxHierarchicalLayout layout, Object[] vertices, List<Object> roots, Object parent) {
		super(layout, vertices, roots, parent);
		verticesList = new ArrayList<>(Arrays.asList(vertices));
	}

	public void fixRanks() {
		int minLayer = Integer.MAX_VALUE;
		int maxLayer = -1;
		for (Object vertex : verticesList) {
			int layer = ((mxCustomCell) vertex).getLayer();
			if (layer < minLayer) {
				minLayer = layer;
			}
			if (layer > maxLayer) {
				maxLayer = layer;
			}
		}
		maxRank = maxLayer - minLayer;

		final mxGraphHierarchyRank[] rankList = new mxGraphHierarchyRank[maxRank + 1];
		ranks = new LinkedHashMap<Integer, mxGraphHierarchyRank>(maxRank + 1);

		for (int i = 0; i < maxRank + 1; i++) {
			rankList[i] = new mxGraphHierarchyRank();
			ranks.put(new Integer(i), rankList[i]);
		}

		// Perform a DFS to obtain an initial ordering for each rank.
		// Without doing this you would end up having to process
		// crossings for a standard tree.
		mxGraphHierarchyNode[] rootsArray = null;

		if (roots != null) {
			Object[] oldRootsArray = roots.toArray();
			rootsArray = new mxGraphHierarchyNode[oldRootsArray.length];

			for (int i = 0; i < oldRootsArray.length; i++) {
				Object node = oldRootsArray[i];
				mxGraphHierarchyNode internalNode = vertexMapper.get(node);
				rootsArray[i] = internalNode;
			}
		}

		int adjustValue = minLayer;

		for (Object vertex : verticesList) {
			mxCustomCell cell = (mxCustomCell) vertex;
			cell.setLayer(maxRank - cell.getLayer() + adjustValue);
		}

		visit(new mxGraphHierarchyModel.CellVisitor() {
			public void visit(mxGraphHierarchyNode parent, mxGraphHierarchyNode cell,
					mxGraphHierarchyEdge connectingEdge, int layer, int seen) {
				mxGraphHierarchyNode node = cell;

				if (seen == 0) {
					mxCustomCell modelCell = (mxCustomCell) node.cell;
					node.maxRank = modelCell.getLayer();
					node.minRank = node.maxRank;
					rankList[node.maxRank].add(cell);
					node.temp[0] = rankList[node.maxRank].size() - 1;
				}

			}
		}, rootsArray, false, null);
		log.debug("layout for hierarchy done");
	}

}
