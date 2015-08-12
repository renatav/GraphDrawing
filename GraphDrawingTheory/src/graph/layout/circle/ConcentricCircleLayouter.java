package graph.layout.circle;

import graph.drawing.Drawing;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.layout.AbstractLayouter;
import graph.layout.GraphLayoutProperties;
import graph.layout.PropertyEnums.ConcentricCircleProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConcentricCircleLayouter <V extends Vertex, E extends Edge<V>> extends AbstractLayouter<V, E>{

	public ConcentricCircleLayouter(Graph<V, E> graph,
			GraphLayoutProperties layoutProperties) {
		super(graph, layoutProperties);
	}

	@Override
	public Drawing<V, E> layout() {
	
		//pass which vertices are in which circle
		Map<Integer, List<V>> verticesInCircles = (Map<Integer, List<V>>) layoutProperties.getProperty(ConcentricCircleProperties.VERTICES_CIRCLES_MAP);
		
		
		
		return null;
	}

}
