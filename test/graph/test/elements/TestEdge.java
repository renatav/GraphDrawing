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
		// TODO Auto-generated method stub
		return origin;
	}

	@Override
	public TestVertex getDestination() {
		// TODO Auto-generated method stub
		return destination;
		
		
	}

	@Override
	public String toString() {
		return " [origin=" + origin + ", destination=" + destination
				+ "]";
	}

}
