package pl.edu.agh.sna.dynamics.gevi;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import pl.edu.agh.sna.dynamics.gevi.jgraph.mxCustomCell;
import pl.edu.agh.sna.dynamics.gevi.jgraph.mxCustomGraph;
import pl.edu.agh.sna.dynamics.gevi.jgraph.mxCustomGraphComponent;
import pl.edu.agh.sna.dynamics.gevi.jgraph.mxCustomHierarchicalLayout;
import pl.edu.agh.sna.measures.DiffSizeMeasure;
import pl.edu.agh.sna.measures.DiffSizeTimesMeasure;
import pl.edu.agh.sna.measures.FlowMeasure;
import pl.edu.agh.sna.measures.JaccardIndex;
import pl.edu.agh.sna.model.Group;
import pl.edu.agh.sna.model.GroupTransition;
import pl.edu.agh.sna.util.GroupTransitionsUtil;

import com.google.common.collect.Multimap;

@Component("gevi")
public class GEVi {
	private static final int DEFAULT_CELL_WIDTH = 80;
	private static final int DEFAULT_CELL_HEIGHT = 40;
	private static final int INITIAL_POSITION = 10;

	private static final String EDGE_STYLE = "elbow=horizontal;strokeWidth=3;strokeColor=#634E4E;labelBackgroundColor=#F5F5DC";
	private static final String DASHED_EDGE_STYLE = "elbow=horizontal;strokeWidth=3;strokeColor=#634E4E;labelBackgroundColor=#F5F5DC;dashed=1";

	private static Logger log = Logger.getLogger(GEVi.class);

	private double interRankCellSpacing = 70.0;
	private double intraCellSpacing = 30.0;
	private double interHierarchySpacing = 60.0;
	private double zoom = 0.85;
	private int cellWidth = DEFAULT_CELL_WIDTH;
	private int cellHeight = DEFAULT_CELL_HEIGHT;

	private String contextName;
	private String category;
	private double maxThresholdForContext = 0.5;
	private mxCustomGraphComponent gComp;
	private boolean firstStart = true;
	private JTabbedPane jtabbed;

	public void visualiseGroupEvolution(
			Multimap<Group, GroupTransition> groupTransitions,
			int thresholdStrongMatching) {
		mxCustomGraph g = buildGraph(groupTransitions, thresholdStrongMatching);
		Map<String, mxCustomCell> vertexNameMap = g.getVertexMap();
		log.debug("nodes in jgraph: " + vertexNameMap.size());

		assignAdditionalInfoForTransitions(vertexNameMap, groupTransitions);
		visualiseGraph(g);
	}

	private void visualiseGraph(mxCustomGraph g) {
		mxCustomHierarchicalLayout layout = new mxCustomHierarchicalLayout(g,
				SwingConstants.WEST);
		layout.setDisableEdgeStyle(false);
		layout.setInterRankCellSpacing(interRankCellSpacing);
		layout.setIntraCellSpacing(intraCellSpacing);
		layout.setInterHierarchySpacing(interHierarchySpacing);
		layout.execute(g.getDefaultParent());
		g.setAutoSizeCells(true);
		g.getModel().endUpdate();

		gComp = new mxCustomGraphComponent(g);
		gComp.zoom(zoom);
		if (contextName != null && category != null) {
			gComp.recolorConvergentVertices(contextName, category,
					maxThresholdForContext);
		}
		if (firstStart) {
			jtabbed = new JTabbedPane();
			JFrame frame = new JFrame();
			frame.setSize(800, 600);
			frame.setVisible(true);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			// frame.getContentPane().setLayout(new FlowLayout());
			// frame.getContentPane().add(createPanel());
			frame.getContentPane().add(jtabbed);
			firstStart = false;
		}
//		jtabbed.addTab("One", gComp);
		 jtabbed.addTab("One", null, gComp, "Tab 1");
	}

