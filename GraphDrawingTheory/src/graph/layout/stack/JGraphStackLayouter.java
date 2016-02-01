package graph.layout.stack;

import com.mxgraph.layout.mxStackLayout;

import graph.elements.Edge;
import graph.elements.Vertex;
import graph.layout.AbstractJGraphXLayouter;
import graph.layout.GraphLayoutProperties;
import graph.layout.PropertyEnums.StackProperties;

public class JGraphStackLayouter< V extends Vertex, E extends Edge<V>> extends AbstractJGraphXLayouter<V, E> {

	
	@Override
	protected void initLayouter(GraphLayoutProperties layoutProperties) {
		
		
		Boolean horizontal = (Boolean) layoutProperties.getProperty(StackProperties.HORIZONTAL);
		Integer spacing = (Integer) layoutProperties.getProperty(StackProperties.SPACING);
		
		mxStackLayout stackLayout;
		if (spacing != null)
			stackLayout = new mxStackLayout(jGraphXGraph, horizontal, spacing);
		else
			stackLayout = new mxStackLayout(jGraphXGraph, horizontal);
		
		layouter = stackLayout;
		
	}

}
