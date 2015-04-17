package gui.view.painters;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PaintingUtil {

	
	public static Dimension calculateStringDimension(FontMetrics metrics, String text){

		// get the height of a line of text in this
		// font and render context
		int hgt = metrics.getHeight();
		// get the advance of my text in this font
		// and render context
		int adv = metrics.stringWidth(text);
		//return size
		Dimension size = new Dimension(adv, hgt);
		return size;
	}
	
	   public static List<Point2D> getCircleLineIntersectionPoint(Point2D pointA,
	            Point2D pointB, Point2D center, double radius) {
	        double baX = pointB.getX() - pointA.getX();
	        double baY = pointB.getY() - pointA.getY();
	        double caX = center.getX() - pointA.getX();
	        double caY = center.getY() - pointA.getY();

	        double a = baX * baX + baY * baY;
	        double bBy2 = baX * caX + baY * caY;
	        double c = caX * caX + caY * caY - radius * radius;

	        double pBy2 = bBy2 / a;
	        double q = c / a;

	        double disc = pBy2 * pBy2 - q;
	        if (disc < 0) {
	            return Collections.emptyList();
	        }
	        // if disc == 0 ... dealt with later
	        double tmpSqrt = Math.sqrt(disc);
	        double abScalingFactor1 = -pBy2 + tmpSqrt;
	        double abScalingFactor2 = -pBy2 - tmpSqrt;

	        Point2D p1 = new Point2D.Double(pointA.getX() - baX * abScalingFactor1, pointA.getY()
	                - baY * abScalingFactor1);
	        if (disc == 0) { // abScalingFactor1 == abScalingFactor2
	            return Collections.singletonList(p1);
	        }
	        Point2D p2 = new Point2D.Double(pointA.getX() - baX * abScalingFactor2, pointA.getY()
	                - baY * abScalingFactor2);
	        return Arrays.asList(p1, p2);
	    }
}
