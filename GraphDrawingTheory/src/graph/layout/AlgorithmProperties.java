package graph.layout;

import java.util.HashMap;
import java.util.Map;

public class AlgorithmProperties {

	private static Map<LayoutAlgorithms, Boolean> oneGraph;
	
	static {
		oneGraph = new HashMap<LayoutAlgorithms, Boolean>();
		oneGraph.put(LayoutAlgorithms.BALLOON, false);
		oneGraph.put(LayoutAlgorithms.BOX, true);
		oneGraph.put(LayoutAlgorithms.CIRCLE, false);
		oneGraph.put(LayoutAlgorithms.CONCENTRIC, true);
		oneGraph.put(LayoutAlgorithms.FRUCHTERMAN_REINGOLD, false);
		oneGraph.put(LayoutAlgorithms.KAMADA_KAWAI, false);
		oneGraph.put(LayoutAlgorithms.RADIAL_TREE, false);
		oneGraph.put(LayoutAlgorithms.RANDOM, false);
		oneGraph.put(LayoutAlgorithms.SPRING, false);
		oneGraph.put(LayoutAlgorithms.TREE, false);
		oneGraph.put(LayoutAlgorithms.TUTTE, true);
		
	}
	
	public static boolean isOneGraph(LayoutAlgorithms algorithm){
		return oneGraph.get(algorithm);
	}
	
}
