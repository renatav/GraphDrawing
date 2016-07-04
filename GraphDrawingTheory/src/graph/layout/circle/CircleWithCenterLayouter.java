package graph.layout.circle;

import graph.drawing.Drawing;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.layout.AbstractLayouter;
import graph.layout.GraphLayoutProperties;

import java.awt.geom.Point2D;


/**
 * This layouter takes places vertices on a circumference of a circle,
 * and places the vertex with most links with other graph vertices in
 * the center of the circle 
 */
public class CircleWithCenterLayouter<V extends Vertex, E extends Edge<V>> extends AbstractLayouter<V, E>{ 



	@Override
	public Drawing<V, E> layout(Graph<V, E> graph,
			GraphLayoutProperties layoutProperties) {
		
		//find the vertex with the largest number of adjacent vertices
		V center = null;
		int maxAdjacent = 0;
		int currentAdjacentSize;
		for (V v : graph.getVertices()){
			currentAdjacentSize = graph.adjacentVertices(v).size();
			if (center == null || currentAdjacentSize > maxAdjacent){
				maxAdjacent = currentAdjacentSize;
				center = v;
			}
		}
		
		graph.removeVertex(center);
		
		CircleLayouter<V,E> circleLayouter = new CircleLayouter<V,E>();
		Drawing<V,E> ret = circleLayouter.layout(graph, layoutProperties);
		ret.getVertexMappings().put(center, new Point2D.Double(0, 0));
		
		return ret;
		
	}
}
