package graph.test.planarity;

import graph.algorithms.planarity.AuslanderParterPlanarity;
import graph.algorithms.planarity.FraysseixMendezPlanarity;
import graph.algorithms.planarity.MaximumPlanaritySubgraph;
import graph.elements.Graph;
import graph.test.elements.TestEdge;
import graph.test.elements.TestVertex;
import junit.framework.TestCase;

import org.junit.Test;

public class TestPlanarity extends TestCase{

	Graph<TestVertex, TestEdge> planarGraph;
	Graph<TestVertex, TestEdge> nonPlanarGraph;


	@Override
	public void setUp(){

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

		planarGraph  = new Graph<TestVertex, TestEdge>(false);
		planarGraph.addVertex(vert1,vert2,vert3,vert4,vert5,vert6, vert7, vert8, vert9);
		planarGraph.addEdge(edge1n, edge2n, edge3n, edge4n, edge5n, edge6n,
				edge7n, edge8n, edge9n, edge10n, edge11n, edge12n, edge13n, edge14n);


		//Construct pentatope - one of the named non-planar graphs

		nonPlanarGraph = new Graph<TestVertex, TestEdge>(false);
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
		nonPlanarGraph.addVertex(vert1, vert2, vert3, vert4, vert5);;
		nonPlanarGraph.addEdge(pentEdge1, pentEdge2, pentEdge3, pentEdge4, pentEdge5, pentEdge6,
				pentEdge7, pentEdge8, pentEdge9, pentEdge10);
		
		Graph<TestVertex, TestEdge> graph = new Graph<>();
		graph.addVertex(vert1, vert2, vert3, vert4, vert5);
		graph.addEdge(pentEdge1, pentEdge5, pentEdge8, pentEdge10, pentEdge2, pentEdge6);
		
 
	}

	public void testPlanarAuslander(){


		AuslanderParterPlanarity<TestVertex, TestEdge> planarTest = new 
				AuslanderParterPlanarity<TestVertex, TestEdge> ();

		assertEquals(true, planarTest.isPlannar(planarGraph));

		assertEquals(false, planarTest.isPlannar(nonPlanarGraph));
	}
	
	public void testFraysseixMendez(){
		
		FraysseixMendezPlanarity<TestVertex, TestEdge> planarTest = new 
				FraysseixMendezPlanarity<TestVertex, TestEdge> ();
		
		assertEquals(true, planarTest.isPlannar(planarGraph));

		assertEquals(false, planarTest.isPlannar(nonPlanarGraph));
		}
		
	
	public void testMaximumPlanarity(){
		MaximumPlanaritySubgraph<TestVertex, TestEdge> maxPlanarity 
			= new MaximumPlanaritySubgraph<>(nonPlanarGraph);
			
		Graph<TestVertex, TestEdge> maxPlanarityGraph = maxPlanarity.calculateMaximumPlanarityGraph();
		assertEquals(9, maxPlanarityGraph.getEdges().size());
		
	}
	
	@Test
	public void testPlanarity(){
		testFraysseixMendez();
	}
		
}
