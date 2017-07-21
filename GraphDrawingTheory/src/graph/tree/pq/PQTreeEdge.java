package graph.tree.pq;

import graph.elements.Edge;

/**
 * Edge of the PQ-tree
 * @author Renata
 */
public class PQTreeEdge implements Edge<PQTreeNode>{

	private PQTreeNode origin;
	private PQTreeNode destination;
	
	
	public PQTreeEdge(PQTreeNode origin, PQTreeNode destination) {
		super();
		this.origin = origin;
		this.destination = destination;
	}
	
	@Override
	public PQTreeNode getOrigin() {
		return origin;
	}
	@Override
	public PQTreeNode getDestination() {
		return destination;
	}
	@Override
	public void setOrigin(PQTreeNode origin) {
		this.origin = origin;
		
	}
	@Override
	public void setDestination(PQTreeNode destination) {
		this.destination = destination;
		
	}
	@Override
	public int getWeight() {
		return 0;
	}
	@Override
	public void setWeight(int weight) {
		
	}

	@Override
	public String toString() {
		return "PQTreeEdge [origin=" + origin + ", destination=" + destination
				+ "]";
	}
}
