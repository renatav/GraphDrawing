package graph.layout.automatic;

import java.util.List;

import org.apache.log4j.Logger;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.layout.LayoutAlgorithms;
import graph.traversal.DFSTreeTraversal;
import graph.trees.dfs.DFSTree;

/**
 * Class used to select an algorithm automatically based on properties
 * of the graph
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type 
 */
public class LayoutPicker<V extends Vertex, E extends Edge<V>> {
	
	/**
	 * Used to determine if balloon layouter should be used
	 */
	private double balloonFactor = 0.8;
	/**
	 * Used to determine if hierarchical layouter should be used 
	 */
	private double hierarchicalFactor = 0.25;
	/**
	 * Used to determine if circle layouter with a vertex in center should be used
	 */
	private double circleCenterFactor = 0.9;
	private Logger log = Logger.getLogger(LayoutPicker.class);

	@SuppressWarnings("unchecked")
	/**
	 * Picks the appropriate algorithm based on the properties of the graph.
	 * Graph is created based on the given lists of vertices and edges
	 * @param vertices Graph's vertices
	 * @param edges Graph's edges
	 * @return The chosen algorithm
	 */
	public LayoutAlgorithms pickAlgorithm(List<V> vertices, List<E> edges){
		Graph<V,E> graph = new Graph<V,E>();
		for (V v : vertices)
			graph.addVertex(v);
		for (E e : edges)
			graph.addEdge(e);
		return pickAlgorithm(graph);
	}
	
	/**
	 * Picks the appropriate algorithm based on the properties of the graph
	 * @param graph The graph
	 * @return The chosen algorithm
	 */
	public LayoutAlgorithms pickAlgorithm(Graph<V,E> graph){
		
		//TODO add detection based on planarity and symmetry when more 
		//layout algorithms are implemented 
		
		LayoutAlgorithms ret;
		
		log.info("Picking layout for graph: " + graph);
		
		if (graph.getEdges().size() == 0 || graph.getVertices().size() < 3) //if there are no edges, just vertices
			ret = LayoutAlgorithms.BOX;   //for example, packages in a class diagram
		else if (graph.getVertices().size() >= 1000) //big graph, use efficient algorithm; ISOM seems to be the fastest one
			ret = LayoutAlgorithms.ISOM;
		else if (graph.isRing())
			ret = LayoutAlgorithms.CIRCLE;
		else if (checkCircularWithCenter(graph))
			ret = LayoutAlgorithms.CIRCLE_CENTER;
		else if (graph.isTree()){
			
			log.info("Graph is a tree. Finding the best tree algorithm");
			//see which of the tree algorithms would be the best choice
			
			//Balloon layout produces a circular "balloon-tree" layout of a tree. 
			//This layout places children nodes radially around their parent
			//if there are a lot of leaf nodes 
			//and not too many nodes that are not connected to some leaf nodes
			//this algorithm seem like a good choice
			
			//it's not really that important if the root has only one edge
			List<V> leaves = graph.getTreeLeaves(null);
			System.out.println(leaves);
			
			//TODO the problem with the balloon is that clusters should have different radiuses
			//implementation of a clustered circle algorithm would fix all
			if (leaves.size() >= balloonFactor*graph.getVertices().size())
				ret = LayoutAlgorithms.BALLOON;
//			else{
//				//level-based approaches produce nice layouts, like we would draw the tree ourselves
//				//but they are too wide if the tree is balanced 
//				BinaryTree<V,E> binaryTree = new BinaryTree<V,E>(graph);
//				if (binaryTree.isCanBeConstructed() && binaryTree.isBalanced())
//					ret = LayoutAlgorithms.COMPACT_TREE; //this doesn't really help 
			//node link tree is the best algorithm
			else
				ret = LayoutAlgorithms.NODE_LINK_TREE;
			
			
		}
		else if (checkHierachicalTendency(graph))
			ret = LayoutAlgorithms.HIERARCHICAL;
		else //else force-directed
			ret =  LayoutAlgorithms.KAMADA_KAWAI;
		
		log.info("Chosen algorithm: " + ret);
		return ret;
	
	}
	
	/**
	 * Methods determines if the circle layout with a vertex in center
	 * would be suitable for the graph
	 * @param graph
	 * @return {@code true} if hierarchical layout is recommended, {@code false} otherwise
	 */
	private boolean checkCircularWithCenter(Graph<V, E> graph) {
		//is there a vertex connected to most of the other vertices
		//if there is, place it in the center, the others around it in a circle
		int numOfVertices = graph.getVertices().size();
		for (V v : graph.getVertices()){
			if (graph.adjacentVertices(v).size() >= circleCenterFactor * (numOfVertices - 1)) //minus that vertex
				return true;
		}
		return false;
	}

	/**
	 * Methods determines if the hierarchical layout would be suitable for the graph
	 * @param graph
	 * @return {@code true} if hierarchical layout is recommended, {@code false} otherwise
	 */
	private boolean checkHierachicalTendency(Graph<V, E> graph) {
		
		//Create a tree, see how many back edges there are
		DFSTreeTraversal<V,E> dfsTraversal = new DFSTreeTraversal<V,E>(graph);
		DFSTree<V, E> dfsTree = dfsTraversal.formDFSTree();
		List<E> graphEdges = graph.getEdges();
		List<E> backEdges = dfsTree.getBackEdges();
		//if back edges are at most 1/3 (or hierarchicalFactor) of all edges, use hierarchical layout
		if ((double)backEdges.size() / (double)graphEdges.size() <= hierarchicalFactor)
			return true;
		
		return false;
	}
}
