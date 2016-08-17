package graph.algorithms.planarity;

import graph.algorithms.numbering.STNumbering;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.tree.pq.PQTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of an algorithm for embedding a planar graph in th plane
 * Based on the work of Chiba, Nishizeki, Abe and Ozava prezented in their paper
 * "A Linear Algorithm for Embedding Planar Graphs Using PQ-trees"
 * The algorithm is based on the vertex algorithm of Lempel, Even and
 * Cederbaum for planarity testing and is a modification of Boot and Lucker's implementation
 * of planarity testing using a PQ-tree
 *
 * @param <V>
 * @param <E>
 */
public class PlanarEmbedding<V extends Vertex, E extends Edge<V>> {
	
	private Graph<V,E> graph;
	private Map<V, List<V>> embedding;
	private List<V> stOrder;
	/**
	 * A map containing graph (subgraphs G1,G2...Gn)
	 * and the lists of virtual edges turning them into
	 * Gk'
	 */
	private Map<Graph<V,E>, List<E>> gPrimMap; 
	
	public PlanarEmbedding(Graph<V,E> graph){
		this.graph = graph;
		embedding = new HashMap<V, List<V>>();
		gPrimMap = new HashMap<Graph<V,E>, List<E>>();
	}
	
	public void execute(){
//		//assign st-numbers to all vertices of G
//		//s and t should be connected, but it is not stated
//		//that they should meet any special condition
//		//so, let st be the first edge
//		E st = graph.getEdges().get(0);
//		V s = st.getOrigin();
//		V t = st.getDestination();
//		STNumbering<V, E> stNumbering = new STNumbering<V,E>(graph, s, t);
//		stOrder = stNumbering.getOrder();
//		
//		System.out.println("s " + s);
//		System.out.println("t " + t);
//		
//		//construct a PQ-tree corresponding to G1'
//		Graph<V,E> g = constructGk(1);
//		PQTree<V, E> pqTree = new PQTree<>(g, gPrimMap.get(g));
//
//		System.out.println(pqTree);
	}
	
	private Graph<V,E> constructGk(int k){
		//vertices in the subgraph
		List<V> vertices = stOrder.subList(0,k); //the second index is exclusive
		
		Graph<V,E> Gk = graph.subgraph(vertices);
		List<E> virtualEdges = new ArrayList<E>();
		for (E e : graph.getEdges())
			if (vertices.contains(e.getOrigin()) && !vertices.contains(e.getDestination()) || 
					vertices.contains(e.getDestination()) && !vertices.contains(e.getOrigin()))
				virtualEdges.add(e);
				
		gPrimMap.put(Gk, virtualEdges);
		return Gk;
		
	}

}
