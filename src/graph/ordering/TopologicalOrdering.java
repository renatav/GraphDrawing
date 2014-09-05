package graph.ordering;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.util.Util;

import java.util.List;

/**
 * Topological ordering of a directed graph is a linear ordering of its vertices such 
 * that for every directed edge uv from vertex u to vertex v, u comes before v in the ordering.
 * @author xxx
 *
 * @param <V>
 * @param <E>
 */
public class TopologicalOrdering<V extends Vertex,E extends Edge<V>> implements Ordering<V,E> {

	/**
	 * Should be a directed acyclic graph (DAG)
	 */
	private Graph<V,E> graph;

	public TopologicalOrdering(Graph<V,E> graph){
		this.graph = graph;



	}

	@SuppressWarnings("unchecked")
	@Override
	public Graph<V,E> order() {


		/*
		 * Remove sinks and  and their edges from the graph, 
		 *and repeat the process  until there are no vertices left.
		 */

		if (!canOrder())
			return null;

		Graph<V,E> copy = Util.copyGraph(graph);
		Graph<V,E> ordered = new Graph<V,E>();

		List<V> sinkVertices = copy.getAllSinks();;
		do{
			for (V sink : sinkVertices){
				for (E e : copy.inEdges(sink))
					copy.removeEdge(e);
				copy.removeVertex(sink);
				ordered.addVertexBeginning(sink);
			}
			sinkVertices = copy.getAllSinks();

		}
		while(sinkVertices.size() != 0);

		for (E e : graph.getEdges())
			ordered.addEdge(e);


		return ordered;
	}


	@Override
	public boolean canOrder() {
		if (!graph.isDirected())
			return false;
		return !graph.isCyclic();
	}



	@Override
	public Graph<V, E> getGraph() {
		return graph;
	}




}
