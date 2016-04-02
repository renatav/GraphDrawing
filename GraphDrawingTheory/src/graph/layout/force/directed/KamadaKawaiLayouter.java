package graph.layout.force.directed;

import edu.uci.ics.jung.algorithms.layout.KKLayout;
import graph.elements.Edge;
import graph.elements.Vertex;
import graph.layout.GraphLayoutProperties;
import graph.layout.PropertyEnums.KamadaKawaiProperties;

public class KamadaKawaiLayouter<V extends Vertex, E extends Edge<V>> extends AbstractForceDirectedLayouter<V,E> {


	@Override
	protected void initLayouter(GraphLayoutProperties layoutProperties) {
		KKLayout<V, E> kkLayout = new KKLayout<>(jungGraph);

		kkLayout.setAdjustForGravity(true);
		
		if (layoutProperties != null){
			Object lengthFactor = layoutProperties.getProperty(KamadaKawaiProperties.LENGTH_FACTOR);
			Object distanceMultiplier = layoutProperties.getProperty(KamadaKawaiProperties.DISCONNECTED_DISTANCE_MULTIPLIER);
			Object maxIterations = layoutProperties.getProperty(KamadaKawaiProperties.MAXIMUM_ITERATIONS);
			if (lengthFactor != null && lengthFactor instanceof Double)
				kkLayout.setLengthFactor((double)lengthFactor);
			if (distanceMultiplier != null && distanceMultiplier instanceof Double)
				kkLayout.setDisconnectedDistanceMultiplier((double)distanceMultiplier);
			if (maxIterations != null && maxIterations instanceof Double)
				kkLayout.setMaxIterations(((Double)maxIterations).intValue()) ;
		}
//		else{
//			if (jungGraph.getVertexCount() < 4){
//				kkLayout.setLengthFactor(0.9);
//				kkLayout.setDisconnectedDistanceMultiplier(0.8);
//			}
//			else if (jungGraph.getVertexCount() < 10){
//				kkLayout.setLengthFactor(1.5);
//				kkLayout.setDisconnectedDistanceMultiplier(3);
//			}
//			else if (jungGraph.getVertexCount() < 20){
//				kkLayout.setLengthFactor(2);
//				kkLayout.setDisconnectedDistanceMultiplier(5);
//			}
//			else {
//				kkLayout.setLengthFactor(3);
//				kkLayout.setDisconnectedDistanceMultiplier(10);
//			}
//		}
		layouter = kkLayout;

	}
}
