package graph.tree.spqr;

import java.util.List;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

public class ChildGraph<V extends Vertex, E extends Edge<V>> extends Graph<V,E>{

	private E referenceEdge;
	

	public ChildGraph(List<V> vertices, List<E> edges, E referenceEdge) {
		super(vertices, edges);
		this.referenceEdge = referenceEdge;
	}

	public E getReferenceEdge() {
		return referenceEdge;
	}

	public void setReferenceEdge(E referenceEdge) {
		this.referenceEdge = referenceEdge;
	}
	
	
	
}
