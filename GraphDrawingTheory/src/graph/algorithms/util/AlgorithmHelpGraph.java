package graph.algorithms.util;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

import java.util.ArrayList;
import java.util.List;

/**
 * A class meant to help implementation of algorithms which involve removal and
 * addition of edges Since the class <code>Graph</code> uses generics, a new
 * edge cannot be constructed without knowing the class and constructor of the
 * actual Edge The idea is not make the usage of the library as easy as
 * possible, so that the users don't have to add any additional code to their
 * classes Also, making several copies of a graph would be quite inefficient
 * Here, we only save the information regarded which edges and vertices were
 * removed and which were added
 * 
 * @author xx
 * @param <V>
 * @param <E>
 */

public class AlgorithmHelpGraph<V extends Vertex, E extends Edge<V>> {

	private Graph<V, E> graph;

	private List<E> removedEdges;
	private List<GraphEdge<V>> addedEdges;
	private List<V> removedVertices;
	private List<V> addedVertices;

	public AlgorithmHelpGraph(Graph<V, E> graph) {
		this.graph = graph;
		removedEdges = new ArrayList<E>();
		addedEdges = new ArrayList<GraphEdge<V>>();
		removedVertices = new ArrayList<V>();
		addedVertices = new ArrayList<V>();
	}

}
