package graph.elements.impl;

import java.awt.Dimension;

import graph.elements.Vertex;

/**
 * A class which implements the Vertex interface
 * @author Renata
 */
public class GraphVertex implements Vertex{
	
	private Dimension size;
	private Object content;
	
	public GraphVertex(){
		
	}
	
	/**
	 * Creates a vertex with the provided size and content
	 * @param size Size of the vertex
	 * @param content Content of the vertex
	 */
	public GraphVertex(Dimension size, Object content){
		this.size = size;
		this.content = content;
	}
	
	/**
	 * Creates a vertex with the provided size
	 * @param size Size of the vertex
	 */
	public GraphVertex(Dimension size){
		this.size = size;
		content = "";
	}
	
	@Override
	public Dimension getSize() {
		return size;
	}

	@Override
	public Object getContent() {
		return content;
	}
	
	@Override
	public void setSize(Dimension size) {
		this.size = size;
	}

	@Override
	public void setContent(Object content) {
		this.content = content;
	}

}
