package graph.traversal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import graph.elements.Edge;
import graph.elements.Vertex;

/**
 * Contains methods for finding path between vertices which meet certain
 * special conditions. Used as part of more complex algorithms.
 */
public class TraversalUtil{


	/**
	 * Finds a path from one vertex to another such that the there are no back edges
	 * and no edge crossings. In other words, the path can be a part of the outer
	 * facial cycle of a planar embedding of a graph.
	 * @param v1 Start vertex
	 * @param v2 Destination vertex
	 * @param adj Adjacency map
	 * @param debug Indicator if debug information should be shown
	 * @param excluding Vertices that cannot be of the path
	 * @param excludingEdges Edges that cannot be of the path
	 * @param <V> The vertex type
	 * @param <E> The edge type
	 * @return Path from v1 to v2 with such that it is a part of the outer facial cycle
	 * of a planar embedding of a graph.
	 */
	public static <V extends Vertex,E extends Edge<V>> List<E> circularNoCrossingsPath(V v1, V v2, Map<V,List<E>> adj, boolean debug, List<V> excluding, List<E> excludingEdges){
		//The basic idea is to follow a path, keeping a record of which edges joining one vertex of the path to some other ones
		//which are not on the path. Test if some of them overlap. If they do, go back and try some other path
		//"back edge" here is called an edge connecting a vertex of the path to some other vertex of the path
		//while it itself is not on the path.
		//If there are two such edges where one starts "above" the start of the other one and ends "above" the end
		//of the other one - not planar
		//If a vertex between two vertices of a "back edge" is connected to a vertex not on the path - not planar
		
		if (debug){
			System.out.println("Excluding vertices: " + excluding);
			System.out.println("Excluding edges: " + excludingEdges);
			System.out.println("from " + v1 + " to " + v2);
		}

		if (excluding != null){
			excluding.remove(v1);
			excluding.remove(v2);
		}

		List<E> ret = new ArrayList<E>();

		Map<V, Integer> indexesMap = new HashMap<V, Integer>();
		Map<Integer, V> inverseIndexesMap = new HashMap<Integer, V>();
		List<E> testList = new ArrayList<E>();

		Map<V, List<List<V>>> connectedOnThePath = new HashMap<V, List<List<V>>>();

		Map<V, List<E>> edgesToTryForVertex = new HashMap<V, List<E>>();

		//important to prevent operations on one map
		//from changing certain lists
		List<E> firstVertexEdges = new ArrayList<E>();
		firstVertexEdges.addAll(adj.get(v1));
		edgesToTryForVertex.put(v1, firstVertexEdges);
		indexesMap.put(v1, 0);
		inverseIndexesMap.put(0,v1);


		//ignore edges in excludingEdges list
		//and those where start or destination is a member of excluding vertices

		if (excluding != null && excluding.size() > 0){
			testList.clear();

			for (int i = 0; i < firstVertexEdges.size(); i++){
				E edge = firstVertexEdges.get(i);
				if (excludingEdges != null && excludingEdges.contains(edge))
					continue;
				V other = edge.getOrigin() == v1 ? edge.getDestination() : edge.getOrigin();
				if (excluding.contains(other))
					continue;
				testList.add(edge);
			}
			firstVertexEdges.clear();
			firstVertexEdges.addAll(testList);
		}

		E currentEdge = firstVertexEdges.get(0);
		firstVertexEdges.remove(0);
		ret.add(currentEdge);

		V firstVertex = currentEdge.getOrigin() == v1 ? currentEdge.getDestination() : currentEdge.getOrigin();

		boolean found = firstVertex == v2;

		int currentIndex = 1;

		while (!found){

			V current = indexesMap.containsKey(currentEdge.getOrigin()) ? currentEdge.getDestination() : currentEdge.getOrigin();

			indexesMap.put(current, currentIndex);
			inverseIndexesMap.put(currentIndex, current);

			if (debug){
				System.out.println("Current edge " + currentEdge);
				System.out.println("Current vertex " + current);
				System.out.println("Current path: "+ ret);
				System.out.println("Edges to try: " + edgesToTryForVertex);
			}


			List<E> edgesToTry;
			if (edgesToTryForVertex.containsKey(current)){
				edgesToTry = edgesToTryForVertex.get(current);
				edgesToTry.clear();
			}
			else{
				edgesToTry = new ArrayList<E>();
				edgesToTryForVertex.put(current, edgesToTry);
			}

			boolean conflict = false;

			//these paths could later contain edges and vertices that are placed on the path
			//so that should be checked
			List<List<V>> connectedPathVertices = connectedWith(current, indexesMap.keySet(), ret, adj, excludingEdges, excluding, debug);

			if (connectedPathVertices.size() > 0){
				//the keys will be vertices which have higher indexes
				connectedOnThePath.put(current,  connectedPathVertices);


				//check for conflicts
				int higher = currentIndex;

				for (List<V> connectedPath : connectedPathVertices){
					
					//connected path is "fresh"
					//just calculated
					//so there is no way that there are some path edges and vertices on it
					
					V last = connectedPath.get(connectedPath.size() - 1);
					int lower = indexesMap.get(last);
					//vertices between the two connected
					for (int index = higher - 1; index > lower; index--){
						V vertexBetween = inverseIndexesMap.get(index);

						//check if vertex between is connected to some vertex belonging to the excluded list
						if (excluding != null && excluding.size() > 0)
							if (pathBetween(vertexBetween, excluding, adj, ret, indexesMap.keySet())){
								conflict = true;
								break;
							}

						if (connectedOnThePath.containsKey(vertexBetween))
							for (List<V> otherPath : connectedOnThePath.get(vertexBetween)){
								//other path might not be "fresh"
								//it could contain so edges that were added to the path
								//after it was calculated
								//so, check that
								//it wouldn't be optimal to find the paths again
								//just check these for obsolete ones
								V otherEnd = otherPath.get(otherPath.size() - 1);
								int otherEndIndex = indexesMap.get(otherEnd);
								if (otherEndIndex < lower){
									//check if the path between the vertices
									//calculated in some earlier step
									//contains a vertex that is now on the path
									
									if (debug)
										System.out.println("Possible conflict with path " + vertexBetween + " " + otherEnd);

									//there still might not be a conflict
									//if there is a vertex that the paths have in common
									//than they are not in conflict
									//try to find such vertex
									//there also be more paths between the two vertices
									//so while some might not cause the conflict, others might

									//TODO ovo treba jos proveriti i osmisliti

									boolean containsSame = false;
									boolean obsoletePath = false;
									for (int i = 1; i < connectedPath.size() - 1; i++){
										if (indexesMap.containsKey(connectedPath.get(i))){
											obsoletePath = true;
											break;
										}
										if (otherPath.contains(connectedPath.get(i))){
											containsSame = true;
										}
									}

									if (obsoletePath)
										continue;
									
									conflict = !containsSame;
									if (conflict)
										break;
								}
							}
						if (conflict)
							break;
					}
					if (conflict)
						break;
				}
			}


			for (E e : adj.get(current)){

				if (ret.contains(e))
					continue;

				V w = e.getOrigin() == current ? e.getDestination() : e.getOrigin();

				if (!indexesMap.containsKey(w))
					edgesToTry.add(e);
			} 

			if (edgesToTry.size() == 0 && currentIndex == 0)
				return null;

			if (conflict || (edgesToTry.size() == 0 && current != v2)){
				//the current embedding is not OK
				E lastEdge = ret.get(ret.size() - 1);

				//remove the last entry from everything
				//see where to start the next path

				int index = currentIndex - 1;
				E nextEdge = null;

				if (debug)
					System.out.println("finding next edge to try after conflict");

				indexesMap.remove(current);
				inverseIndexesMap.remove(currentIndex);
				connectedOnThePath.remove(current);

				boolean foundNext = false;
				while(nextEdge == null){

					V v = inverseIndexesMap.get(index);
					if (debug)
						System.out.println("current v " + v + " of index " + index);

					lastEdge = ret.get(ret.size() - 1);
					ret.remove(ret.size() - 1);


					List<E> nextEdges = edgesToTryForVertex.get(v);

					while (nextEdges.size() > 0){
						nextEdge = nextEdges.get(0);
						if (ret.contains(nextEdge) || nextEdge == lastEdge){
							nextEdges.remove(0);
						}
						else{
							foundNext = true;
							break;
						}
					}

					if (!foundNext){
						//one vertex backwards

						nextEdge = null;
						indexesMap.remove(v);
						inverseIndexesMap.remove(index);
						connectedOnThePath.remove(v);

						index--;
						if (index < 0)
							break;
					}
				}

				currentIndex = index + 1;
				if (debug)
					System.out.println("next edge " + nextEdge);
				currentEdge = nextEdge;
				ret.add(currentEdge);

			}
			else{
				//check if the ending vertex was reached
				//at this point, all checks were performed for this embedding
				//so it it was reached, that's it
				if (current == v2)
					found = true;
				else{

					//the edges which are connecting the vertex to another vertex already 
					//on the path should not be considered as the next ones
					//pick the first one as the next, add others to to try category

					if (excluding != null && excluding.size() > 0){
						testList.clear();
						for (int i = 0; i < edgesToTry.size(); i++){
							E edge = edgesToTry.get(i);
							if (excludingEdges != null && excludingEdges.contains(edge))
								continue;
							V other = edge.getOrigin() == current ? edge.getDestination() : edge.getOrigin();
							if (excluding.contains(other))
								continue;
							testList.add(edge);
						}
						edgesToTry.clear();
						edgesToTry.addAll(testList);
					}


					currentEdge = edgesToTry.get(0);
					edgesToTry.remove(0);
					edgesToTryForVertex.put(current, edgesToTry);
					ret.add(currentEdge);
					currentIndex++;
				}
			}
		}
		return ret;
	}


