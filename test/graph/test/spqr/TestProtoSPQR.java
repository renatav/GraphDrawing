package graph.test.spqr;

import graph.elements.Graph;
import graph.exception.CannotBeAppliedException;
import graph.test.elements.TestEdge;
import graph.test.elements.TestVertex;
import graph.tree.spqr.ProtoSPQRTree;
import junit.framework.TestCase;

import org.junit.Test;

public class TestProtoSPQR extends TestCase{
	
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
	TestEdge edge14 = new TestEdge(vert5, vert9);
	
	TestEdge edge15 = new TestEdge(vert3, vert2);
	
	Graph<TestVertex, TestEdge> graph;
	@Override
	public void setUp(){
		graph = new Graph<>();
		graph.addVertex(vert1, vert2, vert3, vert4, vert5, vert6, vert7, vert8, vert9, vert10);
		graph.addEdge(edge1, edge2, edge3, edge4, edge5, edge6, edge7, edge8, edge9, edge10, 
				edge11, edge12, edge14);
	
	}
	
	@Test
	public void test(){
		
			//series case
			ProtoSPQRTree<TestVertex, TestEdge> protoTree = new ProtoSPQRTree<TestVertex, TestEdge>(edge1, graph);
			assertNotNull(protoTree.getRoot());
			//Rigid case
			graph.addEdge(edge15);
			//ProtoSPQRTree<TestVertex, TestEdge> protoTree = new ProtoSPQRTree<TestVertex, TestEdge>(graph, edge1);
			protoTree = new ProtoSPQRTree<TestVertex, TestEdge>(edge7, graph);
			assertNotNull(protoTree.getRoot());
			
		
	}
	

}
