package graph.traversal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


		List<E> ret = new ArrayList<E>();
		Map<V, Integer> indexesMap = new HashMap<V, Integer>();
		Map<Integer, V> inverseIndexesMap = new HashMap<Integer, V>();
		Map<E, V> otherEdges = new HashMap<E, V>();
		List<E> testList = new ArrayList<E>();

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
			testList.addAll(firstVertexEdges);
			
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


		E currentEdge = adj.get(v1).get(0);
		firstVertexEdges.remove(0);
		ret.add(currentEdge);

		for (E e : firstVertexEdges)
			otherEdges.put(e, v1);

		boolean found = false;
		int currentIndex = 1;

		while (!found){

			V current = indexesMap.containsKey(currentEdge.getOrigin()) ? currentEdge.getDestination() : currentEdge.getOrigin();

			//could happen if there is just one edge
			if (current == v2)
				break;

			indexesMap.put(current, currentIndex);
			inverseIndexesMap.put(currentIndex, current);

			if (debug){
				System.out.println("Current edge " + currentEdge);
				System.out.println("Current vertex " + current);
				System.out.println("Current other edges: "  + otherEdges);
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
			for (E e : adj.get(current)){

				if (ret.contains(e))
					continue;

				V w =  e.getOrigin() == current ? e.getDestination() : e.getOrigin();

				if (debug)
					System.out.println("Current adjacent edge: " + e);

				if (indexesMap.containsKey(w)){ //w has lower index

					int wIndex = indexesMap.get(w);
					int vIndex = currentIndex;

					//check if this edge ruins the planarity of the current embedding
					//is there an edge whose one vertex is between the two vertices of this edge
					//and whose other one is not

					for (E checkEdge : otherEdges.keySet()){
						V lower = otherEdges.get(checkEdge);
						int lowerIndex = indexesMap.get(lower);
						V higher = checkEdge.getOrigin() == lower ? checkEdge.getDestination() : checkEdge.getDestination();


						//one vertex is between the two vertices joined by the edge
						//the other has not been found yet - it cannot be between the two
						if (indexesMap.get(higher) == null && lowerIndex > wIndex && lowerIndex > vIndex){
							conflict = true;
							break;
						}

						if (indexesMap.get(higher) != null){
							int higherIndex = indexesMap.get(higher);
							if (higherIndex > wIndex && lowerIndex < wIndex){
								conflict = true;
								if (debug)
									System.out.println("conflict with edge: " + checkEdge);
								break;
							}
						}
					}

					if (conflict)
						break;
				}
				else{
					//add it to the map of edges to check later for conflicts
					//make note of which of the vertices joined by the edge
					//has lower index
					otherEdges.put(e, current);
					edgesToTry.add(e);
				}
			} //end for

			if (edgesToTry.size() == 0 && currentIndex == 0)
				return null;

			if (conflict || edgesToTry.size() == 0){
				//the current embedding is not OK
				E lastEdge = ret.get(ret.size() - 1);

				//remove the last entry from everything
				//see where to start the next path

				int index = currentIndex - 1;
				E nextEdge = null;

				if (debug)
					System.out.println("finding next edge to try after conflict");

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
							//add next edge to otherEdges - used to check for conflicts
							//add last edge back to the edges to check for conflicts - otherEdges
							otherEdges.put(lastEdge, v);
							foundNext = true;
							break;
						}
					}

					if (!foundNext){
						//one vertex backwards

						nextEdge = null;
						indexesMap.remove(v);
						inverseIndexesMap.remove(index);

						for (E adjEdgeOfRemoved : adj.get(v))
							if (otherEdges.get(adjEdgeOfRemoved) == v)
								otherEdges.remove(adjEdgeOfRemoved);
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
				otherEdges.remove(currentEdge);

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
						testList.addAll(edgesToTry);
						for (int i = 0; i < edgesToTry.size(); i++){
							E edge = firstVertexEdges.get(i);
							if (excludingEdges != null && excludingEdges.contains(edge))
								continue;
							V other = edge.getOrigin() == v1 ? edge.getDestination() : edge.getOrigin();
							if (excluding.contains(other))
								continue;
							testList.add(edge);
						}
						edgesToTry.clear();
						edgesToTry.addAll(testList);
					}


					currentEdge = edgesToTry.get(0);
					edgesToTry.remove(0);
					otherEdges.remove(currentEdge);
					edgesToTryForVertex.put(current, edgesToTry);
					ret.add(currentEdge);
					currentIndex++;
				}
			}
		}
		return ret;
	}

}
