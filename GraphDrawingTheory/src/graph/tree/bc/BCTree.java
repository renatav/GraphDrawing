package graph.tree.bc;

import java.util.ArrayList;
import java.util.List;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;


/**
 * Block-Cut vertex tree
 * Let B be the set of blocks and C be the set of cut vertices of a separable graph G. Construct a
 * graph H with vertex set B U C in which adjacencies are defined as follows: 
 * ci in C is adjacent to bj in B if and only if the block bj of G contains the cut vertex ci of G 
 * @assume graph is separable
 */
public class BCTree<V extends Vertex, E extends Edge<V>> extends Graph<BCTreeNode, BCTreeEdge>{

	private Graph<V,E> graph;
	private List<BCTreeNode> cVertices;
	private List<BCTreeNode> bVertices;
	private BCTreeNode root;
	
	//pendant is a block which contains only one cut vertex
	private List<Graph<V,E>> pendants = new ArrayList<Graph<V,E>>();

	public BCTree(Graph<V,E> graph){
		this.graph = graph;
		cVertices = new ArrayList<BCTreeNode>();
		bVertices = new ArrayList<BCTreeNode>();
		constructTree();

	}
	
	@Override
	public void addVertex(BCTreeNode node){
		super.addVertex(node);
		if (node.getType() == VertexType.C)
			cVertices.add(node);
		if (node.getType() == VertexType.B)
			bVertices.add(node);
	}
	
	@Override
	public void removeVertex(BCTreeNode node){
		super.removeVertex(node);
		if (node.getType() == VertexType.C)
			cVertices.remove(node);
		if (node.getType() == VertexType.B)
			bVertices.remove(node);
	}

	private void constructTree(){
		
		List<V> cutVertices = graph.listCutVertices();
		List<Graph<V,E>> blocks = graph.listBiconnectedComponents();
		
		for (V cutVertex : cutVertices){
			BCTreeNode node = new BCTreeNode( VertexType.C, cutVertex);
			addVertex(node);
			if (root == null)
				root = node;
		}

		for (Graph<V,E> block : blocks){
			BCTreeNode node  = new BCTreeNode(VertexType.B, block);
			addVertex(node);
			int containedCutVerticesCount = 0;
			for (V cutVertex : cutVertices){
				if (block.hasVertex(cutVertex)){
					addEdge(new BCTreeEdge(getVertexByContent(cutVertex),getVertexByContent(block)));
					containedCutVerticesCount++;
				}
			}
			if (containedCutVerticesCount == 1)
				pendants.add(block);
		}
		
		setParents(root, new ArrayList<BCTreeNode>());
	}
	
	private void setParents(BCTreeNode currentNode, List<BCTreeNode> processed){
		processed.add(currentNode);
		for (BCTreeEdge edge : adjacentEdges(currentNode)){
			BCTreeNode other = edge.getOrigin() == currentNode ? edge.getDestination() : edge.getOrigin();
			if (processed.contains(other))
				continue;
			other.setParent(currentNode);
			setParents(other, processed);
		}
	}
	
	/**
	 * @return the cVertices
	 */
	public List<BCTreeNode> getcVertices() {
		return cVertices;
	}

	/**
	 * @param cVertices the cVertices to set
	 */
	public void setcVertices(List<BCTreeNode> cVertices) {
		this.cVertices = cVertices;
	}

	/**
	 * @return the bVertices
	 */
	public List<BCTreeNode> getbVertices() {
		return bVertices;
	}

	/**
	 * @param bVertices the bVertices to set
	 */
	public void setbVertices(List<BCTreeNode> bVertices) {
		this.bVertices = bVertices;
	}

	/**
	 * @return the pendants
	 */
	public List<Graph<V, E>> getPendants() {
		return pendants;
	}

	/**
	 * @param pendants the pendants to set
	 */
	public void setPendants(List<Graph<V, E>> pendants) {
		this.pendants = pendants;
	}

	/**
	 * @return the root
	 */
	public BCTreeNode getRoot() {
		return root;
	}

	/**
	 * @param root the root to set
	 */
	public void setRoot(BCTreeNode root) {
		this.root = root;
	}

}
