package graph.traversal;

import graph.elements.Edge;
import graph.elements.EdgeDirection;
import graph.elements.Graph;
import graph.elements.Path;
import graph.elements.Vertex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * An implementation of Dijkstra's algorithm for finding a path between
 * two graph vertices in a graph
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type 
 */
public class DijkstraAlgorithm<V extends Vertex, E extends Edge<V>> {

	private List<E> edges;
	private Set<V> settledNodes;
	private Set<V> unSettledNodes;
	private Map<V, V> predecessors;
	private Map<V, Integer> distance;
	private Map<V,E> predEdges;
	private boolean directed;
	

	/**
	 * Initialized the algorithm's parameters based on the properties of the graph
	 * @param graph Graph being traversed
	 */
	public DijkstraAlgorithm(Graph<V,E> graph) {

		//copy edges
		this.edges = new ArrayList<E>(graph.getEdges());
		this.directed = graph.isDirected();
	}
	
	/**
	 * Initializes the algorithm's parameters based on the given values
	 * @param edges Edges to be traverse
	 * @param directed Is the graph directed
	 */
	public DijkstraAlgorithm(List<E> edges, boolean directed) {
		this.edges = new ArrayList<E>(edges);
		this.directed = directed;
	}
	
	/**
	 * Sets default value of the directed parameter and leaves the edge empty 
	 */
	public DijkstraAlgorithm(){
		this.directed = false;
	}
	

	/**
	 * Finds a path in the given graph between two vertices if it exists.
	 * @param source Source vertex
	 * @param target Target (destination) vertex
	 * @return A path from {@code source} to {@code target} if it exists, otherwise null
	 */
	public Path<V,E> getPath(V source, V target){
		return getPath(source, target, null);
	}
	

	/**
	 * Finds a path in the given graph between two vertices which doesn't contain
	 * certain specified vertices, if one exists.
	 * @param source Source vertex
	 * @param target Target (destination) vertex
	 * @param excluding Vertices that cannot be on the path
	 * @return A path from {@code source} to {@code target} not containing {@code excluding}
	 * if it exists, otherwise null
	 */
	public Path<V,E> getPath(V source, V target, List<V> excluding) {
		execute(source, excluding);
		List<E> path = new ArrayList<E>();
		List<EdgeDirection> directions = new ArrayList<EdgeDirection>();
		V step = target;
		// check if a path exists
		if (predecessors.get(step) == null) 
			return null;

		E edge;
		while (predecessors.get(step) != null) {
			edge = predEdges.get(step);
			path.add(edge);
			if (edge.getOrigin() == step)
				directions.add(EdgeDirection.TO_ORIGIN); //changed this
			else
				directions.add(EdgeDirection.TO_DESTINATION);
			step = predecessors.get(step);
		}
		// Put it into the correct order
		
		if (path.size() == 0)
			return null;
		
		Collections.reverse(path);
		Collections.reverse(directions);
		
		return new Path<V,E>(path, directions);
	}
	

	private void execute(V source, List<V> excluding) {

		settledNodes = new HashSet<V>();
		unSettledNodes = new HashSet<V>();
		distance = new HashMap<V, Integer>();
		predecessors = new HashMap<V, V>();
		predEdges = new HashMap<V,E>();
		distance.put(source, 0);

		unSettledNodes.add(source);
		while (unSettledNodes.size() > 0) {
			V node = getMinimum(unSettledNodes);
			settledNodes.add(node);
			unSettledNodes.remove(node);
			findMinimalDistances(node, excluding);
		}
	}

	private void findMinimalDistances(V node, List<V> excluding) {
		List<V> adjacentNodes = getNeighbors(node);
		E minEdge;
		for (V target : adjacentNodes) {
			if (excluding != null && excluding.contains(target))
				continue;
			minEdge = getMinDistance(node, target);
			if (getShortestDistance(target) > getShortestDistance(node)
					+ minEdge.getWeight())
				distance.put(target, getShortestDistance(node)
						+ minEdge.getWeight());
			predecessors.put(target, node);
			predEdges.put(target, minEdge);

			unSettledNodes.add(target);
		}
	}


	private E getMinDistance(V node, V target) {
		E minEdge = null;
		Integer min = null;
		for (E edge : edges) {
			if (edge.getOrigin().equals(node)
					&& edge.getDestination().equals(target)) {
				if (min == null || edge.getWeight() < min){
					min = edge.getWeight();
					minEdge = edge;
				}
			}
			if (!directed){
				if (edge.getDestination().equals(node)
						&& edge.getOrigin().equals(target)) {
					if (min == null || edge.getWeight() < min){
						min = edge.getWeight();
						minEdge = edge;
					}
				}
			}
		}
		return minEdge;
	}

	private List<V> getNeighbors(V node) {
		List<V> neighbors = new ArrayList<V>();
		for (E edge : edges) {
			if (edge.getOrigin().equals(node)
					&& !isSettled(edge.getDestination())) {
				neighbors.add(edge.getDestination());
				continue;
			}
			if (!directed){
				if (edge.getDestination().equals(node)
						&& !isSettled(edge.getOrigin())) {
					neighbors.add(edge.getOrigin());
				}
			}
		}
		return neighbors;
	}

	private V getMinimum(Set<V> vertices) {
		V minimum = null;
		for (V vertex : vertices) {
			if (minimum == null) {
				minimum = vertex;
			} else {
				if (getShortestDistance(vertex) < getShortestDistance(minimum)) {
					minimum = vertex;
				}
			}
		}
		return minimum;
	}

	private boolean isSettled(V vertex) {
		return settledNodes.contains(vertex);
	}

	private int getShortestDistance(V destination) {
		Integer d = distance.get(destination);
		if (d == null) {
			return Integer.MAX_VALUE;
		} else {
			return d;
		}
	}

	/**
	 * @param edges Edges to set
	 */
	public void setEdges(List<E> edges) {
		this.edges = edges;
	}

	/**
	 * @param directed Indicator if the graph is directed to set
	 */
	public void setDirected(boolean directed) {
		this.directed = directed;
	}


} 