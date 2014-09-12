package graph.algorithms.planarity;

import graph.elements.Edge;
import graph.elements.Vertex;

public class InterlacementGraphEdge<V extends Vertex, E extends Edge<V>> implements Edge<InterlacementGraphVertex<V,E>>{

	private InterlacementGraphVertex<V,E> origin, destination;
	
	
	public InterlacementGraphEdge(InterlacementGraphVertex<V,E> origin,
			InterlacementGraphVertex<V,E> destination) {
		super();
		this.origin = origin;
		this.destination = destination;
	}

	@Override
	public InterlacementGraphVertex<V,E> getOrigin() {
		return origin;
	}

	@Override
	public InterlacementGraphVertex<V,E> getDestination() {
		return destination;
	}

	@Override
	public String toString() {
		return "InterlacementGraphEdge [origin=" + origin + ", destination="
				+ destination + "]";
	}

	@Override
	public int getWeight() {
		return 1;
	}

}
