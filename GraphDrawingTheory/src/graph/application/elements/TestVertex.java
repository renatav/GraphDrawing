package graph.application.elements;

import graph.elements.Vertex;

import java.awt.Dimension;

public class TestVertex implements Vertex{

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestVertex other = (TestVertex) obj;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		return true;
	}


	private String content;
	public double minDistance = Double.POSITIVE_INFINITY;
	
	public TestVertex(String content) {
		this.content = content;
	}

	
	@Override
	public Dimension getSize() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Object getContent() {
		return content;
	}


	@Override
	public String toString() {
		return content;
	}



}
