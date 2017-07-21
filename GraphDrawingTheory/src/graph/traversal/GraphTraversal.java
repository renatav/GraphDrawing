package graph.traversal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import graph.elements.Edge;
import graph.elements.EdgeDirection;
import graph.elements.Graph;
import graph.elements.Path;
import graph.elements.Vertex;

/**
 * Contains methods which implement certain graph traversal algorithms
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type 
 */
public class GraphTraversal {

	/**
	 * Finds all paths in the given graph between two provided vertices using depth-first search
	 * @param graph Graph
	 * @param source Source vertex
	 * @param target Target (destination) vertex
	 * @return All paths in the {@code graph} between {@code source} and {@code target} vertices
	 */
	public static <V extends Vertex,E extends Edge<V>> List<Path<V, E>> findAllPathsDFS(Graph<V,E> graph, V source, V target){
		List<Path<V,E>> paths = new ArrayList<Path<V,E>>();
		findAllPathsDFS(graph, new ArrayList<E>(), new ArrayList<EdgeDirection>(),  paths, source, source, target, null);
		return paths;
	}

	/**
	 * Finds all paths in the given graph between two provided vertices containing a list of vertices using depth-first search
	 * @param graph Graph
	 * @param source Source vertex
	 * @param target Target (destination) vertex
	 * @param containing A list of vertices the path must contain
	 * @return All paths in the {@code graph} between {@code source} and {@code target} vertices containing all of the {@code containing} vertices.
	 */
	public static <V extends Vertex,E extends Edge<V>>List<Path<V,E>> findAllPathsDFSContaining(Graph<V,E> graph, V source, V target, List<V> containing){
		List<Path<V,E>> paths = new ArrayList<Path<V,E>>();
		findAllPathsDFS(graph, new ArrayList<E>(), new ArrayList<EdgeDirection>(),  paths, source, source, target, null);
		Iterator<Path<V,E>> pathsIter = paths.iterator();
		while (pathsIter.hasNext()){
			Path<V,E> path = pathsIter.next();
			for (V v : containing)
				if (!path.getUniqueVertices().contains(v))
					pathsIter.remove();
		}

		return paths;
	}


	/**
	 * Finds all paths in the given graph between two provided vertices not containing any of vertices in the given list using depth-first search
	 * @param graph Graph
	 * @param source Source vertex
	 * @param target Target (destination) vertex
	 * @param excluding A list of vertices the path shouldn't contain
	 * @return All paths in the {@code graph} between {@code source} and {@code target} vertices not containing any of the {@code excluding} vertices.
	 */
	public static <V extends Vertex,E extends Edge<V>> List<Path<V, E>> findAllPathsDFS(Graph<V,E> graph, V source, V target, List<V> excluding){
		List<Path<V,E>> paths = new ArrayList<Path<V,E>>();
		findAllPathsDFS(graph, new ArrayList<E>(), new ArrayList<EdgeDirection>(),  paths, source, source, target, excluding);
		return paths;
	}
	

	private static <V extends Vertex,E extends Edge<V>> void findAllPathsDFS(Graph<V,E> graph,  List<E> visited, List<EdgeDirection> directions, List<Path<V, E>> paths, 
			V currentVertex, V start, V end, List<V> excluding) {        

		if (currentVertex.equals(end)) { 
			if (!(currentVertex.equals(start) && visited.size() == 0)){
				paths.add(new Path<V, E>(visited, directions));
				return;
			}
		}
		List<E> edges;
		if (graph.isDirected())
			edges = graph.outEdges(currentVertex);
		else
			edges = graph.allEdges(currentVertex);

		for (E e : edges) {
			if (visited.contains(e)) {
				continue;
			}
			if (excluding != null){
				if (excluding.contains(e.getOrigin()) || excluding.contains(e.getDestination()))
					continue;
			}
			List<E> temp = new ArrayList<E>();
			List<EdgeDirection> directionsTemp = new ArrayList<EdgeDirection>();
			temp.addAll(visited);
			temp.add(e);
			directionsTemp.addAll(directions);
			V nextVert;
			if (currentVertex == e.getOrigin()){
				nextVert = e.getDestination();
				directionsTemp.add(EdgeDirection.TO_DESTINATION);
			}
			else{
				nextVert = e.getOrigin();
				directionsTemp.add(EdgeDirection.TO_ORIGIN);
			}

			findAllPathsDFS(graph, temp, directionsTemp, paths, nextVert, start, end, excluding);
		}
	}

	/**
	 * A non-recursive implementation of the depth-first search for finding a path between two vertices
	 * More efficient than the recursive implementation
	 * @param graph Graph
	 * @param source Source vertex
	 * @param target Target (destination) vertex
	 * @return A path in {@code graph} between {@code source} and {@code target} vertices
	 */
	public static <V extends Vertex,E extends Edge<V>> Path<V,E> nonrecursiveDFSPath(Graph<V,E> graph, V source, V target){

		List<V> visited = new ArrayList<V>();
		List<E> visitedEdges = new ArrayList<E>();
		
		List<EdgeDirection> directions = new ArrayList<EdgeDirection>();
		Stack<V> stack = new Stack<V>();
		Stack<E> edgesStack = new Stack<E>();
		List<E> pathEdges = new ArrayList<E>();
		List<E> edges;

		stack.push(source);

		V current;
		E currentEdge = null;
		EdgeDirection currentDirection;
		
		while (!stack.empty()){

			current = stack.pop();
			if (!edgesStack.isEmpty())
				currentEdge = edgesStack.pop();
			
			if (visitedEdges.contains(currentEdge)){
				continue;
			}

			if (currentEdge != null){

				visitedEdges.add(currentEdge);
				
				V other = currentEdge.getOrigin() == current ? currentEdge.getDestination() : currentEdge.getOrigin();
				
				if (visited.get(visited.size() - 1) != other){
					for (int i = visited.size() - 1; i > 0; i--){
						V w = visited.get(i);
						if (w == other)
							break;
						if (pathEdges.size() > 0){
							pathEdges.remove(pathEdges.size() - 1);
							directions.remove(directions.size() - 1);
						}
						else
							break;
					}
				}

				pathEdges.add(currentEdge);
				currentDirection = currentEdge.getDestination() == current ? EdgeDirection.TO_DESTINATION : EdgeDirection.TO_ORIGIN;
				directions.add(currentDirection);
			}

			visited.add(current);
			
			if (current == target){
				//make path
				Path<V,E> path = new Path<V,E>(pathEdges, directions);
				return path;
			}

			if (graph.isDirected())
				edges = graph.outEdges(current);
			else
				edges = graph.adjacentEdges(current);
			
			if (edges == null)
				continue;

			for (E e : edges){
				V other = e.getOrigin() == current ? e.getDestination() : e.getOrigin();
				edgesStack.push(e);
				stack.push(other);
			}
		}
		
		return null;
		
	}
	
	

	/**
	 * Finds the longest path in a graph
	 * Method should be rewritten to increase its effectiveness
	 * @return The longest path between any two vertices in a graph
	 */
	//TODO efikasnije napraviti ovo
	public static <V extends Vertex,E extends Edge<V>> Path<V,E> findLongestPath(Graph<V,E> graph){
		Path<V,E> longestPath = null;


		for (V v1 : graph.getVertices())
			for (V v2 : graph.getVertices()){

				if (v1 == v2)
					continue;


				List<Path<V,E>> paths = findAllPathsDFS(graph, v1, v2);
				for (Path<V,E> path : paths)
					if (longestPath == null || path.size() > longestPath.size())
						longestPath = path;
			}
		return longestPath;
	}


}
