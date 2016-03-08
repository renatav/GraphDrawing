package graph.math;

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


	public Map<V,Point2D> calculatePosition(List<V> vertices, double radius, Point2D center){

		Map<V,Point2D> ret = new HashMap<V,Point2D>();

		if (vertices.size() == 1){
			ret.put(vertices.get(0), center);
			return ret;
		}
		
		if (vertices.size() == 2){
			ret.put(vertices.get(0), new Point2D.Double(center.getX() - radius/2, center.getY()));
			ret.put(vertices.get(1), new Point2D.Double(center.getX() + radius/2, center.getY()));
			return ret;
		}

		double slice = 2 * Math.PI / vertices.size();
		for (int i = 0; i < vertices.size(); i++){
		
			double angle = slice * i;
			int newX = (int)(center.getX() + radius * Math.cos(angle));
			int newY = (int)(center.getY() + radius * Math.sin(angle));
			Point p = new Point(newX, newY);
			ret.put(vertices.get(i), p);
		}
		
		
		
		return ret;
	}
	
	public List<V> organizeVerticesBySuze(List<V> vertices){
		
		List<V> ret = new ArrayList<V>();
		
		List<V> sortedVertices = new ArrayList<V>();
		sortedVertices.addAll(vertices);
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


		int index = 0;
		//take one of the bigger ones, one of the smaller ones etc.
		//see if they overlap
		boolean fromEnd = true;


		int numOfVertices = sortedVertices.size();
		while (ret.size() < numOfVertices){

			if (index == 0){
				ret.add(sortedVertices.get(index));
				index ++;
			}

			V v1, v2 = null;
			if (fromEnd){
				v1 = sortedVertices.get(numOfVertices - index);
				if (index < numOfVertices)
					v2 = sortedVertices.get(numOfVertices - index - 1);
			}
			else{
				v1 = sortedVertices.get(index);
				if (index < numOfVertices);
				v2 = sortedVertices.get(index + 1);
			}

			if (!ret.contains(v1))
				ret.add(v1);
			if (v2 != null && !ret.contains(v2))
				ret.add(0, v2);

			fromEnd = !fromEnd;

			if (fromEnd)
				index += 2;

		}
		
		return ret;
	}

	public double calculateRadius(List<V> vertices,  double treshold){

		double radius = 0D;
		
		if (vertices.size() == 2){
			return vertices.get(0).getSize().getWidth() + vertices.get(1).getSize().getWidth() + treshold;
		}
		

		double slice = 2 * Math.PI / vertices.size();
		V currentV = null;
		V previousV = null;
		double currentTheta = 0;
		double previousTheta = 0;

		for (int i = 1; i <= vertices.size(); i++){

			previousTheta = currentTheta;
			previousV = vertices.get(i - 1);

			if (i < vertices.size()){
				currentV = vertices.get(i);
				currentTheta = slice * i;
			}
			else{
				currentV = vertices.get(0);
				currentTheta = 0;
			}

			double r_x = 0;

			if (Math.abs(Math.cos(currentTheta) - Math.cos(previousTheta)) > 0.01) //not parallel
				r_x = (currentV.getSize().getWidth() + previousV.getSize().getWidth() + treshold) 
				/ (2 * (Math.max(Math.cos(currentTheta), Math.cos(previousTheta)) 
						- Math.min(Math.cos(currentTheta), Math.cos(previousTheta))));

			double r_y = 0;

			if (Math.abs(Math.sin(currentTheta) - Math.sin(previousTheta)) > 0.01) //not parallel
				r_y = (currentV.getSize().getHeight() + previousV.getSize().getHeight() + treshold) 
				/ (2 * (Math.max(Math.sin(currentTheta), Math.sin(previousTheta)) 
						- Math.min(Math.sin(currentTheta), Math.sin(previousTheta))));


			double r_current = Math.min(r_x, r_y);
			
			if (radius < r_current)
				radius = r_current;


		}

		return radius;

	}
}

