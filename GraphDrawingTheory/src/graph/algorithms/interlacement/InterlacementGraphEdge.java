package graph.algorithms.interlacement;

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

	@Override
	public void setOrigin(InterlacementGraphVertex<V, E> origin) {
		 this.origin = origin;
	}

	@Override
	public void setDestination(InterlacementGraphVertex<V, E> destination) {
		this.destination = destination;
		
	}

	@Override
	public void setWeight(int weight) {
		// TODO Auto-generated method stub
		
	}

}
