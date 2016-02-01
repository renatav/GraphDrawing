package graph.layout.partition;

import graph.elements.Edge;
import graph.elements.Vertex;
import graph.layout.AbstractJGraphXLayouter;
import graph.layout.GraphLayoutProperties;
import graph.layout.PropertyEnums.PartitionProperties;

import com.mxgraph.layout.mxPartitionLayout;

public class JGraphPartitionLayouter < V extends Vertex, E extends Edge<V>> extends AbstractJGraphXLayouter<V, E>  {

	@Override
	protected void initLayouter(GraphLayoutProperties layoutProperties) {

		Boolean horizontal = (Boolean) layoutProperties.getProperty(PartitionProperties.HORIZONTAL);
		Integer spacing = (Integer) layoutProperties.getProperty(PartitionProperties.SPACING);
		Integer border = (Integer) layoutProperties.getProperty(PartitionProperties.BORDER);

		mxPartitionLayout parititionLayouer;
		if (spacing != null){
			if (border != null)
				parititionLayouer = new mxPartitionLayout(jGraphXGraph, horizontal, spacing, border);
			else
				parititionLayouer = new mxPartitionLayout(jGraphXGraph, horizontal, spacing);
		}
		else
			parititionLayouer = new mxPartitionLayout(jGraphXGraph, horizontal);


		layouter = parititionLayouer;

	}


}
