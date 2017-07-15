package graph.layout.automatic;

import graph.drawing.Drawing;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.layout.AbstractLayouter;
import graph.layout.DefaultGraphLayoutProperties;
import graph.layout.GraphLayoutProperties;
import graph.layout.LayoutAlgorithms;
import graph.layout.LayouterFactory;

/**
 * Layouter which automatically picks and executes an algorithm
 * based on the properties of the given graph
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type 
 */
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
		
		LayoutAlgorithms algorithm = layoutPicker.pickAlgorithm(graph);
		AbstractLayouter<V, E> layouter = layoutFactory.createLayouter(algorithm);
		positionsEdges = layouter.isPositionsEdges();
		if (layoutProperties == null)
			layoutProperties = DefaultGraphLayoutProperties.getDefaultLayoutProperties(algorithm, graph);
		
		return layouter.layout(graph, layoutProperties);
	}

}
