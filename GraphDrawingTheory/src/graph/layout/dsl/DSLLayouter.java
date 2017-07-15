package graph.layout.dsl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.SwingConstants;

import graph.algorithms.drawing.ConvexDrawing;
import graph.algorithms.planarity.FraysseixMendezPlanarity;
import graph.drawing.Drawing;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.exception.CannotBeAppliedException;
import graph.exception.DSLException;
import graph.layout.DefaultGraphLayoutProperties;
import graph.layout.GraphLayoutProperties;
import graph.layout.LayoutAlgorithms;
import graph.layout.Layouter;
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
import graph.util.Pair;
import interfaces.ILayout;
import interfaces.ILayoutGraph;
import interpreter.java.Interpreter;
import models.java.LayoutGraph;
import models.java.LayoutSubgraphs;

/**
 * A layouter which lays out a given graph in accordance to the user's description
 * which needs to conform to the defined dsl.
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type 
 */
public class DSLLayouter<V extends Vertex, E extends Edge<V>>  {

	/**
	 * List of vertices to be laid out
	 */
	private List<V> vertices;
	/**
	 * List of edges to be laid out
	 */
	private List<E> edges;
	/**
	 * User's description which should conform to the dsl
	 */
	private String userDescription;
	
	private Layouter<V,E> layouter;
	
	private FraysseixMendezPlanarity<V, E> planarityTest = 
			new FraysseixMendezPlanarity<V,E>();
	
	/**
	 * Determines how many vertices should a graph have in order to be considered big
	 */
	private final int bigGraphVertices = 1000;
	/**
	 * Determines how many edges should a graph have in order to be considered big
	 */
	private final int bigGraphEdges = 2000;

	int startX = 200;
	int startY = 200;

	int spaceX = 200;
	int spaceY = 200;
	int numInRow = 4;
	int currentIndex = 1;

	int currentStartPositionX = startX;
	int currentStartPositionY = startY;

	int maxYInRow = 0;


	public DSLLayouter(List<V> vertices, List<E> edges, String userDescription){
		this.vertices = vertices;
		this.edges = edges;
		this.userDescription = userDescription;
		layouter = new Layouter<V,E>();
	}

	public Drawing<V,E> layout() throws DSLException{
		ILayout layoutDescription = Interpreter.getInstance().execute(userDescription);

		Drawing<V,E> drawing;
		if (layoutDescription instanceof LayoutGraph){
			LayoutGraph layoutGraph = (LayoutGraph) layoutDescription;
			if (layoutGraph.getException() != null && !layoutGraph.getException().equals(""))
				throw new DSLException(((LayoutGraph) layoutDescription).getException());
			drawing = executeOne(vertices, edges, (LayoutGraph) layoutDescription);
		}
		else{

			drawing = new Drawing<V,E>();

			Graph<V,E> graph = formOneGraph(vertices, edges);
			List<V> subgraphVertices = new ArrayList<V>();
			List<V> allSubgraphVertices = new ArrayList<V>();
			LayoutGraph others = null;

			LayoutSubgraphs layoutSubgraphs = (LayoutSubgraphs)layoutDescription;
			for (ILayoutGraph layoutGraph : layoutSubgraphs.getSubgraphs()){
				String subgraph = layoutGraph.getGraph();
				if (subgraph.equals("others")){
					others = (LayoutGraph) layoutGraph;
					continue;
				}

				//subgraph is given as a set of vertices
				//each vertex is identified either by its content
				//or by its index
				String[] subgraphVerticesStr = subgraph.split(",");
				subgraphVertices.clear();

				if (!layoutGraph.isGraphContent()){
					for (String indexStr : subgraphVerticesStr){
						if (indexStr.equals(""))
							subgraphVertices.add(vertices.get(0));
						else
							subgraphVertices.add(vertices.get(Integer.parseInt(indexStr)));
					}
				}
				else{
					for (V v : vertices)
						if (subgraphVertices.contains(v.getContent()))
							subgraphVertices.add(v);
				}

				allSubgraphVertices.addAll(subgraphVertices);

				//find all edges between these vertices
				List<E> subgraphEdges = graph.edgesBetween(subgraphVertices);
				Drawing<V,E> oneDrawing = executeOne(subgraphVertices, subgraphEdges, (LayoutGraph) layoutGraph);
				positionDrawing(oneDrawing);
				drawing.getVertexMappings().putAll(oneDrawing.getVertexMappings());
				drawing.getEdgeMappings().putAll(oneDrawing.getEdgeMappings());
			}

			//layout others if specified
			if (others != null){
				List<V> otherVertices=  new ArrayList<V>();
				for (V v : vertices)
					if (!allSubgraphVertices.contains(v))
						otherVertices.add(v);

				List<E> subgraphEdges = graph.edgesBetween(otherVertices);
				Drawing<V,E> oneDrawing = executeOne(otherVertices, subgraphEdges, (LayoutGraph) others);
				positionDrawing(oneDrawing);
				drawing.getVertexMappings().putAll(oneDrawing.getVertexMappings());
				drawing.getEdgeMappings().putAll(oneDrawing.getEdgeMappings());
			}

		}

		return drawing;
	}

