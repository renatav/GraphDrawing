package graph.layout.force.directed;

import edu.uci.ics.jung.algorithms.layout.SpringLayout2;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.layout.GraphLayoutProperties;

public class SpringLayouter<V extends Vertex, E extends Edge<V>> extends AbstractForceDirectedLayouter<V,E> {


	public SpringLayouter(Graph<V, E> graph, GraphLayoutProperties layoutProperties) {
		super(graph, layoutProperties);
	}

	@Override
	protected void initLayouter() {
		SpringLayout2<V, E> springLayout = new SpringLayout2<>(jungGraph);
		springLayout.setForceMultiplier(5);
		springLayout.setRepulsionRange(3);
		layouter = springLayout;
	}

}