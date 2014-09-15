package graph.tree.spqr;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

public class TreeNode<V extends Vertex, E extends Edge<V>> implements Vertex{
	
	
	/**
	 * Node type -S,P,Q,R
	 */
	private NodeType nodeType;
	
	/**
	 * Graph associated with the node
	 */
	private Skeleton<V,E> skeleton;
	
	/**
	 * Children of the node
	 */
	private List<TreeNode<V,E>> children;
	
	/**
	 * Reference edge 
	 */
	private E referenceEdge;
	
	
	public TreeNode() {
		super();
		children = new ArrayList<TreeNode<V,E>>();
	}
	
	public TreeNode(E referenceEdge){
		this.referenceEdge = referenceEdge;
	}
	
	public TreeNode(NodeType nodeType) {
		super();
		this.nodeType = nodeType;
		children = new ArrayList<TreeNode<V,E>>();
	}
	
	public TreeNode(E referenceEdge, Skeleton<V, E> skeleton) {
		this(referenceEdge);
		this.skeleton = skeleton;
	}
	

	public TreeNode(NodeType nodeType, Skeleton<V, E> skeleton) {
		this(nodeType);
		this.skeleton = skeleton;
	}
	
	public TreeNode(NodeType nodeType, Graph<V, E> skeleton) {
		super();
		this.nodeType = nodeType;
		this.skeleton = new Skeleton<>(skeleton.getVertices(), skeleton.getEdges());
	}

	public void addChildNode(TreeNode<V,E> node){
		if (!children.contains(node))
			children.add(node);
	}


	@Override
	public Dimension getSize() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getContent() {
		// TODO Auto-generated method stub
		return null;
	}

	public NodeType getNodeType() {
		return nodeType;
	}

	public void setNodeType(NodeType nodeType) {
		this.nodeType = nodeType;
	}

	public Skeleton<V, E> getSkeleton() {
		return skeleton;
	}

	public void setSkeleton(Skeleton<V, E> skeleton) {
		this.skeleton = skeleton;
	}



	public List<TreeNode<V, E>> getChildren() {
		return children;
	}



	public void setChildren(List<TreeNode<V, E>> children) {
		this.children = children;
	}


	@Override
	public String toString() {
		return "TreeNode [Node type =" + nodeType + ",\n skeleton=" + skeleton +"]";
	}


	public E getReferenceEdge() {
		return referenceEdge;
	}


	public void setReferenceEdge(E referenceEdge) {
		this.referenceEdge = referenceEdge;
	}


	
}
