package graph.layout.circle;

import graph.drawing.Drawing;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.layout.AbstractLayouter;
import graph.layout.GraphLayoutProperties;
import graph.layout.PropertyEnums.SymmetricCircleProperties;
import graph.symmetry.SymmetricGraphDrawing;
import graph.symmetry.nauty.Permutation;

import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class SymmetricCircleLayouter <V extends Vertex, E extends Edge<V>> extends AbstractLayouter<V, E>{

	public SymmetricCircleLayouter(Graph<V, E> graph,
			GraphLayoutProperties layoutProperties) {
		super(graph, layoutProperties);
	}

	@Override
	public Drawing<V, E> layout() {


		CircleLayoutCalc<V> calc = new CircleLayoutCalc<V>();

		Permutation p = null;
		Double distance = null;
		if (layoutProperties.getProperty(SymmetricCircleProperties.DISTANCE) != null)
			distance =  (Double) layoutProperties.getProperty(SymmetricCircleProperties	.DISTANCE);
		if (layoutProperties.getProperty(SymmetricCircleProperties.PERMUTATION)!= null){
			p = (Permutation) layoutProperties.getProperty(SymmetricCircleProperties.PERMUTATION);
		}
		
		if (distance == null){
			//find largest element by x or y
			distance = 0D;
			for (V v : graph.getVertices()){

				if (v.getSize().getHeight() > distance)
					distance = v.getSize().getHeight();

				if (v.getSize().getWidth() > distance)
					distance = v.getSize().getWidth();
			}
			distance *= 1.1;
		}


		SymmetricGraphDrawing<V, E> symmetricDrawing = new SymmetricGraphDrawing<V,E>(graph);
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

			Map<V, Point2D> vertexPositions = calc.calculatePosition(circle, totalRadius, new Point2D.Double(0,0));

			drawing.getVertexMappings().putAll(vertexPositions);

		}


		return drawing;
	}

}
