package graph.elements;

import java.util.Comparator;

/**
 * Class used for sorting vertices based on their degree
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type 
 */
public class VertexDegreeComparator<V extends Vertex, E extends Edge<V>> implements Comparator<V>{

	private Graph<V, E> graph;
	
	public VertexDegreeComparator(Graph<V,E> graph){
		this.graph = graph;
	}
	
	@Override
	/**
	 * Compares two vertices based on their degree
	 * @param o1 The first vertex
	 * @param o2 The second vertex
	 * @return 1 if degree(o1) > degree(o2), 0 if the degrees are the same,
	 * -1 if degree(o1) < degree(o2)
	 * 
	 */
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
