package graph.properties.components;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

/**
 * Class represents a split component of a graph
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type 
 */
public class SplitComponent<V extends Vertex, E extends Edge<V>> extends Graph<V,E>{
	
	/**
	 * Splitting based on the pair
	 */
	private SplitPair<V,E> splitPair;
	/**
	 * Graph which was split
	 */
	private Graph<V,E> graph;
	
	/**
	 * Constructs a split component of the provided split pair and graph
	 * @param splitPair Split pair
	 * @param graph Graph
	 */
	public SplitComponent(SplitPair<V, E> splitPair, Graph<V, E> graph) {
		super();
		this.splitPair = splitPair;
		this.graph = graph;
	}

	/**
	 * @return The split pair
	 */
	public SplitPair<V, E> getSplitPair() {
		return splitPair;
	}

	/**
	 * @param splitPair The split pair to set
	 */
	public void setSplitPair(SplitPair<V, E> splitPair) {
		this.splitPair = splitPair;
	}

	/**
	 * @return The graph
	 */
	public Graph<V, E> getGraph() {
		return graph;
	}

	/**
	 * @param graph The graph to set
	 */
	public void setGraph(Graph<V, E> graph) {
		this.graph = graph;
	}

}
