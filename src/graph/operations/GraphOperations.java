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
}
