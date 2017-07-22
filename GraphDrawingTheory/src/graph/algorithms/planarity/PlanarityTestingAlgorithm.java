package graph.algorithms.planarity;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

/**
 * A class which should be extended by all planarity testing implementations
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type 
 */
public abstract class PlanarityTestingAlgorithm<V extends Vertex, E extends Edge<V>>  {

	/**
	 * Determines if the graph is planar
	 * @param graph Graph whose planarity is being checked
	 * @return {@code true} if graph is planar, otherwise {@code false}
	 */
	public abstract boolean isPlannar(Graph<V,E> graph);

}
