package graph.layout.force.directed;

import edu.uci.ics.jung.algorithms.layout.KKLayout;
import graph.elements.Edge;
import graph.elements.Vertex;
import graph.layout.GraphLayoutProperties;
import graph.layout.PropertyEnums.KamadaKawaiProperties;

/**
 * Layouter which uses JUNG framework's Kamada-Kawai layout
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type 
 */
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
			
			System.out.println(lengthFactor);
			System.out.println(distanceMultiplier);
			System.out.println(maxIterations);
		}

		
		layouter = kkLayout;

	}
}
