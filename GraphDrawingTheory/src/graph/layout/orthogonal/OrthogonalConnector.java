package graph.layout.orthogonal;

import java.awt.geom.Point2D;
import java.util.List;

import graph.elements.Vertex;

public class OrthogonalConnector<V extends Vertex> {

	private V vertex;
	private EntryDirection entryDirection;
	private int number;
	/**
	 * If there are multiple edges entering or leaving the vertex
	 * in the same direction (up, down, left, right)
	 * it is important where the connectors are inside the vertex
	 * Also, it is important to know where the edge then goes
	 * in order to avoid intersections of edges
	 */
	private List<Point2D> nodePositions;
	
	
	public OrthogonalConnector(V vertex, EntryDirection entryDirection, int number) {
		super();
		this.vertex = vertex;
		this.entryDirection = entryDirection;
		this.number = number;
	}
	
	public void incNumber(){
		number++;
	}


	/**
	 * @return the vertex
	 */
	public V getVertex() {
		return vertex;
	}


	/**
	 * @param vertex the vertex to set
	 */
	public void setVertex(V vertex) {
		this.vertex = vertex;
	}


	/**
	 * @return the entryDirection
	 */
	public EntryDirection getEntryDirection() {
		return entryDirection;
	}


	/**
	 * @param entryDirection the entryDirection to set
	 */
	public void setEntryDirection(EntryDirection entryDirection) {
		this.entryDirection = entryDirection;
	}


	/**
	 * @return the number
	 */
	public int getNumber() {
		return number;
	}


	/**
	 * @param number the number to set
	 */
	public void setNumber(int number) {
		this.number = number;
	}

	/**
	 * @return the nodePositions
	 */
	public List<Point2D> getNodePositions() {
		return nodePositions;
	}

	/**
	 * @param nodePositions the nodePositions to set
	 */
	public void setNodePositions(List<Point2D> nodePositions) {
		this.nodePositions = nodePositions;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "OrthogonalConnector [vertex=" + vertex + ", entryDirection=" + entryDirection + ", number=" + number
				+ ", nodePositions=" + nodePositions + "]";
	}
	
	
	
	
}
