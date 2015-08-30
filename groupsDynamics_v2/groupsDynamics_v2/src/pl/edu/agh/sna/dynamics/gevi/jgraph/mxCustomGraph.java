package pl.edu.agh.sna.dynamics.gevi.jgraph;

import java.util.HashMap;
import java.util.Map;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.view.mxGraph;

public class mxCustomGraph extends mxGraph {
	private Map<String, mxCustomCell> vertexMap = new HashMap<>();

	public Object createVertex(Object parent, String id, Object value, double x, double y, double width, double height,
			String style, boolean relative) {
		mxGeometry geometry = new mxGeometry(x, y, width, height);
		geometry.setRelative(relative);

		mxCustomCell vertex = new mxCustomCell(value, geometry, style);
		vertex.setId(id);
		vertex.setVertex(true);
		vertex.setConnectable(true);
		vertexMap.put(id, vertex);

		return vertex;
	}

	public Object createVertex(int layer, String id, Object value, double x, double y, double width, double height) {
		mxGeometry geometry = new mxGeometry(x, y, width, height);
		geometry.setRelative(false);

		mxCustomCell vertex = new mxCustomCell(value, geometry, null);
		vertex.setId(id);
		vertex.setVertex(true);
		vertex.setConnectable(true);
		vertex.setLayer(layer);
		vertexMap.put(id, vertex);

		return vertex;
	}

	public Object insertLayerVertex(int layer, Object parent, String id, Object value, double x, double y,
			double width, double height) {
		Object vertex = createVertex(layer, id, value, x, y, width, height);

		return addCell(vertex, parent);
	}

	@Override
	public Object createEdge(Object parent, String id, Object value, Object source, Object target, String style) {
		mxCustomCell edge = new mxCustomCell(value, new mxGeometry(), style);

		edge.setId(id);
		edge.setEdge(true);
		edge.getGeometry().setRelative(true);

		return edge;
	}

	public Object createEdge(int layer, Object parent, String id, Object value, Object source, Object target,
			String style) {
		mxCustomCell edge = new mxCustomCell(value, new mxGeometry(), style);

		edge.setId(id);
		edge.setEdge(true);
		edge.getGeometry().setRelative(true);
		edge.setLayer(layer);

		return edge;
	}

	public Object insertLayerEdge(int layer, Object parent, String id, Object value, Object source, Object target,
			String style) {
		Object edge = createEdge(layer, parent, id, value, source, target, style);

		return addEdge(edge, parent, source, target, null);
	}

	@Override
	public String convertValueToString(Object cell) {
		return cell.toString();
	}

	@Override
	public String getToolTipForCell(Object cell) {
		return ((mxCustomCell) cell).toDisplayAsTooltip();
	}

	public Map<String, mxCustomCell> getVertexMap() {
		return vertexMap;
	}

}
