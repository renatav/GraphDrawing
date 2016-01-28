package graph.layout.organic;

import com.mxgraph.layout.mxFastOrganicLayout;

import graph.elements.Edge;
import graph.elements.Vertex;
import graph.layout.AbstractJGraphXLayouter;
import graph.layout.GraphLayoutProperties;
import graph.layout.PropertyEnums.FastOrganicProperties;

public class JGraphFastorganicLayouter<V extends Vertex, E extends Edge<V>> extends AbstractJGraphXLayouter<V, E> {

	@Override
	protected void initLayouter(GraphLayoutProperties layoutProperties) {
		
		mxFastOrganicLayout fastOrganicLayout = new mxFastOrganicLayout(jGraphXGraph);
		
		Double forceContstant = (Double) layoutProperties.getProperty(FastOrganicProperties.FORCE_CONSTANT);
		if (forceContstant != null)
			fastOrganicLayout.setForceConstant(forceContstant);
		
		Double minDistanceLimit = (Double) layoutProperties.getProperty(FastOrganicProperties.MINIMAL_DISTANCE_LIMIT);
		if (minDistanceLimit != null)
			fastOrganicLayout.setMinDistanceLimit(minDistanceLimit);
		
		Double initalTempt = (Double) layoutProperties.getProperty(FastOrganicProperties.INITIAL_TEMP);
		if (initalTempt != null)
			fastOrganicLayout.setInitialTemp(initalTempt);
		
		Double maxIterations = (Double) layoutProperties.getProperty(FastOrganicProperties.MAX_ITERATIONS);
		if (maxIterations != null)
			fastOrganicLayout.setMaxIterations(maxIterations);
		
		layouter = fastOrganicLayout;
		
	}
	
	

}
