package graph.traversal;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Path;
import graph.elements.Vertex;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Contains methods which implement certain graph traversal algorithms
 * @author xxx
 *
 * @param <V>
 * @param <E>
 */
public class GraphTraversal<V extends Vertex,E extends Edge<V>> {

	private Graph<V,E> graph;

	public GraphTraversal(Graph<V,E> graph){
		this.graph = graph;
	}

	/**
	 * Depth-First Search
	 * The depth-first-search algorithm is similar to the standard algorithm for traversing binary trees; 
	 * it first fully explores one subtree before returning to the current node and then exploring the other subtree.
	 * Another way to think of depth-first-search is by saying that it is similar to breadth-first search except that it uses a stack instead of a queue.
	 * @param visited
	 * @param paths
	 * @param currentVertex
	 * @param end
	 */
	public void findAllPathsDFS(List<E> visited, List<Path<V, E>> paths, V currentVertex, V end) {        
		if (currentVertex.equals(end)) { 
			paths.add(new Path<V, E>(visited));
			return;
		}
		else {
			LinkedList<E> edges;
			if (graph.isDirected())
				edges = graph.outEdges(currentVertex);
			else
				edges = graph.allEdges(currentVertex);

			for (E e : edges) {
				if (visited.contains(e)) {
					continue;
				} 
				List<E> temp = new ArrayList<E>();
				temp.addAll(visited);
				temp.add(e);
				V nextVert = currentVertex == e.getOrigin() ? e.getDestination() : e.getOrigin();

				findAllPathsDFS(temp, paths, nextVert, end);
			}
		}
	}
	
	public Path<V,E> getShortestPath(V source, V target){
		Path<V,E> ret = null;
		List<Path<V, E>> paths = new ArrayList<Path<V, E>>();
		findAllPathsDFS(new ArrayList<E>(), paths, source, target);
		for (Path<V,E> path : paths)
			if (ret == null || path.size() < ret.size())
				ret = path;
		return ret;
	}



}
