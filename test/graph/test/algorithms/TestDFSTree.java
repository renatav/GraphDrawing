package graph.test.algorithms;

import graph.application.elements.TestEdge;
import graph.application.elements.TestVertex;
import graph.elements.Graph;
import graph.traversal.DFSTreeTraversal;
import graph.trees.DFSTree;

import org.junit.Test;

public class TestDFSTree extends GraphTestCase{

	
	@Test
	public void test(){
		DFSTreeTraversal<TestVertex, TestEdge> traversal = new DFSTreeTraversal<>(graph);
		DFSTree<TestVertex, TestEdge> tree = traversal.formDFSTree(vert1);


		Graph<TestVertex, TestEdge> graph2 = new Graph<TestVertex, TestEdge>();
		for (TestVertex v  : tree.getVertices())
			graph2.addVertex(v);
		for (TestEdge e : tree.getTreeEdges())
			graph2.addEdge(e);

		assertEquals(true, tree.isCyclic());
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
