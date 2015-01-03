package graph.drawing;

import java.util.HashMap;
import java.util.Map;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

public class StraightLineDrawing<V extends Vertex, E extends Edge<V>> {
	
	
	private Map<V, Coordinate> vertexMappings;
	private Graph<V,E> graph;
	
	public StraightLineDrawing(Graph<V,E> graph){
		this.graph = graph;
		vertexMappings = new HashMap<V, Coordinate>();
	}
	
	public void insertMapping(V vertex, Coordinate position){
		vertexMappings.put(vertex, position);
	}

	public Map<V, Coordinate> getVertexMappings() {
		return vertexMappings;
	}


}
