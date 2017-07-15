package graph.elements.impl;

import graph.elements.Edge;

/**
 * A class which implements the Edge interface. 
 * @author Renata
 */
public class GraphEdge implements Edge<GraphVertex>{
	
	private GraphVertex origin;
	private GraphVertex destination;
	private int weight = 0;
	
	public GraphEdge() {
	}


	public GraphEdge(GraphVertex origin, GraphVertex destination, int weight) {
		super();
		this.origin = origin;
		this.destination = destination;
		this.weight = weight;
	}

	
	public GraphEdge(GraphVertex origin, GraphVertex destination) {
		super();
		this.origin = origin;
		this.destination = destination;
	}

	@Override
	public int getWeight() {
		return weight;
	}

	@Override
	public void setWeight(int weight) {
		this.weight = weight;
		
	}

	@Override
	public void setOrigin(GraphVertex origin) {
		this.origin = origin;
		
	}

	@Override
	public void setDestination(GraphVertex destination) {
		this.destination = destination;
	}

	public GraphVertex getOrigin() {
		return origin;
	}

	public GraphVertex getDestination() {
		return destination;
	}

}
