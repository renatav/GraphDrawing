package graph.layout.orthgonal;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;

import graph.algorithms.drawing.VisibilityRepresentation;
import graph.drawing.Drawing;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.layout.AbstractLayouter;
import graph.layout.GraphLayoutProperties;

public class VisibilityRepresentationLayout<V extends Vertex, E extends Edge<V>> extends AbstractLayouter<V, E>{

	
	private int xDistance = 50;
	private int yDistance = 50;
	
	@Override
	public Drawing<V, E> layout(Graph<V, E> graph, GraphLayoutProperties layoutProperties) {
		
		VisibilityRepresentation<V, E> visibilityRepresentation = new VisibilityRepresentation<>(graph);
		
		Map<V, Integer> vYMap = visibilityRepresentation.getvYMap();
		Map<V, Integer> vXMinMap = visibilityRepresentation.getvXMinMap();
		Map<V, Integer> vXMaxMap = visibilityRepresentation.getvXMaxMap();
		Map<E, Integer> eXMap = visibilityRepresentation.geteXMap();
		Map<E, Integer> eYMaxMap = visibilityRepresentation.geteYMaxMap();
		Map<E, Integer> eYMinMap = visibilityRepresentation.geteYMinMap();
		
		//position vertices based on the calculated values
		
		//first idea
		//calculate the values as (max-min)/2
		
		
		
		Drawing<V,E> drawing = new Drawing<V,E>();
		Map<V, Point2D> vertexMappings = drawing.getVertexMappings();
		for (V v : graph.getVertices()){
			int xMin = vXMinMap.get(v);
			int xMax = vXMaxMap.get(v);
			Point2D position = new Point2D.Double (((xMax - xMin) / 2) * xDistance, vYMap.get(v) * yDistance);
			vertexMappings.put(v, position);
		}
		
		Map<E, List<Point2D>> edgesMappings = drawing.getEdgeMappings();
		
		//now route the edges
			
		return drawing;
	}
	

}
