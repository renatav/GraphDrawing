package dsl.test;

import interpreter.java.Interpreter;
import models.java.LayoutGraph;

import org.junit.Test;

import junit.framework.TestCase;

public class TestLanguageAndInterpreter extends TestCase{

	private Interpreter interpreter;
	
	@Override
	public void setUp(){
		interpreter = Interpreter.getInstance();
	}
	
	@Test
	public void testAlgorithms(){
		
		LayoutGraph result;
		result = (LayoutGraph) interpreter.execute("layout graph algorithm level based tree  horizontal = 5, vertical = 10");
		assertEquals("level", result.getAlgorithm().get("name"));
		assertEquals(5, result.getAlgorithm().get("xDist"));
		assertEquals(10, result.getAlgorithm().get("yDist"));
		
		result = (LayoutGraph) interpreter.execute("layout graph algorithm node link tree orientation = left");
		assertEquals("node", result.getAlgorithm().get("name"));
		assertEquals("left", result.getAlgorithm().get("orientation"));
		
		result = (LayoutGraph) interpreter.execute("layout graph algorithm hierarchical same "
				+ "layer spacing = 10,  parent border = 10, move parent, hierarchy spacing = 3, fine tune ");
		assertEquals("hierarchical", result.getAlgorithm().get("name"));
		assertEquals(10.0, result.getAlgorithm().get("intraCellSpacing"));
		assertEquals(10, result.getAlgorithm().get("parentBorder"));
		assertEquals(true, result.getAlgorithm().get("moveParent"));
		assertEquals(3.0, result.getAlgorithm().get("interHierarchySpacing"));
		assertEquals(true, result.getAlgorithm().get("fineTune"));
		
		result = (LayoutGraph) interpreter.execute("layout graph algorithm circular");
		assertEquals("circular", result.getAlgorithm().get("name"));
		
		result = (LayoutGraph) interpreter.execute("layout graph algorithm Kamada Kawai distance multiplier = 23.2, length factor = 4");
		assertEquals("Kamada", result.getAlgorithm().get("name"));
		assertEquals(23.2, result.getAlgorithm().get("distanceMultiplier"));
		assertEquals(4.0, result.getAlgorithm().get("lengthFactor"));
		
		result = (LayoutGraph) interpreter.execute("layout graph algorithm organic fine tune, fine tuning radius = 12.2, optimize edge crossings, edge crossing factor = 12, optimize node distribution, average scale factor = 23, optimize border line,  border line factor = 12");
		assertEquals("organic", result.getAlgorithm().get("name"));
		assertEquals(true, result.getAlgorithm().get("fineTune"));
		assertEquals(12.2, result.getAlgorithm().get("fineTuningRadius"));
		assertEquals(true, result.getAlgorithm().get("optimizeEdgeCrossings"));
		assertEquals(12.0, result.getAlgorithm().get("edgeCrossingFactor"));
		assertEquals(true, result.getAlgorithm().get("optimizeNodeDistribution"));
		assertEquals(23.0, result.getAlgorithm().get("averageScaleFactor"));
		assertEquals(true, result.getAlgorithm().get("optimizeBorderLine"));
		assertEquals(12.0, result.getAlgorithm().get("borderLineFactor"));
		
	}
	
	@Test
	public void testAestheticCriteria(){
		LayoutGraph result;
		result = (LayoutGraph) interpreter.execute("layout graph criteria maximize minimal angle threshold=5, uniform flow direction = left, distribute nodes evenly ");
		assertNotNull(result);
		assertEquals(3, result.getAestheticCriteria().size());
		assertEquals(5, result.getAestheticCriteria().get(0).get("threshold"));
		assertEquals("angle", result.getAestheticCriteria().get(0).get("criterion"));
		assertEquals("flow", result.getAestheticCriteria().get(1).get("criterion"));
		assertEquals("left", result.getAestheticCriteria().get(1).get("direction"));
		assertEquals("distribute", result.getAestheticCriteria().get(2).get("criterion"));
		
	}

}
