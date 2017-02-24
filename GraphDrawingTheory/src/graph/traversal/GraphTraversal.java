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
 * @author xxx
 *
 * @param <V>
 * @param <E>
 */
public class GraphTraversal {


	/**
	 * Depth-First Search
	 * The depth-first-search algorithm is similar to the standard algorithm for traversing binary trees; 
	 * it first fully explores one subtree before returning to the current node and then exploring the other subtree.
	 * Another way to think of depth-first-search is by saying that it is similar to breadth-first search except that it uses a stack instead of a queue.
	 * @param first
	 * @param target
	 * @return
	 */
	public static <V extends Vertex,E extends Edge<V>> List<Path<V, E>> findAllPathsDFS(Graph<V,E> graph, V first, V target){
		List<Path<V,E>> paths = new ArrayList<Path<V,E>>();
		findAllPathsDFS(graph, new ArrayList<E>(), new ArrayList<EdgeDirection>(),  paths, first, first, target, null);
		return paths;
	}

	/**
	 * Finds all paths between first and target which contains all specified vertices
	 * @param first
	 * @param target
	 * @param containing
	 * @return
	 */
	public static <V extends Vertex,E extends Edge<V>>List<Path<V,E>> findAllPathsDFSContaining(Graph<V,E> graph, V first, V target, List<V> containing){
		List<Path<V,E>> paths = new ArrayList<Path<V,E>>();
		findAllPathsDFS(graph, new ArrayList<E>(), new ArrayList<EdgeDirection>(),  paths, first, first, target, null);
		Iterator<Path<V,E>> pathsIter = paths.iterator();
		while (pathsIter.hasNext()){
			Path<V,E> path = pathsIter.next();
			for (V v : containing)
				if (!path.getUniqueVertices().contains(v))
					pathsIter.remove();
		}

		return paths;
	}



	public static <V extends Vertex,E extends Edge<V>> List<Path<V, E>> findAllPathsDFS(Graph<V,E> graph, V first, V target, List<V> excluding){
		List<Path<V,E>> paths = new ArrayList<Path<V,E>>();
		findAllPathsDFS(graph, new ArrayList<E>(), new ArrayList<EdgeDirection>(),  paths, first, first, target, excluding);
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

	public static <V extends Vertex,E extends Edge<V>> Path<V,E> nonrecursiveDFSPath(Graph<V,E> graph, V start, V end){

		List<V> visited = new ArrayList<V>();
		List<E> visitedEdges = new ArrayList<E>();
		
		List<EdgeDirection> directions = new ArrayList<EdgeDirection>();
		Stack<V> stack = new Stack<V>();
		Stack<E> edgesStack = new Stack<E>();
		List<E> pathEdges = new ArrayList<E>();
		List<E> edges;

		stack.push(start);

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
			
			if (current == end){
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
	 * @return
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
