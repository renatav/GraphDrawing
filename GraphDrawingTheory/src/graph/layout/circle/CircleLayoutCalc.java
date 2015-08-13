package graph.layout.circle;

import graph.elements.Vertex;
import graph.util.Pair;

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


		double slice = 2 * Math.PI / vertices.size();
		for (int i = 0; i < vertices.size(); i++)
		{
			double angle = slice * i;
			int newX = (int)(center.getX() + radius * Math.cos(angle));
			int newY = (int)(center.getY() + radius * Math.sin(angle));
			Point p = new Point(newX, newY);
			ret.put(vertices.get(i), p);
		}
	}

	public Pair<Double, List<V>> organizeVerticesAndDetermineRadius(List<V> vertices, boolean organize){


		List<V> ret = new ArrayList<V>();

		if (organize){
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

			System.out.println("sorted " + sortedVertices);



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

			System.out.println(ret);
		}
		else 
			ret.addAll(vertices);


		Double radius = 0D;

		double slice = 2 * Math.PI / vertices.size();
		V currentV = null;
		V previousV = null;
		double currentTheta = 0;
		double previousTheta = 0;

		for (int i = 1; i <= vertices.size(); i++){

			previousTheta = currentTheta;
			previousV = ret.get(i - 1);

			if (i < ret.size()){
				currentV = ret.get(i);
				currentTheta = slice * i;
			}
			else{
				currentV = ret.get(0);
				currentTheta = 0;
			}


			System.out.println("Current v " + currentV);
			System.out.println("Previous v " + previousV);
			System.out.println("Current theta " + currentTheta);
			System.out.println("Previous theta " + previousTheta);


			double r_x = 0;

			if (Math.abs(Math.cos(currentTheta) - Math.cos(previousTheta)) > 0.01) //not parallel
				r_x = (currentV.getSize().getWidth() + previousV.getSize().getWidth()) 
				/ (2 * (Math.max(Math.cos(currentTheta), Math.cos(previousTheta)) 
						- Math.min(Math.cos(currentTheta), Math.cos(previousTheta))));

			double r_y = 0;

			if (Math.abs(Math.sin(currentTheta) - Math.sin(previousTheta)) > 0.01) //not parallel
				r_y = (currentV.getSize().getHeight() + previousV.getSize().getHeight()) 
				/ (2 * (Math.max(Math.sin(currentTheta), Math.sin(previousTheta)) 
						- Math.min(Math.sin(currentTheta), Math.sin(previousTheta))));

			System.out.println("Rx " + r_x);
			System.out.println("Ry " + r_y);


			double r_current_max = Math.max(r_x, r_y);
			
			if (radius < r_current_max)
				radius = r_current_max;


		}

		System.out.println("Radius " + radius);

		return new Pair<Double, List<V>>(radius, ret);

	}
}

