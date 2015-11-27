package graph.layout.force.directed;

import edu.uci.ics.jung.algorithms.layout.SpringLayout2;
import graph.elements.Edge;
import graph.elements.Vertex;
import graph.layout.GraphLayoutProperties;

public class SpringLayouter<V extends Vertex, E extends Edge<V>> extends AbstractForceDirectedLayouter<V,E> {


	@Override
	protected void initLayouter(GraphLayoutProperties layoutProperties) {
		SpringLayout2<V, E> springLayout = new SpringLayout2<>(jungGraph);
		springLayout.setForceMultiplier(5);
		springLayout.setRepulsionRange(3);
		layouter = springLayout;
	}

}