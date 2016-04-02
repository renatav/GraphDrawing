package graph.layout.automatic;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.layout.LayoutAlgorithms;

import java.util.List;

public class LayoutPicker<V extends Vertex, E extends Edge<V>> {

	@SuppressWarnings("unchecked")
	public LayoutAlgorithms pickAlgorithm(List<V> vertices, List<E> edges){
		Graph<V,E> graph = new Graph<V,E>();
		for (V v : vertices)
			graph.addVertex(v);
		for (E e : edges)
			graph.addEdge(e);
		return pickAlgorithm(graph);
	}
	
	public LayoutAlgorithms pickAlgorithm(Graph<V,E> graph){
		//check graph properties
		if (graph.isTree())
			return LayoutAlgorithms.TREE;
		else
			return LayoutAlgorithms.KAMADA_KAWAI;
	}
}
