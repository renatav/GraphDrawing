package graph.algorithms.util;

import graph.elements.Edge;
import graph.elements.Vertex;


/**
 * If a graph 
 * @author xx
 *
 * @param <V>
 */
public class GraphEdge< V extends Vertex> implements Edge<V>{

	private V origin, destination;
	
	public GraphEdge(V origin, V destination) {
		this.origin = origin;
		this.destination = destination;
	}

	@Override
	public V getOrigin() {
		return origin;
	}

	@Override
	public V getDestination() {
		return destination;
	}

	@Override
	public void setOrigin(V origin) {
		this.origin = origin;
	}

	@Override
	public void setDestination(V destination) {
		this.destination = destination;
	}

	@Override
	public int getWeight() {
		return 0;
	}
	

}
