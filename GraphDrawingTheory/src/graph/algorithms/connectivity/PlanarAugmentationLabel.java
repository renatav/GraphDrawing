package graph.algorithms.connectivity;

import java.util.ArrayList;
import java.util.List;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.tree.bc.BCTreeNode;

public class PlanarAugmentationLabel<V extends Vertex, E extends Edge<V>> {

	private List<Graph<V,E>> pendantsBundle;
	private BCTreeNode parent;
	
	public PlanarAugmentationLabel(){
		pendantsBundle = new ArrayList<Graph<V,E>>();
	}
	
	public int size(){
		return pendantsBundle.size();
	}
	

	/**
	 * @return the pendantsBundle
	 */
	public List<Graph<V, E>> getPendantsBundle() {
		return pendantsBundle;
	}

	/**
	 * @param pendantsBundle the pendantsBundle to set
	 */
	public void setPendantsBundle(List<Graph<V, E>> pendantsBundle) {
		this.pendantsBundle = pendantsBundle;
	}

	/**
	 * @return the parent
	 */
	public BCTreeNode getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(BCTreeNode parent) {
		this.parent = parent;
	}
	
	
	
}
