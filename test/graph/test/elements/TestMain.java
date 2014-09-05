package graph.test.elements;

import graph.elements.Graph;
import graph.elements.Path;
import graph.ordering.TopologicalOrdering;
import graph.traversal.GraphTraversal;

import java.util.ArrayList;
import java.util.List;

public class TestMain {

	public static void main(String[] args) {
		Graph<TestVertex, TestEdge> graph = new Graph<TestVertex, TestEdge>(true);
		TestVertex vert1 = new TestVertex("1");
		TestVertex vert2 = new TestVertex("2");
		TestVertex vert3 = new TestVertex("3");
		TestVertex vert4 = new TestVertex("4");
		TestVertex vert5 = new TestVertex("5");
		TestVertex vert6 = new TestVertex("6");
		
		TestEdge edge1 = new TestEdge(vert1, vert2);
		TestEdge edge2 = new TestEdge(vert1, vert3);
		TestEdge edge3 = new TestEdge(vert2, vert6);
		TestEdge edge4 = new TestEdge(vert6, vert3);
		TestEdge edge5 = new TestEdge(vert3, vert5);
		TestEdge edge6 = new TestEdge(vert3, vert4);
		TestEdge edge7 = new TestEdge(vert5, vert4);
		
		graph.addVertex(vert1,vert2,vert3,vert4,vert5,vert6);
		graph.addEdge(edge1,edge2,edge3,edge4,edge5,edge6,edge7);
		
		GraphTraversal<TestVertex,TestEdge> traverse = new GraphTraversal<TestVertex, TestEdge>(graph);
		List<Path<TestVertex, TestEdge>> paths = new ArrayList<Path<TestVertex, TestEdge>>();
		paths = traverse.findAllPathsDFS(vert1, vert5);
		
		for (Path<TestVertex, TestEdge> p : paths)
			System.out.println(p);
		
		//System.out.println(traverse.getShortestPath(vert2, vert3));
		
		//System.out.println(graph.isConnected());
		
		System.out.println(graph.isCyclic());
		
		TopologicalOrdering<TestVertex, TestEdge> topOrder = new TopologicalOrdering<>(graph);
		System.out.println(topOrder.order());
		
		
	}

}
