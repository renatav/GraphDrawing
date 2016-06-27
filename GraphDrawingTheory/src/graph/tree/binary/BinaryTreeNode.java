package graph.tree.binary;

import graph.elements.Vertex;

public class BinaryTreeNode<V extends Vertex> {
	
	private V vertex;
	private V left;
	private V right;
	
	
	public BinaryTreeNode(V vertex, V left, V right) {
		super();
		this.vertex = vertex;
		this.left = left;
		this.right = right;
	}


	/**
	 * @return the vertex
	 */
	public V getVertex() {
		return vertex;
	}


	/**
	 * @param vertex the vertex to set
	 */
	public void setVertex(V vertex) {
		this.vertex = vertex;
	}


	/**
	 * @return the left
	 */
	public V getLeft() {
		return left;
	}


	/**
	 * @param left the left to set
	 */
	public void setLeft(V left) {
		this.left = left;
	}

	/**
	 * @return the right
	 */
	public V getRight() {
		return right;
	}


	/**
	 * @param right the right to set
	 */
	public void setRight(V right) {
		this.right = right;
	}
	

}
