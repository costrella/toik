package pl.edu.agh.sna.dynamics.gevi.jgraph;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Collection;
import java.util.List;

import javax.swing.BoundedRangeModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.ToolTipManager;

import org.apache.commons.collections.CollectionUtils;

import pl.edu.agh.sna.model.TimeSlot;
import pl.edu.agh.sna.util.ColorsUtil;

import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.view.mxInteractiveCanvas;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxGraphView;

public class mxCustomGraphComponent extends mxGraphComponent {
	private boolean compareGroupMembers = false;

	private static final long serialVersionUID = -7498805989626086160L;

	public mxGraphModel getMxGraph() {
		return (mxGraphModel) getGraph().getModel();
	}

	public mxCustomGraphComponent(mxGraph graph, final List<TimeSlot> timeslots) {
		super(graph);
		setToolTips(true);
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
		setConnectable(false);
		graph.setCellsDisconnectable(false);
		graph.setCellsEditable(false);
		graph.setCellsDeletable(false);
		graph.setCellsBendable(false);
		graph.setCellsMovable(false);
		graph.setCellsResizable(false);
		graph.setCellsCloneable(false);
		graph.setVertexLabelsMovable(false);
		setPanning(true);

		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyChar() == 'f') {
					showDialog();
				}
				if (e.getKeyChar() == 't') {
					showTimeSlotInfo(timeslots);
				}
			}
		});

		this.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				Rectangle r = getVisibleRect();
				r.x = e.getX();
				r.y = e.getY();
				mouseWheelMovedM(e);
			}
		});

		getGraphControl().addMouseListener(new MouseAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void mousePressed(MouseEvent e) {
				mxCustomCell cell = (mxCustomCell) getCellAt(e.getX(), e.getY());
				if (cell != null && cell.isVertex()) {
					if (compareGroupMembers) {
						cleanAllVerticesStyle();
					}
					compareGroupMembers = true;
					cell.setStyle("fillColor=FF8157;strokeColor=turqoise;strokeWidth=3");
					Object[] vertices = getGraph().getChildVertices(
							getGraph().getDefaultParent());

					for (Object vertexObject : vertices) {
						mxCustomCell vertex = (mxCustomCell) vertexObject;
						if (vertex != cell) {
							Collection<String> commonElements = CollectionUtils
									.intersection(cell.getGroupMembers(),
											vertex.getGroupMembers());
							if (commonElements.size() > 0) {
								vertex.setStyle("fillColor=FFFFF");
								vertex.setCommonElements(commonElements);
							}
						}
					}

					getGraph().refresh();
				} else if (compareGroupMembers) {
					compareGroupMembers = false;
					cleanAllVerticesStyle();
				}

			}
		});
	}

	private void showDialog() {
		String value = JOptionPane.showInputDialog(new JFrame(),
				"Enter node name:", "Find node");
		if (value != null) {
			Object cell = ((mxGraphModel) getGraph().getModel()).getCell(value);
			if (cell != null) {
				scrollCellToVisible(cell, true);
				selectCellForEvent(cell, null);
			}
		}
	}

	private void showTimeSlotInfo(List<TimeSlot> timeslots) {
		String info = "<html><body><b>TIME SLOT INFO:</b><br>";
		for (TimeSlot time : timeslots) {
			System.out.println("#"+time.getNumber()+", from: "+time.getStartDate().toString() + "; to:"
					+ time.getEndDate());
			info +="#"+time.getNumber()+", from: "+time.getStartDate().toString() + "; to:"
					+ time.getEndDate()+"<br>";
			
		}
		JOptionPane.showMessageDialog(new JFrame(),
				info);
	}

	private void cleanAllVerticesStyle() {
		Object[] vertices = getGraph().getChildVertices(
				getGraph().getDefaultParent());
		for (Object vertexObject : vertices) {
			mxCustomCell vertex = (mxCustomCell) vertexObject;
			vertex.setStyle("none");
			vertex.setCommonElements(null);
		}
		getGraph().refresh();
	}

	protected void mouseWheelMovedM(MouseWheelEvent e) {
		Rectangle r = getBounds();
		double partX = 1.0 * e.getX() / r.width;
		double partY = 1.0 * e.getY() / r.height;
		Point centerTranslate = new Point((int) r.getCenterX() - e.getX(),
				(int) r.getCenterY() - e.getY());

		double localZoomFactor = 0;

		if (e.getWheelRotation() < 0) {
			localZoomFactor = zoomFactor;
		} else {
			localZoomFactor = 1 / zoomFactor;
		}
		zoom(localZoomFactor, partX, partY, centerTranslate);

	}

	public void zoom(double factor, double partX, double partY,
			Point centerTranslate) {
		mxGraphView view = graph.getView();
		double newScale = (double) ((int) (view.getScale() * 100 * factor)) / 100;

		if (newScale != view.getScale() && newScale > 0.04) {
			mxPoint translate = (pageVisible && centerPage) ? getPageTranslate(newScale)
					: new mxPoint();
			graph.getView().scaleAndTranslate(newScale, translate.getX(),
					translate.getY());

			if (keepSelectionVisibleOnZoom && !graph.isSelectionEmpty()) {
				getGraphControl().scrollRectToVisible(
						view.getBoundingBox(graph.getSelectionCells())
								.getRectangle());
			} else {
				maintainScrollBar(true, factor, partX);
				maintainScrollBar(false, factor, partY);
			}
		}

	}

	protected void maintainScrollBar(boolean horizontal, double factor,
			double part) {
		JScrollBar scrollBar = (horizontal) ? getHorizontalScrollBar()
				: getVerticalScrollBar();

		if (scrollBar != null) {
			BoundedRangeModel model = scrollBar.getModel();

			int newValue = (int) Math.round(model.getValue() * factor);
			if (factor > 1) {
				newValue += (int) Math.round(model.getExtent() * (factor - 1)
						* part);
			} else {
				newValue += (int) Math.round(model.getExtent() * (factor - 1)
						* part);
			}
			if (newValue > model.getMaximum()) {

				model.setMaximum((int) Math.round(model.getMaximum() * factor) + 20);

				newValue = model.getMaximum() - 15;
				model.setValue(model.getMaximum() - model.getExtent());
			} else
				model.setValue(newValue);
		}
	}

	@Override
	public mxInteractiveCanvas createCanvas() {
		return new mxCustomInteractiveCanvas();
	}

	@Override
	public boolean isPanningEvent(MouseEvent event) {
		return true;
	}

	public void recolorConvergentVertices(String contextName, String category,
			double maxThresholdForContext) {
		cleanAllVerticesStyle();
		Object[] vertices = getGraph().getChildVertices(
				getGraph().getDefaultParent());
		for (Object vertexObject : vertices) {
			mxCustomCell vertex = (mxCustomCell) vertexObject;
			Double value = vertex.getContext(contextName).get(category);
			if (value == null) {
				value = 0d;
			}
			if (value != null) {
				double maxThreshold = 0.5;
				if (category.equals("density")) {
					maxThreshold = 1.0;
				} else if (contextName.equals("topics")) {
					maxThreshold = 0.3;
				}
				String color = ColorsUtil.getColorSaturation(value,
						maxThreshold);
				color += ";fontSize=15";
				vertex.setStyle(color);
			}
		}
		getGraph().refresh();
	}

}
