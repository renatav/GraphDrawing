package graph.layout;

import java.awt.Dimension;

/**
* A simple layout algorithm which places elements in a table like structure
* Suitable if the elements are not linked
* @author xxx
*
*/
public class BoxLayout {
	
//	private int numberInRow;
//	private int xStart = 150, yStart = 150;
//	
//	public BoxLayout (GraphEditView view){
//		super(view);
//		xOffset = layoutProperties.getIntValue("boxOffsetX");
//		yOffset = layoutProperties.getIntValue("boxOffsetY");
//		numberInRow = layoutProperties.getIntValue("boxNumberInRow");
//	}
//	
//
//	@Override
//	public void layout() throws LayouterException {
//		int numberOfRows = elementsToLayout.size() / numberInRow;
//		int currentIndex = 0;
//		GraphElement currentElement;
//		int yPos = yStart;
//		Dimension currentDim;
//		int[] maxHeights = maxYInRows(numberOfRows);
//		for (int i = 0; i <= numberOfRows; i++){
//			int xPos = xStart;
//			for (int j = 0; j < numberInRow; j++){
//				if (elementsToLayout.size() == currentIndex)
//					break;
//				currentElement = elementsToLayout.get(currentIndex);
//				setPosition(strategy, view, (LinkableElement) currentElement, xPos, yPos);
//				currentDim = (Dimension) currentElement.getProperty(GraphElementProperties.SIZE);
//				xPos += currentDim.getWidth() + xOffset;
//				currentIndex ++;
//			}
//			if (i < numberOfRows)
//				yPos += yOffset + maxHeights[i]/2 + maxHeights[i+1]/2;
//		}
//		
//	}
//	
//	private int[] maxYInRows(int numberOfRows){
//		int[] ret = new int[numberOfRows + 1];
//		GraphElement currentElement;
//		Dimension currentDim;
//		int currentIndex = 0;
//		for (int i = 0; i <= numberOfRows; i++){
//			int maxYInRow = 0;
//			for (int j = 0; j < numberInRow; j++){
//				if (elementsToLayout.size() == currentIndex)
//					break;
//				currentElement = elementsToLayout.get(currentIndex);
//				currentDim = (Dimension) currentElement.getProperty(GraphElementProperties.SIZE);
//				if (currentDim.getHeight() > maxYInRow)
//					maxYInRow = (int) currentDim.getHeight();
//				currentIndex ++;
//			}
//			ret[i] = maxYInRow;
//		}
//		return ret;
//	}
		

}