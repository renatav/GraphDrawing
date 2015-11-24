package graph.layout.tree;

import edu.uci.ics.jung.algorithms.layout.BalloonLayout;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.Forest;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.layout.AbstractJungLayouter;
import graph.layout.GraphLayoutProperties;

public class BalloonLayouter<V extends Vertex, E extends Edge<V>> extends AbstractJungLayouter<V, E>{

	public BalloonLayouter(Graph<V, E> graph,
			GraphLayoutProperties layoutProperties) {
		super(graph, layoutProperties);
	}

	@Override
	protected void createJungGraph(){
		jungGraph = new DelegateForest<>();
	}
	
	@Override
	protected void initLayouter() {
		BalloonLayout<V,E> baloonLayout = new BalloonLayout<V, E>((Forest<V, E>) jungGraph);
		layouter = baloonLayout;
		
	}
}