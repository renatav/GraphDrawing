package graph.test.tree;

import graph.application.elements.TestEdge;
import graph.application.elements.TestVertex;
import graph.elements.Graph;
import graph.tree.bc.BCTree;
import junit.framework.TestCase;

import org.junit.Test;

public class TestBCTree extends TestCase {
	
	TestVertex vertA = new TestVertex("A");
	TestVertex vertB = new TestVertex("B");
	TestVertex vertC = new TestVertex("C");
	TestVertex vertD = new TestVertex("D");
	TestVertex vertE = new TestVertex("E");
	TestVertex vertF = new TestVertex("F");
	TestVertex vertG = new TestVertex("G");
	TestVertex vertH = new TestVertex("H");
	TestVertex vertI = new TestVertex("I");
	
	TestEdge edgeA1 = new TestEdge(vertA, vertB);
	TestEdge edgeA2 = new TestEdge(vertA, vertC);
	TestEdge edgeA3 = new TestEdge(vertB, vertC);
	TestEdge edgeA4 = new TestEdge(vertC, vertD);
	TestEdge edgeA5 = new TestEdge(vertC, vertE);
	TestEdge edgeA6 = new TestEdge(vertD, vertE);
	TestEdge edgeA7 = new TestEdge(vertD, vertF);
	TestEdge edgeA8 = new TestEdge(vertE, vertG);
	TestEdge edgeA9 = new TestEdge(vertG, vertH);
	TestEdge edgeA10 = new TestEdge(vertH, vertI);
	TestEdge edgeA11 = new TestEdge(vertG, vertI);

	Graph<TestVertex, TestEdge> graph;
	
	@Override
	public void setUp(){
		
		graph = new Graph<>();
		graph.addVertex(vertA, vertB, vertC, vertD, vertE, vertF, vertG, vertH, vertI);
		graph.addEdge(edgeA1, edgeA2, edgeA3, edgeA4, edgeA5, edgeA6, edgeA7, edgeA8, edgeA9, edgeA10, edgeA11);
		
	}
	
	@Test
	public void test(){
		BCTree<TestVertex, TestEdge> tree = new BCTree<>(graph);
		assertNotNull(tree.getVertices());
		assertTrue(tree.getVertices().size() > 0);
		assertTrue(tree.getEdges().size() > 0);
		System.out.println(tree);
		
	}
}
