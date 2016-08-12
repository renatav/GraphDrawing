package graph.tree.pq;

import graph.elements.Edge;

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
		// TODO Auto-generated method stub
		return origin;
	}
	@Override
	public PQTreeNode getDestination() {
		// TODO Auto-generated method stub
		return destination;
	}
	@Override
	public void setOrigin(PQTreeNode origin) {
		// TODO Auto-generated method stub
		this.origin = origin;
		
	}
	@Override
	public void setDestination(PQTreeNode destination) {
		// TODO Auto-generated method stub
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
		return "PQTreeEdge [origin=" + origin + ", destination=" + destination
				+ "]";
	}
}
