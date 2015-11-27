package graph.layout.circle;

import graph.drawing.Drawing;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.layout.AbstractLayouter;
import graph.layout.GraphLayoutProperties;
import graph.layout.PropertyEnums.CircleProperties;
import graph.ordering.circular.Circular;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;

public class CircleLayouter<V extends Vertex, E extends Edge<V>> extends AbstractLayouter<V, E>
{


	@Override
	public Drawing<V, E> layout(Graph<V, E> graph,GraphLayoutProperties layoutProperties) {
		
		//TODO sta raditi sa cvorovima koji nisu povezani
		
		Circular<V,E> circular = new Circular<V,E>(graph);
		List<V> ordering = circular.circularOrdering();
		
		Double distance = 0D;
		if (layoutProperties.getProperty(CircleProperties.DISTANCE) != null)
			distance =  (Double) layoutProperties.getProperty(CircleProperties.DISTANCE);
		
		//graph.setVertices(ordering);

		CircleLayoutCalc<V> calc = new CircleLayoutCalc<V>();
		
		double radius = calc.calculateRadius(graph.getVertices(), distance);

		Map<V, Point2D> vertexPositions = calc.calculatePosition(ordering, radius, new Point2D.Double(0,0));
		
		Drawing<V, E> drawing = new Drawing<>();
		drawing.setVertexMappings(vertexPositions);
		
		drawing.positionEdges(graph.getEdges());
		
		return drawing;
	}

}
