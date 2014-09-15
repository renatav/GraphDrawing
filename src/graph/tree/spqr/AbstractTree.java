package graph.tree.spqr;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

public class AbstractTree<V extends Vertex, E extends Edge<V>> extends Graph<TreeNode<V,TreeEdgeWithContent<V,E>>, Edge<TreeNode<V,TreeEdgeWithContent<V,E>>>> {


	/**
	 * Root of the tree
	 */
	protected TreeNode<V,TreeEdgeWithContent<V,E>> root;
	
	/**
	 * Reference edge
	 */
	protected E referenceEdge;

	/**
	 * Original graph. Must be biconnected
	 */
	protected Graph<V,E> graph; 
	
	

	public AbstractTree(E referenceEdge, Graph<V, E> graph) {
		super();
		this.referenceEdge = referenceEdge;
		this.graph = graph;
	}

	public TreeNode<V, TreeEdgeWithContent<V, E>> getRoot() {
		return root;
	}

	public void setRoot(TreeNode<V, TreeEdgeWithContent<V, E>> root) {
		this.root = root;
	}
	
}
