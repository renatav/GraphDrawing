package graph.algorithms.planarity.dual;

import graph.elements.Edge;
import graph.elements.Vertex;

/**
 * Each edge  of dual graphs connects a face to the left of a graph's edge and the face to the right 
 * @author xx
 *
 */
public class DualGraphEdge<V extends Vertex, E extends Edge<V>> implements Edge<DualGraphVertex<V,E>>{

	private DualGraphVertex<V,E> origin, destination;
	
	
	public DualGraphEdge(DualGraphVertex<V, E> origin, DualGraphVertex<V, E> destination) {
		super();
		this.origin = origin;
		this.destination = destination;
	}

	@Override
	public DualGraphVertex<V, E> getOrigin() {
		return origin;
	}

	@Override
	public DualGraphVertex<V, E> getDestination() {
		// TODO Auto-generated method stub
		return destination;
	}

	@Override
	public void setOrigin(DualGraphVertex<V, E> origin) {
		this.origin = origin;
		
	}

	@Override
	public void setDestination(DualGraphVertex<V, E> destination) {
		this.destination = destination;
		
	}

	@Override
	public int getWeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setWeight(int weight) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toString() {
		return "DualGraphEdge [origin=" + origin + ", destination="
				+ destination + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((destination == null) ? 0 : destination.hashCode());
		result = prime * result + ((origin == null) ? 0 : origin.hashCode());
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DualGraphEdge<V,E> other = (DualGraphEdge<V,E>) obj;
		if (destination == null) {
			if (other.destination != null)
				return false;
		} else if (!destination.equals(other.destination))
			return false;
		if (origin == null) {
			if (other.origin != null)
				return false;
		} else if (!origin.equals(other.origin))
			return false;
		return true;
	}



}
