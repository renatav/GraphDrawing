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
	
	/**
	 * Returns index of a vertex in the order 
	 * @param v Vertex
	 * @return Index of the vertex in accordance with the order
	 */
	public int numberOf(V v){
		return order.indexOf(v) + 1;
	}

	/**
	 * Order of vertices
	 * @return Sorted list of vertices
	 */
	public List<V> getOrder() {
		return order;
	}
	
	

}
