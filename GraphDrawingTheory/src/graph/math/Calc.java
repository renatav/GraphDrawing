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
		
		double k;
		if (x1 == x2)
			//parallel to y axis
			return new Line(null, x1); 
			
		else
			k = Math.abs((y1 - y2)/(x1-x2));
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
		
		Double k1 = l1.getK();
		Double k2 = l2.getK();
		Double n1 = l1.getN();
		Double n2 = l2.getN();
		
		System.out.println("Intersection of lines: " + l1 + " " + l2);
		
		if (k1 != null && k2 != null){
			x = (n2-n1)/(k1-k2);
			y = k1*x + n1;
			return new Point2D.Double(x, y); 
		}
		else if (k1 != null && k2 == null){
			//line 2 is parallel to the y axis
			//y = k1*x + n1
			//x = n2 -> y = n1 + k1*n2
			return new Point2D.Double(n2, n1+k1*n2);
		}
		else if (k1 == null && k2 != null)
			return new Point2D.Double(n1, n2+k2*n1);
		else
			return null;
		
		
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
		double y = (a.getY() + b.getY() + c.getY())/3;
		return new Point2D.Double(x,y);
	}
	
	public static Point2D triangleCentroid(Triangle t){
		return triangleCentroid(t.getA(), t.getB(), t.getC());
	}
	
	public static Line parallelLineThroughPoint(Line line1, Point2D point){
		Double k = line1.getK();
		if (k == null) //parallel to y axis
			return new Line(null, point.getX());
		//y = kx + n1
		//n = y - k*x
		double n = point.getY() - k * point.getX();
		return new Line(k,n);
	}
	
	public static Point2D symmetricPoint(Point2D p1, Line line){
		
		//(x,y)-> 1/(a^2 + b^2)(((b^2 - a^2)-4a^2b^2)(x,y)-(2ac, 2bc)
		//line ax+by+c = 0  
		//y = kx + n
		//kx-y+n =0 -> b = -1
		
		double a = line.getK();
		double b = -1;
		double c = line.getN();
		
		double divider = 1/(Math.pow(a, 2) +Math.pow(b,2));
		double determinant = ((Math.pow(b,2) - Math.pow(a, 2) *(Math.pow(a, 2) - Math.pow(b, 2)))
				- 4*Math.pow(a, 2)*Math.pow(b, 2));
		
		double x = divider*(determinant * p1.getX() - 2*a*c);
		double y = divider*(determinant * p1.getY() - 2*b*c);
		
		return new Point2D.Double(x,y);
		
		
		
	}

	
	
}
