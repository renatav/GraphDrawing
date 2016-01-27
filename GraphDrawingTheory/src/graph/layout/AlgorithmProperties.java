package graph.layout;

import java.util.HashMap;
import java.util.Map;

public class AlgorithmProperties {

	private static Map<LayoutAlgorithms, Boolean> oneGraph;
	
	static {
		oneGraph = new HashMap<LayoutAlgorithms, Boolean>();
		oneGraph.put(LayoutAlgorithms.BALLOON, true);
		oneGraph.put(LayoutAlgorithms.BOX, true);
		oneGraph.put(LayoutAlgorithms.CIRCLE, false);
		oneGraph.put(LayoutAlgorithms.CONCENTRIC, true);
		oneGraph.put(LayoutAlgorithms.FRUCHTERMAN_REINGOLD, false);
		oneGraph.put(LayoutAlgorithms.KAMADA_KAWAI, false);
		oneGraph.put(LayoutAlgorithms.RADIAL_TREE, true);
		oneGraph.put(LayoutAlgorithms.RANDOM, false);
		oneGraph.put(LayoutAlgorithms.SPRING, false);
		oneGraph.put(LayoutAlgorithms.DAG, false);
		oneGraph.put(LayoutAlgorithms.TREE, true);
		oneGraph.put(LayoutAlgorithms.TUTTE, true);
		oneGraph.put(LayoutAlgorithms.ISOM, true);
		oneGraph.put(LayoutAlgorithms.COMPACT_TREE, false);
		
		
	}
	
	public static boolean isOneGraph(LayoutAlgorithms algorithm){
		return oneGraph.get(algorithm);
	}
	
}
