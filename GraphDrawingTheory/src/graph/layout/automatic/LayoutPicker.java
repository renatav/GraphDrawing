package graph.layout.automatic;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.layout.LayoutAlgorithms;
import graph.traversal.DFSTreeTraversal;
import graph.tree.binary.BinaryTree;
import graph.trees.DFSTree;

import java.util.List;

import org.apache.log4j.Logger;

public class LayoutPicker<V extends Vertex, E extends Edge<V>> {
	
	private double balloonFactor = 0.8;
	private double hierarchicalFactor = 0.34;
	private double circleCenterFactor = 0.9;
	private Logger log = Logger.getLogger(LayoutPicker.class);

	@SuppressWarnings("unchecked")
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
	 * @param graph
	 * @return Chosen algorithm
	 */
	public LayoutAlgorithms pickAlgorithm(Graph<V,E> graph){
		
		LayoutAlgorithms ret;
		
		log.info("Picking layout for graph: " + graph);
		
		if (graph.getEdges().size() == 0) //if there are no edges, just vertices
			ret = LayoutAlgorithms.BOX;   //for example, packages in a class diagram
		else if (graph.isRing())
			ret = LayoutAlgorithms.CIRCLE;
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
			
			if (leaves.size() >= balloonFactor*graph.getVertices().size())
				ret = LayoutAlgorithms.BALLOON;
			else{
				//level-based approaches produce nice layouts, like we would draw the tree ourselves
				//but they are too wide if the tree is balanced 
				BinaryTree<V,E> binaryTree = new BinaryTree<V,E>(graph);
				if (binaryTree.isCanBeConstructed() && binaryTree.isBalanced())
					ret = LayoutAlgorithms.COMPACT_TREE;
				else
					ret = LayoutAlgorithms.NODE_LINK_TREE;
			}
			
		}
		else if (checkHierachicalTendency(graph))
			ret = LayoutAlgorithms.HIERARCHICAL;
		else if (checkCircularWithCenter(graph)){
			ret = LayoutAlgorithms.CIRCLE_CENTER;
		}
		else //else force-directed
			ret =  LayoutAlgorithms.KAMADA_KAWAI;
		
		log.info("Chosen algorithm: " + ret);
		return ret;
	
	}
	
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
	 * @return true if hierarchical layout is recommended, false otherwise
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
