package graph.layout.force.directed;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import graph.elements.Edge;
import graph.elements.Vertex;
import graph.layout.GraphLayoutProperties;

public class FruchtermanReingoldLayouter <V extends Vertex, E extends Edge<V>> extends AbstractForceDirectedLayouter<V,E> {



	@Override
	protected void initLayouter(GraphLayoutProperties layoutProperties) {
		layouter = new FRLayout<V, E>(jungGraph);
		((FRLayout<V,E>)layouter).setRepulsionMultiplier(3);
		
	}

}
