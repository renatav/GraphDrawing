package graph.algorithms.planarity;

import java.util.List;
import java.util.Map;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

public class PlanarFaces<V extends Vertex, E extends Edge<V>> {
	
	private Map<E, List<List<E>>> faces;
	private Graph<V,E> graph;
	
	public PlanarFaces(Graph<V,E> graph){
		this.graph = graph;
	}
	
	public void formFaces(){
		
	}
	
	

}
