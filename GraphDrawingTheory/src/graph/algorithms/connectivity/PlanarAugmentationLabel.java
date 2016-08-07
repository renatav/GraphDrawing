package graph.algorithms.connectivity;

import java.util.ArrayList;
import java.util.List;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.tree.bc.BCTreeNode;
import graph.tree.bc.VertexType;

/**
 * A bundle of pendants together with its parent
 * is called a label. If parent is a c-vertex, the label is also
 * called c-label, otherwise it is calse a b-label
 * The size of a label is the number of pendants contained by the bundle
 * @author xx
 *
 * @param <V>
 * @param <E>
 */
public class PlanarAugmentationLabel<V extends Vertex, E extends Edge<V>> {

	
	private BCTreeNode parent;
	private List<Graph<V,E>> children;
	
	public PlanarAugmentationLabel(BCTreeNode parent){
		this.parent = parent;
		children = new ArrayList<Graph<V,E>>();
	}
	
	
	public int size(){
		return children.size();
	}
	
	public void addChild(Graph<V,E> pendant){
		children.add(pendant);
	}
	
	public VertexType getType(){
		return parent.getType();
	}
	

	/**
	 * @return the children
	 */
	public List<Graph<V,E>> getChildren() {
		return children;
	}



	/**
	 * @param children the children to set
	 */
	public void setChildren(List<Graph<V,E>> children) {
		this.children = children;
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


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PlanarAugmentationLabel [parent=" + parent + ", children=" + children + "]";
	}


	


	
	
	
}
