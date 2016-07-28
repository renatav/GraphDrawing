package graph.tree.bc;

import java.awt.Dimension;

import graph.elements.Vertex;

public class BCTreeNode implements Vertex {

	private Object content;


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
		return "BCTreeNode [" + content + "]";
	}

	@Override
	public void setSize(Dimension size) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setContent(Object content) {
		// TODO Auto-generated method stub
		
	}

}
