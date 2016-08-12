package graph.tree.pq;

import java.awt.Dimension;

import graph.elements.Vertex;

public class PQTreeNode implements Vertex{

	/**
	 * Type of the node - either P or Q or a leaf
	 */
	private PQNodeType type;
	/**
	 * Content of the node. A cut vertex if the type is P or 
	 * a block if the type is Q. Virtual vertex if it is a leaf
	 */
	private Object content;
	
	public PQTreeNode(PQNodeType type, Object content) {
		super();
		this.type = type;
		this.content = content;
	}

	/**
	 * @return the type
	 */
	public PQNodeType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(PQNodeType type) {
		this.type = type;
	}

	/**
	 * @return the content
	 */
	public Object getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(Object content) {
		this.content = content;
	}

	@Override
	public Dimension getSize() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSize(Dimension size) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
}
