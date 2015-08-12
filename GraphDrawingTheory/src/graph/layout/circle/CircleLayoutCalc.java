package graph.layout.circle;

import graph.elements.Vertex;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CircleLayoutCalc<V extends Vertex> {


	public void calculatePosition(List<V> vertices, double radius, Point2D center){

		Map<V,Point> ret = new HashMap<V,Point>();

		List<V> sortedVertices = new ArrayList<V>();
		//sort by size
		Collections.sort(sortedVertices, new Comparator<V>() {

			@Override
			public int compare(V o1, V o2) {

				if (o1.getSize().getWidth() * o1.getSize().getHeight()  > 
				o2.getSize().getHeight() * o2.getSize().getWidth())
					return 1;
				if (o1.getSize().getWidth() * o1.getSize().getHeight()  ==
						o2.getSize().getHeight() * o2.getSize().getWidth())
					return 0;
				return -1;
			}
		});

		System.out.println("Sorted: " + sortedVertices);


		int index = 0;
		//take one of the bigger ones, one of the smaller ones etc.
		//see if they overlap
		V current;
		V previous;

		while (sortedVertices.size() > 0){


		}

		double slice = 2 * Math.PI / vertices.size();
		for (int i = 0; i < vertices.size(); i++)
		{
			double angle = slice * i;
			int newX = (int)(center.getX() + radius * Math.cos(angle));
			int newY = (int)(center.getY() + radius * Math.sin(angle));
			Point p = new Point(newX, newY);
		}
	}
}

