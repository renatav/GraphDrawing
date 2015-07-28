package graph.elements;

import java.util.Comparator;

public class VertexDegreeComparator<V extends Vertex, E extends Edge<V>> implements Comparator<V>{

	private Graph<V, E> graph;
	
	public VertexDegreeComparator(Graph<V,E> graph){
		this.graph = graph;
	}
	
	@Override
	public int compare(V o1, V o2) {
		int degree1 = graph.vertexDegree(o1);
		int degree2 = graph.vertexDegree(o2);
		if (degree1 > degree2)
			return 1;
		else if (degree1 == degree2)
			return 0;
		return -1;
	}

}
