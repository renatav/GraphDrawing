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
 * Algorithm for finding a path between two graph vertices in a graph
 * @author xxx
 *
 * @param <V>
 * @param <E>
 */

public class DijkstraAlgorithm<V extends Vertex, E extends Edge<V>> {

	private final List<E> edges;
	private Set<V> settledNodes;
	private Set<V> unSettledNodes;
	private Map<V, V> predecessors;
	private Map<V, Integer> distance;
	private Graph<V,E> graph;
	private Map<V,E> predEdges;

	public DijkstraAlgorithm(Graph<V,E> graph) {

		//copy edges
		this.graph = graph;
		this.edges = new ArrayList<E>(graph.getEdges());
	}

	/**
	 * Returns path from source to target if one exists, otherwise null
	 * @param target
	 * @return shortest path if one exists, otherwise null
	 */
	public Path<V,E> getPath(V source, V target) {
		execute(source);
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
				directions.add(EdgeDirection.TO_DESTINATION);
			else
				directions.add(EdgeDirection.TO_ORIGIN);
			step = predecessors.get(step);
		}
		// Put it into the correct order
		Collections.reverse(path);
		Collections.reverse(directions);
		return new Path<V,E>(path, directions);
	}

	private void execute(V source) {

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
			findMinimalDistances(node);
		}
	}

	private void findMinimalDistances(V node) {
		List<V> adjacentNodes = getNeighbors(node);
		E minEdge;
		for (V target : adjacentNodes) {
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
			if (!graph.isDirected()){
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
			if (!graph.isDirected()){
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


} 