   /**
    * Initializes needed objects in order to initialize the recursive method of the same name
    *  * @param v Current vertex
	 * @param startVertex Start vertex
	 * @param pathVertices Vertices for which it is checked if there is a path from the start vertex to them
	 * @param pathEdges Edges of the cyclic path - should not be traversed
	 * @param adjacency Adjacency lists
	 * @param debug
	 * @param excludingEdges Edges that should not be traversed
	 * @param excludingVertices Vertices that should be skipped
    * @return
    */
	private static<V extends Vertex, E extends Edge<V>> List<List<V>> connectedWith (V v, Set<V> pathVertices, List<E> pathEdges, 
			Map<V,List<E>> adjacency,List<E> excludingEdges, List<V> excludingVertices, boolean debug){

		List<V> visited = new ArrayList<V>();
		visited.add(v);
		List<E> visitedEdges = new ArrayList<E>();
		List<List<V>> paths = new ArrayList<List<V>>();
		connectedWith(v, v, pathVertices, pathEdges, adjacency, excludingEdges, excludingVertices, visited, visitedEdges, paths, debug);
		if (debug){
			System.out.println("Paths with other path vertices: " + paths);
		}
		return paths;
	}

	/**
	 * Finds paths from the given start vertex to those belonging to a specified path.
	 * Uses DFS. The result is stored in parameter {@code part} 
	 * @param v Current vertex
	 * @param startVertex Start vertex
	 * @param pathVertices Vertices for which it is checked if there is a path from the start vertex to them
	 * @param pathEdges Edges of the cyclic path - should not be traversed
	 * @param adjacency Adjacency lists
	 * @param debug
	 * @param excludingEdges Edges that should not be traversed
	 * @param excludingVertices Vertices that should be skipped
	 * @param paths List of path containing the given vertices
	 */
	private static<V extends Vertex, E extends Edge<V>> void connectedWith (V v, V startVertex, Set<V> pathVertices, List<E> pathEdges, 
			Map<V,List<E>> adjacency, List<E> excludingEdges, List<V> excludingVertices, List<V> visited, List<E> visitedEdges, List<List<V>> paths,
			boolean debug){

		for (E edge : adjacency.get(v)){

			if (excludingEdges != null && excludingEdges.contains(edge))
				continue;
			if (visitedEdges.contains(edge))
				continue;
			if (pathEdges.contains(edge))
				continue;

			visitedEdges.add(edge);

			V other = edge.getOrigin() == v ? edge.getDestination() : edge.getOrigin();
			if (excludingVertices != null && excludingVertices.contains(other))
				continue;

			if (visited.contains(other))
				continue;
			
			if (other != startVertex && pathVertices.contains(other)){
			//	if (debug)
				//	System.out.println("found path vertex: " + other);

				//save path
				List<V> currentPath = new ArrayList<V>();
				currentPath.addAll(visited);
				currentPath.add(other);
				paths.add(currentPath);
				//don't continue dfs
			}
			else{
				visited.add(other);
				connectedWith(other, startVertex, pathVertices, pathEdges, adjacency, excludingEdges, excludingVertices, visited, visitedEdges, paths, debug);
				visited.remove(visited.size() - 1);
			}
		}
	}

