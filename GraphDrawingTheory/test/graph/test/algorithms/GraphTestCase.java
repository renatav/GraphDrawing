package graph.test.algorithms;

import graph.elements.Graph;
import graph.test.elements.TestEdge;
import graph.test.elements.TestVertex;
import junit.framework.TestCase;

public class GraphTestCase extends TestCase{

	protected Graph<TestVertex, TestEdge> graph;
	protected TestVertex vert1 = new TestVertex("1");
	protected TestVertex vert2 = new TestVertex("2");
	protected TestVertex vert3 = new TestVertex("3");
	protected TestVertex vert4 = new TestVertex("4");
	protected TestVertex vert5 = new TestVertex("5");
	protected TestVertex vert6 = new TestVertex("6");
	protected TestVertex vert7 = new TestVertex("7");
	protected TestVertex vert8 = new TestVertex("8");
	protected TestVertex vert9 = new TestVertex("9");
	protected TestVertex vert10 = new TestVertex("10");
	protected TestVertex vert11 = new TestVertex("11");

	protected TestEdge edge1 = new TestEdge(vert1, vert2);
	protected TestEdge edge2 = new TestEdge(vert2, vert3);
	protected TestEdge edge3 = new TestEdge(vert3, vert4);
	protected TestEdge edge4 = new TestEdge(vert3, vert5);
	protected TestEdge edge5 = new TestEdge(vert5, vert6);
	protected TestEdge edge6 = new TestEdge(vert5, vert7);
	protected TestEdge edge7 = new TestEdge(vert1, vert4);
	protected TestEdge edge8 = new TestEdge(vert2, vert6);
	protected TestEdge edge9 = new TestEdge(vert2, vert8);
	protected TestEdge edge10 = new TestEdge(vert8, vert9);
	protected TestEdge edge11 = new TestEdge(vert9, vert10);
	protected TestEdge edge12 = new TestEdge(vert9, vert11);
	protected TestEdge edge13 = new TestEdge(vert1, vert10);
	protected TestEdge edge14 = new TestEdge(vert1, vert11);
	protected TestEdge edge15 = new TestEdge(vert7, vert3);

	@Override
	public void setUp(){

		graph = new Graph<TestVertex, TestEdge>(false);

		graph.addVertex(vert1, vert2, vert3, vert4, vert5, vert6, vert7, vert8, vert9, vert10, vert11);
		graph.addEdge(edge1, edge2, edge3, edge4, edge5, edge6, edge7, edge8, edge9, edge10, edge11, edge12, edge13, edge14, edge15);

	}
}
