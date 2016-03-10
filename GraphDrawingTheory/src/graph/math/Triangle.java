package graph.math;

import java.awt.geom.Point2D;

public class Triangle {

	public Point2D a;
	public Point2D b;
	public Point2D c;
	
	public Triangle(Point2D a, Point2D b, Point2D c) {
		super();
		this.a = a;
		this.b = b;
		this.c = c;
	}

	public Point2D getA() {
		return a;
	}

	public void setA(Point2D a) {
		this.a = a;
	}

	public Point2D getB() {
		return b;
	}

	public void setB(Point2D b) {
		this.b = b;
	}

	public Point2D getC() {
		return c;
	}

	public void setC(Point2D c) {
		this.c = c;
	}
	
	
	
	
}