	/**
	 * Checks if there is a path between a vertex and any of the vertices belonging to a list of vertices
	 * @param v
	 * @param endVertices
	 * @param adjacency
	 * @param excludingEdges
	 * @param excludingVertices
	 * @return
	 */
	private static<V extends Vertex, E extends Edge<V>> boolean pathBetween(V v, List<V> endVertices, Map<V,List<E>> adjacency, List<E> excludingEdges, Set<V> excludingVertices){

		List<V> visited = new ArrayList<V>();
		List<E> visitedEdges = new ArrayList<E>();
		Stack<V> stack = new Stack<V>();
		stack.push(v);
		V current;
		while (!stack.isEmpty()){
			current = stack.pop();
			if (visited.contains(current))
				continue;
			visited.add(current);

			for (E edge : adjacency.get(current)){

				if (excludingEdges != null && excludingEdges.contains(edge))
					continue;
				if (visitedEdges.contains(edge))
					continue;

				visitedEdges.add(edge);

				V other = edge.getOrigin() == current ? edge.getDestination() : edge.getOrigin();
				if (excludingVertices != null && excludingVertices.contains(other))
					continue;

				if (visited.contains(other))
					continue;

				if (endVertices.contains(other))
					return true;

				stack.push(other);	
			}
		}
		return false;
	}

}
