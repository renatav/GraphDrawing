package graph.layout;

import java.security.AlgorithmConstraints;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class used to map aethetic criteria and algoritithms which were designed
 * in accordance with them
 * @author Renata
 */
public class LayoutAestheticMap {

	/**
	 * Map of aeshtetic criteria and a list of algorithms which were designed in
	 * accordance to them
	 */
	public static Map<AestheticCriteria, List<LayoutAlgorithms>> aestheticCriteriaAlgorithms;
	public static Map<LayoutAlgorithms, List<AestheticCriteria>> algorithmsCriteriaMap;
	
	static {
		aestheticCriteriaAlgorithms = new HashMap<AestheticCriteria, List<LayoutAlgorithms>>();
		List<LayoutAlgorithms> planar = new ArrayList<LayoutAlgorithms>();
		planar.add(LayoutAlgorithms.CONVEX);
		planar.add(LayoutAlgorithms.TUTTE);
		aestheticCriteriaAlgorithms.put(AestheticCriteria.PLANAR, planar);
		List<LayoutAlgorithms> minEdgeCrosses = new ArrayList<LayoutAlgorithms>();
		minEdgeCrosses.add(LayoutAlgorithms.FAST_ORGANIC);
		minEdgeCrosses.add(LayoutAlgorithms.FRUCHTERMAN_REINGOLD);
		minEdgeCrosses.add(LayoutAlgorithms.ISOM);
		minEdgeCrosses.add(LayoutAlgorithms.KAMADA_KAWAI);
		aestheticCriteriaAlgorithms.put(AestheticCriteria.MINIMAL_EDGE_CROESSES, minEdgeCrosses);
		List<LayoutAlgorithms> symmetric = new ArrayList<LayoutAlgorithms>();
		symmetric.add(LayoutAlgorithms.CONCENTRIC);
		aestheticCriteriaAlgorithms.put(AestheticCriteria.SYMMETRIC, symmetric);
		List<LayoutAlgorithms> straightLine = new ArrayList<LayoutAlgorithms>();
		straightLine.add(LayoutAlgorithms.TUTTE);
		straightLine.add(LayoutAlgorithms.CONVEX);
		List<LayoutAlgorithms> flow = new ArrayList<LayoutAlgorithms>();
		flow.add(LayoutAlgorithms.HIERARCHICAL);
		aestheticCriteriaAlgorithms.put(AestheticCriteria.UNIFORM_FLOW, flow);
		
		algorithmsCriteriaMap = new HashMap<LayoutAlgorithms, List<AestheticCriteria>>();
		List<AestheticCriteria> organicCriteria = new ArrayList<AestheticCriteria>();
		organicCriteria.add(AestheticCriteria.MINIMAL_EDGE_CROESSES);
		organicCriteria.add(AestheticCriteria.NODE_DISTRIBUTION);
		organicCriteria.add(AestheticCriteria.MINIMUM_BANDS);
		organicCriteria.add(AestheticCriteria.EDGE_VARIATION);
		organicCriteria.add(AestheticCriteria.EDGE_LENGTHS);
		algorithmsCriteriaMap.put(LayoutAlgorithms.ORGANIC, organicCriteria);
		

		List<AestheticCriteria> radialCriteria = new ArrayList<AestheticCriteria>();
		radialCriteria.add(AestheticCriteria.PLANAR);
		radialCriteria.add(AestheticCriteria.SYMMETRIC);
		radialCriteria.add(AestheticCriteria.NODE_DISTRIBUTION);
		radialCriteria.add(AestheticCriteria.MINIMUM_BANDS);
		radialCriteria.add(AestheticCriteria.EDGE_VARIATION);
		radialCriteria.add(AestheticCriteria.EDGE_LENGTHS);
		algorithmsCriteriaMap.put(LayoutAlgorithms.RADIAL_TREE, radialCriteria);
	
		
		List<AestheticCriteria> levelBaseCriteria = new ArrayList<AestheticCriteria>();
		levelBaseCriteria.add(AestheticCriteria.PLANAR);
		levelBaseCriteria.add(AestheticCriteria.UNIFORM_FLOW);
		levelBaseCriteria.add(AestheticCriteria.MINIMUM_BANDS);
		levelBaseCriteria.add(AestheticCriteria.EDGE_LENGTHS);
		levelBaseCriteria.add(AestheticCriteria.EDGE_VARIATION);
		algorithmsCriteriaMap.put(LayoutAlgorithms.NODE_LINK_TREE, levelBaseCriteria);
				
		
		List<AestheticCriteria> balloonCriteria = new ArrayList<AestheticCriteria>();
		balloonCriteria.add(AestheticCriteria.PLANAR);
		balloonCriteria.add(AestheticCriteria.SYMMETRIC);
		balloonCriteria.add(AestheticCriteria.MINIMUM_BANDS);
		algorithmsCriteriaMap.put(LayoutAlgorithms.BALLOON, balloonCriteria);
		
		List<AestheticCriteria> hierarhchicalCriteria = new ArrayList<AestheticCriteria>();
		hierarhchicalCriteria.add(AestheticCriteria.MINIMAL_EDGE_CROESSES);
		hierarhchicalCriteria.add(AestheticCriteria.UNIFORM_FLOW);
		algorithmsCriteriaMap.put(LayoutAlgorithms.HIERARCHICAL, hierarhchicalCriteria);
	
		List<AestheticCriteria> symmetricCriteria = new ArrayList<AestheticCriteria>();
		symmetricCriteria.add(AestheticCriteria.SYMMETRIC);
		symmetricCriteria.add(AestheticCriteria.MINIMUM_BANDS);
		algorithmsCriteriaMap.put(LayoutAlgorithms.CONCENTRIC, symmetricCriteria);
		
		List<AestheticCriteria> tutteCriteria = new ArrayList<AestheticCriteria>();
		tutteCriteria.add(AestheticCriteria.PLANAR);
		tutteCriteria.add(AestheticCriteria.MINIMUM_BANDS);
		algorithmsCriteriaMap.put(LayoutAlgorithms.TUTTE, tutteCriteria);
		
		//TODO when more stable
//		List<AestheticCriteria> convexCriteria = new ArrayList<AestheticCriteria>();
//		convexCriteria.add(AestheticCriteria.PLANAR);
//		convexCriteria.add(AestheticCriteria.MINIMUM_BANDS);
//		convexCriteria.add(AestheticCriteria.MINIMUM_ANGLES);
//		algorithmsCriteriaMap.put(LayoutAlgorithms.CONVEX, convexCriteria);
		
		List<AestheticCriteria> circularCriteria = new ArrayList<AestheticCriteria>();
		circularCriteria.add(AestheticCriteria.MINIMUM_BANDS);
		algorithmsCriteriaMap.put(LayoutAlgorithms.CIRCLE, circularCriteria);
		
		List<AestheticCriteria> boxCriteria = new ArrayList<AestheticCriteria>();
		algorithmsCriteriaMap.put(LayoutAlgorithms.BOX, boxCriteria);
		
	}
	
	
	
}