	private mxCustomGraph buildGraph(
			Multimap<Group, GroupTransition> groupTransitions,
			int ratioStrongMatching) {
		Set<String> vertexes = new HashSet<>();
		Set<Group> keys = groupTransitions.keySet();
		JaccardIndex jIdx = new JaccardIndex();
		DiffSizeTimesMeasure diffSizeTimesMeasure = new DiffSizeTimesMeasure();
		Map<String, Object> vertexNameMap = new HashMap<String, Object>();

		mxCustomGraph g = new mxCustomGraph();
		g.getModel().beginUpdate();

		Object parent = g.getDefaultParent();

		for (Group key : keys) {
			if (!vertexes.contains(key.getName())) {
				vertexes.add(key.getName());
				String groupName = key.getName();
				String label = key.getName() + " [" + key.getMembers().size()
						+ "]";
				Object o = g.insertLayerVertex(key.getSlotNo(), parent,
						groupName, label, INITIAL_POSITION, INITIAL_POSITION,
						cellWidth, cellHeight);
				vertexNameMap.put(key.getName(), o);
				((mxCustomCell) o).setGroupMembers(key.getMembers());
				((mxCustomCell) o).setContextMap(key.getContextMap());
			}
			Collection<GroupTransition> transitions = groupTransitions.get(key);

			for (GroupTransition transition : transitions) {
				String targetNode = transition.getTo().getName();
				if (!vertexes.contains(targetNode)) {
					vertexes.add(targetNode);
					String label = transition.getTo().getName() + " ["
							+ transition.getTo().getMembers().size() + "]";
					Object o = g.insertLayerVertex(transition.getTo()
							.getSlotNo(), parent, transition.getTo().getName(),
							label, INITIAL_POSITION, INITIAL_POSITION,
							cellWidth, cellHeight);
					vertexNameMap.put(targetNode, o);
					((mxCustomCell) o).setGroupMembers(transition.getTo()
							.getMembers());
					((mxCustomCell) o).setContextMap(transition.getTo()
							.getContextMap());
				}
				Object from = vertexNameMap.get(transition.getFrom().getName());
				Object to = vertexNameMap.get(targetNode);

				int intersectionSize = CollectionUtils.intersection(
						transition.getFrom().getMembers(),
						transition.getTo().getMembers()).size();
				String sValue = String.valueOf(intersectionSize);

				int times = diffSizeTimesMeasure.calculate(transition.getFrom()
						.getMembers(), transition.getTo().getMembers());

				double jaccard = jIdx.calculate(transition.getFrom()
						.getMembers(), transition.getTo().getMembers());
				Object e = null;
				if (times >= ratioStrongMatching) {
					e = g.insertLayerEdge(transition.getFrom().getSlotNo(),
							parent, null, sValue, from, to, DASHED_EDGE_STYLE);
				} else {
					e = g.insertLayerEdge(transition.getFrom().getSlotNo(),
							parent, null, sValue, from, to, EDGE_STYLE);
				}
				((mxCustomCell) e).setStability(jaccard);

			}
		}

		return g;
	}

