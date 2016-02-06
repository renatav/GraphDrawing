package graph.layout.tree;

import graph.elements.Edge;
import graph.elements.Vertex;
import graph.layout.AbstractPrefuseLayouter;
import graph.layout.GraphLayoutProperties;
import graph.layout.PropertyEnums.SquarifiedTreemapProperties;
import prefuse.action.layout.graph.SquarifiedTreeMapLayout;

public class PrefuseSquarifiedTreeMapLayouter<V extends Vertex, E extends Edge<V>> extends AbstractPrefuseLayouter<V, E> {

	@Override
	protected void initLayouter(GraphLayoutProperties layoutProperties) {
		
		Double frame = (Double) layoutProperties.getProperty(SquarifiedTreemapProperties.FRAME);
		if (frame != null)
			layouter = new SquarifiedTreeMapLayout("graph", frame);
		else 
			layouter = new SquarifiedTreeMapLayout("graph");
	}

}
