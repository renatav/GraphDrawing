package graph.layout;

import graph.elements.Graph;
import graph.layout.PropertyEnums.HierarchicalProperties;
import graph.layout.PropertyEnums.KamadaKawaiProperties;
import graph.layout.PropertyEnums.OrganicProperties;
import graph.layout.PropertyEnums.PartitionProperties;
import graph.layout.PropertyEnums.StackProperties;

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
		
		else if (algorithm == LayoutAlgorithms.ORGANIC){
			properties.setProperty(OrganicProperties.IS_FINE_TUNING, true);
			properties.setProperty(OrganicProperties.IS_OPTIMIZE_BORDER_LINE, true);
			properties.setProperty(OrganicProperties.IS_OPTIMIZE_EDGE_CROSSING, true);
			properties.setProperty(OrganicProperties.IS_OPTIMIZE_EDGE_DISTANCE, true);
			properties.setProperty(OrganicProperties.IS_OPTIMIZE_NODE_DISTRIBUTION, true);
		}
		
		else if (algorithm == LayoutAlgorithms.HIERARCHICAL){
			properties.setProperty(HierarchicalProperties.RESIZE_PARENT, true);
			properties.setProperty(HierarchicalProperties.MOVE_PARENT, false);
			properties.setProperty(HierarchicalProperties.FINE_TUNING, true);
			
		}
		
		else if (algorithm == LayoutAlgorithms.STACK){
			properties.setProperty(StackProperties.HORIZONTAL, true);
		}
		
		else if (algorithm == LayoutAlgorithms.PARTITION){
			properties.setProperty(PartitionProperties.HORIZONTAL, true);
		}

		return properties;
	}

}
