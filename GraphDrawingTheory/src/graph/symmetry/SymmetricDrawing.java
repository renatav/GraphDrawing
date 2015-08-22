package graph.symmetry;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

public class SymmetricDrawing<V extends Vertex, E extends Edge<V>> {

	private PermutationGroup permutationGroup;
	private Graph<V,E> graph;
	
	
	public SymmetricDrawing(PermutationGroup permutationGroup, Graph<V, E> graph) {
		this.permutationGroup = permutationGroup;
		this.graph = graph;
	}
	
	
	
}
