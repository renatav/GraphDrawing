package gui.model;

import graph.elements.Vertex;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;

public class GraphVertex extends GraphElement implements Vertex{

	private Dimension size;
	private String content;
	private Point2D position;
	
	public GraphVertex(Point2D position, String content){
		size = new Dimension(50,50);
		this.position = position;
		this.content = content;
		color = Color.lightGray;
	}
	
	@Override
	public Dimension getSize() {
		return size;
	}

	@Override
	public Object getContent() {
		return content;
	}

	public Point2D getPosition() {
		return position;
	}

	public void setPosition(Point2D position) {
		this.position = position;
	}

	public void setSize(Dimension size) {
		this.size = size;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
