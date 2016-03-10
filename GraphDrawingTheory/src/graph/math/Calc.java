package graph.math;

import java.awt.geom.Point2D;

public class Calc {

	public static Line lineThroughTwoPoints(Point2D p1, Point2D p2){
		
		double x1 = p1.getX();
		double y1 = p1.getY();
		double x2 = p2.getX();
		double y2 = p2.getY();
		
		//y1 = k*x1 + n
		//y2 = k*x2 + n
		
		double k = Math.abs((x1 - x2)/(y1-y2));
		double n = y1 - k*x1;
		
		return new Line(k,n);
	}
	
	public static Line perperndicularLineIntersectingOnPoint(Line l1, Point2D p){
		
		//k1*k2 = -1
		
		double k2 = -1/l1.getK();
		double x2 = p.getX();
		double y2 = p.getY();
		
		double n2 = y2 - k2*x2;
		
		return new Line(k2,n2);
		
	}
	
	public static Point2D intersectionOfLines(Line l1, Line l2){
		
		double x, y;
		//y = k1*x + n1
		//y = k2*x + n2
		//k1*x + n1 = k2*x + n2
		//x*(k1-k2) = n2 - n1
		//x = (n2-n1)/(k1-k2)
		
		double k1 = l1.getK();
		double k2 = l2.getK();
		double n1 = l1.getN();
		double n2 = l2.getN();
		
		x = (n2-n1)/(k1-k2);
		y = k1*x + n1;
		
		return new Point2D.Double(x, y);
	}
	
	public static double[] angleBetweenTwoLines(Line l1, Line l2){
		double[] ret = new double[2];
		
		//angle = arctan (+- (k1 - k2)/(1+k1*k2))
		
		double k1 = l1.getK();
		double k2 = l2.getK();
		double temp = (k1 - k2)/(1 + k1*k2);
		
		double angle1 = Math.atan(temp);
		double angle2 = Math.atan(-temp);
		ret[0] = angle1;
		ret[1] = angle2;
		
		return ret;
	}
	
	public static double distanceBetweenTwoPoints(Point2D p1, Point2D p2){
		//d = sqrt((x2-x1)^2 + (y2-y1)^2)
		
		double x1 = p1.getX();
		double x2 = p2.getX();
		double y1 = p1.getY();
		double y2 = p2.getY();
		
		return Math.sqrt(Math.pow((x2-x1),2) + Math.pow((y2-y1),2));
	}
	
	public static double triangleAngle(double a, double b, double c){
		
		//c^2 = a^2 + b^2 -2a*b*cosC
		return Math.acos((Math.pow(a,2) + Math.pow(b,2) - Math.pow(c,2))/(2*a*b));
	}
	
	
	public static Point2D triangleCentroid(Point2D a, Point2D b, Point2D c){
		
		double x = (a.getX() + b.getX() + c.getX())/3;
		double y = (a.getY() + b.getY() + c.getY()/3);
		return new Point2D.Double(x,y);
	}
	
	public static Point2D triangleCentroid(Triangle t){
		return triangleCentroid(t.getA(), t.getB(), t.getC());
	}
	
	public static Line parallelLineThroughPoint(Line line1, Point2D point){
		double k = line1.getK();
		//y = kx + n
		//n = y - k*x
		double n = point.getY() - k * point.getX();
		return new Line(k,n);
	}

	
	
}
