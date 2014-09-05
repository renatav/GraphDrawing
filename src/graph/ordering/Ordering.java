package graph.ordering;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

public interface Ordering<V extends Vertex, E extends Edge<V>> {

	public Graph<V,E> order();
	
	public boolean canOrder();
	
	public Graph<V,E> getGraph();
}
