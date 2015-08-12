package graph.symmetry.nauty;

import graph.elements.Vertex;

import java.util.ArrayList;
import java.util.List;

public class SearchTreeNode<V extends Vertex>{
	
	private OrderedPartition<V> nodePartition;
	private List<V> splittingList;
	private SearchTreeNode<V> parent;
	private List<SearchTreeNode<V>> children;
	
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

	public OrderedPartition<V> getNodePartition() {
		return nodePartition;
	}

	public List<V> getSplittingList() {
		return splittingList;
	}

	public SearchTreeNode<V> getParent() {
		return parent;
	}

	public List<SearchTreeNode<V>> getChildren() {
		return children;
	}
	
	
	


}
