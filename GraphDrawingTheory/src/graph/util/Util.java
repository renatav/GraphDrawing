package graph.util;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A class with some util methods
 * @author Renata
 */
public class Util {

	
	/**
	 * Creates a copy of a graph
	 * @param graph Graph to be copied
	 * @param <V> The vertex type
	 * @param <E> The edge type
	 * @return Copy of {@code graph}
	 */
	@SuppressWarnings("unchecked")
	public static <V extends Vertex, E extends Edge<V>> Graph<V,E> copyGraph(Graph<V,E> graph){
		Graph<V,E> copy = new Graph<V,E>();
		for (V v : graph.getVertices())
			copy.addVertex(v);
		for (E e : graph.getEdges())
			copy.addEdge(e);
		return copy;
	}

	/**
	 * Reverses the given list
	 * @param list List to be reversed
	 * @param <T> Type od list's elements
	 */
	public static <T> void reverseList(List<T> list){
		List<T> reverse = new ArrayList<T>();
		for (int i = list.size() - 1; i >= 0; i--)
			reverse.add(list.get(i));
		list.clear();
		list.addAll(reverse);
	}

	
	/**
	 * Creates a graph edge between two vertices of the given class
	 * @param origin The first vertex
	 * @param destination The second vertex
	 * @param edgeClass Class of the edge
	 * @param <V> The vertex type
	 * @param <E> The edge type
	 * @return Edge between {@code origin} and {@code destination}
	 */
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


	/**
	 * Creates a vertex of the given class
	 * @param vertexClass Vertex class
	 * @param <V> The vertex type
	 * @return New vertex
	 */
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
	 * @param numberOfVertices Number of vertices the graph will have
	 * @param vertexClass Vertex class
	 * @param numberOfEdges Number of edges the graph will have
	 * @param edgesClass Edge class
	 * @param <V> The vertex type
	 * @param <E> The edge type
	 * @return Created graph
	 */
	public static <V extends Vertex, E extends Edge<V>> List<?>[] generateRandomGraph(int numberOfVertices, 
			Class<?> vertexClass, int numberOfEdges, Class<?> edgesClass){
		
		List<V> vertices = new ArrayList<V>();
		List<E> edges = new ArrayList<E>();
		
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
	
	/**
	 * Inserts new lines into a string, after a certain number of characters and right after a specified sequence of characters
	 * @param input Original string
	 * @param after Sequence of characters after which a new line can be placed
	 * @param numberOfChars A number of characters after which a new line can be placed
	 * @return Input with added new lines
	 */
	public static String addNewLines(String input, String after, int numberOfChars){
		String ret = "";
		String splitRegexp = after;
		if (after.contains(")"))
			splitRegexp = after.replace(")", "\\)");
		int num = 1;
		String[] split = input.split(splitRegexp);
		String str;
		for (int i = 0; i < split.length; i++){
			str = split[i];
			ret += str;
			if (i < split.length - 1)
				ret += after;
			num += str.length();
			if (num >= numberOfChars){
				ret += "\n";
				num = 1;
			}
		}
		return ret;
	}
	
	public static String replaceSquareBrackets(String input){
		return input.replace("[", "(").replace("]", ")");
	}
	
	public static String removeSquareBrackets(String input){
		return input.replace("[", "").replace("]", "");
	}

}
