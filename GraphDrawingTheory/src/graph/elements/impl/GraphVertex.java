package graph.elements.impl;

import java.awt.Dimension;

import graph.elements.Vertex;

public class GraphVertex implements Vertex{
	
	private Dimension size;
	private Object content;
	
	public GraphVertex(){
		
	}
	
	public GraphVertex(Dimension size, Object content){
		this.size = size;
		this.content = content;
	}
	
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

	public void setSize(Dimension size) {
		this.size = size;
	}

	public void setContent(Object content) {
		this.content = content;
	}

}
