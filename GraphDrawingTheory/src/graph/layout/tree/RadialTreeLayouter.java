package graph.layout.tree;

import edu.uci.ics.jung.algorithms.layout.RadialTreeLayout;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.Forest;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.layout.AbstractJungLayouter;
import graph.layout.GraphLayoutProperties;
import graph.layout.PropertyEnums.RadialTreeProperties;

public class RadialTreeLayouter<V extends Vertex, E extends Edge<V>> extends AbstractJungLayouter<V, E>{
	
	@Override
	protected void createJungGraph(Graph<V,E> graph){
		jungGraph = new DelegateForest<>();
		super.createJungGraph(graph);
	}
	
	@Override
	protected void initLayouter(GraphLayoutProperties layoutProperties) {
		
		
		layoutProperties.getProperty(RadialTreeProperties.X_DISTANCE);

		Object xDistValue = layoutProperties.getProperty(RadialTreeProperties.X_DISTANCE);
		Object yDistValue = layoutProperties.getProperty(RadialTreeProperties.Y_DISTANCE);
		
		Integer xDist = null, yDist = null;
		
		if (xDistValue instanceof Double)
			xDist = ((Double) xDistValue).intValue();
		
		if (yDistValue instanceof Double)
			yDist = ((Double) xDistValue).intValue();
		
		RadialTreeLayout<V,E> radialTreeLayout;
		if (xDist != null){
			if (yDist != null)
				radialTreeLayout = new RadialTreeLayout<V, E>((Forest<V, E>) jungGraph, xDist, yDist);
			else
				radialTreeLayout = new RadialTreeLayout<V, E>((Forest<V, E>) jungGraph, xDist);
		}
		else 
			radialTreeLayout = new RadialTreeLayout<V, E>((Forest<V, E>) jungGraph);
		
		layouter = radialTreeLayout;
		
	}
}
