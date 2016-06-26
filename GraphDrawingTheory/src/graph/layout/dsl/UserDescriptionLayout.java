package graph.layout.dsl;

import graph.elements.Edge;
import graph.elements.Vertex;
import graph.layout.GraphLayoutProperties;
import graph.layout.LayoutAlgorithms;
import graph.layout.PropertyEnums.BalloonProperties;
import graph.layout.PropertyEnums.BoxProperties;
import graph.layout.PropertyEnums.CircleProperties;
import graph.layout.PropertyEnums.CompactTreeProperties;
import graph.layout.PropertyEnums.FastOrganicProperties;
import graph.layout.PropertyEnums.FruchtermanReingoldProperties;
import graph.layout.PropertyEnums.HierarchicalProperties;
import graph.layout.PropertyEnums.KamadaKawaiProperties;
import graph.layout.PropertyEnums.NodeLinkTreeProperties;
import graph.layout.PropertyEnums.OrganicProperties;
import graph.layout.PropertyEnums.RadialTreeProperties;
import graph.layout.PropertyEnums.SpringProperties;
import graph.layout.PropertyEnums.TreeProperties;
import graph.layout.PropertyEnums.TutteProperties;
import graph.util.Pair;
import interfaces.ILayout;

import java.util.List;
import java.util.Map;

import javax.swing.SwingConstants;

import models.java.LayoutGraph;

public class UserDescriptionLayout<V extends Vertex, E extends Edge<V>>  {

	public UserDescriptionLayout(List<V> vertices, List<E> edges, ILayout layoutInstructions){

		if (layoutInstructions instanceof LayoutGraph){
			selectLayout(vertices, edges, (LayoutGraph) layoutInstructions);
		}

	}


