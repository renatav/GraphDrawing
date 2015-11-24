package graph.layout.tree;

import edu.uci.ics.jung.algorithms.layout.RadialTreeLayout;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.Forest;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.layout.AbstractJungLayouter;
import graph.layout.GraphLayoutProperties;

public class RadialTreeLayouter<V extends Vertex, E extends Edge<V>> extends AbstractJungLayouter<V, E>{

	public RadialTreeLayouter(Graph<V, E> graph,
			GraphLayoutProperties layoutProperties) {
		super(graph, layoutProperties);
	}

	@Override
	protected void createJungGraph(){
		jungGraph = new DelegateForest<>();
	}
	
	@Override
	protected void initLayouter() {
		RadialTreeLayout<V,E> radialTreeLayout = new RadialTreeLayout<V, E>((Forest<V, E>) jungGraph);
		layouter = radialTreeLayout;
		
	}
}
