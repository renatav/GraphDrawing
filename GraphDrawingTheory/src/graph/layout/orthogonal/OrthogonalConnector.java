package graph.layout.orthogonal;

import graph.elements.Vertex;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

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
	private List<List<Point2D>> edgesWithNodePositions;
	
	
	public OrthogonalConnector(V vertex, EntryDirection entryDirection, int number) {
		super();
		this.vertex = vertex;
		this.entryDirection = entryDirection;
		this.number = number;
		edgesWithNodePositions = new ArrayList<List<Point2D>>();
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
	public List<List<Point2D>> getEdgesWithNodePositions() {
		return edgesWithNodePositions;
	}

	/**
	 * @param nodePositions the nodePositions to set
	 */
	public void addEdge(List<Point2D> nodePositions) {
		edgesWithNodePositions.add(nodePositions);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "OrthogonalConnector [vertex=" + vertex + ", entryDirection=" + entryDirection + ", number=" + number + "]";
	}
	
	
	
	
}
