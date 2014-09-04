package graph.elements;

import java.awt.Dimension;
import java.awt.geom.Point2D;

public interface Vertex{
	
	
	Dimension getSize();
	
	Point2D getPosition();
	
	void setPosition(int xPos, int yPos);
	
	Object getContent();
	
	 

}
