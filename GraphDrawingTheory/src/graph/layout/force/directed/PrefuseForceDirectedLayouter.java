package graph.layout.force.directed;

import graph.elements.Edge;
import graph.elements.Vertex;
import graph.layout.AbstractPrefuseLayouter;
import graph.layout.GraphLayoutProperties;
import prefuse.action.layout.graph.ForceDirectedLayout;

public class PrefuseForceDirectedLayouter<V extends Vertex, E extends Edge<V>> extends AbstractPrefuseLayouter<V, E> {

	@Override
	protected void initLayouter(GraphLayoutProperties layoutProperties) {
		
		//don't enforce bounds 
		//don't animate, run once
		layouter = new ForceDirectedLayout("graph", false, true);
		
	}
	

}
