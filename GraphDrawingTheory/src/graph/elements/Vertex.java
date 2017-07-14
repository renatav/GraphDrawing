package graph.elements;

import java.awt.Dimension;

/**
 * Represent vertex of the graph
 * @author Renata
 */
public interface Vertex {
	
	/**
	 * @return Size of the vertex
	 */
	Dimension getSize();
	
	/**
	 * @return Content of the vertex
	 */
	Object getContent();
	
	void setSize(Dimension size);
	
	void setContent(Object content);
	

}
