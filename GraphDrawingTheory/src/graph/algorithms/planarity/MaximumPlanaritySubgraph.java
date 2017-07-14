package graph.algorithms.planarity;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.traversal.DFSTreeTraversal;
import graph.trees.DFSTree;

import java.util.ArrayList;
import java.util.List;

/**
 * Finds maximum planar subgraph
 * A widely used standard heuristic for finding a maximal planar subgraph is to start with
 * a spanning tree of G, and to iteratively try to add the remaining edges one by one
 * In every step, a planarity testing algorithm is called for the obtained graph.
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type 
 */
public class MaximumPlanaritySubgraph<V extends Vertex, E extends Edge<V>> {
	
	/**
	 * Graph whose subgraph is being found
	 */
	private Graph<V,E> graph;
	/**
	 * A planarity testing algorithm
	 */
	private PlanarityTestingAlgorithm<V, E> planarityTest;
	/**
	 * Found planar subgraph
	 */
	private Graph<V,E> planarSubgraph;
	private List<E> remainingEdges;
	
	public MaximumPlanaritySubgraph(Graph<V,E> graph){
		this.graph = graph;
		planarityTest = new FraysseixMendezPlanarity<V,E>();
		remainingEdges = new ArrayList<E>();
		planarSubgraph = calculateMaximumPlanarityGraph();
	}
	
	
	@SuppressWarnings("unchecked")
	public Graph<V,E> calculateMaximumPlanarityGraph(){
		DFSTree<V, E> tree = new DFSTreeTraversal<V,E>(graph).formDFSTree();
		Graph<V,E> testGraph = new Graph<>(tree.getVertices(), tree.getTreeEdges());
		
		for (E e : tree.getBackEdges()){
			testGraph.addEdge(e);
			if (!planarityTest.isPlannar(testGraph)){
				testGraph.removeEdge(e);
				remainingEdges.add(e);
			}
		}
		
		return testGraph;
	}

	public Graph<V, E> getPlanarSubgraph() {
		return planarSubgraph;
	}

	
}
