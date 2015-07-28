package graph.representation.visibility;

import java.awt.geom.Point2D;

public class Segment {
	
	private Point2D startPoint, endPoint;
	private int lenght;
	
	
	public Segment(Point2D startPoint, Point2D endPoint, int lenght) {
		super();
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.lenght = lenght;
	}
	
	
	public Point2D getStartPoint() {
		return startPoint;
	}
	public void setStartPoint(Point2D startPoint) {
		this.startPoint = startPoint;
	}
	public Point2D getEndPoint() {
		return endPoint;
	}
	public void setEndPoint(Point2D endPoint) {
		this.endPoint = endPoint;
	}
	public int getLenght() {
		return lenght;
	}
	public void setLenght(int length) {
		this.lenght = length;
	}
	
	

}
