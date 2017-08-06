package gui.model;

import java.awt.Color;
import java.awt.geom.Point2D;

public class LinkNode extends GraphElement{
	
	private Point2D position;
	private int size = 10;
	
	public LinkNode(Point2D position){
		this.position = position;
		color = Color.BLACK;
	}

	public Point2D getPosition() {
		return position;
	}

	public void setPosition(Point2D position) {
		this.position = position;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

}
