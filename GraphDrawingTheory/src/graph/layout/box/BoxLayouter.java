package graph.layout.box;

import graph.drawing.Drawing;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.layout.AbstractLayouter;
import graph.layout.GraphLayoutProperties;
import graph.layout.PropertyEnums.BoxProperties;

import java.awt.Dimension;
import java.awt.Point;
import java.util.List;

/**
 * A simple layout algorithm which places elements in a table like structure
 * Suitable if the elements are not linked
 * @author xxx
 *
 */
public class BoxLayouter<V extends Vertex, E extends Edge<V>> extends AbstractLayouter<V,E>{
	
	private int numberInRow = 5;
	private int xStart = 200, yStart = 200, xOffset = 100, yOffset = 50;
	private List<V> elementsToLayout;
	

	@Override
	public Drawing<V,E> layout(Graph<V,E> graph, GraphLayoutProperties layoutProperties) {
		
		elementsToLayout = graph.getVertices();
		
		Drawing<V,E> drawing = new Drawing<V,E>();
		
		if (layoutProperties.getProperty(BoxProperties.COLUMNS) != null)
			numberInRow = (Integer) layoutProperties.getProperty(BoxProperties.COLUMNS);
		
		int numberOfRows = elementsToLayout.size() / numberInRow;
		int currentIndex = 0;
		V currentElement;
		int yPos = yStart;
		Dimension currentDim;
		int[] maxHeights = maxYInRows(numberOfRows);
		for (int i = 0; i <= numberOfRows; i++){
			int xPos = xStart;
			for (int j = 0; j < numberInRow; j++){
				if (elementsToLayout.size() == currentIndex)
					break;
				currentElement = elementsToLayout.get(currentIndex);
				drawing.setVertexPosition(currentElement, new Point(xPos, yPos));
				currentDim = currentElement.getSize();
				xPos += currentDim.getWidth() + xOffset;
				currentIndex ++;
			}
			if (i < numberOfRows)
				yPos += yOffset + maxHeights[i]/2 + maxHeights[i+1]/2;
		}
		
		return drawing;
		
	}
	
	private int[] maxYInRows(int numberOfRows){
		int[] ret = new int[numberOfRows + 1];
		V currentElement;
		Dimension currentDim;
		int currentIndex = 0;
		for (int i = 0; i <= numberOfRows; i++){
			int maxYInRow = 0;
			for (int j = 0; j < numberInRow; j++){
				if (elementsToLayout.size() == currentIndex)
					break;
				currentElement = elementsToLayout.get(currentIndex);
				currentDim = currentElement.getSize();
				if (currentDim.getHeight() > maxYInRow)
					maxYInRow = (int) currentDim.getHeight();
				currentIndex ++;
			}
			ret[i] = maxYInRow;
		}
		return ret;
	}
		

}
