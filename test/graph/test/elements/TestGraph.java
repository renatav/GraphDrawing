package graph.test.elements;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import graph.algorithms.bipartite.Bipartite;
import graph.algorithms.planarity.AuslanderParterPlanarity;
import graph.elements.Graph;
import graph.traversal.GraphTraversal;

import org.junit.Test;

public class TestGraph {

	
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
//
//			assertTrue(graph.isBiconnected());
//
//
//			graph = new Graph<TestVertex, TestEdge>(false);
//			graph.addVertex(vert1,vert2,vert3, vert4);
//			graph.addEdge(edge1,edge3, edge5);
//			assertFalse(graph.isBiconnected());

//		}
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


	@Test
	public void testPlanar(){

		TestVertex vert1 = new TestVertex("1");
		TestVertex vert2 = new TestVertex("2");
		TestVertex vert3 = new TestVertex("3");
		TestVertex vert4 = new TestVertex("4");
		TestVertex vert5 = new TestVertex("5");
		TestVertex vert6 = new TestVertex("6");
		TestVertex vert7 = new TestVertex("7");
		TestVertex vert8 = new TestVertex("8");
		TestVertex vert9 = new TestVertex("9");

		TestEdge edge1n = new TestEdge(vert1, vert2);
		TestEdge edge2n = new TestEdge(vert2, vert3);
		TestEdge edge3n = new TestEdge(vert3, vert4);
		TestEdge edge4n =  new TestEdge(vert4, vert5);
		TestEdge edge5n = new TestEdge(vert5, vert6);
		TestEdge edge14n = new TestEdge(vert6, vert1);

		//s2
		TestEdge edge6n = new TestEdge(vert1, vert7);
		TestEdge edge7n = new TestEdge(vert7, vert5);

		//s1
		TestEdge edge8n = new TestEdge(vert6, vert2);

		//s3
		TestEdge edge9n = new TestEdge(vert2, vert8);
		TestEdge edge10n = new TestEdge(vert8, vert3);
		TestEdge edge11n = new TestEdge(vert8, vert4);

		//s4
		TestEdge edge12n = new TestEdge(vert6, vert9);
		TestEdge edge13n = new TestEdge(vert9, vert4);

		Graph<TestVertex, TestEdge> graph1 = new Graph<TestVertex, TestEdge>(false);
		graph1.addVertex(vert1,vert2,vert3,vert4,vert5,vert6, vert7, vert8, vert9);
		graph1.addEdge(edge1n, edge2n, edge3n, edge4n, edge5n, edge6n,
				edge7n, edge8n, edge9n, edge10n, edge11n, edge12n, edge13n, edge14n);

		AuslanderParterPlanarity<TestVertex, TestEdge> planarTest = new 
				AuslanderParterPlanarity<TestVertex, TestEdge> ();

		assertEquals(true, planarTest.isPlannar(graph1));
		
		
		//Construct pentatope - one of the named non-planar graphs
		
		Graph<TestVertex, TestEdge> graph2 = new Graph<TestVertex, TestEdge>(false);
		TestEdge pentEdge1 = new TestEdge(vert1, vert2);
		TestEdge pentEdge2 = new TestEdge(vert1, vert3);
		TestEdge pentEdge3 = new TestEdge(vert1, vert4);
		TestEdge pentEdge4 = new TestEdge(vert1, vert5);
		TestEdge pentEdge5 = new TestEdge(vert2, vert3);
		TestEdge pentEdge6 = new TestEdge(vert2, vert4);
		TestEdge pentEdge7 = new TestEdge(vert2, vert5);
		TestEdge pentEdge8 = new TestEdge(vert3, vert4);
		TestEdge pentEdge9 = new TestEdge(vert3, vert5);
		TestEdge pentEdge10 = new TestEdge(vert4, vert5);
		graph2.addVertex(vert1, vert2, vert3, vert4, vert5);;
		graph2.addEdge(pentEdge1, pentEdge2, pentEdge3, pentEdge4, pentEdge5, pentEdge6,
				pentEdge7, pentEdge8, pentEdge9, pentEdge10);
		
		
		assertEquals(false, planarTest.isPlannar(graph2));
	}


}
