package graph.layout.force.directed;

import edu.uci.ics.jung.algorithms.layout.SpringLayout2;
import graph.elements.Edge;
import graph.elements.Vertex;
import graph.layout.GraphLayoutProperties;
import graph.layout.PropertyEnums.SpringProperties;

/**
 * Layouter using JUNG framework's spring layout
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type 
 */
public class SpringLayouter<V extends Vertex, E extends Edge<V>> extends AbstractForceDirectedLayouter<V,E> {

	@Override
	protected void initLayouter(GraphLayoutProperties layoutProperties) {
		SpringLayout2<V, E> springLayout = new SpringLayout2<>(jungGraph);
		
		Object forceMultiplierVal = layoutProperties.getProperty(SpringProperties.FORCE_MULTIPLIER);
		Object repulsionRangeVal = layoutProperties.getProperty(SpringProperties.REPULSION_RANGE);
		Object stretchVal = layoutProperties.getProperty(SpringProperties.STRETCH);
		
		if (forceMultiplierVal != null && forceMultiplierVal instanceof Double)
			springLayout.setForceMultiplier((double) forceMultiplierVal);
		if (repulsionRangeVal != null && repulsionRangeVal instanceof Double)
			springLayout.setRepulsionRange(((Double)repulsionRangeVal).intValue());
		if (stretchVal != null && stretchVal instanceof Double)
			springLayout.setStretch((double)stretchVal);
		
		//springLayout.setForceMultiplier(5);
		//springLayout.setRepulsionRange(3);
		layouter = springLayout;
	}

}