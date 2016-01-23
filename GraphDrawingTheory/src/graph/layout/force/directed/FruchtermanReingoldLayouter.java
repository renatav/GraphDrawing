package graph.layout.force.directed;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import graph.elements.Edge;
import graph.elements.Vertex;
import graph.layout.GraphLayoutProperties;
import graph.layout.PropertyEnums.FruchtermanReingoldProperties;

public class FruchtermanReingoldLayouter <V extends Vertex, E extends Edge<V>> extends AbstractForceDirectedLayouter<V,E> {



	@Override
	protected void initLayouter(GraphLayoutProperties layoutProperties) {
		FRLayout<V,E> frLayout = new FRLayout<V, E>(jungGraph);
		
		Object attractionMultiplier = layoutProperties.getProperty(FruchtermanReingoldProperties.ATTRACTION_MULTIPLIER);
		Object repulsionMultiplier = layoutProperties.getProperty(FruchtermanReingoldProperties.REPULSION_MULTIPLIER);
		Object maximumIterations = layoutProperties.getProperty(FruchtermanReingoldProperties.MAXIMUM_ITERATIONS);
		
		if (attractionMultiplier != null && attractionMultiplier instanceof Double)
			frLayout.setAttractionMultiplier((double)attractionMultiplier);
		if (repulsionMultiplier != null && repulsionMultiplier instanceof Double)
			frLayout.setRepulsionMultiplier((double)repulsionMultiplier);
		if (maximumIterations != null && maximumIterations instanceof Double)
			frLayout.setMaxIterations(((Double)maximumIterations).intValue());
		
		layouter = frLayout;
		
		
	}

}