	private void assignAdditionalInfoForTransitions(
			Map<String, mxCustomCell> vertexMap,
			Multimap<Group, GroupTransition> groupTransitions) {

		FlowMeasure flowMeasure = new FlowMeasure();
		JaccardIndex jaccard = new JaccardIndex();
		DiffSizeMeasure diffSizeM = new DiffSizeMeasure();
		Map<String, Group> groupMap = GroupTransitionsUtil
				.extractGroups(groupTransitions);

		for (Group group : groupMap.values()) {
			Collection<Group> succesors = GroupTransitionsUtil
					.getGroupSuccessors(group, groupTransitions);
			mxCustomCell currentGroupC = vertexMap.get(group.getName());

			// simple
			if (succesors.size() == 1) {
				Group nextGroup = succesors.iterator().next();
				double value = flowMeasure.calculate(group.getMembers(),
						nextGroup.getMembers());
				int leftDiff = diffSizeM.calculate(group.getMembers(),
						nextGroup.getMembers(), true);

				if (value <= 1 && value > -1 && value != 0) {
					currentGroupC.setOutgoingAdds();
					currentGroupC.setOutgoingValue(leftDiff);
				} else if (value == 0) {
					double jValue = jaccard.calculate(group.getMembers(),
							nextGroup.getMembers());
					if (jValue < 1 && jValue > 0) {
						currentGroupC.setOutgoingAdds();
						currentGroupC.setOutgoingValue(leftDiff);
					}
				}
			} else if (succesors.size() > 1) {// split
				Set<String> sumSplittedGroups = new HashSet<>();
				for (Group succ : succesors) {
					sumSplittedGroups.addAll(succ.getMembers());
				}
				double value = flowMeasure.calculate(group.getMembers(),
						sumSplittedGroups);
				int leftDiff = diffSizeM.calculate(group.getMembers(),
						sumSplittedGroups, true);
				if (value <= 1 && value > -1 && value != 0) {
					currentGroupC.setOutgoingAdds();
					currentGroupC.setOutgoingValue(leftDiff);
				} else {
					if (value == 0) {
						double jValue = jaccard.calculate(group.getMembers(),
								sumSplittedGroups);
						if (jValue < 1 && jValue > 0) {
							currentGroupC.setOutgoingAdds();
							currentGroupC.setOutgoingValue(leftDiff);
						}
					}
				}

			}

			Collection<Group> predecessors = GroupTransitionsUtil
					.getGroupPredecessors(group, groupTransitions);
			// simple
			if (predecessors.size() == 1) {
				Group prevGroup = predecessors.iterator().next();
				double value = flowMeasure.calculate(prevGroup.getMembers(),
						group.getMembers());
				int rightDiff = diffSizeM.calculate(prevGroup.getMembers(),
						group.getMembers(), false);

				if (value < 1 && value >= -1 && value != 0) {
					currentGroupC.setIncomingAdds();
					currentGroupC.setIncomingValue(rightDiff);
				} else if (value == 0) {
					double jValue = jaccard.calculate(prevGroup.getMembers(),
							group.getMembers());
					if (jValue < 1 && jValue > 0) {
						currentGroupC.setIncomingAdds();
						currentGroupC.setIncomingValue(rightDiff);
					}
				}
			} else if (predecessors.size() > 1) {// merges
				Set<String> sumMergedGroups = new HashSet<>();
				for (Group pred : predecessors) {
					sumMergedGroups.addAll(pred.getMembers());
				}
				double value = flowMeasure.calculate(sumMergedGroups,
						group.getMembers());
				int rightDiff = diffSizeM.calculate(sumMergedGroups,
						group.getMembers(), false);
				if (value < 1 && value >= -1 && value != 0) {
					currentGroupC.setIncomingAdds();
					currentGroupC.setIncomingValue(rightDiff);
				} else if (value == 0) {
					double jValue = jaccard.calculate(sumMergedGroups,
							group.getMembers());
					if (jValue < 1 && jValue > 0) {
						currentGroupC.setIncomingAdds();
						currentGroupC.setIncomingValue(rightDiff);
					}
				}

			}
		}

	}

	public double getInterRankCellSpacing() {
		return interRankCellSpacing;
	}

	public void setInterRankCellSpacing(double interRankCellSpacing) {
		this.interRankCellSpacing = interRankCellSpacing;
	}

	public double getIntraCellSpacing() {
		return intraCellSpacing;
	}

	public void setIntraCellSpacing(double intraCellSpacing) {
		this.intraCellSpacing = intraCellSpacing;
	}

	public double getInterHierarchySpacing() {
		return interHierarchySpacing;
	}

	public void setInterHierarchySpacing(double interHierarchySpacing) {
		this.interHierarchySpacing = interHierarchySpacing;
	}

	public double getZoom() {
		return zoom;
	}

	public void setZoom(double zoom) {
		this.zoom = zoom;
	}

	public int getCellWidth() {
		return cellWidth;
	}

	public void setCellWidth(int cellWidth) {
		this.cellWidth = cellWidth;
	}

	public int getCellHeight() {
		return cellHeight;
	}

	public void setCellHeight(int cellHeight) {
		this.cellHeight = cellHeight;
	}

	public String getContextName() {
		return contextName;
	}

	public void setContextName(String contextName) {
		this.contextName = contextName;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public double getMaxThresholdForContext() {
		return maxThresholdForContext;
	}

	public void setMaxThresholdForContext(double maxThresholdForContext) {
		this.maxThresholdForContext = maxThresholdForContext;
	}

	public void refreshAfterContextChanges() {
		if (contextName != null && category != null) {
			gComp.recolorConvergentVertices(contextName, category,
					maxThresholdForContext);
		}
	}

}
