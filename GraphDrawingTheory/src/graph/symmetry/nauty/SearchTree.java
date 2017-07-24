package graph.symmetry.nauty;

import graph.elements.Vertex;

import java.util.ArrayList;
import java.util.List;

/**
 * Class represents a search tree defined and used in graph labeling algorithm
 * @author Renata
 * @param <V> The vertex type
 */
public class SearchTree<V extends Vertex>{
	
	/**
	 * Root of the search tree
	 */
	private SearchTreeNode<V> root;
	
	/**
	 * Creates a search tree given its root partition
	 * @param rootPartition Root partition
	 */
	public SearchTree(OrderedPartition<V> rootPartition){
		root = new SearchTreeNode<V>(rootPartition, null, null);
	}
	
	/**
	 * Finds all terminal nodes
	 * @return A list of terminal search tree nodes
	 */
	public List<SearchTreeNode<V>> getTerminalNodes(){
		List<SearchTreeNode<V>> ret = new ArrayList<SearchTreeNode<V>>();
		getTerminalNodes(ret, root);
		return ret;
	}
	
	private void getTerminalNodes(List<SearchTreeNode<V>> terminalNodes, SearchTreeNode<V> currentNode){
		if (currentNode.getChildren().size() == 0)
			terminalNodes.add(currentNode);
		else{
			for (SearchTreeNode<V> child : currentNode.getChildren())
				getTerminalNodes(terminalNodes, child);
		}
	}

	/**
	 * @return The root of the search tree
	 */
	public SearchTreeNode<V> getRoot() {
		return root;
	}

	/**
	 * @param root The root to set
	 */
	public void setRoot(SearchTreeNode<V> root) {
		this.root = root;
	}
	

}
