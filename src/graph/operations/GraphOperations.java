package graph.operations;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

import java.util.ArrayList;
import java.util.List;

public class GraphOperations<V extends Vertex, E extends Edge<V>> {


	public List<V> verticesInCommon(Graph<V,E> graph1, Graph<V,E> graph2){

		List<V> ret = new ArrayList<V>();
		for (V v : graph1.getVertices())
			if (graph2.getVertices().contains(v))
				ret.add(v);
		return ret;
	}


	public List<E> edgesInCommon(Graph<V,E> graph1, Graph<V,E> graph2){

		List<E> ret = new ArrayList<E>();
		for (E e : graph1.getEdges())
			if (graph2.getEdges().contains(e))
				ret.add(e);
		return ret;
	}

	@SuppressWarnings("unchecked")
	public Graph<V,E> union(List<Graph<V,E>> graphs){
		Graph<V,E> ret = new Graph<V,E>();
		for (Graph<V,E> graph : graphs){
			for (V v : graph.getVertices())
				if (!ret.getVertices().contains(v))
					ret.addVertex(v); 

			for (E e : graph.getEdges())
				if (!ret.getEdges().contains(e))
					ret.addEdge(e);
		}

		return ret;
	}
	
	public boolean isSubgraph(Graph<V,E> supergraph, Graph<V,E> subgraph){
		for (V v : subgraph.getVertices())
			if (!supergraph.getVertices().contains(v))
				return false;
		
		for (E e : subgraph.getEdges())
			if (!supergraph.getEdges().contains(e))
				return false;
		
		return true;
	}
	
	/**
	 * H is a proper subgraph of G, if V(H)!=V(G) || E(H)!=E(G)
	 * @param supergraph
	 * @param subgraph
	 * @return
	 */
	public boolean isProperSubgraph(Graph<V,E> supergraph, Graph<V,E> subgraph){
		
		if (!isSubgraph(supergraph, subgraph))
			return false;
		
		if (supergraph.getVertices().size() != subgraph.getVertices().size() || 
				supergraph.getEdges().size() != subgraph.getEdges().size())
			return true;
		return false; 
	}
	
	public Graph<V,E> removeEdgeFromGraph(Graph<V,E> graph, E edge){
		Graph<V,E> ret = new Graph<V,E>(graph.getVertices(), graph.getEdges());
		ret.removeEdge(edge);
		return ret;
	}
	
}
