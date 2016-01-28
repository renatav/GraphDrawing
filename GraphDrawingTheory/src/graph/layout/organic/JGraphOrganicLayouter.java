package graph.layout.organic;

import com.mxgraph.layout.mxOrganicLayout;

import graph.elements.Edge;
import graph.elements.Vertex;
import graph.layout.AbstractJGraphXLayouter;
import graph.layout.GraphLayoutProperties;

public class JGraphOrganicLayouter<V extends Vertex, E extends Edge<V>> extends AbstractJGraphXLayouter<V, E>{

	@Override
	protected void initLayouter(GraphLayoutProperties layoutProperties) {
		mxOrganicLayout organiclayouter = new mxOrganicLayout(jGraphXGraph);
		
		
		layouter = organiclayouter;
		
	}
	

}
