package graph.test.elements;

import graph.algorithms.bipartite.Bipartite;
import graph.elements.Graph;
import graph.traversal.DFSTree;
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
	public void testDFSTree(){

		Graph<TestVertex, TestEdge> graph = new Graph<TestVertex, TestEdge>(false);
		TestVertex vert1 = new TestVertex("1");
		TestVertex vert2 = new TestVertex("2");
		TestVertex vert3 = new TestVertex("3");
		TestVertex vert4 = new TestVertex("4");
		TestVertex vert5 = new TestVertex("5");
		TestVertex vert6 = new TestVertex("6");
		TestVertex vert7 = new TestVertex("7");
		TestVertex vert8 = new TestVertex("8");
		TestVertex vert9 = new TestVertex("9");
		TestVertex vert10 = new TestVertex("10");
		TestVertex vert11 = new TestVertex("11");

		TestEdge edge1 = new TestEdge(vert1, vert2);
		TestEdge edge2 = new TestEdge(vert1, vert3);
		TestEdge edge3 = new TestEdge(vert3, vert4);
		TestEdge edge4 = new TestEdge(vert3, vert5);
		TestEdge edge5 = new TestEdge(vert5, vert6);
		TestEdge edge6 = new TestEdge(vert5, vert7);
		TestEdge edge7 = new TestEdge(vert1, vert6);
		TestEdge edge8 = new TestEdge(vert2, vert4);
		TestEdge edge9 = new TestEdge(vert2, vert8);
		TestEdge edge10 = new TestEdge(vert2, vert9);
		TestEdge edge11 = new TestEdge(vert9, vert10);
		TestEdge edge12 = new TestEdge(vert9, vert11);
		TestEdge edge13 = new TestEdge(vert1, vert10);
		TestEdge edge14 = new TestEdge(vert1, vert11);
		
		graph.addVertex(vert1, vert2, vert3, vert4, vert5, vert6, vert7, vert8, vert9, vert10, vert11);
		graph.addEdge(edge1, edge2, edge3, edge4, edge5, edge6, edge7, edge8, edge9, edge10, edge11, edge12, edge13, edge14);
		
		GraphTraversal<TestVertex, TestEdge> traversal = new GraphTraversal<>(graph);
		DFSTree<TestVertex, TestEdge> tree = traversal.formDFSTree(vert1);
		System.out.println(tree);
				


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
