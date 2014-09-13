package graph.test.graph.properties;

import graph.elements.Graph;
import graph.properties.splitting.SplitPair;
import graph.properties.splitting.Splitting;
import graph.test.elements.TestEdge;
import graph.test.elements.TestVertex;
import junit.framework.TestCase;

import org.junit.Test;

public class TestSplitting extends TestCase{
	
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

	TestEdge edge1 = new TestEdge(vert1, vert2);
	TestEdge edge2 = new TestEdge(vert1, vert3);
	TestEdge edge3 = new TestEdge(vert2, vert4);
	TestEdge edge4 = new TestEdge(vert4, vert3);
	TestEdge edge5 = new TestEdge(vert1, vert7);
	TestEdge edge6 = new TestEdge(vert4, vert5);
	TestEdge edge7 = new TestEdge(vert7, vert6);
	TestEdge edge8 = new TestEdge(vert6, vert5);
	TestEdge edge9 = new TestEdge(vert6, vert8);
	TestEdge edge10 = new TestEdge(vert8, vert10);
	TestEdge edge11 = new TestEdge(vert6, vert9);
	TestEdge edge12 = new TestEdge(vert8, vert9);
	TestEdge edge13 = new TestEdge(vert10, vert9);
	
	TestEdge edge1_2 = new TestEdge(vert1, vert2);
	TestEdge edge2_2 = new TestEdge(vert1, vert3);
	TestEdge edge3_2 = new TestEdge(vert2, vert4);
	TestEdge edge4_2 = new TestEdge(vert3, vert4);
	TestEdge edge5_2 = new TestEdge(vert1, vert4);
	TestEdge edge6_2 = new TestEdge(vert1, vert5);
	TestEdge edge7_2 = new TestEdge(vert3, vert5);
	
	Graph<TestVertex, TestEdge> graph, graph2;
	
	
	@Override
	public void setUp(){
		graph = new Graph<>();
		graph.addVertex(vert1, vert2, vert3, vert4, vert5, vert6, vert7, vert8, vert9, vert10);
		graph.addEdge(edge1, edge2, edge3, edge4, edge5, edge6, edge7, edge8, edge9, edge10, edge11, edge12, edge13);
		graph2 = new Graph<>();
		graph2.addVertex(vert1, vert2, vert3, vert4, vert5);
		graph2.addEdge(edge1_2, edge2_2, edge3_2, edge4_2, edge5_2, edge6_2, edge7_2);
	}
	
	@Test
	public void test(){
		Splitting<TestVertex, TestEdge> splitting = new Splitting<TestVertex,TestEdge>();
		//System.out.println(splitting.findAllSplitPairs(graph2));
		SplitPair<TestVertex, TestEdge> pair1 = new SplitPair<TestVertex, TestEdge>(vert1, vert4);
		
		assertTrue(splitting.testSplitComponents(splitting.findAllSplitComponents(graph2, pair1), pair1));
		assertTrue(splitting.testSplitComponents(splitting.findAllSplitComponents(graph, pair1), pair1));
		
//		System.out.println(splitting.splitGraph(splitting.findAllSplitComponents(graph2, pair1), edge7_2));
		
		SplitPair<TestVertex, TestEdge> split1 = new SplitPair<TestVertex, TestEdge>(vert1, vert3);
		SplitPair<TestVertex, TestEdge> split2 = new SplitPair<TestVertex, TestEdge>(vert1, vert4);
		TestEdge edge = edge3_2;
		
		assertTrue(splitting.splitPairIsDominantedBy(graph2, split1, split2, edge));
		assertFalse(splitting.splitPairIsDominantedBy(graph2, split2, split1, edge));
		
		System.out.println(splitting.maximalSplitPairs(graph2, edge));
	}

}
