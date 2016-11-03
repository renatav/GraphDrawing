package graph.test.planarity;

import graph.algorithms.planarity.BoyerMyrvoldPlanarity;
import graph.algorithms.planarity.PlanarityTestingAlgorithm;
import graph.elements.Graph;
import graph.test.elements.TestEdge;
import graph.test.elements.TestVertex;

import org.junit.Test;

import junit.framework.TestCase;

public class BoyerMyrvoldPlanarityTest extends TestCase {

	@Test
	public void testBoyerMyrvoldPlanarity() {
		TestVertex[] V = new TestVertex[28];
		int[][] E = {{9,11},{9,13},{9,16},{9,25},{11,10},{11,15},{11,24},{10,12},{10,15},{10,26},
				     {13,12},{13,14},{13,4},{12,5},{12,6},{15,14},{15,27},{14,17},{14,20},{17,16},
				     {17,21},{17,23},{16,19},{16,22},{19,18},{19,7},{19,0},{18,21},{18,1},{18,2},
				     {21,3},{21,8}};

		Graph<TestVertex, TestEdge> g = new Graph<TestVertex, TestEdge>(false);
		for (int i=0; i<28; i++) {
			V[i] = new TestVertex(String.valueOf(i));
			g.addVertex(V[i]);
		}
		for (int[] e : E) {
			TestEdge edge = new TestEdge(V[e[0]], V[e[1]]);
			g.addEdge(edge);
		}

		PlanarityTestingAlgorithm<TestVertex, TestEdge> pt = new BoyerMyrvoldPlanarity<>();

		assertEquals(pt.isPlannar(g), true);
	}
	
}
