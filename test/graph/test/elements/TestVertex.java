package graph.test.elements;

import graph.elements.Vertex;

import java.awt.Dimension;

public class TestVertex implements Vertex{

	private String content;
	public double minDistance = Double.POSITIVE_INFINITY;
	
	public TestVertex(String content) {
		this.content = content;
	}

	
	@Override
	public Dimension getSize() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Object getContent() {
		return content;
	}


	@Override
	public String toString() {
		return content;
	}


}
