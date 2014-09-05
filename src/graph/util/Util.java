package graph.util;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

import java.util.ArrayList;
import java.util.List;

public class Util {

	@SuppressWarnings("unchecked")
	public static <V extends Vertex, E extends Edge<V>> Graph<V,E> copyGraph(Graph<V,E> graph){
		Graph<V,E> copy = new Graph<V,E>();
		for (V v : graph.getVertices())
			copy.addVertex(v);
		for (E e : graph.getEdges())
			copy.addEdge(e);
		return copy;
	}

	public static <T> void reverseList(List<T> list){
		List<T> reverse = new ArrayList<T>();
		for (int i = list.size() - 1; i >= 0; i--)
			reverse.add(list.get(i));
		list.clear();
		list.addAll(reverse);
	}

}
