package graph.layout.force.driven;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

public class CircleLayouter<V extends Vertex, E extends Edge<V>> extends AbstractForceDrivenLayouter<V,E>{
		
	public CircleLayouter(Graph<V, E> graph) {
		super(graph);
	}

	protected void initLayouter() {
		layouter = new CircleLayout<V, E>(jungGraph);
		
	}
}
