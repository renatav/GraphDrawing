package graph.properties.splitting;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

/**
 * A block is a maximal biconnected subgraph of a given graph 
 * @author xxx
 *
 * @param <V>
 * @param <E>
 */
public class Block<V extends Vertex, E extends Edge<V>> extends Graph<V,E>{

	/**
	 * Original block
	 */
	private Graph<V,E> graph;

	public Block(Graph<V, E> graph) {
		super();
		this.graph = graph;
	}

	public Graph<V, E> getGraph() {
		return graph;
	}

	public void setGraph(Graph<V, E> graph) {
		this.graph = graph;
	}
	
	

}
