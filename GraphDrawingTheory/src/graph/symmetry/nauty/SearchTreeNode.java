package graph.symmetry.nauty;

import graph.elements.Vertex;

import java.util.ArrayList;
import java.util.List;

/**
 * Class represents a search tree node
 * @author Renata
 * @param <V> The vertex type
 */
public class SearchTreeNode<V extends Vertex>{
	
	/**
	 * Ordered partition associated with the node 
	 */
	private OrderedPartition<V> nodePartition;
	/**
	 * A list of vertices that split the node
	 */
	private List<V> splittingList;
	/**
	 * Node's parent
	 */
	private SearchTreeNode<V> parent;
	/**
	 * Node's children
	 */
	private List<SearchTreeNode<V>> children;
	
	/**
	 * Creates a node of the search tree given its partition, a vertex that splits it and its parent
	 * @param nodePartition An ordered partition connected with the node
	 * @param split A vertex that splits the partition
	 * @param parent Node's parent
	 */
	public SearchTreeNode(OrderedPartition<V> nodePartition, V split, SearchTreeNode<V> parent){
		this.nodePartition = nodePartition;
		this.parent = parent;
		splittingList = new ArrayList<V>();
		children = new ArrayList<SearchTreeNode<V>>();
		if (parent != null){
			splittingList.addAll(parent.getSplittingList());
			parent.getChildren().add(this);
		}
		if (split != null)
			splittingList.add(split);
	}

	/**
	 * @return Partition of the node
	 */
	public OrderedPartition<V> getNodePartition() {
		return nodePartition;
	}

	/**
	 * @return A list of vertices that split the node
	 */
	public List<V> getSplittingList() {
		return splittingList;
	}

	/**
 	 * @return The node's parent
	 */
	public SearchTreeNode<V> getParent() {
		return parent;
	}

	/**
	 * @return The node's children
	 */
	public List<SearchTreeNode<V>> getChildren() {
		return children;
	}
	
	
	


}
