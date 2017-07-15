package graph.layout;

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
	private static Map<AestheticCriteria, List<LayoutAlgorithms>> aestheticCriteriaAlgorithms;
	
	static {
		aestheticCriteriaAlgorithms = new HashMap<AestheticCriteria, List<LayoutAlgorithms>>();
		List<LayoutAlgorithms> planar = new ArrayList<LayoutAlgorithms>();
		planar.add(LayoutAlgorithms.CONVEX);
		planar.add(LayoutAlgorithms.TUTTE);
		aestheticCriteriaAlgorithms.put(AestheticCriteria.PLANAR, planar);
		List<LayoutAlgorithms> minEdgeCrosses = new ArrayList<LayoutAlgorithms>();
		minEdgeCrosses.add(LayoutAlgorithms.DAG);
		minEdgeCrosses.add(LayoutAlgorithms.FAST_ORGANIC);
		minEdgeCrosses.add(LayoutAlgorithms.FRUCHTERMAN_REINGOLD);
		minEdgeCrosses.add(LayoutAlgorithms.ISOM);
		minEdgeCrosses.add(LayoutAlgorithms.KAMADA_KAWAI);
		minEdgeCrosses.add(LayoutAlgorithms.SPTING2);
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
	}
	
	public static LayoutAlgorithms pick(AestheticCriteria criterion){
		//for now
		return aestheticCriteriaAlgorithms.get(criterion).get(0);
	}
}
