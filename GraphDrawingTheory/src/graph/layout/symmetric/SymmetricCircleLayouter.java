package graph.layout.symmetric;

import graph.drawing.Drawing;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.layout.GraphLayoutProperties;
import graph.layout.PropertyEnums.SymmetricProperties;
import graph.symmetry.CyclicSymmetricGraphDrawing;
import graph.symmetry.Permutation;

import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class SymmetricCircleLayouter <V extends Vertex, E extends Edge<V>> extends SymmetricLayouter<V, E>{


	@Override
	public Drawing<V, E> layout(Graph<V, E> graph, GraphLayoutProperties layoutProperties) {


		if (layoutProperties.getProperty(SymmetricProperties.DISTANCE) != null)
			distance =  (Double) layoutProperties.getProperty(SymmetricProperties.DISTANCE);
		if (layoutProperties.getProperty(SymmetricProperties.PERMUTATION) != null)
			p = (Permutation) layoutProperties.getProperty(SymmetricProperties.PERMUTATION);
		if (layoutProperties.getProperty(SymmetricProperties.CENTER) != null)
			center = (Point2D) layoutProperties.getProperty(SymmetricProperties.CENTER);

		init(graph);

		CyclicSymmetricGraphDrawing<V, E> symmetricDrawing = new CyclicSymmetricGraphDrawing<V,E>(graph);
		List<List<V>> circles;
		if (p == null || p.getPermutation().size() == 0)
			circles = symmetricDrawing.execute();
		else
			circles = symmetricDrawing.execute(p);

		Drawing<V, E> drawing = new Drawing<>();

		Collections.sort(circles, new Comparator<List<V>>() {

			@Override
			public int compare(List<V> o1, List<V> o2) {
				if (o1.size() > o2.size())
					return 1;
				if (o1.size() < o2.size())
					return -1;
				return 0;
			}
		});

		double totalRadius = 0;

		for (List<V> circle : circles){

			double radius = calc.calculateRadius(circle, distance);
			if (radius <= totalRadius + distance){
				radius = totalRadius + distance;
				totalRadius += distance;
			}
			else
				totalRadius = radius;

			Map<V, Point2D> vertexPositions = calc.calculatePosition(circle, totalRadius, center);

			drawing.getVertexMappings().putAll(vertexPositions);

		}


		return drawing;
	}

}
