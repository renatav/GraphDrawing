package graph.test.operations;

import graph.application.elements.TestEdge;
import graph.application.elements.TestVertex;
import graph.elements.Graph;
import graph.operations.GraphOperations;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

public class TestGraphOperations extends TestCase{
	
	@Test
	public void test(){
		
		GraphOperations<TestVertex, TestEdge> operations = new GraphOperations<>();
		
		TestVertex vert1 = new TestVertex("1");
		TestVertex vert2 = new TestVertex("2");
		TestVertex vert3 = new TestVertex("3");
		List<TestVertex> vertices = new ArrayList<>();
		vertices.add(vert1);
		vertices.add(vert2);
		vertices.add(vert3);
		
		Graph<TestVertex, TestEdge> cycle = operations.formCycleGraph(vertices, TestEdge.class);

		assertTrue(cycle.edgeesBetween(vert1, vert2).size() == 1);
		assertTrue(cycle.edgeesBetween(vert2, vert3).size() == 1);
		assertTrue(cycle.edgeesBetween(vert1, vert3).size() == 1);
	}
}