	private Drawing<V,E> executeOne(List<V> vertices, List<E> edges, LayoutGraph layoutDescription){

		Pair<LayoutAlgorithms, GraphLayoutProperties> algorithmAndProperties = selectLayout(vertices, edges, (LayoutGraph) layoutDescription);
		LayoutAlgorithms algorithm = algorithmAndProperties.getKey();
		GraphLayoutProperties properties = algorithmAndProperties.getValue();
		layouter.setVertices(vertices);
		layouter.setEdges(edges);
		layouter.setAlgorithm(algorithm);
		if (properties != null)
			layouter.setLayoutProperties(properties);

		try {
			Drawing<V,E> oneDrawing = layouter.layout();
			return oneDrawing;
		} catch (CannotBeAppliedException e) {
			e.printStackTrace();
		}

		return null;
	}

	private void positionDrawing(Drawing<V,E> drawing){


		int currentLeftmost = drawing.findLeftmostPosition();
		int currentTop = drawing.findTop();


		//leftmost should start at point currentStartPositionX
		int moveByX = currentStartPositionX - currentLeftmost;

		//top should start at point currentStartPositionY
		int moveByY = currentStartPositionY - currentTop;

		drawing.moveByIncludingEdges(moveByX, moveByY);

		int[] bounds = drawing.getBounds();
		if (bounds[1] > maxYInRow)
			maxYInRow = bounds[1];

		currentStartPositionX += bounds[0] + spaceX;

		if (currentIndex % numInRow == 0){
			currentStartPositionY += maxYInRow + spaceY;
			maxYInRow = 0;
			currentStartPositionX = startX;
		}

	}


