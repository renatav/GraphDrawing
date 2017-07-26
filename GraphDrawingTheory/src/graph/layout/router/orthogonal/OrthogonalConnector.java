package graph.layout.router.orthogonal;

import graph.elements.Vertex;
import graph.layout.orthogonal.EntryDirection;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Class represents a point where an edge begins or ends inside a vertex
 * Used to implement the orthogonal layout
 * @author Renata
 * @param <V> The vertex type
 */
public class OrthogonalConnector<V extends Vertex> {

	/**
	 * The vertex containing the connector
	 */
	private V vertex;
	/**
	 * Direction in which edges enter the connector
	 */
	private EntryDirection entryDirection;
	/**
	 * The number of edges containing the connector
	 */
	private int number;
	/**
	 * If there are multiple edges entering or leaving the vertex
	 * in the same direction (up, down, left, right)
	 * it is important where the connectors are inside the vertex
	 * Also, it is important to know where the edge then goes
	 * in order to avoid intersections of edges
	 */
	private List<List<Point2D>> edgesWithNodePositions;
	
	
	/**
	 * Creates a new orthogonal connector belonging to a certain vertex given
	 * the entry direction and current number of edges
	 * @param vertex Vertex containing the connector
	 * @param entryDirection Direction in which edges enter the connector
	 * @param number The number of edges containing the connector
	 */
	public OrthogonalConnector(V vertex, EntryDirection entryDirection, int number) {
		super();
		this.vertex = vertex;
		this.entryDirection = entryDirection;
		this.number = number;
		edgesWithNodePositions = new ArrayList<List<Point2D>>();
	}
	
	/**
	 * Number of edges using containing the connector
	 */
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
	 * @return the entry direction
	 */
	public EntryDirection getEntryDirection() {
		return entryDirection;
	}


	/**
	 * @param entryDirection the entry direction to set
	 */
	public void setEntryDirection(EntryDirection entryDirection) {
		this.entryDirection = entryDirection;
	}

	/**
	 * @return the number of edges
	 */
	public int getNumber() {
		return number;
	}


	/**
	 * @param number the number of edges to set
	 */
	public void setNumber(int number) {
		this.number = number;
	}

	/**
	 * @return A list of edges with their node positions
	 */
	public List<List<Point2D>> getEdgesWithNodePositions() {
		return edgesWithNodePositions;
	}

	/**
	 * @param nodePositions Positions of nodes of the new edge
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
