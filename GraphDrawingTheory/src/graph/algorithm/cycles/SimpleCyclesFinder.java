package graph.algorithm.cycles;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

import java.util.List;

/**
 * Finds all simple cycles of a given graph.
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type
 */
public class SimpleCyclesFinder<V extends Vertex, E extends Edge<V>> {
	
	public SimpleCyclesFinder(){
		
	}
	
	/**
	 * Finds cycle basis of the given graph. Calls the appropriate
	 * algorithm, depending on if the graph is directed or not. 
	 * @param graph Graph whose simple cycle basis is to be found
	 * @return A list of all cycles belonging to the cycle basis,
	 * where a cycle is represented by a list of vertices.
	 */
	public List<List<V>> findCycles(Graph<V,E> graph){
		
		if (graph.isDirected())
			return new JohnsonSimpleCycles<V,E>(graph).findSimpleCycles();
		else
			return new PatonSimpleCycles<V,E>(graph).findSimpleCycles();
		
	}

}
