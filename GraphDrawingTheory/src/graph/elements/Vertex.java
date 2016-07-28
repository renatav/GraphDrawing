package graph.elements;

import java.awt.Dimension;

public interface Vertex {
	
	
	Dimension getSize();
	
	Object getContent();
	
	void setSize(Dimension size);
	
	void setContent(Object content);
	

}
