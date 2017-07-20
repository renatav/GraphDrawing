package graph.layout.tree;

import prefuse.action.layout.graph.RadialTreeLayout;
import graph.elements.Edge;
import graph.elements.Vertex;
import graph.layout.AbstractPrefuseLayouter;
import graph.layout.GraphLayoutProperties;
import graph.layout.PropertyEnums.RadialTree2Properties;

/**
 * A layouter which creates a drawing of a graph using prefuse's radial tree drawing algorithm
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type 
 */
public class PrefuseRadialTreeLayouter<V extends Vertex, E extends Edge<V>> extends AbstractPrefuseLayouter<V, E>{

	@Override
	protected void initLayouter(GraphLayoutProperties layoutProperties) {
		
		RadialTreeLayout radialTreeLayouter = new RadialTreeLayout("graph");
		
		Object radiusIncrement = layoutProperties.getProperty(RadialTree2Properties.RADIUS_INCREMENT);
		Object autoScale = layoutProperties.getProperty(RadialTree2Properties.AUSTO_SCALE);
		
		if (radiusIncrement != null)
			radialTreeLayouter.setRadiusIncrement((double) radiusIncrement);
		if (autoScale != null)
		radialTreeLayouter.setAutoScale((boolean) autoScale);
		
		layouter = radialTreeLayouter;
		
	}

}
