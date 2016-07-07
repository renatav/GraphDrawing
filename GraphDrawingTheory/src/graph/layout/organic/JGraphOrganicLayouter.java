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
		
		Object optimizeEdgeCrossing = layoutProperties.getProperty(OrganicProperties.IS_OPTIMIZE_EDGE_CROSSING);
		Object edgeCrossingFactor = layoutProperties.getProperty(OrganicProperties.EDGE_CROSSING_FACTOR);
		Object optimizeEdgeDistance = layoutProperties.getProperty(OrganicProperties.IS_OPTIMIZE_EDGE_DISTANCE);
		Object edgeDistanceFactor = layoutProperties.getProperty(OrganicProperties.EDGE_DISTANCE_FACTOR);
		Object optimizeBorderLine = layoutProperties.getProperty(OrganicProperties.IS_OPTIMIZE_BORDER_LINE);
		Object borderLineFactor = layoutProperties.getProperty(OrganicProperties.BORDER_LINE_FACTOR);
		Object optimizeNodeDistribution = layoutProperties.getProperty(OrganicProperties.IS_OPTIMIZE_NODE_DISTRIBUTION);
		Object nodeDistributionFactor = layoutProperties.getProperty(OrganicProperties.NODE_DISTRIBUTION_FACTOR);
		Object fineTuning = layoutProperties.getProperty(OrganicProperties.IS_FINE_TUNING);
		Object fineTuningRadius = layoutProperties.getProperty(OrganicProperties.FINE_TUNING_RADIUS);
		Object averageNodeArea = layoutProperties.getProperty(OrganicProperties.AVERAGE_NODE_AREA);
		Object averageScaleFactor = layoutProperties.getProperty(OrganicProperties.AVERAGE_SCALE_FACTOR);
		Object maxIterations = layoutProperties.getProperty(OrganicProperties.MAX_ITERATIONS);
		
		if (optimizeEdgeCrossing != null)
			organicLayouter.setOptimizeEdgeCrossing((boolean) optimizeEdgeCrossing);
		
		if (optimizeEdgeCrossing!= null && (boolean)optimizeEdgeCrossing && edgeCrossingFactor != null)
			organicLayouter.setEdgeCrossingCostFactor((double) edgeCrossingFactor);
		
		if (optimizeEdgeDistance != null)
			organicLayouter.setOptimizeEdgeDistance((boolean) optimizeEdgeDistance);
		
		if (optimizeEdgeDistance!= null && (boolean)optimizeEdgeDistance && edgeDistanceFactor != null)
			organicLayouter.setEdgeDistanceCostFactor((double) edgeDistanceFactor);
		
		if (optimizeBorderLine != null)
			organicLayouter.setOptimizeBorderLine((boolean) optimizeBorderLine);
		
		if (optimizeBorderLine!= null && (boolean)optimizeBorderLine && borderLineFactor != null)
			organicLayouter.setBorderLineCostFactor((double) borderLineFactor);
		
		if (optimizeNodeDistribution != null)
			organicLayouter.setOptimizeNodeDistribution((boolean) optimizeNodeDistribution);
		
		if (optimizeNodeDistribution!= null && (boolean)optimizeNodeDistribution && nodeDistributionFactor != null )
			organicLayouter.setOptimizeNodeDistribution((boolean) optimizeNodeDistribution);
		
		if (fineTuning != null)
			organicLayouter.setFineTuning((boolean) fineTuning);
		
		if (fineTuning!= null && (boolean)fineTuning && fineTuningRadius != null)
			organicLayouter.setFineTuningRadius((double)fineTuningRadius);
		
		if (averageNodeArea != null)
			organicLayouter.setAverageNodeArea((double) averageNodeArea);
		
		if (averageScaleFactor != null)
			organicLayouter.setAverageNodeArea((double) averageScaleFactor);
		
		if (maxIterations != null)
			organicLayouter.setMaxIterations((int) maxIterations);
		
		layouter = organicLayouter;
		
	}
	

}
