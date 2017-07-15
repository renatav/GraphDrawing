package graph.layout.force.directed;

import edu.uci.ics.jung.algorithms.layout.DAGLayout;
import graph.elements.Edge;
import graph.elements.Vertex;
import graph.layout.GraphLayoutProperties;
import graph.layout.PropertyEnums.SpringProperties;

/**
 * Layouter using JUNG framework's DAG layout
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type 
 */
public class DAGLayouter<V extends Vertex, E extends Edge<V>> extends AbstractForceDirectedLayouter<V,E>{

	@Override
	protected void initLayouter(GraphLayoutProperties layoutProperties) {
		DAGLayout<V, E> dagLayout = new DAGLayout<>(jungGraph);
		
		Object forceMultiplierVal = layoutProperties.getProperty(SpringProperties.FORCE_MULTIPLIER);
		Object repulsionRangeVal = layoutProperties.getProperty(SpringProperties.REPULSION_RANGE);
		Object stretchVal = layoutProperties.getProperty(SpringProperties.STRETCH);
		
		if (forceMultiplierVal != null && forceMultiplierVal instanceof Double)
			dagLayout.setForceMultiplier((double) forceMultiplierVal);
		if (repulsionRangeVal != null && repulsionRangeVal instanceof Double)
			dagLayout.setRepulsionRange(((Double)repulsionRangeVal).intValue());
		if (stretchVal != null && stretchVal instanceof Double)
			dagLayout.setStretch((double)stretchVal);
		
		layouter = dagLayout;
		
	}

}
