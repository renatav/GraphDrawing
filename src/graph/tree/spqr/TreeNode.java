package graph.tree.spqr;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

import java.awt.Dimension;

public class TreeNode<V extends Vertex, E extends Edge<V>> implements Vertex{
	
	
	private NodeType nodeType;
	
	/**
	 * Each node is associated with a special graph which is called a skeleton of the node
	 */
	private Graph<V,E> skeleton;
	
	
	public TreeNode(NodeType nodeType) {
		super();
		this.nodeType = nodeType;
	}
	
	

	public TreeNode(NodeType nodeType, Graph<V, E> skeleton) {
		super();
		this.nodeType = nodeType;
		this.skeleton = skeleton;
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

	public Graph<V, E> getSkeleton() {
		return skeleton;
	}

	public void setSkeleton(Graph<V, E> skeleton) {
		this.skeleton = skeleton;
	}

	
}
