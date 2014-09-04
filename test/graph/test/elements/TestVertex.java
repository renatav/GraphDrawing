package graph.test.elements;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import graph.elements.Vertex;

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
	public Point2D getPosition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPosition(int xPos, int yPos) {
		// TODO Auto-generated method stub
		
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
