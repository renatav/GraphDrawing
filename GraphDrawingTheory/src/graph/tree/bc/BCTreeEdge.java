package graph.tree.bc;

import graph.elements.Edge;

/**
 * Edge of the block-cut vertex tree
 * @author Renata
 */
public class BCTreeEdge implements Edge<BCTreeNode>{

	private BCTreeNode origin;
	private BCTreeNode destination;

	/**
	 * Construct a BC-tree edge with the specified origina and destination nodes
	 * @param origin Origin node
	 * @param destination Destination node
	 */
	public BCTreeEdge(BCTreeNode origin, BCTreeNode destination) {
		super();
		this.origin = origin;
		this.destination = destination;
	}

	@Override
	public BCTreeNode getOrigin() {
		return origin;
	}

	@Override
	public BCTreeNode getDestination() {
		return destination; 
	}

	@Override
	public void setOrigin(BCTreeNode origin) {
		this.origin = origin;
		
	}

	@Override
	public void setDestination(BCTreeNode destination) {
		this.destination = destination;
		
	}

	@Override
	public int getWeight() {
		return 0;
	}

	@Override
	public String toString() {
		return "BCTreeEdge [origin=" + origin + ", destination=" + destination
				+ "]";
	}

	@Override
	public void setWeight(int weight) {
		
	}

}
