package graph.test.elements;

import graph.elements.Edge;

public class TestEdge implements Edge<TestVertex>{
	

	private TestVertex origin, destination;
	
	public TestEdge(TestVertex origin, TestVertex destination) {
		super();
		this.origin = origin;
		this.destination = destination;
	}
	
	@Override
	public TestVertex getOrigin() {
		return origin;
	}

	@Override
	public TestVertex getDestination() {
		return destination;
		
		
	}

	@Override
	public String toString() {
		return " [origin=" + origin + ", destination=" + destination
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((destination == null) ? 0 : destination.hashCode());
		result = prime * result + ((origin == null) ? 0 : origin.hashCode());
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
		TestEdge other = (TestEdge) obj;
		if (destination == null) {
			if (other.destination != null)
				return false;
		} else if (!destination.equals(other.destination))
			return false;
		if (origin == null) {
			if (other.origin != null)
				return false;
		} else if (!origin.equals(other.origin))
			return false;
		return true;
	}
}
