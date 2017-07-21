package graph.tree.binary;

import graph.elements.Vertex;

/**
 * Node of the binary tree
 * @author Renata
 * @param <V> The vertex type
 */
public class BinaryTreeNode<V extends Vertex> {
	
	/**
	 * Vertex of the original graph corresponding to the node
	 */
	private V vertex;
	/**
	 * Node left of the current node
	 */
	private BinaryTreeNode<V> left;
	/**
	 * Node right of the current node
	 */
	private BinaryTreeNode<V> right;
	/**
	 * Node's parent
	 */
	private BinaryTreeNode<V> parent;
	/**
	 * Node's height
	 */
	private int height = 1;
	
	
	public BinaryTreeNode(V vertex) {
		super();
		this.vertex = vertex;
	}
	

	public BinaryTreeNode(V vertex, BinaryTreeNode<V> left,
			BinaryTreeNode<V> right) {
		super();
		this.vertex = vertex;
		this.left = left;
		this.right = right;
	}


	public BinaryTreeNode(V vertex, BinaryTreeNode<V> left,
			BinaryTreeNode<V> right, BinaryTreeNode<V> parent) {
		super();
		this.vertex = vertex;
		this.left = left;
		this.right = right;
		this.parent = parent;
	}

	public V getVertex() {
		return vertex;
	}

	public void setVertex(V vertex) {
		this.vertex = vertex;
	}

	public BinaryTreeNode<V> getLeft() {
		return left;
	}

	public void setLeft(BinaryTreeNode<V> left) {
		this.left = left;
	}

	public BinaryTreeNode<V> getRight() {
		return right;
	}

	public void setRight(BinaryTreeNode<V> right) {
		this.right = right;
	}

	public BinaryTreeNode<V> getParent() {
		return parent;
	}

	public void setParent(BinaryTreeNode<V> parent) {
		this.parent = parent;
	}
	
	@Override
	public String toString() {
		String ret = "BinaryTreeNode [vertex=" + vertex + " height = " + height;
		if (left != null)
			ret += "  left = " + left.getVertex();
		if (right != null)
			ret += " right =" + right.getVertex();
		if (parent != null)
			ret += " parent = " + parent.getVertex();
		ret +=" ]";
		
		return ret;
		
	}


	public int getHeight() {
		return height;
	}


	public void setHeight(int height) {
		this.height = height;
	}


}
