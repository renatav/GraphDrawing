package graph.tree.spqr;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.exception.CannotBeAppliedException;

/**
 * SPQR-trees implicitly represent all embeddings of a graph
 * The triconnected components of a biconnected graph are a system of smaller graphs
 * that describe all of the 2-vertex cuts in the graph. 
 * An SPQR tree is used to represent the triconnected components of a graph.
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type 
 */
public class SPQRTree<V extends Vertex,E extends Edge<V>> extends AbstractTree<V,E>{
	
	
	public  SPQRTree(E referenceEdge, Graph<V, E> graph) throws CannotBeAppliedException {
		super(referenceEdge, graph);
		
		//check if is biconnected
		if (!graph.isBiconnected())
			throw new CannotBeAppliedException("Cannot construct SPQR tree for provided graph. Graph must be biconnected.");
		
		
		
		constructTree();
		
	}
	
	/** Construct the spqr tree
	 *  The SPQR tree of a biconnected planar graph G
	 *  consists of a Q node representing the reference edge e whose child is the root of the
	 *  Proto-SPQR tree for G with reference edge e.
	 */
	@SuppressWarnings("unchecked")
	private void constructTree(){
		Skeleton<V,TreeEdgeWithContent<V,E>> skeleton = new Skeleton<>();
		skeleton.addVertex(referenceEdge.getOrigin(), referenceEdge.getDestination());
		skeleton.addEdge(new TreeEdgeWithContent<V,E>(referenceEdge.getOrigin(), referenceEdge.getDestination()));
		root = new SPQRTreeNode<>(NodeType.Q, skeleton);
		
		ProtoSPQRTree<V, E> protoTree = new ProtoSPQRTree<V,E>(referenceEdge, graph);
		root.addChildNode(protoTree.getRoot());
		
	}
	
	
	public void printTree(){
		root.print("", false);
	}
	
	
	
	

}
