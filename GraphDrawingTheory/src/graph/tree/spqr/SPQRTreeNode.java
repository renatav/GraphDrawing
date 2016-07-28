package graph.tree.spqr;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

public class SPQRTreeNode<V extends Vertex, E extends Edge<V>> implements Vertex{


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
	private List<SPQRTreeNode<V,E>> children;


	public SPQRTreeNode() {
		super();
		children = new ArrayList<SPQRTreeNode<V,E>>();
	}


	public SPQRTreeNode(NodeType nodeType) {
		super();
		this.nodeType = nodeType;
		children = new ArrayList<SPQRTreeNode<V,E>>();
	}


	public SPQRTreeNode(NodeType nodeType, Skeleton<V, E> skeleton) {
		this(nodeType);
		this.skeleton = skeleton;
	}

	public SPQRTreeNode(NodeType nodeType, Graph<V, E> skeleton) {
		super();
		this.nodeType = nodeType;
		this.skeleton = new Skeleton<>(skeleton.getVertices(), skeleton.getEdges());
	}

	public void addChildNode(SPQRTreeNode<V,E> node){
		if (!children.contains(node))
			children.add(node);
	}

	public void print(String prefix, boolean isTail) {
		System.out.println(prefix + (isTail ? "└── " : "├── ") + nodeType);
		for (int i = 0; i < children.size() - 1; i++) {
			children.get(i).print(prefix + (isTail ? "    " : "│   "), false);
		}
		if (children.size() > 0) {
			children.get(children.size() - 1).print(prefix + (isTail ?"    " : "│   "), true);
		}
	}
	

	@Override
	public Dimension getSize() {
		return null;
	}

	@Override
	public Object getContent() {
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



	public List<SPQRTreeNode<V, E>> getChildren() {
		return children;
	}



	public void setChildren(List<SPQRTreeNode<V, E>> children) {
		this.children = children;
	}


	@Override
	public String toString() {
		return "TreeNode [Node type =" + nodeType + ",\n skeleton=" + skeleton +"]";
	}


	@Override
	public void setSize(Dimension size) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setContent(Object content) {
		// TODO Auto-generated method stub
		
	}



}
