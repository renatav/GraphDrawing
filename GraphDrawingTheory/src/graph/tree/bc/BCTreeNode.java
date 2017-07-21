package graph.tree.bc;

import java.awt.Dimension;

import graph.elements.Vertex;

/**
 * Node of the block-cut vertex tree
 * @author Renata
 */
public class BCTreeNode implements Vertex {

	private Object content;
	private BCNodeType type;
	private BCTreeNode parent;
	
	public BCTreeNode(BCNodeType type, Object content){
		this.type = type;
		this.content = content;
	}


	public BCTreeNode(Object content) {
		super();
		this.content = content;
	}

	@Override
	public Dimension getSize() {
		return null;
	}

	@Override
	public Object getContent() {
		return content;
	}
	
	
	@Override
	public String toString() {
		return "BCTreeNode [" + content +  " parent = " + (parent == null ? parent : parent.getContent()) + "]\n";
	}

	@Override
	public void setSize(Dimension size) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setContent(Object content) {
		// TODO Auto-generated method stub
		
	}


	/**
	 * @return the type
	 */
	public BCNodeType getType() {
		return type;
	}


	/**
	 * @param type the type to set
	 */
	public void setType(BCNodeType type) {
		this.type = type;
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
