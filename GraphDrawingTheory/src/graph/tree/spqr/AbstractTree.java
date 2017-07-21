package graph.tree.spqr;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

/**
 * Abstract class, extended by SPQR tree and proto SPQR tree
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type 
 */
public class AbstractTree<V extends Vertex, E extends Edge<V>> extends Graph<SPQRTreeNode<V,TreeEdgeWithContent<V,E>>, Edge<SPQRTreeNode<V,TreeEdgeWithContent<V,E>>>> {

	/**
	 * Root of the tree
	 */
	protected SPQRTreeNode<V,TreeEdgeWithContent<V,E>> root;
	
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

	public SPQRTreeNode<V, TreeEdgeWithContent<V, E>> getRoot() {
		return root;
	}

	public void setRoot(SPQRTreeNode<V, TreeEdgeWithContent<V, E>> root) {
		this.root = root;
	}
	
}
