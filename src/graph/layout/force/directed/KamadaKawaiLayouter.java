package graph.layout.force.directed;

import edu.uci.ics.jung.algorithms.layout.KKLayout;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

public class KamadaKawaiLayouter<V extends Vertex, E extends Edge<V>> extends AbstractForceDirectedLayouter<V,E> {


	public KamadaKawaiLayouter(Graph<V, E> graph) {
		super(graph);
	}

	@Override
	protected void initLayouter() {
		KKLayout<V, E> kkLayout = new KKLayout<>(jungGraph);

		kkLayout.setAdjustForGravity(true);

		if (jungGraph.getVertexCount() < 4){
			kkLayout.setLengthFactor(0.9);
			kkLayout.setDisconnectedDistanceMultiplier(0.8);
		}
		else if (jungGraph.getVertexCount() < 10){
			kkLayout.setLengthFactor(1.5);
			kkLayout.setDisconnectedDistanceMultiplier(3);
		}
		else if (jungGraph.getVertexCount() < 20){
			kkLayout.setLengthFactor(2);
			kkLayout.setDisconnectedDistanceMultiplier(5);
		}
		else {
			kkLayout.setLengthFactor(3);
			kkLayout.setDisconnectedDistanceMultiplier(10);
		}
		layouter = kkLayout;

	}
}
