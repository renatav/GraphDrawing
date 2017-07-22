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

	/**
	 * Creates an edge with the specified origin vertex, destination vertex and weight
	 * @param origin Origin vertex
	 * @param destination Destination vertex
	 * @param weight Weight
	 */
	public GraphEdge(GraphVertex origin, GraphVertex destination, int weight) {
		super();
		this.origin = origin;
		this.destination = destination;
		this.weight = weight;
	}

	
	/**
	 * Creates an edge with the specified origin and destination vertices
	 * @param origin Origin vertex
	 * @param destination Destination vertex
	 */
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

	/**
	 * @return Origin vertex
	 */
	public GraphVertex getOrigin() {
		return origin;
	}

	/**
	 * @return Destination vertexx
	 */
	public GraphVertex getDestination() {
		return destination;
	}

}
