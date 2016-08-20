package graph.tree.pq;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

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

	private Map<V, Integer> stNumbering;


	public PQTree(Graph<V,E> graph, List<E> virtualEdges, Map<V, Integer> stNumbering){
		super();
		this.graph = graph;
		pNodes = new ArrayList<PQTreeNode>();
		qNodes = new ArrayList<PQTreeNode>();
		leaves = new ArrayList<PQTreeNode>();
		this.stNumbering = stNumbering;
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

	@Override
	public void addEdge(PQTreeEdge...edges){
		super.addEdge(edges);
		for (PQTreeEdge edge : edges){
			edge.getDestination().setParent(edge.getOrigin());
		}
	}

	private void constructTree(List<E> virtualEdges){
		//the tree contains leaves
		//which represent virtual vertices
		//pay attention to the fact that there could be more leaves referencing the same vertex


		for (V v : graph.getVertices()){
			PQTreeNode node = new PQTreeNode( PQNodeType.P, v);
			addVertex(node);

			if (root == null)
				root = node;
		}

		for (E edge : graph.getEdges()){
			V v1 = edge.getOrigin();
			V v2 = edge.getDestination();

			if (stNumbering.get(v1) < stNumbering.get(v2)){
				addEdge(new PQTreeEdge(getVertexByContent(v1), getVertexByContent(v2)));
				getVertexByContent(v1).addChild(getVertexByContent(v2));
			}
			else{
				addEdge(new PQTreeEdge(getVertexByContent(v2), getVertexByContent(v1)));
				getVertexByContent(v2).addChild(getVertexByContent(v1));
			}

		}


		for (E e : virtualEdges){
			PQTreeNode treeNode = null;
			PQTreeNode node = null;
			if (graph.getVertices().contains(e.getOrigin())){
				node = new PQTreeNode(PQNodeType.LEAF,  e.getDestination());
				node.setVirtualEdge(e);
				addVertex(node);
				treeNode = getVertexByContent(e.getOrigin());


			}
			else if (graph.getVertices().contains(e.getDestination())){
				node = new PQTreeNode(PQNodeType.LEAF,  e.getOrigin());
				node.setVirtualEdge(e);
				addVertex(node);
				treeNode = getVertexByContent(e.getDestination());
			}

			if (treeNode != null){
				addEdge(new PQTreeEdge(treeNode, node));
				treeNode.addChild(node);
			}
		}

	}

	public List<PQTreeNode> findLeavesOf(List<E> virtualEdges){
		List<PQTreeNode> ret = new ArrayList<PQTreeNode>();
		for (E e : virtualEdges){
			if (getVertexByContent(e.getOrigin()).getType() == PQNodeType.LEAF)
				ret.add(getVertexByContent(e.getOrigin()));
			else if (getVertexByContent(e.getDestination()).getType() == PQNodeType.LEAF)
				ret.add(getVertexByContent(e.getDestination())); 
		}
		return ret;

	}

	/**
	 * Finds all descendants of a tree node
	 * @param node Node
	 * @return A list of descendants
	 */
	public List<PQTreeNode> allDescendantsOf(PQTreeNode node){
		System.out.println("finding descendants of " + node);
		List<PQTreeNode> ret = new ArrayList<PQTreeNode>();
		allDescendantsRecursive(node, ret);
		return ret;
	}

	private void allDescendantsRecursive(PQTreeNode node, List<PQTreeNode> descendants){
		if (node.getChildren() != null)
			for (PQTreeNode child : node.getChildren()){
				System.out.println(child);
				descendants.add(child);
				allDescendantsRecursive(child, descendants);
			}
	}
	

	@Override
	public String toString() {
		
		return "PQTree [pNodes=" + pNodes + ", qNodes=" + qNodes + ", leaves="
				+ leaves + " " + edges + "] ";
	}


	public PQTreeNode getRoot() {
		return root;
	}


	public void setRoot(PQTreeNode root) {
		this.root = root;
	}


	/**
	 * @return the pNodes
	 */
	public List<PQTreeNode> getpNodes() {
		return pNodes;
	}


	/**
	 * @param pNodes the pNodes to set
	 */
	public void setpNodes(List<PQTreeNode> pNodes) {
		this.pNodes = pNodes;
	}


	/**
	 * @return the qNodes
	 */
	public List<PQTreeNode> getqNodes() {
		return qNodes;
	}


	/**
	 * @param qNodes the qNodes to set
	 */
	public void setqNodes(List<PQTreeNode> qNodes) {
		this.qNodes = qNodes;
	}


	/**
	 * @return the leaves
	 */
	public List<PQTreeNode> getLeaves() {
		return leaves;
	}


	/**
	 * @param leaves the leaves to set
	 */
	public void setLeaves(List<PQTreeNode> leaves) {
		this.leaves = leaves;
	}


}
