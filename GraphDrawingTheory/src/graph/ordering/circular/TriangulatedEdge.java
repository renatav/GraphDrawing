package graph.ordering.circular;

import graph.elements.Edge;
import graph.elements.Vertex;

/**
 * Triangulated edge as defined in algorithm CIRCULAR
 * @author Renata
 * @param <V> The vertex type
 */
public class TriangulatedEdge<V extends Vertex> implements Edge<V> {

	private V origin, destination;
	private int weight = 1;
	
	
	public TriangulatedEdge(V origin, V destination) {
		super();
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
	public int getWeight() {
		return weight;
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
	public void setWeight(int weight) {
		this.weight = weight;
	}
}
