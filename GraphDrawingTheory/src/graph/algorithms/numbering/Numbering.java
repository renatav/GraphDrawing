package graph.algorithms.numbering;

import graph.elements.Edge;
import graph.elements.Vertex;

import java.util.List;

/**
 * Abstract class meant to be extended by all implementing a numbering algorithm
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type
 */
public abstract class Numbering<V extends Vertex,  E extends Edge<V>> {
	
	protected List<V> order;
	
	public int numberOf(V v){
		return order.indexOf(v) + 1;
	}

	public List<V> getOrder() {
		return order;
	}
	
	

}
