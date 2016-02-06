package graph.layout.tree;

import prefuse.action.layout.graph.RadialTreeLayout;
import graph.elements.Edge;
import graph.elements.Vertex;
import graph.layout.AbstractPrefuseLayouter;
import graph.layout.GraphLayoutProperties;
import graph.layout.PropertyEnums.RadialTree2Properties;

public class PrefuseRadialTreeLayouter<V extends Vertex, E extends Edge<V>> extends AbstractPrefuseLayouter<V, E>{

	@Override
	protected void initLayouter(GraphLayoutProperties layoutProperties) {
		
		RadialTreeLayout radialTreeLayouter = new RadialTreeLayout("graph");
		
		Double radiusIncrement = (Double) layoutProperties.getProperty(RadialTree2Properties.RADIUS_INCREMENT);
		Boolean autoScale = (Boolean)layoutProperties.getProperty(RadialTree2Properties.AUSTO_SCALE);
		
		if (radiusIncrement != null)
			radialTreeLayouter.setRadiusIncrement(radiusIncrement);
		
		radialTreeLayouter.setAutoScale(autoScale);
		
		layouter = radialTreeLayouter;
		
	}

}
