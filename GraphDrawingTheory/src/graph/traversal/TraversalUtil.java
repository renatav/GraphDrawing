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
 * Contains methods for finding path between vertices with certain characteristics 
 * needed in certain algorithms
 */
public class TraversalUtil{


	/**
	 * Finds a path from one vertex to another such that the there are no back edges
	 * and no edge crossings
	 * @param v1 Start vertex
	 * @param v2 Destination vertex
	 * @param adj Adjacency map
	 * @param debug Indicator if debug information should be shown
	 * @return Path from v1 to v2
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

		Map<V, List<V>> connectedOnThePath = new HashMap<V, List<V>>();

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


		System.out.println("first vertex edges: " + firstVertexEdges);

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

			List<V> connectedPathVertices = connectedWith(current, indexesMap.keySet(), ret, adj, debug, excludingEdges, excluding);
			System.out.println("Connected path vertices: " + connectedPathVertices);

			if (connectedPathVertices.size() > 0){
				//the keys will be vertices which have higher indexes
				connectedOnThePath.put(current,  connectedPathVertices);
				//check for conflicts

				int higher = currentIndex;

				for (V connectedVertex : connectedPathVertices){
					int lower = indexesMap.get(connectedVertex);
					//vertices between the two connected
					for (int index = higher - 1; index > lower; index--){
						V vertexBetween = inverseIndexesMap.get(index);
						if (connectedOnThePath.containsKey(vertexBetween))
							for (V otherEnd : connectedOnThePath.get(vertexBetween)){
								int otherEndIndex = indexesMap.get(otherEnd);
								if (otherEndIndex < lower){
									if (debug)
										System.out.println("Possible conflict with path " + vertexBetween + " " + otherEnd);
									
									//there still might not be a conflict
									//if there is a vertex that the paths have in common
									//than they are not in conflict
									//try to find such vertex
									//there also be more paths between the two vertices
									//so while some might not cause the conflict, others might
									
									conflict = true;
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

				System.out.println("INDEX MAP" + indexesMap);
				System.out.println("PATH: " + ret);

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

	private static<V extends Vertex, E extends Edge<V>> List<V> connectedWith (V v, Set<V> pathVertices, List<E> pathEdges, 
			Map<V,List<E>> adjacency, boolean debug, List<E> excludingEdges, List<V> excludingVertices){

//		if (debug){
//			System.out.println("finding path vertices to which the current vertex is connected");
//			System.out.println("start vertex: " + v);
//			System.out.println("path vertices " + pathVertices);
//			System.out.println("path edges " + pathEdges);
//		}

		List<V> visited = new ArrayList<V>();
		List<E> visitedEdges = new ArrayList<E>();
		List<V> ret = new ArrayList<V>();
		Stack<V> stack = new Stack<V>();
		stack.push(v);
		V current;
		List<List<E>> paths = new ArrayList<List<E>>();



		while (!stack.isEmpty()){
			current = stack.pop();
			//System.out.println("current " + current);
			if (visited.contains(current))
				continue;
			visited.add(current);
			for (E edge : adjacency.get(current)){
				
				if (excludingEdges != null && excludingEdges.contains(edge))
					continue;
				if (visitedEdges.contains(edge))
					continue;
				if (pathEdges.contains(edge))
					continue;
				
				visitedEdges.add(edge);

				V other = edge.getOrigin() == current ? edge.getDestination() : edge.getOrigin();
				if (excludingVertices != null && excludingVertices.contains(other))
					continue;
				
				
				System.out.println("edge: " + edge);
				
				if (other != v && pathVertices.contains(other) && !ret.contains(other)){
					if (debug)
						System.out.println("found path vertex: " + other);
					ret.add(other); 
					//don't continue dfs
				}
				else{
					stack.push(other);	
				}
			}
		}

		return ret;
	}

}
