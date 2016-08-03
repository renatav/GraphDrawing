package graph.math;

import java.awt.geom.Point2D;

public class Triangle {

	private Point2D a;
	private Point2D b;
	private Point2D c;
	private Point2D centroid;
	private Triangle parentTriangle;
	private boolean beforeParent;
	
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

	@Override
	public String toString() {
		return "Triangle [a=" + a + ", b=" + b + ", c=" + c + "]";
	}

	/**
	 * @return the centroid
	 */
	public Point2D getCentroid() {
		return centroid;
	}

	/**
	 * @param centroid the centroid to set
	 */
	public void setCentroid(Point2D centroid) {
		this.centroid = centroid;
	}

	/**
	 * @return the parentTriangle
	 */
	public Triangle getParentTriangle() {
		return parentTriangle;
	}

	/**
	 * @param parentTriangle the parentTriangle to set
	 */
	public void setParentTriangle(Triangle parentTriangle) {
		this.parentTriangle = parentTriangle;
	}

	/**
	 * @return the beforeParent
	 */
	public boolean isBeforeParent() {
		return beforeParent;
	}

	/**
	 * @param beforeParent the beforeParent to set
	 */
	public void setBeforeParent(boolean beforeParent) {
		this.beforeParent = beforeParent;
	}
	
	
	
	
}
