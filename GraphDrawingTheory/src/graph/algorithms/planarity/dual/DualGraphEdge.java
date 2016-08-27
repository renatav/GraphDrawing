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



}
