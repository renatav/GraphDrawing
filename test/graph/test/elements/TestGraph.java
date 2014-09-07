package graph.test.elements;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import graph.algorithms.bipartite.Bipartite;
import graph.algorithms.planarity.AuslanderParterPlanarity;
import graph.elements.Graph;
import graph.exception.AlgorithmCannotBeAppliedException;

import org.junit.Test;

public class TestGraph {

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

		Graph<TestVertex, TestEdge> graph2 = new Graph<TestVertex, TestEdge>(false);
		graph2.addVertex(vert1,vert2,vert3,vert4,vert5,vert6, vert7, vert8, vert9);
		graph2.addEdge(edge1n, edge2n, edge3n, edge4n, edge5n, edge6n,
				edge7n, edge8n, edge9n, edge10n, edge11n, edge12n, edge13n, edge14n);
		
		AuslanderParterPlanarity<TestVertex, TestEdge> planarTest = new 
				AuslanderParterPlanarity<TestVertex, TestEdge> (graph2);
		
		try {
			System.out.println(planarTest.isPlannar());
		} catch (AlgorithmCannotBeAppliedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
