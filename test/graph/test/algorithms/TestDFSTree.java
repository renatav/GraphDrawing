package graph.test.algorithms;

import graph.elements.Graph;
import graph.test.elements.TestEdge;
import graph.test.elements.TestVertex;
import graph.traversal.DFSTree;
import graph.traversal.GraphTraversal;

import org.junit.Test;

import junit.framework.TestCase;

public class TestDFSTree extends TestCase{

	private Graph<TestVertex, TestEdge> graph;
	private TestVertex vert1 = new TestVertex("1");
	private TestVertex vert2 = new TestVertex("2");
	private TestVertex vert3 = new TestVertex("3");
	private TestVertex vert4 = new TestVertex("4");
	private TestVertex vert5 = new TestVertex("5");
	private TestVertex vert6 = new TestVertex("6");
	private TestVertex vert7 = new TestVertex("7");
	private TestVertex vert8 = new TestVertex("8");
	private TestVertex vert9 = new TestVertex("9");
	private TestVertex vert10 = new TestVertex("10");
	private TestVertex vert11 = new TestVertex("11");

	private TestEdge edge1 = new TestEdge(vert1, vert2);
	private TestEdge edge2 = new TestEdge(vert2, vert3);
	private TestEdge edge3 = new TestEdge(vert3, vert4);
	private TestEdge edge4 = new TestEdge(vert3, vert5);
	private TestEdge edge5 = new TestEdge(vert5, vert6);
	private TestEdge edge6 = new TestEdge(vert5, vert7);
	private TestEdge edge7 = new TestEdge(vert1, vert4);
	private TestEdge edge8 = new TestEdge(vert2, vert6);
	private TestEdge edge9 = new TestEdge(vert2, vert8);
	private TestEdge edge10 = new TestEdge(vert8, vert9);
	private TestEdge edge11 = new TestEdge(vert9, vert10);
	private TestEdge edge12 = new TestEdge(vert9, vert11);
	private TestEdge edge13 = new TestEdge(vert1, vert10);
	private TestEdge edge14 = new TestEdge(vert1, vert11);
	private TestEdge edge15 = new TestEdge(vert7, vert3);

	@Override
	public void setUp(){

		graph = new Graph<TestVertex, TestEdge>(false);

		graph.addVertex(vert1, vert2, vert3, vert4, vert5, vert6, vert7, vert8, vert9, vert10, vert11);
		graph.addEdge(edge1, edge2, edge3, edge4, edge5, edge6, edge7, edge8, edge9, edge10, edge11, edge12, edge13, edge14, edge15);

	}

	@Test
	public void test(){
		GraphTraversal<TestVertex, TestEdge> traversal = new GraphTraversal<>(graph);
		DFSTree<TestVertex, TestEdge> tree = traversal.formDFSTree(vert1);


		Graph<TestVertex, TestEdge> graph2 = new Graph<TestVertex, TestEdge>();
		for (TestVertex v  : tree.getVertices())
			graph2.addVertex(v);
		for (TestEdge e : tree.getTreeEdges())
			graph2.addEdge(e);

		assertEquals(false, tree.isCyclic());
		assertEquals(10, tree.allDescendantsOf(vert1, false).size());
		assertEquals(9, tree.allDescendantsOf(vert2, false).size());
		assertEquals(4, tree.allDescendantsOf(vert3, false).size());
		assertEquals(0, tree.allDescendantsOf(vert4, false).size());
		
		assertEquals(1, tree.lowpt(vert2));
		assertEquals(1, tree.lowpt(vert3));
		assertEquals(3, tree.highpt(vert5));
		assertEquals(2, tree.lowpt(vert5));
		assertEquals(1, tree.lowpt(vert8));
		
		assertEquals(edge7, tree.returningEdges(edge2).get(0));
		assertEquals(edge8, tree.returningEdges(edge4).get(0));
		assertEquals(edge14, tree.returningEdges(edge12).get(0));

	}


}