	@SuppressWarnings("unused")
	private Pair<LayoutAlgorithms, GraphLayoutProperties> selectLayout(List<V> vertices, List<E> edges, LayoutGraph layoutInstructions){

		Graph<V,E> graph = formOneGraph(vertices, edges);
		
		boolean big = graph.getVertices().size() >= bigGraphVertices &&
				graph.getEdges().size() >= bigGraphEdges;

		if (layoutInstructions.getType().equals("algorithm")){
			LayoutAlgorithms layoutAlgorithm = null;
			GraphLayoutProperties layoutProperties = new GraphLayoutProperties();

			Map<String, Object> algorithm = layoutInstructions.getAlgorithm();
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
					String orientation = (String)algorithm.get("orientation");
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
					String orientation = (String)algorithm.get("orientation");
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
			else if (algorithm.get("name").equals("circular")){
				layoutAlgorithm = LayoutAlgorithms.CIRCLE;
				System.out.println(algorithm);
				if (algorithm.containsKey("optimize"))
					layoutProperties.setProperty(CircleProperties.OPTIMIZE_CROSSINGS, algorithm.get("optimize"));
				if (algorithm.containsKey("dist"))
					layoutProperties.setProperty(CircleProperties.DISTANCE, algorithm.get("dist"));
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
			else if (algorithm.get("name").equals("concentric")){
				System.out.println("symmetric");
				layoutAlgorithm = LayoutAlgorithms.CONCENTRIC;
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
				return new Pair<LayoutAlgorithms, GraphLayoutProperties>(LayoutAlgorithms.CIRCLE, 
						DefaultGraphLayoutProperties.getDefaultLayoutProperties(LayoutAlgorithms.CIRCLE, graph));
			}
			else if (style.equals("tree")){
				return new Pair<LayoutAlgorithms, GraphLayoutProperties>(LayoutAlgorithms.COMPACT_TREE, 
						DefaultGraphLayoutProperties.getDefaultLayoutProperties(LayoutAlgorithms.COMPACT_TREE, graph));
			}
			else if (style.equals("hierarchical")){
				return new Pair<LayoutAlgorithms, GraphLayoutProperties>(LayoutAlgorithms.HIERARCHICAL, 
						DefaultGraphLayoutProperties.getDefaultLayoutProperties(LayoutAlgorithms.HIERARCHICAL, graph));
			}
			else if (style.equals("symmetric")){
				return new Pair<LayoutAlgorithms, GraphLayoutProperties>(LayoutAlgorithms.CONCENTRIC,
						DefaultGraphLayoutProperties.getDefaultLayoutProperties(LayoutAlgorithms.CONCENTRIC, graph)); //TODO replace with better algorithm when implemented
			}
			else if (style.equals("general")){
				if (!big)
					return new Pair<LayoutAlgorithms, GraphLayoutProperties>(LayoutAlgorithms.KAMADA_KAWAI,
						DefaultGraphLayoutProperties.getDefaultLayoutProperties(LayoutAlgorithms.KAMADA_KAWAI, graph));
				else
					return new Pair<LayoutAlgorithms, GraphLayoutProperties>(LayoutAlgorithms.ISOM,
							DefaultGraphLayoutProperties.getDefaultLayoutProperties(LayoutAlgorithms.ISOM, graph));
			}
		}
		else if (layoutInstructions.getType().equals("criteria")){
			List<Map<String, Object>> criteriaMaps = layoutInstructions.getAestheticCriteria();
			LayoutAlgorithms layoutAlgorithm;
			GraphLayoutProperties layoutProperties = new GraphLayoutProperties();


			//node distribution, node lengths, node variation
			//minimization of edge crossings etc.
			//can be handled by using a force-directed layout and
			//setting its parameters
			//organic layout is particularly powerful
			//force directed layouts also tend to produce a
			//symmetric layout
			//if more than one criteria is specified
			//it might not be possible to create a drawing
			//that will satisfy all of them completely
			//for example, a graph might not have a planar drawing at all
			//and even if it does, it is very unlikely that it has
			//a drawing that is both planar and symmetric
			//as more algorithms are implemented
			//there will be more possibilities

			//if graph is a tree, there are more possibilities
			//the drawing can always be planar and level-based
			//approaches produce relatively symmetric drawings 

			//possible criteria:
			//edge crossings (and planarity), minimum angles, minimum bands
			//uniform flow, symmetry, node distribution, edge lengths
			//edge variation

			//TODO expand as new algorithms are added
			int planar = -1, symmetric = -1,  edgeCrossings = -1, mimimumAngles = -1, minimumBands = -1,
					uniformFlow = -1, nodeDistribution = -1, edgeLengths = -1, edgeVariation = -1;

			String name;
			for (int i = 0; i < criteriaMaps.size(); i++){

				Map<String, Object> criterion = criteriaMaps.get(i);

				name = (String) criterion.get("criterion");
				switch (name){
				case "planar":
					planar = i;
					break;
				case "crossings":
					edgeCrossings = i;
					break;
				case "angle":
					mimimumAngles = i;
					break;
				case "bands":
					minimumBands = i;
					break;
				case "flow":
					uniformFlow = i;
					break;
				case "symmetric":
					symmetric = i;
					break;
				case "distribute":
					nodeDistribution = i;
					break;
				case "optimize":
					edgeLengths = i;
					break;
				case "similar":
					edgeVariation = i;
					break;
				}
			}

			//check if graph is a tree
			boolean tree = graph.isTree();

			boolean isPlanar = tree;

			if (planar != -1 || edgeCrossings != -1){
				//check if the graph is planar
				if (!tree)
					isPlanar = planarityTest.isPlannar(graph);
			}


			if ((planar != -1 || edgeCrossings != -1) && criteriaMaps.size() == 1){
				try{
					//see if a convex drawing exists
					//TODO
					//za sada, posto ovo nije skroz stabilno
					//kada se on zavrsi, samo proveriti da li ima konveksni 
					//izvrsiti samo convex test

					if (!tree){
						ConvexDrawing<V,E> convex = new ConvexDrawing<V,E>(graph);
						convex.execute();
						layoutAlgorithm = LayoutAlgorithms.CONVEX;
					}
					else
						layoutAlgorithm = LayoutAlgorithms.COMPACT_TREE;

				}
				catch(Exception ex){
					//if there is no convex drawing, and is not a tree
					//apply Kamada-Kawai for now
					//until other specialized planar drawing algorithms
					//are implemented
					if (!big)
						layoutAlgorithm = LayoutAlgorithms.ORGANIC;
					else
						layoutAlgorithm = LayoutAlgorithms.ISOM;
				}
			}
			else if (tree && (nodeDistribution == -1 || uniformFlow != -1)){
				//if graph is a tree, simply drawing a tree
				//is the best option, it is mostly symmetric, planar
				//edges are of equal length
				//there are no bands, the flow is uniform
				//the only exception if it was specified that
				//nodes should be distributed evenly
				//in that case one force-directed approach
				//should be used

				layoutAlgorithm = LayoutAlgorithms.NODE_LINK_TREE;
				layoutProperties = DefaultGraphLayoutProperties.getDefaultLayoutProperties(LayoutAlgorithms.NODE_LINK_TREE, graph);
				if (uniformFlow != -1 || symmetric != -1){
					if (uniformFlow > symmetric){
						//check if a specific direction was specified
						//in that case an algorithm that allows this
						//property to be set should be used
						String orientation = (String) criteriaMaps.get(uniformFlow).get("direction");
						if (orientation != null){
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
				}
			}
			else if (symmetric != -1 && criteriaMaps.size() == 1){
				//TODO permutation check
				//better algorithm when it is implemented
				layoutAlgorithm = LayoutAlgorithms.CONCENTRIC;
			}
			else if (uniformFlow > nodeDistribution && uniformFlow > edgeLengths && uniformFlow > edgeVariation){
				//hierarchical
				layoutAlgorithm = LayoutAlgorithms.HIERARCHICAL;
				layoutProperties = DefaultGraphLayoutProperties.getDefaultLayoutProperties(LayoutAlgorithms.HIERARCHICAL, graph);
				String orientation = (String) criteriaMaps.get(uniformFlow).get("direction"); 
				if (orientation != null){
					if (orientation.equals("right"))
						layoutProperties.setProperty(HierarchicalProperties.ORIENTATION, 0);
					else if (orientation.equals("left"))
						layoutProperties.setProperty(HierarchicalProperties.ORIENTATION, 1);
					else if (orientation.equals("down"))
						layoutProperties.setProperty(HierarchicalProperties.ORIENTATION, 2);
					else if (orientation.equals("up"))
						layoutProperties.setProperty(HierarchicalProperties.ORIENTATION, 3);
				}
			}
			else{
				//organic, set properties
				layoutAlgorithm = LayoutAlgorithms.ORGANIC;

				if (edgeCrossings != -1)
					layoutProperties.setProperty(OrganicProperties.IS_OPTIMIZE_EDGE_CROSSING, true);
				//if (algorithm.containsKey("edgeCross ingFactor"))
				//layoutProperties.setProperty(OrganicProperties.EDGE_CROSSING_FACTOR, algorithm.get("edgeCrossingFactor"));
				if (edgeLengths != -1)
					layoutProperties.setProperty(OrganicProperties.IS_OPTIMIZE_EDGE_DISTANCE, true);
				//if (algorithm.containsKey("edgeDistanceFactor"))
				//layoutProperties.setProperty(OrganicProperties.EDGE_DISTANCE_FACTOR, algorithm.get("edgeDistanceFactor"));
				if (nodeDistribution != -1)
					layoutProperties.setProperty(OrganicProperties.NODE_DISTRIBUTION_FACTOR, true);

				layoutProperties.setProperty(OrganicProperties.IS_FINE_TUNING, true);
			}

			System.out.println("Chosen algorithm: " + layoutAlgorithm);


			return new Pair<LayoutAlgorithms, GraphLayoutProperties>(layoutAlgorithm, layoutProperties);

		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private Graph<V,E> formOneGraph(List<V> vertices, List<E> edges){
		Graph<V,E> graph = new Graph<V,E>();

		for (V v : vertices)
			graph.addVertex(v);

		for (E e : edges)
			graph.addEdge(e);

		return graph;
	}

}
