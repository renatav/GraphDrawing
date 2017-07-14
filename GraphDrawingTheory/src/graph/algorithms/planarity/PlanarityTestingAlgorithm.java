package graph.algorithms.planarity;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

/**
 * A class which should be extented by all planarity testing implementations
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type 
 */
public abstract class PlanarityTestingAlgorithm<V extends Vertex, E extends Edge<V>>  {

	/**
	 * @param graph Graph whose planarity is being checked
	 * @return true if graph is planar, otherwise false
	 */
	public abstract boolean isPlannar(Graph<V,E> graph);

}
