package graph.tree.binary;

import graph.elements.Vertex;

public class BinaryTreeNode<V extends Vertex> {
	
	private V vertex;
	private BinaryTreeNode<V> left;
	private BinaryTreeNode<V> right;
	private BinaryTreeNode<V> parent;
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
