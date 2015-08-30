package pl.edu.agh.sna.dynamics.gevi.jgraph;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;

public class mxCustomCell extends mxCell {

	private static final long serialVersionUID = 148486757061595573L;

	private boolean outgoingAdds;
	private boolean incomingAdds;
	private int incomingValue;
	private int outgoingValue;
	private int layer;
	private Set<String> groupMembers;
	private Collection<String> commonElements;
	private double stability;
	private Map<String, Map<String, Double>> contextMap;

	public mxCustomCell() {
		super();
	}

	public mxCustomCell(Object value, mxGeometry geometry, String style) {
		super(value, geometry, style);
	}

	public mxCustomCell(Object value) {
		super(value);
	}

	public boolean isOutgoingAdds() {
		return outgoingAdds;
	}

	public boolean isIncomingAdds() {
		return incomingAdds;
	}

	public int getIncomingValue() {
		return incomingValue;
	}

	public void setIncomingValue(int incomingValue) {
		this.incomingValue = incomingValue;
	}

	public int getOutgoingValue() {
		return outgoingValue;
	}

	public void setOutgoingValue(int outgoingValue) {
		this.outgoingValue = outgoingValue;
	}

	public void setOutgoingAdds() {
		this.outgoingAdds = true;
	}

	public void setIncomingAdds() {
		this.incomingAdds = true;
	}

	public int getLayer() {
		return layer;
	}

	public void setLayer(int layer) {
		this.layer = layer;
	}

	public Set<String> getGroupMembers() {
		return groupMembers;
	}

	public void setGroupMembers(Set<String> groupMembers) {
		this.groupMembers = groupMembers;
	}

	public Collection<String> getCommonElements() {
		return commonElements;
	}

	public void setCommonElements(Collection<String> commonElements) {
		this.commonElements = commonElements;
	}

	public double getStability() {
		return stability;
	}

	public void setStability(double stability) {
		this.stability = stability;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(value);
		if (isVertex()) {
			if (commonElements != null && commonElements.size() > 0) {
				sb.append("\n<" + commonElements.size() + ">");
			}
		}
		return sb.toString();
	}

	public String toDisplayAsTooltip() {
		StringBuilder sb = new StringBuilder("<html>");
		if (isVertex()) {
			sb.append(value);
			if (commonElements != null && commonElements.size() > 0) {
				sb.append("<br><b>common members:</b>");
				for (String element : commonElements) {
					sb.append("<br>" + element);
				}
			} else if (groupMembers != null) {
				int i = 0;
				sb.append("<br><b>members:</b>");
				for (String element : groupMembers) {
					if (i == 20) {
						sb.append("<br>...");
						break;
					}
					sb.append("<br>" + element);
					i++;
				}
			}
			for (String context : contextMap.keySet()) {
				sb.append("<br><b>" + context + ":</b>");
				for (String category : getContext(context).keySet()) {
					sb.append("<br>" + category + ": " + getContext(context).get(category));
				}
			}
		} else if (isEdge()) {
			sb.append("<b>members</b>: " + value);
			sb.append("<br><b>stability</b>: " + new DecimalFormat("#.##").format(stability));
		}
		sb.append("</html>");
		return sb.toString();
	}

	public Map<String, Double> getContext(String contextName) {
		if (contextMap.containsKey(contextName)) {
			return contextMap.get(contextName);
		} else {
			return new HashMap<String, Double>();
		}
	}

	public void setContextMap(Map<String, Map<String, Double>> contextMap) {
		this.contextMap = contextMap;
	}

}
