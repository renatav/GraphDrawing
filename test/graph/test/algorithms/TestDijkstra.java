package graph.test.algorithms;

import graph.test.elements.TestEdge;
import graph.test.elements.TestVertex;
import graph.traversal.DijkstraAlgorithm;

import org.junit.Test;


public class TestDijkstra extends GraphTestCase{
	
	@Test
	public void test(){
		DijkstraAlgorithm<TestVertex, TestEdge> dijkstra = new DijkstraAlgorithm<>(graph);
		assertNotNull(dijkstra.getPath(vert1, vert7));
		assertNotNull(dijkstra.getPath(vert6, vert5));
		assertNotNull(dijkstra.getPath(vert1, vert3));
		
		System.out.println(dijkstra.getPath(vert1, vert7));
		System.out.println(dijkstra.getPath(vert6, vert5));
		System.out.println(dijkstra.getPath(vert1, vert3));
		
	}
	

}
