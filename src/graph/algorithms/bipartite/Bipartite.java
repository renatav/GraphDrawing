package graph.algorithms.bipartite;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bipartite graph (or bigraph) is a graph whose vertices can be divided into two disjoint sets U and V 
 *(that is, U and V are each independent sets) such that every edge connects a vertex in U to one in V.
 * Vertex set U and V are often denoted as partite sets.
 * @author xxx
 *
 * @param <V>
 * @param <E>
 */
public class Bipartite<V extends Vertex,E extends Edge<V>> {

	private Graph<V,E> graph;
	private Map<V, Boolean> colour; //only two colours are needed
	private List<V> marked;
	private boolean isBipartite;

	/**
	 * Determines whether an undirected graph is bipartite and finds either a
	 * bipartition or an odd-length cycle.
	 * @param Graph graph
	 */
	public Bipartite(Graph<V,E> graph) {

		this.graph = graph;
		colour = new HashMap<V, Boolean>();
		marked = new ArrayList<V>();
	}

	public boolean isBipartite(){
		
		isBipartite = true;
		colour.clear();
		marked.clear();
		for (V v : graph.getVertices())
			if (!marked.contains(v)){
				dfs(v);
				if (isBipartite == false)
					return false;
			}
		return true;
	}

	private void dfs(V v) {
		
		if (colour.get(v) == null)
			colour.put(v, true);
		marked.add(v);
		for (E e: graph.allEdges(v)){
			V other = e.getOrigin() == v ? e.getDestination() : e.getOrigin();

			if (!marked.contains(other)){
				//adjacement vertices of the current one should have the other colour
				colour.put(other, !colour.get(v));
				dfs(other);
			}
			//if colours of the current vertex and one of its adjacenet ones mathc => contradiction
			//graph isn't bipartite
			else if (colour.get(v) == colour.get(other)) 
				isBipartite = false;

		}
	}
}

