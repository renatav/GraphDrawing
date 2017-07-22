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
	
	/**
	 * @param size Size to set
	 */
	void setSize(Dimension size);
	
	/**
	 * @param content Content to set
	 */
	void setContent(Object content);
	

}
