package graph.math;

import java.awt.geom.Point2D;

/**
 * A triangle suited for the use inside convex drawing algorithm
 * @author Renata
 */
public class Triangle {

	/**
	 * Position of the first apex
	 */
	private Point2D a;
	/**
	 * Position of the second apex
	 */
	private Point2D b;
	/**
	 * Position of the third apex
	 */
	private Point2D c;
	/**
	 * Position of the ceontroid of the triangle
	 */
	private Point2D centroid;
	/**
	 * A triangle containing this triangle (used in convex drawing)
	 */
	private Triangle parentTriangle;
	/**
	 * Indicator if the triangle should be placed before its parent
	 * (used in convex drawing)
	 */
	private boolean beforeParent;
	
	/**
	 * Constructs a triangle with the given 3 points as its apices
	 * @param a Position of the first apex
	 * @param b Position of the second apex
	 * @param c Position of he third apex
	 */
	public Triangle(Point2D a, Point2D b, Point2D c) {
		super();
		this.a = a;
		this.b = b;
		this.c = c;
	}

	/**
	 * @return Position of the A apex
	 */
	public Point2D getA() {
		return a;
	}

	/**
	 * @param a Position of the A apex to set
	 */
	public void setA(Point2D a) {
		this.a = a;
	}

	/**
	 * 
	 * @return Position of the B apex
	 */
	public Point2D getB() {
		return b;
	}

	/**
	 * @param b Position of the B apex to set
	 */
	public void setB(Point2D b) {
		this.b = b;
	}

	/**
	 * @return Position of the C apex
	 */
	public Point2D getC() {
		return c;
	}

	/**
	 * @param c Position of the C apex to set
	 */
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
	 * @return the parent triangle
	 */
	public Triangle getParentTriangle() {
		return parentTriangle;
	}

	/**
	 * @param parentTriangle the parent triangle to set
	 */
	public void setParentTriangle(Triangle parentTriangle) {
		this.parentTriangle = parentTriangle;
	}

	/**
	 * @return Indicator if the triangle should be placed before its parent
	 */
	public boolean isBeforeParent() {
		return beforeParent;
	}

	/**
	 * @param Indicator if the triangle should be placed before its parent to set
	 */
	public void setBeforeParent(boolean beforeParent) {
		this.beforeParent = beforeParent;
	}
	
	
	
	
}
