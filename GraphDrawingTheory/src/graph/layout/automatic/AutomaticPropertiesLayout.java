package graph.layout.automatic;

import graph.drawing.Drawing;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.layout.AbstractLayouter;
import graph.layout.GraphLayoutProperties;
import graph.layout.LayouterFactory;

public class AutomaticPropertiesLayout<V extends Vertex, E extends Edge<V>> extends AbstractLayouter<V,E> {

	protected LayoutPicker<V,E> layoutPicker;
	protected LayouterFactory<V,E> layoutFactory;
	
	public AutomaticPropertiesLayout(){
		this.oneGraph = false;
		layoutPicker = new LayoutPicker<V,E>();
		layoutFactory = new LayouterFactory<V,E>();
	}
	
	
	@Override
	public Drawing<V, E> layout(Graph<V, E> graph,
			GraphLayoutProperties layoutProperties) {
		
		AbstractLayouter<V, E> layouter = layoutFactory.createLayouter(layoutPicker.pickAlgorithm(graph));
		return layouter.layout(graph, null);
		
	}

}
