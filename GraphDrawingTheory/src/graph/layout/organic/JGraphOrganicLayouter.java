package graph.layout.organic;

import com.mxgraph.layout.mxOrganicLayout;

import graph.elements.Edge;
import graph.elements.Vertex;
import graph.layout.AbstractJGraphXLayouter;
import graph.layout.GraphLayoutProperties;
import graph.layout.PropertyEnums.OrganicProperties;

public class JGraphOrganicLayouter<V extends Vertex, E extends Edge<V>> extends AbstractJGraphXLayouter<V, E>{

	@Override
	protected void initLayouter(GraphLayoutProperties layoutProperties) {
		mxOrganicLayout organicLayouter = new mxOrganicLayout(jGraphXGraph);
		
		Boolean optimizeEdgeCrossing = (Boolean) layoutProperties.getProperty(OrganicProperties.IS_OPTIMIZE_EDGE_CROSSING);
		Double edgeCrossingFactor = (Double) layoutProperties.getProperty(OrganicProperties.EDGE_CROSSING_FACTOR);
		Boolean optimizeEdgeDistance = (Boolean) layoutProperties.getProperty(OrganicProperties.IS_OPTIMIZE_EDGE_DISTANCE);
		Double edgeDistanceFactor = (Double) layoutProperties.getProperty(OrganicProperties.EDGE_DISTANCE_FACTOR);
		Boolean optimizeBorderLine = (Boolean) layoutProperties.getProperty(OrganicProperties.IS_OPTIMIZE_BORDER_LINE);
		Double borderLineFactor = (Double) layoutProperties.getProperty(OrganicProperties.BORDER_LINE_FACTOR);
		Boolean optimizeNodeDistribution = (Boolean) layoutProperties.getProperty(OrganicProperties.IS_OPTIMIZE_NODE_DISTRIBUTION);
		Double nodeDistributionFactor = (Double) layoutProperties.getProperty(OrganicProperties.NODE_DISTRIBUTION_FACTOR);
		Boolean fineTuning = (Boolean) layoutProperties.getProperty(OrganicProperties.IS_FINE_TUNING);
		Double fineTuningRadius = (Double) layoutProperties.getProperty(OrganicProperties.FINE_TUNING_RADIUS);
		Double averageNodeArea = (Double) layoutProperties.getProperty(OrganicProperties.AVERAGE_NODE_AREA);
		Double averageScaleFactor = (Double) layoutProperties.getProperty(OrganicProperties.AVERAGE_SCALE_FACTOR);
		Integer maxIterations = (Integer) layoutProperties.getProperty(OrganicProperties.MAX_ITERATIONS);
		
		organicLayouter.setOptimizeEdgeCrossing(optimizeEdgeCrossing);
		if (optimizeEdgeCrossing && edgeCrossingFactor != null)
			organicLayouter.setEdgeCrossingCostFactor(edgeCrossingFactor);
		
		organicLayouter.setOptimizeEdgeDistance(optimizeEdgeDistance);
		if (optimizeEdgeDistance && edgeDistanceFactor != null)
			organicLayouter.setEdgeDistanceCostFactor(edgeDistanceFactor);
		
		organicLayouter.setOptimizeBorderLine(optimizeBorderLine);
		if (optimizeBorderLine && borderLineFactor != null)
			organicLayouter.setBorderLineCostFactor(borderLineFactor);
		
		organicLayouter.setOptimizeNodeDistribution(optimizeNodeDistribution);
		if (optimizeNodeDistribution && nodeDistributionFactor != null )
			organicLayouter.setOptimizeNodeDistribution(optimizeNodeDistribution);
		
		organicLayouter.setFineTuning(fineTuning);
		if (fineTuning && fineTuningRadius != null)
			organicLayouter.setFineTuningRadius(fineTuningRadius);
		
		if (averageNodeArea != null)
			organicLayouter.setAverageNodeArea(averageNodeArea);
		
		if (averageScaleFactor != null)
			organicLayouter.setAverageNodeArea(averageScaleFactor);
		
		if (maxIterations != null)
			organicLayouter.setMaxIterations(maxIterations);
		
		layouter = organicLayouter;
		
	}
	

}
