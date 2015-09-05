package pl.edu.agh.sna.dynamics.gevi.jgraph;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Map;

import pl.edu.agh.sna.io.ResourcesReader;

import com.mxgraph.shape.mxIShape;
import com.mxgraph.swing.view.mxInteractiveCanvas;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxCellState;

public class mxCustomInteractiveCanvas extends mxInteractiveCanvas {

	@Override
	public Object drawCell(mxCellState state) {
		Map<String, Object> style = state.getStyle();
		style.put(mxConstants.STYLE_FONTSIZE, 15);
		mxIShape shape = getShape(style);
		mxCustomCell cell = (mxCustomCell) state.getCell();
		if (g != null && shape != null) {
			// Creates a temporary graphics instance for drawing this shape
			float opacity = mxUtils.getFloat(style, mxConstants.STYLE_OPACITY, 100);
			Graphics2D previousGraphics = g;
			g = createTemporaryGraphics(style, opacity, state);

			// Paints the shape and restores the graphics object
			shape.paintShape(this, state);

			if (cell.isVertex()) {
				Rectangle rect = state.getRectangle();
				int imgSize = Math.round(ResourcesReader.ARROW_SIZE * new Float(scale));

				Font scaledFont = mxUtils.getFont(style, scale);
				g.setFont(scaledFont);

				int scaledDistanceFromCell = Math.round(10 * new Float(scale));
				int specificScaledDistanceFromCell = Math.round(13 * new Float(scale));

				if (cell.isIncomingAdds()) {
					g.drawImage(ResourcesReader.getBottomRightArrowImg(), rect.x - rect.width / 4, rect.y - rect.height
							/ 2 - scaledDistanceFromCell, imgSize, imgSize, null);

					g.setColor(Color.BLACK);
					g.drawString(String.valueOf(cell.getIncomingValue()), rect.x - rect.width / 4 + 3
							* scaledDistanceFromCell, rect.y - rect.height / 2 + scaledDistanceFromCell);
				}
				if (cell.isOutgoingAdds()) {
					g.drawImage(ResourcesReader.getTopRightArrowImg(), rect.x + 3 * rect.width / 4, rect.y
							- rect.height / 2 - scaledDistanceFromCell, imgSize, imgSize, null);
					g.setColor(Color.RED);
					g.drawString(String.valueOf(cell.getOutgoingValue()), rect.x + 3 * rect.width / 4
							+ specificScaledDistanceFromCell, rect.y - rect.height / 2 + scaledDistanceFromCell);
				}
			}

			g.dispose();
			g = previousGraphics;
		}

		return shape;
	}
}