	private Pair<LayoutAlgorithms, GraphLayoutProperties> selectLayout(List<V> vertices, List<E> edges, LayoutGraph layoutInstructions){
		//TODO prepraviti gramatiku tako da se osobine algoritama mogu navoditi u proizvoljnom rasporedu
		//recimo, napraviti pravilo xxProperty, pa staviti 0 ili vise dodelu
		//TODO git ignore za DSL projekat
		
		if (layoutInstructions.getType().equals("algorithm")){
			LayoutAlgorithms layoutAlgorithm = null;
			GraphLayoutProperties layoutProperties = new GraphLayoutProperties();

			Map<String, String> algorithm = layoutInstructions.getAlgorithm();
			if (algorithm.get("name").equals("radial")){
				layoutAlgorithm = LayoutAlgorithms.RADIAL_TREE;
				if (algorithm.containsKey("xDist"))
					layoutProperties.setProperty(RadialTreeProperties.X_DISTANCE, algorithm.get("xDist"));
				if (algorithm.containsKey("yDist"))
					layoutProperties.setProperty(RadialTreeProperties.Y_DISTANCE, algorithm.get("yDist"));
			}
			else if (algorithm.get("name").equals("level")){
				layoutAlgorithm = LayoutAlgorithms.TREE;
				if (algorithm.containsKey("xDist"))
					layoutProperties.setProperty(TreeProperties.X_DISTANCE, algorithm.get("xDist"));
				if (algorithm.containsKey("yDist"))
					layoutProperties.setProperty(TreeProperties.Y_DISTANCE, algorithm.get("yDist"));
			}
			else if(algorithm.get("name").equals("compact")){
				layoutAlgorithm = LayoutAlgorithms.COMPACT_TREE;
				if (algorithm.containsKey("horizontal"))
					layoutProperties.setProperty(CompactTreeProperties.HORIZONTAL, algorithm.get("horizontal"));
				if (algorithm.containsKey("invert"))
					layoutProperties.setProperty(CompactTreeProperties.INVERT, algorithm.get("invert"));
				if (algorithm.containsKey("resizeParents"))
					layoutProperties.setProperty(CompactTreeProperties.RESIZE_PARENTS, algorithm.get("resizeParents"));
				if (algorithm.containsKey("levelDistance"))
					layoutProperties.setProperty(CompactTreeProperties.LEVEL_DISTANCE, algorithm.get("levelDistance"));
				if (algorithm.containsKey("nodeDistance"))
					layoutProperties.setProperty(CompactTreeProperties.NODE_DISTANCE, algorithm.get("nodeDistance"));
			}
			else if(algorithm.get("name").equals("node")){
				layoutAlgorithm = LayoutAlgorithms.NODE_LINK_TREE;
				if (algorithm.containsKey("spacingSiblings"))
					layoutProperties.setProperty(NodeLinkTreeProperties.SPACING_SIBLINGS, algorithm.get("spacingSiblings"));
				if (algorithm.containsKey("spacingSubtrees"))
					layoutProperties.setProperty(NodeLinkTreeProperties.SPACING_SUBTREES, algorithm.get("spacingSubtrees"));
				if (algorithm.containsKey("spacingLevels"))
					layoutProperties.setProperty(NodeLinkTreeProperties.SPACING_DEPTH_LEVELS, algorithm.get("spacingLevels"));
				if (algorithm.containsKey("offsetRootNode"))
					layoutProperties.setProperty(NodeLinkTreeProperties.SPACING_ROOT_NODE, algorithm.get("offsetRootNode"));
				//TODO razmisliti o orijentaciji u jeziku, da li je ok samo left, right, ili left to right itd.
				if (algorithm.containsKey("orientation")){
					String orientation = algorithm.get("orientation");
					if (orientation.equals("right"))
						layoutProperties.setProperty(NodeLinkTreeProperties.ORIENTATION, 0);
					else if (orientation.equals("left"))
						layoutProperties.setProperty(NodeLinkTreeProperties.ORIENTATION, 1);
					else if (orientation.equals("down"))
						layoutProperties.setProperty(NodeLinkTreeProperties.ORIENTATION, 2);
					else if (orientation.equals("up"))
						layoutProperties.setProperty(NodeLinkTreeProperties.ORIENTATION, 3);
				}
			}
			else if (algorithm.get("name").equals("balloon")){
				layoutAlgorithm = LayoutAlgorithms.BALLOON;
				if (algorithm.containsKey("minRadius"))
					layoutProperties.setProperty(BalloonProperties.MIN_RADIUS, algorithm.get("minRadius"));
			}
			else if (algorithm.get("name").equals("hierarchical")){
				layoutAlgorithm = LayoutAlgorithms.HIERARCHICAL;
				if (algorithm.containsKey("resizeParent"))
					layoutProperties.setProperty(HierarchicalProperties.RESIZE_PARENT, algorithm.get("resizeParent"));
				if (algorithm.containsKey("moveParent"))
					layoutProperties.setProperty(HierarchicalProperties.MOVE_PARENT, algorithm.get("moveParent"));
				if (algorithm.containsKey("parentBorder"))
					layoutProperties.setProperty(HierarchicalProperties.PARENT_BORDER, algorithm.get("parentBorder"));
				if (algorithm.containsKey("intraCellSpacing"))
					layoutProperties.setProperty(HierarchicalProperties.INTRA_CELL_SPACING, algorithm.get("intraCellSpacing"));
				if (algorithm.containsKey("interRankSpacing"))
					layoutProperties.setProperty(HierarchicalProperties.INTER_RANK_CELL_SPACING, algorithm.get("interRankSpacing"));
				if (algorithm.containsKey("interHierarchySpacing"))
					layoutProperties.setProperty(HierarchicalProperties.INTER_HIERARCHY_SPACING, algorithm.get("interHierarchySpacing"));
				if (algorithm.containsKey("parallelEdgesSpacing"))
					layoutProperties.setProperty(HierarchicalProperties.PARALLEL_EDGE_SPACING, algorithm.get("parallelEdgesSpacing"));
				if (algorithm.containsKey("fineTune"))
					layoutProperties.setProperty(HierarchicalProperties.FINE_TUNING, algorithm.get("fineTune"));
				if (algorithm.containsKey("orientation")){
					String orientation = algorithm.get("orientation");
					if (orientation.equals("right"))
						layoutProperties.setProperty(HierarchicalProperties.ORIENTATION, SwingConstants.EAST);
					else if (orientation.equals("left"))
						layoutProperties.setProperty(NodeLinkTreeProperties.ORIENTATION, SwingConstants.WEST);
					else if (orientation.equals("down"))
						layoutProperties.setProperty(NodeLinkTreeProperties.ORIENTATION, SwingConstants.SOUTH);
					else if (orientation.equals("up"))
						layoutProperties.setProperty(NodeLinkTreeProperties.ORIENTATION, SwingConstants.NORTH);
				}
			}
			//TODO circle layouter bez optimizacije
			else if (algorithm.get("name").equals("circular")){
				layoutAlgorithm = LayoutAlgorithms.CIRCLE;
				//if (algorithm.containsKey("optimize"))
					//layoutProperties.setProperty(CircleProperties.OPTIMIZE, algorithm.get("resizeParent"));
				if (algorithm.containsKey("distance"))
					layoutProperties.setProperty(CircleProperties.DISTANCE, algorithm.get("distance"));
			}
			else if (algorithm.get("name").equals("Kamada")){
				layoutAlgorithm = LayoutAlgorithms.KAMADA_KAWAI;
				if (algorithm.containsKey("distanceMultiplier"))
					layoutProperties.setProperty(KamadaKawaiProperties.DISCONNECTED_DISTANCE_MULTIPLIER, algorithm.get("distanceMultiplier"));
				if (algorithm.containsKey("lengthFactor"))
					layoutProperties.setProperty(KamadaKawaiProperties.LENGTH_FACTOR, algorithm.get("lengthFactor"));
				if (algorithm.containsKey("maxIterations"))
					layoutProperties.setProperty(KamadaKawaiProperties.MAXIMUM_ITERATIONS, algorithm.get("maxIterations"));
			}
			else if (algorithm.get("name").equals("Fruchterman")){
				layoutAlgorithm = LayoutAlgorithms.FRUCHTERMAN_REINGOLD;
				if (algorithm.containsKey("attractionMultiplier"))
					layoutProperties.setProperty(FruchtermanReingoldProperties.ATTRACTION_MULTIPLIER, algorithm.get("attractionMultiplier"));
				if (algorithm.containsKey("resolutionMultiplier"))
					layoutProperties.setProperty(FruchtermanReingoldProperties.REPULSION_MULTIPLIER, algorithm.get("resolutionMultiplier"));
				if (algorithm.containsKey("maxIterations"))
					layoutProperties.setProperty(FruchtermanReingoldProperties.MAXIMUM_ITERATIONS, algorithm.get("maxIterations"));
			}
			else if (algorithm.get("name").equals("spring")){
				layoutAlgorithm = LayoutAlgorithms.SPRING;
				if (algorithm.containsKey("stretch"))
					layoutProperties.setProperty(SpringProperties.STRETCH, algorithm.get("stretch"));
				if (algorithm.containsKey("resolutionRange"))
					layoutProperties.setProperty(SpringProperties.REPULSION_RANGE, algorithm.get("resolutionRange"));
				if (algorithm.containsKey("forceMultiplier"))
					layoutProperties.setProperty(SpringProperties.FORCE_MULTIPLIER, algorithm.get("forceMultiplier"));
			}
			else if (algorithm.get("name").equals("organic") && algorithm.containsKey("type")){
				//fast organic
				layoutAlgorithm = LayoutAlgorithms.FAST_ORGANIC;
				if (algorithm.containsKey("forceConstant"))
					layoutProperties.setProperty(FastOrganicProperties.FORCE_CONSTANT, algorithm.get("forceConstant"));
				if (algorithm.containsKey("minimalDistanceLimit"))
					layoutProperties.setProperty(FastOrganicProperties.MINIMAL_DISTANCE_LIMIT, algorithm.get("minimalDistanceLimit"));
				if (algorithm.containsKey("initialTemperature"))
					layoutProperties.setProperty(FastOrganicProperties.INITIAL_TEMP, algorithm.get("initialTemperature"));
				if (algorithm.containsKey("maxIterations"))
					layoutProperties.setProperty(FastOrganicProperties.MAX_ITERATIONS, algorithm.get("maxIterations"));
			}
			else if (algorithm.get("name").equals("organic")) {
				layoutAlgorithm = LayoutAlgorithms.ORGANIC;
				if (algorithm.containsKey("optimizeEdgeCrossings"))
					layoutProperties.setProperty(OrganicProperties.IS_OPTIMIZE_EDGE_CROSSING, algorithm.get("optimizeEdgeCrossings"));
				if (algorithm.containsKey("edgeCrossingFactor"))
					layoutProperties.setProperty(OrganicProperties.EDGE_CROSSING_FACTOR, algorithm.get("edgeCrossingFactor"));
				if (algorithm.containsKey("optimizeEdgeDistance"))
					layoutProperties.setProperty(OrganicProperties.IS_OPTIMIZE_EDGE_DISTANCE, algorithm.get("optimizeEdgeDistance"));
				if (algorithm.containsKey("edgeDistanceFactor"))
					layoutProperties.setProperty(OrganicProperties.EDGE_DISTANCE_FACTOR, algorithm.get("edgeDistanceFactor"));
				if (algorithm.containsKey("borderLineFactor"))
					layoutProperties.setProperty(OrganicProperties.BORDER_LINE_FACTOR, algorithm.get("borderLineFactor"));
				if (algorithm.containsKey("nodeDistributionFactor"))
					layoutProperties.setProperty(OrganicProperties.NODE_DISTRIBUTION_FACTOR, algorithm.get("nodeDistributionFactor"));
				if (algorithm.containsKey("fineTune"))
					layoutProperties.setProperty(OrganicProperties.IS_FINE_TUNING, algorithm.get("fineTune"));
				if (algorithm.containsKey("fineTuningRadius"))
					layoutProperties.setProperty(OrganicProperties.FINE_TUNING_RADIUS, algorithm.get("fineTuningRadius"));
				if (algorithm.containsKey("averageNodeArea"))
					layoutProperties.setProperty(OrganicProperties.AVERAGE_NODE_AREA, algorithm.get("averageNodeArea"));
				if (algorithm.containsKey("averageScaleFactor"))
					layoutProperties.setProperty(OrganicProperties.AVERAGE_SCALE_FACTOR, algorithm.get("averageScaleFactor"));
				if (algorithm.containsKey("maxIterations"))
					layoutProperties.setProperty(OrganicProperties.MAX_ITERATIONS, algorithm.get("maxIterations"));
			}
			else if (algorithm.get("name").equals("box")){
				layoutAlgorithm = LayoutAlgorithms.BOX;
				if (algorithm.containsKey("numOfColumns"))
					layoutProperties.setProperty(BoxProperties.COLUMNS, algorithm.get("numOfColumns"));
			}
			
			//TODO  simetricni i konveksni (kada se srede)
			return new Pair<LayoutAlgorithms, GraphLayoutProperties>(layoutAlgorithm, layoutProperties);
		}
		else if (layoutInstructions.getType().equals("style")){
			String style = layoutInstructions.getStyle();
			if (style.equals("automatic")){
				return new Pair<LayoutAlgorithms, GraphLayoutProperties>(LayoutAlgorithms.AUTOMATIC, null);
			}
			else if (style.equals("circular")){
				return new Pair<LayoutAlgorithms, GraphLayoutProperties>(LayoutAlgorithms.CIRCLE, null);
			}
			else if (style.equals("tree")){
				return new Pair<LayoutAlgorithms, GraphLayoutProperties>(LayoutAlgorithms.TREE, null);
			}
			else if (style.equals("hierarchical")){
				return new Pair<LayoutAlgorithms, GraphLayoutProperties>(LayoutAlgorithms.HIERARCHICAL, null);
			}
			else if (style.equals("symmetrical")){
				return new Pair<LayoutAlgorithms, GraphLayoutProperties>(LayoutAlgorithms.CONCENTRIC, null); //TODO replace with better algorithm when implemented
			}
			else if (style.equals("general")){
				return new Pair<LayoutAlgorithms, GraphLayoutProperties>(LayoutAlgorithms.KAMADA_KAWAI, null);
			}
		}
		else if (layoutInstructions.getType().equals("criteria")){
			System.out.println("aesthetic criteria");
			//organic layout za estetske kriterijume zvuci savrseno
			//moze se podesavati sta da se optimizuje
			//ako je zadato vise kreiterijuma
		}

		return null;
	}

}
