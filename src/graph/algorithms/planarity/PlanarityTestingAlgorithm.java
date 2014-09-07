package graph.algorithms.planarity;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.exception.AlgorithmCannotBeAppliedException;
import graph.traversal.GraphTraversal;

public abstract class PlanarityTestingAlgorithm<V extends Vertex, E extends Edge<V>> {


	protected Graph<V,E> graph;
	protected GraphTraversal<V, E> traversal;

	/**
	 * planar graph is a graph that can be embedded in the plane, i.e., 
	 * it can be drawn on the plane in such a way that its edges intersect only at their endpoints. 
	 * In other words, it can be drawn in such a way that no edges cross each other.
	 * @return true if graph is planar, otherwise false
	 */
	public PlanarityTestingAlgorithm(Graph<V,E> graph){
		this.graph = graph;
		traversal = new GraphTraversal<>(graph);

	}
	
	public abstract boolean isPlannar() throws AlgorithmCannotBeAppliedException;

}
