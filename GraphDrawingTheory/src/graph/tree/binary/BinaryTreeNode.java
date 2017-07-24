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
	
	/**
	 * Constructs a binary tree node associated with the given vertex
	 * @param vertex Vertex associated with the node
	 */
	public BinaryTreeNode(V vertex) {
		super();
		this.vertex = vertex;
	}

	/**
	 * Construct a binary tree node associated with the given vertex
	 * and sets nodes to its right and left
	 * @param vertex Vertex associated with the node
	 * @param left Node to the left of the node
	 * @param right Node to the right of the node
	 */
	public BinaryTreeNode(V vertex, BinaryTreeNode<V> left,
			BinaryTreeNode<V> right) {
		super();
		this.vertex = vertex;
		this.left = left;
		this.right = right;
	}


	/**
	 * Construct a binary tree node associated with the given vertex
	 * and sets nodes to its right and left as well as its parent
	 * @param vertex Vertex associated with the node
	 * @param left Node to the left of the node
	 * @param right Node to the right of the node
	 * @param parent Node's parent
	 */
	public BinaryTreeNode(V vertex, BinaryTreeNode<V> left,
			BinaryTreeNode<V> right, BinaryTreeNode<V> parent) {
		super();
		this.vertex = vertex;
		this.left = left;
		this.right = right;
		this.parent = parent;
	}

	/**
	 * @return Vertex associated with the node
	 */
	public V getVertex() {
		return vertex;
	}

	/**
	 * @param vertex Vertex associated with the node to set
	 */
	public void setVertex(V vertex) {
		this.vertex = vertex;
	}

	/**
	 * @return Node to the left of the node
	 */
	public BinaryTreeNode<V> getLeft() {
		return left;
	}

	/**
	 * @param left Node to the left of the node to set
	 */
	public void setLeft(BinaryTreeNode<V> left) {
		this.left = left;
	}

	/**
	 * @return Node to the right of the node
	 */
	public BinaryTreeNode<V> getRight() {
		return right;
	}

	/**
	 * @param right Node to the right of the node to set
	 */
	public void setRight(BinaryTreeNode<V> right) {
		this.right = right;
	}

	/**
	 * @return Node's parent
	 */
	public BinaryTreeNode<V> getParent() {
		return parent;
	}

	/**
	 * @param parent Parent to set
	 */
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


	/**
	 * @return Node's height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height Height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}


}
