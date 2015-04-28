package graph.layout;

import graph.elements.Graph;
import graph.layout.PropertyEnums.KamadaKawaiProperties;

public class DefaultGraphLayoutProperties {

	public static GraphLayoutProperties getDefaultLayoutProperties(LayoutAlgorithms algorithm, Graph<?,?> graph){
		GraphLayoutProperties properties = new GraphLayoutProperties();

		if (algorithm == LayoutAlgorithms.KAMADA_KAWAI){

			if (graph.getVertices().size() < 4){
				properties.setProperty(KamadaKawaiProperties.LENGTH_FACTOR, 0.9);
				properties.setProperty(KamadaKawaiProperties.DISCONNECTED_DISTANCE_MULTIPLIER, 0.8);
			}
			else if (graph.getVertices().size() < 10){
				properties.setProperty(KamadaKawaiProperties.LENGTH_FACTOR, 1.5);
				properties.setProperty(KamadaKawaiProperties.DISCONNECTED_DISTANCE_MULTIPLIER, 3);
			}
			else if (graph.getVertices().size() < 20){
				properties.setProperty(KamadaKawaiProperties.LENGTH_FACTOR, 2);
				properties.setProperty(KamadaKawaiProperties.DISCONNECTED_DISTANCE_MULTIPLIER, 5);
			}
			else {
				properties.setProperty(KamadaKawaiProperties.LENGTH_FACTOR, 3);
				properties.setProperty(KamadaKawaiProperties.DISCONNECTED_DISTANCE_MULTIPLIER, 10);
			}
		}

		return properties;
	}

}
