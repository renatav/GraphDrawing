package graph.tree.bc;

import java.util.ArrayList;
import java.util.List;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;


/**
 * Class represents a block-cut vertex tree and contains methods for its construction given
 * a separable graph
 * Let B be the set of blocks and C be the set of cut vertices of a separable graph G. Construct a
 * graph H with vertex set B U C in which adjacencies are defined as follows: 
 * ci in C is adjacent to bj in B if and only if the block bj of G contains the cut vertex ci of G 
 * @assume graph is separable
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type 
 */
public class BCTree<V extends Vertex, E extends Edge<V>> extends Graph<BCTreeNode, BCTreeEdge>{

	/**
	 * Graph for which the BC-tree is constructed
	 */
	private Graph<V,E> graph;
	/**
	 * A list of C-typed nodes
	 */
	private List<BCTreeNode> cVertices;
	/**
	 * A list of B-typed nodes
	 */
	private List<BCTreeNode> bVertices;
	/**
	 * Root of the tree
	 */
	private BCTreeNode root;
	
	/**
	 * A list of pendants - block which contain only one cut vertex
	 */
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
		if (node.getType() == BCNodeType.C)
			cVertices.add(node);
		if (node.getType() == BCNodeType.B)
			bVertices.add(node);
	}
	
	@Override
	public void removeVertex(BCTreeNode node){
		super.removeVertex(node);
		if (node.getType() == BCNodeType.C)
			cVertices.remove(node);
		else if (node.getType() == BCNodeType.B)
			bVertices.remove(node);
	}

	private void constructTree(){
		
		List<V> cutVertices = graph.listCutVertices();
		List<Graph<V,E>> blocks = graph.listBiconnectedComponents();
		
		for (V cutVertex : cutVertices){
			BCTreeNode node = new BCTreeNode( BCNodeType.C, cutVertex);
			addVertex(node);
			if (root == null)
				root = node;
		}

		for (Graph<V,E> block : blocks){
			BCTreeNode node  = new BCTreeNode(BCNodeType.B, block);
			addVertex(node);
			int containedCutVerticesCount = 0;
			for (V cutVertex : cutVertices){
				if (block.hasVertex(cutVertex)){
					addEdge(new BCTreeEdge(getVertexByContent(cutVertex),node));
					containedCutVerticesCount++;
				}
			}
			if (containedCutVerticesCount == 1)
				pendants.add(block);
		}
		
		setParents(root, new ArrayList<BCTreeNode>());
	}
	

	@SuppressWarnings("unchecked")
	/**
	 * Updates the list of pendants after certain tree nodes were added or removed,
	 * thus changing degrees of other nodes
	 */
	public void updatePendants(){
		pendants.clear();
		for (BCTreeNode blockNode : bVertices){
			if (vertexDegree(blockNode) == 1)
				pendants.add((Graph<V, E>) blockNode.getContent());
		}
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
