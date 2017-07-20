package graph.properties.components;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

/**
 * Class represent a block of a graph
 * A block is a maximal biconnected subgraph of a given graph 
 * @return {@code true} if graph is connected, @{code false} otherwise
 */
public class Block<V extends Vertex, E extends Edge<V>> extends Graph<V,E>{

	/**
	 * Original block
	 */
	private Graph<V,E> graph;
	
	/**
	 * Block's cut vertex
	 */
	private V cutVertex;

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

	public V getCutVertex() {
		return cutVertex;
	}

	public void setCutVertex(V cutVertex) {
		this.cutVertex = cutVertex;
	}
	
	

}
