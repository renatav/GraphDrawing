package graph.util;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

	@SuppressWarnings("unchecked")
	public static <V extends Vertex, E extends Edge<V>> E createEdge(V origin, V destination, Class<?> edgeClass){
		try {
			E e = (E) edgeClass.newInstance();
			e.setDestination(destination);
			e.setOrigin(origin);
			return e;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <V extends Vertex> V createVertex(Class<?> vertexClass){
		try{
			V v = (V) vertexClass.newInstance();
			return v;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Generates a random graph
	 * @param numberOfVertices
	 * @param vertexClass C
	 * @param numberOfEdges
	 * @param edgesClass
	 * @return Created graph
	 */
	public static <V extends Vertex, E extends Edge<V>> List<?>[] generateRandomGraph(int numberOfVertices, 
			Class<?> vertexClass, int numberOfEdges, Class<?> edgesClass){
		
		List<V> vertices = new ArrayList<V>();
		List<E> edges = new ArrayList<E>();
		String content;
		
		for (int i = 0; i < numberOfVertices; i++){
			V v = createVertex(vertexClass);
			vertices.add(v);
		}
		
		Random rn = new Random();
		int originIndex, destinationIndex;
		
		for (int i = 0; i < numberOfEdges; i++){
			//randomly select origin and destination
			originIndex = rn.nextInt(numberOfVertices);
			destinationIndex = rn.nextInt(numberOfVertices);
			
			E e = createEdge(vertices.get(originIndex), vertices.get(destinationIndex), edgesClass);
			edges.add(e);
		}
		
		List<?>[] ret = new ArrayList[2];
		ret[0] = vertices;
		ret[1] = edges;
		return ret;
		
	}

}
