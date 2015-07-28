package graph.ordering.circular;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Path;
import graph.elements.Vertex;
import graph.elements.VertexDegreeComparator;
import graph.traversal.GraphTraversal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

public class Circular<V extends Vertex, E extends Edge<V>> {

	private Graph<V,E> graph;
	private Logger log = Logger.getLogger(Circular.class);

	public Circular(Graph<V,E> graph){
		this.graph = graph;
	}

	public List<V> circularOrdering(){

		/*
		 * Define a wave front node to be adjacent to the last node processed; see Figure 9.3.
		A wave center node is adjacent to some other node that has already been processed.
		 */

		GraphCopy<V,E> copyGraph = new GraphCopy<V,E>(graph.getVertices(), graph.getEdges());

		List<V> waveFrontNodes = new ArrayList<V>();
		List<V> waveCenterNodes = new ArrayList<V>();
		List<V> processedNodes = new ArrayList<V>();
		List<E> removalList = new ArrayList<E>();

		//step one - sort the nodes by ascending degree
		List<V> nodes = new ArrayList<V>();
		nodes.addAll(graph.getVertices());
		Collections.sort(nodes, new VertexDegreeComparator<V,E>(graph));


		//step two set counter to 1
		int counter = 1;

		//step three - while counter <= n-3
		int n = nodes.size();

		V currentNode = null;

		while (counter <= n-3){

			//find current node

			if (currentNode == null)
				currentNode = nodes.get(0);
			else{

				int lowestDegree = copyGraph.vertexDegree(nodes.get(0));

				int currentDegree = lowestDegree;
				V waveCenterNode = null, waveFrontNode = null;
				V testNode = nodes.get(0);
				int index = 0;
				
				while (currentDegree == lowestDegree){

					if (waveFrontNodes.contains(testNode)){
						waveFrontNode = testNode;
						break;
					}

					//currentNode will be set to this if no wave front node has the lowest degree
					if (waveCenterNodes.contains(testNode))
						waveCenterNode = testNode;

					testNode = nodes.get(++ index);
					currentDegree = copyGraph.vertexDegree(testNode);

				}

				//step 4 - If a wave front node u has lowest degree, then currentNode = u.
				if (waveFrontNode!= null)
					currentNode = waveFrontNode;
				//step 5 - Else If a wave center node v has lowest degree, then currentNode = v.
				else if (waveCenterNode != null)
					currentNode = waveCenterNode;
				//Else set currentNode to be some node with lowest degree
				else 
					currentNode = nodes.get(0);
			}

			log.info("processing node " + currentNode);
			processedNodes.add(currentNode);
			
			log.info("setting wave front and center nodes");
			
			//Define a wave front node to be adjacent to the last node processed
			//A wave center node is adjacent to some other node that has already been processed.
			waveFrontNodes.clear();
			List<V> currentAdjacent = graph.adjacentVertices(currentNode); 
			waveFrontNodes.addAll(currentAdjacent);
			waveCenterNodes.addAll(currentAdjacent);
			
			

			
			//step 7 - Visit the adjacent nodes consecutively
			List<V> adjacentVertices = copyGraph.adjacentVerticesWithTriangulated(currentNode);
			for (int i = 0; i < adjacentVertices.size() - 1; i++){

				//for each two nodes:
				V v1 = adjacentVertices.get(i);
				V v2 = adjacentVertices.get(i+1);

				//step 8 - If a pair edge exists place the edge into removalList.
				
				if (copyGraph.adjacentVertices(v1).contains(v2)){
					//removalList.add(copyGraph.edgeesBetweenWithTriangulated(v1, v2).get(0));
					//original graph can't contain triangulated edges, so no need to remove them afterwards
					E e = copyGraph.edgeBetween(v1, v2);
					if (e == null){
						System.out.println("ima u ajdacent, nema edge");
						System.out.println(v1 + " " + v2);
					}
					
					removalList.add(copyGraph.edgeBetween(v1, v2));
				}
				//step 9 - Else place a triangulation edge between the current pair
				//of neighbors and also into removalList.
				else if (!copyGraph.adjacentVerticesWithTriangulated(v1).contains(v2)){
					//no need to create multiple triangulated edges
					 copyGraph.addTriangulatedEdge(v1, v2);
				}
			}
			

			
			//step 10 - Update the location of currentNode’s neighbors in T
			nodes.clear();
			nodes.addAll(copyGraph.getVertices());
			Collections.sort(nodes, new VertexDegreeComparator<V,E>(copyGraph));

			

			//step 11 - Remove currentNode and incident edges from G
			nodes.remove(currentNode);
			copyGraph.removeVertex(currentNode);

			for (Edge<V> e : copyGraph.allAdjacentEdgesWithTriangulated(currentNode))
				copyGraph.removeEdgeWithTriangulated(e);

			//step 12 - Increment counter by 1
			counter++;

		}
		
		
		//step 13 - Restore G to its original topology.
		//create a copy of the original graph, leave the real one intact
		
		Graph<V,E> originalGraphCopy = new Graph<V,E>(graph.getVertices(), graph.getEdges());
		for (E e : removalList){
			log.info("Removing edge " + e.getOrigin() + " - " + e.getDestination());
			originalGraphCopy.removeEdge(e);
		}
		
		
		//step 14 - Perform a DFS (or a longest path heuristic) on G (copy in this case)
		GraphTraversal<V, E> traversal = new GraphTraversal<>(originalGraphCopy);
		Path<V,E> longestPath = traversal.findLongestPath();
		
		List<V> embeddingOrder = longestPath.pathVertivesWithoutDuplicates();
		System.out.println(embeddingOrder);
		
		/*step 17 - If there are any nodes that have not been placed, then place the remaining nodes
		into the embedding order with the following priority:
			(i) between two neighbors, (ii) next to one neighbor, (iii) next to
			zero neighbors.*/
		
			
		if (embeddingOrder.size() < graph.getVertices().size()){
			
			for (V v : graph.getVertices()){
				
				if (embeddingOrder.contains(v))
					continue;
				
				//else find where to insert
				int insertPositionNextToANeighbour = -1;
				boolean inserted = false;
				
				for (int i = 0; i < embeddingOrder.size() - 1; i++){
					V v1 = embeddingOrder.get(i);
					V v2 = embeddingOrder.get(i+1);
					
					E e1 = graph.edgeBetween(v, v1);
					E e2 = graph.edgeBetween(v, v2);
					
					if (e1 != null && e2 == null)
						insertPositionNextToANeighbour = i;
					else if (e1 == null && e2 != null)
						insertPositionNextToANeighbour = i + 1;
					else {
						embeddingOrder.add(i, v);
						inserted = true;
						break;
					}
				}
				if (!inserted){
					if (insertPositionNextToANeighbour != -1)
						embeddingOrder.add(insertPositionNextToANeighbour, v);
					else
						embeddingOrder.add(v);
				}
			}
		}
		
		return embeddingOrder;

	}



}
