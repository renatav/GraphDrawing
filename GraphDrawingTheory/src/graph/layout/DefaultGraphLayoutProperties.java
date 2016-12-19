package graph.layout;

import graph.elements.Graph;
import graph.layout.PropertyEnums.BalloonProperties;
import graph.layout.PropertyEnums.CircleProperties;
import graph.layout.PropertyEnums.CompactTreeProperties;
import graph.layout.PropertyEnums.HierarchicalProperties;
import graph.layout.PropertyEnums.KamadaKawaiProperties;
import graph.layout.PropertyEnums.NodeLinkTreeProperties;
import graph.layout.PropertyEnums.OrganicProperties;
import graph.layout.PropertyEnums.PartitionProperties;
import graph.layout.PropertyEnums.RadialTree2Properties;
import graph.layout.PropertyEnums.StackProperties;

public class DefaultGraphLayoutProperties {

	public static GraphLayoutProperties getDefaultLayoutProperties(LayoutAlgorithms algorithm, Graph<?,?> graph){
		GraphLayoutProperties properties = new GraphLayoutProperties();

		if (algorithm == LayoutAlgorithms.KAMADA_KAWAI){

			if (graph.getVertices().size() < 4){
				properties.setProperty(KamadaKawaiProperties.LENGTH_FACTOR, 0.4);
				properties.setProperty(KamadaKawaiProperties.DISCONNECTED_DISTANCE_MULTIPLIER, 0.8);
			}
			else if (graph.getVertices().size() < 10){
				properties.setProperty(KamadaKawaiProperties.LENGTH_FACTOR, 0.6);
				properties.setProperty(KamadaKawaiProperties.DISCONNECTED_DISTANCE_MULTIPLIER, 3);
			}
			else if (graph.getVertices().size() < 20){
				properties.setProperty(KamadaKawaiProperties.LENGTH_FACTOR, 1);
				properties.setProperty(KamadaKawaiProperties.DISCONNECTED_DISTANCE_MULTIPLIER, 5);
			}
			else {
				properties.setProperty(KamadaKawaiProperties.LENGTH_FACTOR, 1.2);
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
		
		else if (algorithm == LayoutAlgorithms.RADIAL_TREE2){
			properties.setProperty(RadialTree2Properties.AUSTO_SCALE, true);
		}
		
		else if (algorithm == LayoutAlgorithms.BALLOON){
			properties.setProperty(BalloonProperties.MIN_RADIUS, 30);
		}
		else if (algorithm == LayoutAlgorithms.CIRCLE || algorithm == LayoutAlgorithms.CIRCLE_CENTER){
			properties.setProperty(CircleProperties.OPTIMIZE_CROSSINGS, true);
			properties.setProperty(CircleProperties.DISTANCE, 50);
		}
		else if (algorithm == LayoutAlgorithms.COMPACT_TREE){
			properties.setProperty(CompactTreeProperties.HORIZONTAL, false);
			properties.setProperty(CompactTreeProperties.INVERT, false);
		}
		else if (algorithm == LayoutAlgorithms.NODE_LINK_TREE){
			properties.setProperty(NodeLinkTreeProperties.ORIENTATION, 2);
			properties.setProperty(NodeLinkTreeProperties.SPACING_DEPTH_LEVELS, 50.0);
			properties.setProperty(NodeLinkTreeProperties.SPACING_ROOT_NODE, 20.0);
			properties.setProperty(NodeLinkTreeProperties.SPACING_SUBTREES, 50.0);
			properties.setProperty(NodeLinkTreeProperties.SPACING_SIBLINGS, 50.0);
		}
		return properties;
	}

}
