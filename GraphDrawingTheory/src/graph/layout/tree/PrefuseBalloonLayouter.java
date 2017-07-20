package graph.layout.tree;

import graph.elements.Edge;
import graph.elements.Vertex;
import graph.layout.AbstractPrefuseLayouter;
import graph.layout.DefaultGraphLayoutProperties;
import graph.layout.GraphLayoutProperties;
import graph.layout.LayoutAlgorithms;
import graph.layout.PropertyEnums.BalloonProperties;
import prefuse.action.layout.graph.BalloonTreeLayout;

/**
 * A layouter which creates a drawing of a graph using prefuse's balloon tree drawing algorithm
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type 
 */
public class PrefuseBalloonLayouter<V extends Vertex, E extends Edge<V>> extends AbstractPrefuseLayouter<V, E>{

	@Override
	protected void initLayouter(GraphLayoutProperties layoutProperties) {
		Integer minRadius = null;
		if (layoutProperties == null)
			layoutProperties = DefaultGraphLayoutProperties.getDefaultLayoutProperties(LayoutAlgorithms.BALLOON, graph);
		
		if (layoutProperties.getProperty(BalloonProperties.MIN_RADIUS) != null)
			minRadius = (Integer) layoutProperties.getProperty(BalloonProperties.MIN_RADIUS);
		if (minRadius != null)
			layouter = new BalloonTreeLayout("graph", minRadius);
		else
			layouter = new BalloonTreeLayout("graph");
		
	}

}
