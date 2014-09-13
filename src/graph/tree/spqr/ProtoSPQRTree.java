package graph.tree.spqr;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.operations.GraphOperations;

public class ProtoSPQRTree<V extends Vertex, E extends Edge<V>> extends Graph<TreeNode<V,E>, Edge<TreeNode<V,E>>>{
	
	/**
	 * Original graph - reference edge
	 */
	private Graph<V,E> gPrim;
	
	private E referenceEdge;
	
	/**
	 * Original graph. Must be biconnected
	 */
	private Graph<V,E> graph; 
	
	//TODO mozda napraviti neki AbstractTree
	
	public ProtoSPQRTree(Graph<V,E> graph, E referenceEdge){
		this.referenceEdge = referenceEdge;
		this.graph = graph;
		GraphOperations<V, E> operations = new GraphOperations<>();
		gPrim = operations.removeEdgeFromGraph(graph, referenceEdge);
		constructTree();
		
	}
	
	private void constructTree(){
		
		//trivial case
		if (gPrimIsASingleEdge()){ //is g prim is a single edge
			TreeNode<V, E> node = new TreeNode<>(NodeType.Q, graph);
			addVertex(node);
		}
		//series case
		else if (!gPrim.isBiconnected()){ //g prim is not a single edge and is not biconnected
			
		}
	}
	
	
	private boolean gPrimIsASingleEdge(){
		if (gPrim.getEdges().size() == 1 && gPrim.getVertices().size() == 2)
			return true;
		return false;
			
	}

	public Graph<V, E> getgPrim() {
		return gPrim;
	}

	public void setgPrim(Graph<V, E> gPrim) {
		this.gPrim = gPrim;
	}

	public E getReferenceEdge() {
		return referenceEdge;
	}

	public void setReferenceEdge(E referenceEdge) {
		this.referenceEdge = referenceEdge;
	}

	public Graph<V, E> getGraph() {
		return graph;
	}

	public void setGraph(Graph<V, E> graph) {
		this.graph = graph;
	}

}
