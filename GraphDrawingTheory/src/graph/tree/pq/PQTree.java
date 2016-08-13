package graph.tree.pq;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tree used in some algorithms for planarity testing
 * P nodes are cut vertices
 * Q nodes are nonseparable components
 * Leaves are virtual vertices (vertices on the other side of edges where one vertex is on subgraph Gk and the
 * other one is in V-Vk)
 * @author xx
 *
 */
public class PQTree <V extends Vertex, E extends Edge<V>> extends Graph<PQTreeNode, PQTreeEdge> {

	private List<PQTreeNode> pNodes, qNodes, leaves;
	private Graph<V,E> graph;
	/**
	 * The root of the PQ-tree is the unique node having no immediate siblings and no parent
	 */
	private PQTreeNode root;

	public PQTree(Graph<V,E> graph, List<E> virtualEdges){
		super();
		this.graph = graph;
		pNodes = new ArrayList<PQTreeNode>();
		qNodes = new ArrayList<PQTreeNode>();
		leaves = new ArrayList<PQTreeNode>();
		constructTree(virtualEdges);

	}


	@Override
	public void addVertex(PQTreeNode node){
		super.addVertex(node);
		if (node.getType() == PQNodeType.P)
			pNodes.add(node);
		else if (node.getType() == PQNodeType.Q)
			qNodes.add(node);
		else if (node.getType() == PQNodeType.LEAF)
			leaves.add(node);
	}

	@Override
	public void removeVertex(PQTreeNode node){
		super.removeVertex(node);
		if (node.getType() == PQNodeType.P)
			pNodes.remove(node);
		else if (node.getType() == PQNodeType.Q)
			qNodes.remove(node);
		else if (node.getType() == PQNodeType.LEAF)
			leaves.remove(node);
	}

	private void constructTree(List<E> virtualEdges){
		//this is similar to forming a bc tree
		//with the exception that the tree also contains the leaves
		//which represent virtual vertices

		System.out.println(graph);
		
		List<V> cutVertices = graph.listCutVertices();
		if (graph.getVertices().size() < 3) //TODO check this. Should definitely add the one vertex if there is only one
			cutVertices.addAll(graph.getVertices());
		
		System.out.println(cutVertices);
		
		List<Graph<V,E>> blocks = graph.listBiconnectedComponents();

		//graph vertex is key, virtual vertex is values
		Map<V,V> graphVirtualVerticesMap = new HashMap<V,V>();

		for (E e : virtualEdges){
			if (graph.getVertices().contains(e.getOrigin())){
				graphVirtualVerticesMap.put(e.getOrigin(), e.getDestination());
				PQTreeNode node = new PQTreeNode(PQNodeType.LEAF,  e.getDestination());
				addVertex(node);
			}
			else if (graph.getVertices().contains(e.getDestination())){
				graphVirtualVerticesMap.put(e.getDestination(), e.getOrigin());
				PQTreeNode node = new PQTreeNode(PQNodeType.LEAF,  e.getOrigin());
				addVertex(node);
			}
		}


		for (V cutVertex : cutVertices){
			PQTreeNode node = new PQTreeNode( PQNodeType.P, cutVertex);
			addVertex(node);
			if (graphVirtualVerticesMap.containsKey(cutVertices)){
				PQTreeNode leafNode = getVertexByContent(graphVirtualVerticesMap.get(cutVertex));
				addEdge(new PQTreeEdge(node,leafNode));
			}
		}

		for (Graph<V,E> block : blocks){
			PQTreeNode node  = new PQTreeNode(PQNodeType.Q, block);
			addVertex(node);
			for (V v : block.getVertices()){
				if (cutVertices.contains(v)){
					addEdge(new PQTreeEdge(getVertexByContent(v),node));
				}
				else if (graphVirtualVerticesMap.containsKey(v)){
					PQTreeNode leafNode = getVertexByContent(graphVirtualVerticesMap.get(v));
					addEdge(new PQTreeEdge(node,leafNode));
				}
			}
		}
	}


	@Override
	public String toString() {
		return "PQTree [pNodes=" + pNodes + ", qNodes=" + qNodes + ", leaves="
				+ leaves + "]";
	}


	public PQTreeNode getRoot() {
		return root;
	}


	public void setRoot(PQTreeNode root) {
		this.root = root;
	}
	
	
}
