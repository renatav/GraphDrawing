package graph.layout.tree;

import graph.elements.Edge;
import graph.elements.Vertex;
import graph.layout.AbstractPrefuseLayouter;
import graph.layout.GraphLayoutProperties;
import graph.layout.PropertyEnums.BalloonProperties;
import prefuse.action.layout.graph.BalloonTreeLayout;

public class PrefuseBalloonLayouter<V extends Vertex, E extends Edge<V>> extends AbstractPrefuseLayouter<V, E>{

	@Override
	protected void initLayouter(GraphLayoutProperties layoutProperties) {
		Integer minRadius = (Integer) layoutProperties.getProperty(BalloonProperties.MIN_RADIUS);
		if (minRadius != null)
			layouter = new BalloonTreeLayout("graph", minRadius);
		else
			layouter = new BalloonTreeLayout("graph");
		
	}

}
