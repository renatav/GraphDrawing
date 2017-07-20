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
	
	public SplitComponent(SplitPair<V, E> splitPair, Graph<V, E> graph) {
		super();
		this.splitPair = splitPair;
		this.graph = graph;
	}

	public SplitPair<V, E> getSplitPair() {
		return splitPair;
	}

	public void setSplitPair(SplitPair<V, E> splitPair) {
		this.splitPair = splitPair;
	}

	public Graph<V, E> getGraph() {
		return graph;
	}

	public void setGraph(Graph<V, E> graph) {
		this.graph = graph;
	}
	
	
	
	

}
