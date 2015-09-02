package graph.test.graph.properties;

import graph.elements.Graph;
import graph.properties.Bipartite;
import graph.test.elements.TestEdge;
import graph.test.elements.TestVertex;
import graph.traversal.GraphTraversal;
import junit.framework.TestCase;

import org.junit.Test;

public class TestGraph extends TestCase{


	@Test
	public void testTraversal(){

		Graph<TestVertex, TestEdge> graph = new Graph<TestVertex, TestEdge>(false);
		TestVertex vert1 = new TestVertex("1");
		TestVertex vert2 = new TestVertex("2");
		TestVertex vert3 = new TestVertex("3");

		TestEdge edge1 = new TestEdge(vert1, vert2);
		TestEdge edge2 = new TestEdge(vert1, vert3);
		TestEdge edge3 = new TestEdge(vert2, vert3);

		graph.addVertex(vert1,vert2,vert3);
		graph.addEdge(edge1,edge2,edge3);

		GraphTraversal<TestVertex, TestEdge> traversal = new GraphTraversal<>(graph);

		assertEquals(2, traversal.nonrecursiveDFS(vert1, vert3).size());
		assertEquals(2, traversal.findAllPathsDFS(vert1, vert3).size());



		TestVertex vert4 = new TestVertex("4");
		TestEdge edge4 = new TestEdge(vert1, vert4);
		TestEdge edge5 = new TestEdge(vert2, vert4);
		graph.addVertex(vert4);
		graph.addEdge(edge4, edge5);

		assertEquals(4, traversal.nonrecursiveDFS(vert3, vert4).size());
		assertEquals(4, traversal.findAllPathsDFS(vert3, vert4).size());

		assertTrue(graph.isBiconnected());


		graph = new Graph<TestVertex, TestEdge>(false);
		graph.addVertex(vert1,vert2,vert3, vert4);
		graph.addEdge(edge1,edge3, edge5);
		assertFalse(graph.isBiconnected());

	}



	@Test
	public void testConnected() {

		Graph<TestVertex, TestEdge> graph = new Graph<TestVertex, TestEdge>(false);
		TestVertex vert1 = new TestVertex("1");
		TestVertex vert2 = new TestVertex("2");
		TestVertex vert3 = new TestVertex("3");

		TestEdge edge1 = new TestEdge(vert1, vert2);
		TestEdge edge2 = new TestEdge(vert1, vert3);
		TestEdge edge3 = new TestEdge(vert2, vert3);

		graph.addVertex(vert1,vert2,vert3);
		graph.addEdge(edge1,edge2,edge3);

		assertTrue(graph.isBiconnected());

		TestVertex vert4 = new TestVertex("4");
		TestEdge edge4 = new TestEdge(vert1, vert4);
		TestEdge edge5 = new TestEdge(vert2, vert4);
		graph.addVertex(vert4);
		graph.addEdge(edge4, edge5);

		assertTrue(graph.isBiconnected());


		graph = new Graph<TestVertex, TestEdge>(false);
		graph.addVertex(vert1,vert2,vert3, vert4);
		graph.addEdge(edge1,edge3, edge5);
		assertFalse(graph.isBiconnected());

	}

	@Test
	public void testBipartite(){
		TestVertex vert1 = new TestVertex("1");
		TestVertex vert2 = new TestVertex("2");
		TestVertex vert3 = new TestVertex("3");
		TestVertex vert4 = new TestVertex("4");
		TestVertex vert5 = new TestVertex("5");
		TestVertex vert6 = new TestVertex("6");

		TestEdge edge1 = new TestEdge(vert1, vert4);
		TestEdge edge2 = new TestEdge(vert1, vert5);
		TestEdge edge3 = new TestEdge(vert1, vert6);
		TestEdge edge4 = new TestEdge(vert2, vert4);
		TestEdge edge5 = new TestEdge(vert2, vert5);
		TestEdge edge6 = new TestEdge(vert3, vert6);


		Graph<TestVertex, TestEdge> graph = new Graph<TestVertex,TestEdge>();
		graph.addVertex(vert1, vert2, vert3, vert4, vert5, vert6);
		graph.addEdge(edge1, edge2, edge3, edge4, edge5, edge6);

		Bipartite<TestVertex, TestEdge> bipartite= new Bipartite<TestVertex, TestEdge>(graph);
		assertEquals(true, bipartite.isBipartite());

		//insert another edge which causes the graph not to be bipartite
		TestEdge edge7 = new TestEdge(vert1, vert2);
		graph.addEdge(edge7);

		assertEquals(false, bipartite.isBipartite());


	}







}
