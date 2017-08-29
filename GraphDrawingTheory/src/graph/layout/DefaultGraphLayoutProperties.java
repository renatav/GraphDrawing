package graph.layout;

import graph.elements.Graph;
import graph.elements.Vertex;
import graph.layout.PropertyEnums.BalloonProperties;
import graph.layout.PropertyEnums.CircleProperties;
import graph.layout.PropertyEnums.CompactTreeProperties;
import graph.layout.PropertyEnums.HierarchicalProperties;
import graph.layout.PropertyEnums.KamadaKawaiProperties;
import graph.layout.PropertyEnums.NodeLinkTreeProperties;
import graph.layout.PropertyEnums.OrganicProperties;
import graph.layout.force.directed.KamadaKawaiLayouter;

/**
 * Sets default properties of layout algorithms. 
 * @author Renata
 */
public class DefaultGraphLayoutProperties {
	
	private static double spacing = 50;

	/**
	 * Sets default layout properties of the algorithm given a graph
	 * @param algorithm Layout algorithm
	 * @param graph Graph
	 * @return Default properties of the algorithm
	 */
	public static GraphLayoutProperties getDefaultLayoutProperties(LayoutAlgorithms algorithm, Graph<?,?> graph){
		GraphLayoutProperties properties = new GraphLayoutProperties();
		
		double[] maxValues = maxWidthHeight(graph);
		double maxWidth = maxValues[0];
		double maxHeight = maxValues[1];
		double max = Math.max(maxWidth, maxHeight);

		if (algorithm == LayoutAlgorithms.KAMADA_KAWAI){

			double factor = max/70;
			
			if (graph.getVertices().size() < 4){
				properties.setProperty(KamadaKawaiProperties.LENGTH_FACTOR, 0.4 * factor);
				properties.setProperty(KamadaKawaiProperties.DISCONNECTED_DISTANCE_MULTIPLIER, 0.8  * factor);
			}
			else if (graph.getVertices().size() < 10){
				properties.setProperty(KamadaKawaiProperties.LENGTH_FACTOR, 0.8  * factor);
				properties.setProperty(KamadaKawaiProperties.DISCONNECTED_DISTANCE_MULTIPLIER, 4  * factor);
			}
			else if (graph.getVertices().size() < 20){
				factor = max/100;
				properties.setProperty(KamadaKawaiProperties.LENGTH_FACTOR, 1 * factor);
				properties.setProperty(KamadaKawaiProperties.DISCONNECTED_DISTANCE_MULTIPLIER, 5  * factor);
			}
			else {
				factor = max/100;
				properties.setProperty(KamadaKawaiProperties.LENGTH_FACTOR, 1.2 * factor);
				properties.setProperty(KamadaKawaiProperties.DISCONNECTED_DISTANCE_MULTIPLIER, 10  * factor);
			}
		}
		
		else if (algorithm == LayoutAlgorithms.ORGANIC){
			
			properties.setProperty(OrganicProperties.IS_FINE_TUNING, true);
			properties.setProperty(OrganicProperties.IS_OPTIMIZE_BORDER_LINE, true);
			properties.setProperty(OrganicProperties.IS_OPTIMIZE_EDGE_CROSSING, true);
			properties.setProperty(OrganicProperties.IS_OPTIMIZE_EDGE_DISTANCE, true);
			properties.setProperty(OrganicProperties.IS_OPTIMIZE_NODE_DISTRIBUTION, true);
			properties.setProperty(OrganicProperties.EDGE_DISTANCE_FACTOR, max * 2000);
		}
		
		else if (algorithm == LayoutAlgorithms.HIERARCHICAL){
			
			double[] params = maxWidthHeight(graph);
			
			properties.setProperty(HierarchicalProperties.RESIZE_PARENT, true);
			properties.setProperty(HierarchicalProperties.MOVE_PARENT, false);
			properties.setProperty(HierarchicalProperties.FINE_TUNING, true);
			properties.setProperty(HierarchicalProperties.INTRA_CELL_SPACING, params[0] + spacing);
			properties.setProperty(HierarchicalProperties.INTER_RANK_CELL_SPACING, params[1] + spacing);
			properties.setProperty(HierarchicalProperties.ORIENTATION, 1);
		}
		
		else if (algorithm == LayoutAlgorithms.BALLOON){
			properties.setProperty(BalloonProperties.MIN_RADIUS, (int)(max * 0.6));
		}
		else if (algorithm == LayoutAlgorithms.CIRCLE){
			properties.setProperty(CircleProperties.OPTIMIZE_CROSSINGS, true);
			properties.setProperty(CircleProperties.DISTANCE, (int)max);
		}
		else if (algorithm == LayoutAlgorithms.CIRCLE_CENTER){
			properties.setProperty(CircleProperties.DISTANCE,  2 * (int) max);
			properties.setProperty(CircleProperties.OPTIMIZE_CROSSINGS, true);
		}
		else if (algorithm == LayoutAlgorithms.COMPACT_TREE){
			properties.setProperty(CompactTreeProperties.HORIZONTAL, false);
			double[] params = maxWidthHeight(graph);
			properties.setProperty(CompactTreeProperties.LEVEL_DISTANCE,  (int)(params[1] + spacing/2));
			properties.setProperty(CompactTreeProperties.NODE_DISTANCE,  (int)(params[0] + spacing));
			properties.setProperty(CompactTreeProperties.INVERT, false);
		}
		else if (algorithm == LayoutAlgorithms.NODE_LINK_TREE){
			properties.setProperty(NodeLinkTreeProperties.ORIENTATION, 2);
			double[] params = maxWidthHeight(graph);
			properties.setProperty(NodeLinkTreeProperties.SPACING_DEPTH_LEVELS, params[1] + spacing/2);
			properties.setProperty(NodeLinkTreeProperties.SPACING_ROOT_NODE, 20.0);
			properties.setProperty(NodeLinkTreeProperties.SPACING_SUBTREES,params[0]);
			properties.setProperty(NodeLinkTreeProperties.SPACING_SIBLINGS, params[0] + spacing);
		}
		return properties;
	}
	
	private static double[] maxWidthHeight(Graph<?,?> graph){
		double maxHeight = 0, maxWidth = 0;
		double ret[] = new double[2];
		for (Vertex v : graph.getVertices()){
			if (v.getSize().getWidth() > maxWidth)
				maxWidth = v.getSize().getWidth();
			else if (v.getSize().getHeight() > maxHeight)
				maxHeight = v.getSize().getHeight();
		}
		ret[0] = maxWidth;
		ret[1] = maxHeight;
		return ret;
		
	}

}
