package graph.test.algorithms;

import graph.algorithms.planarity.LRPartition;
import graph.application.elements.TestEdge;
import graph.application.elements.TestVertex;
import graph.elements.Graph;
import graph.traversal.DFSTreeTraversal;
import graph.trees.DFSTree;
import junit.framework.TestCase;

import org.junit.Test;

public class TestLRPartitioning extends TestCase{

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
		LRPartition<TestVertex, TestEdge> partition = new LRPartition<>(graph);
		partition.createLRPartition();
		
		DFSTreeTraversal<TestVertex, TestEdge> traversal = new  DFSTreeTraversal<TestVertex,TestEdge>(graph);
		DFSTree<TestVertex, TestEdge> tree = traversal.formDFSTree(graph.getVertices().get(0));
		

		assertEquals(tree.getBackEdges().size(), partition.getLeft().size() + partition.getRight().size());
		for (TestEdge e : tree.getBackEdges()){
			assertEquals(true, partition.getLeft().contains(e) || partition.getRight().contains(e));
			
			if (partition.getLeft().contains(e))
				assertEquals(false, partition.getRight().contains(e));
			
			if (partition.getRight().contains(e))
				assertEquals(false, partition.getLeft().contains(e));
			
			
		}
		
		
		
		System.out.println(partition);
		
		
		

	}


}
