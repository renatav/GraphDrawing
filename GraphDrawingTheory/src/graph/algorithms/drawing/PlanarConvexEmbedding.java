package graph.algorithms.drawing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import graph.algorithms.planarity.FraysseixMendezPlanarity;
import graph.algorithms.planarity.PlanarityTestingAlgorithm;
import graph.elements.Edge;
import graph.elements.EdgeDirection;
import graph.elements.Graph;
import graph.elements.Path;
import graph.elements.Vertex;
import graph.exception.CannotBeAppliedException;
import graph.properties.components.HopcroftTarjanSplitComponent;
import graph.properties.components.SplitTriconnectedComponentType;
import graph.properties.splitting.AlgorithmErrorException;
import graph.properties.splitting.HopcroftTarjanSplitting;
import graph.traversal.DijkstraAlgorithm;
import graph.traversal.TraversalUtil;
import graph.util.Util;

public class PlanarConvexEmbedding<V extends Vertex, E extends Edge<V>>  {
	

	/**A list of prime separation pairs represented by a virtual edge which contains
	 * its two vertices as end points. A separation pair is prime if the virtual edge
	 * is contained in a triconnected component 
	 */
	private List<E> primeSeparationPairs;
	/**
	 * A list of forbidden separation pairs. A prime separation pair is forbidden if it has at least 
	 * 4 split components or 3 components none of which is either a ring or a bond 
	 */
	private List<E> forbiddenSeparationPairs;
	/**
	 * A list of critical separation pairs. A prime separation  pair is prime if it has either
	 * i) three {x,y} split components including a ring or a bond or
	 * ii) two {x,y} split components neither of which is a ring
	 */
	private List<E> criticalSeparationPairs; 
	/**
	 * A map which contains a critical separation pair as the keys and a list of its split components as values
	 */
	private Map<E, List<HopcroftTarjanSplitComponent<V, E>>> splitComponentsOfPair;
	/**
	 * Planarity testing algorithm used to test the existence of an extendable facial cycle i.e. the possibility
	 * of creating a convex drawing
	 */
	private PlanarityTestingAlgorithm<V, E> planarityTesting;
	/**
	 * A list of virtual edges. They correspond to found separation pairs
	 */
	private List<E> virtualEdges;

	/**A map with virtual edges (separation pairs) as  keys and a list of its
	 * split components as values
	 */
	private Map<E, List<HopcroftTarjanSplitComponent<V, E>>> virtualEdgesSplitComponentsMap;

	/**
	 * An algorithm which will be used to find a path between two vertices, as that is necessary on several occasions
	 */
	private DijkstraAlgorithm<V, E> dijkstra;

	/**
	 * A map which indicates if a split component of a separation pair is complex (was joined with other components or not)
	 */
	private Map<HopcroftTarjanSplitComponent<V, E>, List<E>> componentsJoinedOnMap;

	/**
	 * Hopcroft-Tarjan's algorithm for dividing a graph into split components and detecting separation pairs
	 */
	private HopcroftTarjanSplitting<V, E> hopcroftTarjanSplitting;
	
	private Class<?> edgeClass, vertexClass;
	
	private Logger log = Logger.getLogger(PlanarConvexEmbedding.class);
	
	private Graph<V,E> graph;
	
	public PlanarConvexEmbedding(Graph<V,E> graph){
		this.graph = graph;
		edgeClass = graph.getEdges().get(0).getClass();
		vertexClass = graph.getVertices().get(0).getClass();
		dijkstra = new DijkstraAlgorithm<V,E>();
		virtualEdgesSplitComponentsMap = new HashMap<E, List<HopcroftTarjanSplitComponent<V, E>>>();
		planarityTesting = new FraysseixMendezPlanarity<V,E>();
		dijkstra.setDirected(false);
		componentsJoinedOnMap = new HashMap<HopcroftTarjanSplitComponent<V,E>, List<E>>();
	}
	
	/**
	 * Determines if a 2-connected graph has a convex drawing
	 * and finds all the extendable facial cycles
	 * @throws CannotBeAppliedException 
	 */
	@SuppressWarnings("unchecked")
	public Path<V,E> convexTesting() throws CannotBeAppliedException{

		//STEP 1 
		//Find all separation pairs using Hopcroft-Tarjan triconnected division
		//Form three sets: prime separation pairs, critical separation pairs and forbidden separation pairs

		testSeparationPairs();

		if (forbiddenSeparationPairs.size() > 0)
			throw new CannotBeAppliedException("Forbidden separation pair found. Graph doesn't have a convex drawing");

		List<E> pathEdges = new ArrayList<E>();

		if (criticalSeparationPairs.size() == 0){

			//all cycles are extendable
			//take one
			//for example, the one found during Hopcroft-Tarjan path finding phase
			//the first of those paths is a cycle

			log.info("All facial cycles are extendable");
			pathEdges = hopcroftTarjanSplitting.getPaths().get(0);
		}

		else if (criticalSeparationPairs.size() == 1){
			//set S based on figure 4 from Chibba's paper
			//finds those seven possible cycles
			//the extendable facial cycle should contain the two vertices belonging to the separation pairs
			//it should be a cycle
			//there are either two or three split components of that pair
			//two triconnected graphs
			//or three components where at least one is a bond or a ring
			//to satisfy condition 2, a facial cycle must contain edges of all but one
			//split component of every critical separation pair (that one component is optional)
			//that component is either a ring or a bond
			//so, take paths from one critical pair vertex to the other one of the components, join them...
			//easy if it is a ring

			log.info("Graph has one critical separation pair");

			E pair = criticalSeparationPairs.get(0);
			V v1 = pair.getOrigin();
			V v2 = pair.getDestination();
			List<HopcroftTarjanSplitComponent<V, E>> splitComponents = splitComponentsOfPair.get(pair);
			log.info("Components: " + splitComponents);

			List<List<E>> mustContainPaths = new ArrayList<List<E>>();
			List<List<E>> optionalPaths = new ArrayList<List<E>>();

			for (HopcroftTarjanSplitComponent<V, E> splitComponent : splitComponents){
				List<E> edges = new ArrayList<E>();
				if (splitComponent.getType() == SplitTriconnectedComponentType.BOND){
					edges.add(pair);
					optionalPaths.add(edges);
				}
				else if (splitComponent.getType() == SplitTriconnectedComponentType.RING){
					edges.addAll(splitComponent.getEdges());
					edges.remove(pair);
					optionalPaths.add(edges);
				}
				else{
					edges.addAll(splitComponent.getEdges());
					edges.remove(pair);
					dijkstra.setEdges(edges);
					dijkstra.setDirected(false);
					edges = dijkstra.getPath(v1, v2).getPath();
					mustContainPaths.add(edges);
				}
			}

			//paths now contain a path from v1 to v2 in every split component
			//joining them should result in acquiring the facial cycle

			for (List<E> path : mustContainPaths)
				for (E e : path)
					pathEdges.add(e);

			//now about the other ones
			//if there exist optional paths, there must be three split components
			//if the there are already two triconncted graphs, no need to add anything else
			//that would just ruin the cycle
			//else, there are at least 2 rings or bonds
			//having a triconnected graph and 2 rings or 3 rings would not result in a cycle
			//so that is not an option
			//bonds are not important since they don't add more vertices
			//adding or not adding them is pretty much irrelevant
			if (mustContainPaths.size() < 2){
				Collections.sort(optionalPaths, new Comparator<List<E>>() {

					@Override
					public int compare(List<E> o1, List<E> o2) {
						if (o1.size() > o2.size())
							return -1;
						else if (o1.size() < o2.size())
							return 1;
						else
							return 0;
					}
				});

				for (int i = 0; i < 2 - mustContainPaths.size(); i++)
					for (E e : optionalPaths.get(i))
						pathEdges.add(e);
			}


		}
		else{
			//STEP 2 construct graphs G1 and G2
			//test if G2 is planar
			//if it isn't, G doesn't have a convex drawing
			//otherwise set S as v-cycle of planar graph G2  ? 
			//G1 is created in the following way:
			//for each critical separation pair {x,y} of G
			//is (x,y) in E, delete edge (x,y) from G
			//if exactly one {X,y} split component is a ring
			//delete the x-y path in the component from G

			log.info("Creating graph G1");
			Graph<V, E> G1 = Util.copyGraph(graph);

			Map<V, Integer> indexedCriticalSeparationPairVertices = new HashMap<V, Integer>();
			int index = -1;

			for (E criticalSeparationPair : criticalSeparationPairs){

				//System.out.println("separation pair " + criticalSeparationPair);
				V x = criticalSeparationPair.getOrigin();
				V y = criticalSeparationPair.getDestination();

				if (!indexedCriticalSeparationPairVertices.containsKey(x))
					indexedCriticalSeparationPairVertices.put(x, ++index);

				if (!indexedCriticalSeparationPairVertices.containsKey(y))
					indexedCriticalSeparationPairVertices.put(y, ++index);

				//check if e is an edge in graph
				E foundEdge = null;
				for (E graphEdge : graph.adjacentEdges(x)){
					V other = graphEdge.getOrigin() == x ? graphEdge.getDestination() : graphEdge.getOrigin();
					if (other == y){
						foundEdge = graphEdge;
						break;
					}
				}
				if (foundEdge != null){
					//delete edge {x,y} from graph
					log.info("Deleting edge " + foundEdge);
					G1.removeEdge(foundEdge);
				}
				else{
					List<HopcroftTarjanSplitComponent<V, E>> pairComponents = splitComponentsOfPair.get(criticalSeparationPair);
					//check if there is exactly one split component which is a ring
					int ringsCount = 0;
					HopcroftTarjanSplitComponent<V, E> ring = null;
					for (HopcroftTarjanSplitComponent<V, E> splitComponent : pairComponents){
						//System.out.println(splitComponent);
						if (splitComponent.getType() == SplitTriconnectedComponentType.RING){
							ring = splitComponent;
							ringsCount ++;
							if (ringsCount > 1)
								break;
						}

					}
					if (ringsCount == 1){
						//delete x-y path in the component from graph
						log.info("Removing x-y path of component from G1");
						//TODO double check this
						//x-y path in a ring contains all edges of the ring except for the virtual edges
						//that's why there is no need to use a path finder algorithm
						for (E e : ring.getEdges())
							if (e != criticalSeparationPair)
								G1.removeEdge(e);
					}
				}
			}

			log.info("Created graph G1 " + G1.toString());

			//create graph G2 by adding a vertex v and joining it to all vertices of critical separation pairs
			//no need to copy, we don't need G1

			Graph<V,E> G2 = G1;
			log.info("Creating graph G2");
			V v = Util.createVertex(vertexClass);
			v.setContent("v");
			G2.addVertex(v);

			//if one vertex belongs to two or more separation pairs, only create one edge between it and v
			//list of all vertices belonging to separation pairs was already formed

			for (V v1 : indexedCriticalSeparationPairVertices.keySet()){
				E newEdge = Util.createEdge(v, v1, edgeClass);
				G2.addEdge(newEdge);
			}

			log.info("Created graph G2 " + G2.toString());

			//now check if G2 is planar
			log.info("Checking planarity of G2");
			boolean planar = planarityTesting.isPlannar(G2);
			log.info("planar? " + planar);

			if (!planar)
				throw new CannotBeAppliedException("Graph G2 is not planar. Graph doesn't have a convex drawing");

			log.info("Graph has a convex drawing. Finding an extendable facial cycle");

			//S cycle exists, now find one
			//The main idea here is based on the second condition
			//For each {x,y} critical separation pair there exists at most one component having no edges in S
			//that component is either a bond (if (x,y) in E or a ring otherwise
			//S should contain all of the critical separation pair vertices
			//If {x,y} is a critical separation pair, it has either 2 {x,y} split component which are triconnected graphs
			//or 3 split components including a bond or a ring

			//Observations:
			//If {x,y} has two {x,y} split components, two triconnected graphs
			//one of them is a split component (found using Hopcroft-Tarjan splitting)
			//and it doesn't contain any other critical separation pair vertices other than x and y
			//It is enough to find one path (since we're not searching for all, just for one face)
			//Then, the task is reduced to finding another path from x to y (the part in the split component is covered)
			//do that for every critical pair component which has no other critical separation pairs
			//The idea is to do as little search as possible, not to look for all paths or something similar

			//
			//			for (E e : criticalSeparationPairs){
			//				System.out.println("split components of " + e);
			//				System.out.println(splitComponentsOfPair.get(e));
			//			}
			List<E> graphEdges = new ArrayList<E>();
			graphEdges.addAll(graph.getEdges());
			List<HopcroftTarjanSplitComponent<V, E>> complexSplitComponents = new ArrayList<HopcroftTarjanSplitComponent<V,E>>();
			List<E> searchEdges = new ArrayList<E>();
			List<List<E>> optionalPaths = new ArrayList<List<E>>();
			int simpleComponentsAdded;

			//used later to find a path between critical separation pair vertices
			//for optimization
			int size = indexedCriticalSeparationPairVertices.size();
			boolean[][] criticalSeparationPairsConnectivity = new boolean[size][size];

			for (E criticalPair : criticalSeparationPairs){
				log.info("Current critical separation pair: " + criticalPair);
				V v1 = criticalPair.getOrigin();
				V v2 = criticalPair.getDestination();
				int index1 = indexedCriticalSeparationPairVertices.get(v1);
				int index2 = indexedCriticalSeparationPairVertices.get(v2);

				criticalSeparationPairsConnectivity[index1][index2] = true;
				criticalSeparationPairsConnectivity[index2][index1] = true;

				optionalPaths.clear();
				simpleComponentsAdded = 0;

				//similar logic as above for simple components
				//i.e. those that don't contain another critical separation pair
				//the face should be a cycle, so we shouldn't connect the vertices of the separation pair twice on the same side
				//each critical separation pair must contain at least one component which contains other separation pairs
				//so, if there are two simple components, just one of them should be in the resulting face
				//otherwise, the face wouldn't be a cycle
				for (HopcroftTarjanSplitComponent<V, E> splitComponent : splitComponentsOfPair.get(criticalPair)){
					//does the split component contain other critical pair separation vertices and not just the ones of the current pair
					//was it merged with a another component on another virtual edge belonging to a critical separation pair
					boolean joinedWithAnotherCriticalSeparaionPiar = false;
					if (componentsJoinedOnMap.containsKey(splitComponent))
						for (E e : componentsJoinedOnMap.get(splitComponent))
							if (criticalSeparationPairs.contains(e)){
								joinedWithAnotherCriticalSeparaionPiar = true;
								break;
							}

					if (joinedWithAnotherCriticalSeparaionPiar){
						complexSplitComponents.add(splitComponent);
						log.info("Complex " + splitComponent);
					}
					else{
						searchEdges.clear();
						searchEdges.addAll(splitComponent.getEdges());
						//remove virtual edge
						searchEdges.remove(criticalPair);
						//if the {x,y} split component is a ring, it is actually just a triangle
						//two edges and a virtual edge
						//the path between the two separation pair vertices just includes those two
						//non-virtual edges
						if (splitComponent.getType() == SplitTriconnectedComponentType.RING){
							optionalPaths.add(searchEdges);
						}
						else if (splitComponent.getType() == SplitTriconnectedComponentType.TRICONNECTED_GRAPH){
							//it is necessary to find a planar embedding here
							//not just any path
							//find a cycle containing v1 and v2
							//using dfs
							System.out.println("DFS!!!!!!!!");
							List<E> path = TraversalUtil.circularNoCrossingsPath(v1, v2, splitComponent.adjacencyMap(), true, null, null);
							//dijkstra.setEdges(searchEdges);
							//List<E> path = dijkstra.getPath(v1, v2).getPath();
							pathEdges.addAll(path);
							log.info("Adding path edges: " + path);
							simpleComponentsAdded++;
						}

						//remove the edges, they will either be added to the path or discarded (won't be in the resulting facial cycle)
						for (E e : splitComponent.getEdges()){
							if (e != criticalPair)
								graphEdges.remove(e);
						}

						//add virtual edge
						//so that we can perform the search on the remaining edges
						//trying a different approach, leaving this for now just in case
						//if (!graphEdges.contains(criticalPair))
						//graphEdges.add(criticalPair);

						log.info("Remaining graph edges: " + graphEdges);
					}
				}

				//if there is only one optional path
				//that means that there are two triconnected graphs and they will be added (or parts of them)
				//thus condition 2 will be met
				if (simpleComponentsAdded == 0 && optionalPaths.size() == 2){
					log.info("Should add one of the optional paths: " + optionalPaths);
					List<E> path1 = optionalPaths.get(0);
					List<E> path2 = optionalPaths.get(1);
					if (path1.size() > path2.size()){
						pathEdges.addAll(path1);
						log.info("Adding path edges: " + path1);
					}
					else{
						pathEdges.addAll(path2);
						log.info("Adding path edges: " + path2);
					}
				}

				log.info("Current path edges " + pathEdges );

			}

			//the resulting graph should be much more simple now
			//find a path which connects all all critical pair vertices, complete the cycle
			//instead of trying to form a cycle, find one big path
			//we try to connect a critical separation vertex to all of the other ones
			//since we removed a lot of the edges before, we will not traverse through the whole graph
			//it is much simpler now
			//if we encounter another critical pair vertex on the way, we mark that it is also connected to the 
			//ones we started with
			//the main idea is to minimize the number of path finding calls

			dijkstra.setEdges(graphEdges);
			List<V> traversedCriticalVertices = new ArrayList<V>();

			for (V v1: indexedCriticalSeparationPairVertices.keySet()){
				int v1Index = indexedCriticalSeparationPairVertices.get(v1);
				for (V v2 : indexedCriticalSeparationPairVertices.keySet()){
					int v2Index = indexedCriticalSeparationPairVertices.get(v2);	
					if (v1 == v2 || criticalSeparationPairsConnectivity[v1Index][v2Index] || 
							criticalSeparationPairsConnectivity[v2Index][v1Index])
						continue;

					//else find path if exists
					Path<V,E> path = dijkstra.getPath(v1, v2);
					if (path != null){
						//System.out.println("Path between " + v1 + ", " + v2 + ": " + path.getPath());
						traversedCriticalVertices.clear();
						for (V pathVertex : path.getUniqueVertices()){
							if (indexedCriticalSeparationPairVertices.containsKey(pathVertex))
								traversedCriticalVertices.add(pathVertex);
						}

						for (V traversed1 : traversedCriticalVertices)
							for (V traversed2 : traversedCriticalVertices){
								int traverseIndex1 = indexedCriticalSeparationPairVertices.get(traversed1);
								int traverseIndex2 = indexedCriticalSeparationPairVertices.get(traversed2);	
								criticalSeparationPairsConnectivity[traverseIndex1][traverseIndex2] = true;
								criticalSeparationPairsConnectivity[traverseIndex2][traverseIndex1] = true;
							}
						for (E pathEdge : path.getPath())
							if (!pathEdges.contains(pathEdge))
								pathEdges.add(pathEdge);
					}
				}
			}

		}
		log.info("Path edges: " + pathEdges);

		//We now have the edges, but they are not sorted, the order is semi-random 
		//Sort them before the face is formed

		dijkstra.setEdges(pathEdges);
		E first = pathEdges.get(0);
		pathEdges.remove(0);
		Path<V,E> result = dijkstra.getPath(first.getDestination(), first.getOrigin());
		List<E> resultingEdges = result.getPath();
		result.addEdge(first, EdgeDirection.TO_DESTINATION);
		log.info("Resulting path edges" + resultingEdges);

		//face.addAll(result.pathVertices());
		//log.info("Face " + face);

		return result;

	}

	private void testSeparationPairs(){

		log.info("Determining types of separation pairs");

		hopcroftTarjanSplitting = new HopcroftTarjanSplitting<V,E>(graph, false);

		try {
			hopcroftTarjanSplitting.execute();
			List<HopcroftTarjanSplitComponent<V, E>> splitComponenets = hopcroftTarjanSplitting.getSplitComponents();
			initVirtualEdgesComponentsMap(splitComponenets);
			virtualEdges = hopcroftTarjanSplitting.getVirtualEdges();

			//form triconnected components

			List<HopcroftTarjanSplitComponent<V, E>> triconnectedComponents = formTriconnectedComponents(splitComponenets);
			primeSeparationPairs = new ArrayList<E>();

			//find prime separation pairs
			//a separation pair {x,y} is prime if x and y are end vertices of a virtual edge
			//contained in a triconnected component
			for (HopcroftTarjanSplitComponent<V, E> triconnected : triconnectedComponents){
				for (E e : triconnected.getEdges())
					if (!primeSeparationPairs.contains(e) && virtualEdges.contains(e))
						primeSeparationPairs.add(e);
			}

			log.info("Prime separation pairs: " + primeSeparationPairs);

			//find forbidden separation pairs
			//a prime separation pair is forbidden if it has at least 4 split components or 3 components none
			//of which is either a ring or a bond (meaning, just triconncted graphs) 
			forbiddenSeparationPairs = new ArrayList<E>();

			//find critical separation pairs
			//a prime separation  pair is prime if it has either
			//three {x,y} split components including a ring or a bond
			//or two {x,y} split components neither of which is a ring
			criticalSeparationPairs = new ArrayList<E>();
			//prepare a map of critical separation pairs and the split components it contains
			splitComponentsOfPair = new HashMap<E, List<HopcroftTarjanSplitComponent<V, E>>>();


			for (E e : primeSeparationPairs){
				List<HopcroftTarjanSplitComponent<V, E>> xySplitComponents = formXYSplitComponents(splitComponenets, e);
				if (xySplitComponents.size() >= 4)
					forbiddenSeparationPairs.add(e);
				else if (xySplitComponents.size() == 3){
					boolean ringOrBond = false;
					for (HopcroftTarjanSplitComponent<V, E> splitComponent : xySplitComponents)
						if (splitComponent.getType() != SplitTriconnectedComponentType.TRICONNECTED_GRAPH){
							ringOrBond = true;
							break;
						}
					if (!ringOrBond)
						forbiddenSeparationPairs.add(e);
					else{
						criticalSeparationPairs.add(e);
						splitComponentsOfPair.put(e, xySplitComponents);
					}
				}
				else if (xySplitComponents.size() == 2){
					boolean ring = false;
					for (HopcroftTarjanSplitComponent<V, E> splitComponent : xySplitComponents)
						if (splitComponent.getType() == SplitTriconnectedComponentType.RING){
							ring = true;
							break;
						}
					if (!ring){
						criticalSeparationPairs.add(e);
						splitComponentsOfPair.put(e, xySplitComponents);
					}
				}

			}

			log.info("Forbidden separation pairs: " + forbiddenSeparationPairs);
			log.info("Critical separation pairs: " + criticalSeparationPairs);



		} catch (AlgorithmErrorException e) {
			e.printStackTrace();
		}
	}


	private List<HopcroftTarjanSplitComponent<V, E>> formTriconnectedComponents(List<HopcroftTarjanSplitComponent<V, E>>  components){
		List<HopcroftTarjanSplitComponent<V,E>> triconnectedComponents = new ArrayList<HopcroftTarjanSplitComponent<V,E>>();

		List<HopcroftTarjanSplitComponent<V, E>> processedComponents = new ArrayList<HopcroftTarjanSplitComponent<V,E>>();

		for (HopcroftTarjanSplitComponent<V, E> splitComponent : components){
			if (processedComponents.contains(splitComponent))
				continue;
			if (splitComponent.getType() == SplitTriconnectedComponentType.TRICONNECTED_GRAPH)
				triconnectedComponents.add(splitComponent);
			else {
				//if the component is a ring or a bond, merge it with other components to get a ring or a bond
				//depending on the type of the component
				if (splitComponent.getType() == SplitTriconnectedComponentType.TRIPLE_BOND){
					HopcroftTarjanSplitComponent<V, E> bond = new HopcroftTarjanSplitComponent<V,E>();
					bond.getEdges().addAll(splitComponent.getEdges());
					bond.getVirtualEdges().addAll(splitComponent.getVirtualEdges());
					bond.setType(SplitTriconnectedComponentType.BOND);

					//find other triple bonds and merge them
					int numOfMeges = 0;
					//all virtual edges should be the same, get one
					E virtualEdge = splitComponent.getVirtualEdges().get(0);
					for (HopcroftTarjanSplitComponent<V, E> component : virtualEdgesSplitComponentsMap.get(virtualEdge))
						if (component.getType() == SplitTriconnectedComponentType.TRIPLE_BOND && component != splitComponent){
							numOfMeges ++;
							bond.getEdges().addAll(component.getEdges());
							bond.getVirtualEdges().addAll(component.getVirtualEdges());
							processedComponents.add(component);
						}

					//remove virtual edges where the merge took place
					//one merge = remove two edges
					if (numOfMeges > 0)
						for (int i = 0; i < 2*numOfMeges; i++){
							bond.getEdges().remove(virtualEdge);
							bond.getVirtualEdges().remove(virtualEdge);
						}
					triconnectedComponents.add(bond);							
				}
				//the component is a triangle, merge it with other triangles as far as possible
				else{
					//TODO proveriti ovo spajanje... kako uopste treba da se radi
					HopcroftTarjanSplitComponent<V, E> ring = new HopcroftTarjanSplitComponent<V,E>();
					ring.getEdges().addAll(splitComponent.getEdges());
					ring.getVirtualEdges().addAll(splitComponent.getVirtualEdges());
					ring.setType(SplitTriconnectedComponentType.RING);
					List<E> edgesToBeMerged = new ArrayList<E>(ring.getVirtualEdges());
					List<E> newVirtualEdges = new ArrayList<E>();
					boolean merged;
					while (edgesToBeMerged.size() > 0){
						newVirtualEdges.clear();

						for (E e : edgesToBeMerged){
							merged = false;
							//	System.out.println("virtual edge to merge on " + e);
							for (HopcroftTarjanSplitComponent<V, E> component : virtualEdgesSplitComponentsMap.get(e)){
								if (processedComponents.contains(component)|| component == splitComponent || 
										component.getType() != SplitTriconnectedComponentType.TRIANGLE || 
										component.getVirtualEdges().size() == component.getEdges().size()) // from looking at the example in chiba's paper (TODO - see if this is correct)
									continue;
								merged = true;
								//	System.out.println("merging with component " + component);
								for (E componentEdge : component.getEdges())
									if (component.getVirtualEdges().contains(componentEdge)){
										if (componentEdge != e){
											newVirtualEdges.add(componentEdge);
											ring.addVirtualEdge(componentEdge);
										}
									}
									else
										ring.addEdge(componentEdge);
								processedComponents.add(component);
							}
							if (merged){
								ring.getEdges().remove(e);
								ring.getVirtualEdges().remove(e);
							}
						}
						edgesToBeMerged.clear();
						edgesToBeMerged.addAll(newVirtualEdges);
					}
					triconnectedComponents.add(ring);
				}
			}
			processedComponents.add(splitComponent);
		}
		return triconnectedComponents;
	}


	/**
	 * Forms {x,y} split components by joining split components previously detected split components
	 * @param components A list of split components
	 * @param virtualEdge Virtual edge (x,y) - corresponds to the split pair
	 */
	private List<HopcroftTarjanSplitComponent<V,E>> formXYSplitComponents(List<HopcroftTarjanSplitComponent<V, E>>  components, E virtualEdge){

		System.out.println("Virtual edge " + virtualEdge);
		//to start with, retrieve a list of components which contain the virtual edges representing the split pair
		List<HopcroftTarjanSplitComponent<V, E>> baseComponents = virtualEdgesSplitComponentsMap.get(virtualEdge);
		List<HopcroftTarjanSplitComponent<V,E>> xySplitComponents = new ArrayList<HopcroftTarjanSplitComponent<V,E>>();

		//then join components which share a virtual edge with the current base component to form a {x,y} split component
		for (HopcroftTarjanSplitComponent<V, E> currentBaseComponent : baseComponents){
			System.out.println("base component " + currentBaseComponent);
			HopcroftTarjanSplitComponent<V, E> xySplitComponent = formCurrentComponent(currentBaseComponent, virtualEdge);
			System.out.println("Result: " + xySplitComponent);
			//split component must contain at least one non-virtual edge
			if(xySplitComponent.getEdges().size() != xySplitComponent.getVirtualEdges().size()){
				xySplitComponents.add(xySplitComponent);
			}
		}
		return xySplitComponents;
	}


	private HopcroftTarjanSplitComponent<V,E> formCurrentComponent(HopcroftTarjanSplitComponent<V,E> baseComponent,  E virtualEdge){

		HopcroftTarjanSplitComponent<V, E> current = new HopcroftTarjanSplitComponent<V,E>();
		current.getEdges().addAll(baseComponent.getEdges());
		current.getVirtualEdges().addAll(baseComponent.getVirtualEdges());

		//set initial type, might be changes later if base component is a triangle and it is joined with a triconnected graph
		if (baseComponent.getType() == SplitTriconnectedComponentType.TRIPLE_BOND)
			current.setType(SplitTriconnectedComponentType.BOND);
		else if (baseComponent.getType() == SplitTriconnectedComponentType.TRIANGLE)
			current.setType(SplitTriconnectedComponentType.RING);
		else
			current.setType(SplitTriconnectedComponentType.TRICONNECTED_GRAPH);

		//form the component by merging it with all components that share a virtual edge different
		//than the one representing the separation pair
		//merge the merged components with those which share the newly added virtual edges
		//and so on until no further merging can be done
		//the resulting components should not contain any virtual edges except for the one representing
		//the separation pair

		//System.out.println("Current base component " + baseComponent);
		List<E> joinVirtualEdges = new ArrayList<E>();
		joinVirtualEdges.addAll(baseComponent.getVirtualEdges());
		for (E ve : baseComponent.getVirtualEdges()){
			if (ve == virtualEdge)
				joinVirtualEdges.remove(ve);
			else{
				current.getEdges().remove(ve);
				current.getVirtualEdges().remove(ve);
			}
		}

		if (joinVirtualEdges.size() == 0)
			return current;

		List<E> newVirtualEdges = new ArrayList<E>();
		List<HopcroftTarjanSplitComponent<V, E>> processedComponents = new ArrayList<HopcroftTarjanSplitComponent<V,E>>();
		processedComponents.add(baseComponent);
		List<E> joinedOn = new ArrayList<E>();
		//TODO further test this observation
		int joinsForEdge; //If we merge two triangles on the same virtual edge, then the resulting component is no longer a ring
		boolean typeChanged = false; //optimization
		while (joinVirtualEdges.size() > 0){
			newVirtualEdges.clear();
			for (E e : joinVirtualEdges){
				System.out.println("virtual edge to merge on " + e);
				joinsForEdge = 0;				
				for (HopcroftTarjanSplitComponent<V, E> component : virtualEdgesSplitComponentsMap.get(e)){
					if (processedComponents.contains(component))
						continue;
					if (!typeChanged){
						if (component.getType() != SplitTriconnectedComponentType.BOND)
							joinsForEdge ++;
						if (joinsForEdge == 2){
							current.setType(SplitTriconnectedComponentType.TRICONNECTED_GRAPH);
							typeChanged = true;
						}
					}
					System.out.println("merging with component " + component);
					System.out.println(component.getType());
					if (!typeChanged && component.getType() == SplitTriconnectedComponentType.TRICONNECTED_GRAPH)
						current.setType(SplitTriconnectedComponentType.TRICONNECTED_GRAPH);
					for (E componentEdge : component.getEdges())
						if (!component.getVirtualEdges().contains(componentEdge)){
							current.addEdge(componentEdge);
						}
						else if (componentEdge != virtualEdge && componentEdge != e)
							newVirtualEdges.add(componentEdge);
					processedComponents.add(component);
					joinedOn.add(e);
				}

			}

			//System.out.println(newVirtualEdges);
			joinVirtualEdges.clear();
			joinVirtualEdges.addAll(newVirtualEdges);
		}

		componentsJoinedOnMap.put(current, joinedOn);
		return current;
	}

	private void initVirtualEdgesComponentsMap(List<HopcroftTarjanSplitComponent<V, E>>  components){
		virtualEdgesSplitComponentsMap.clear();
		for (HopcroftTarjanSplitComponent<V, E> splitComponent : components){
			for (E e : splitComponent.getVirtualEdges()){
				if (!virtualEdgesSplitComponentsMap.containsKey(e))
					virtualEdgesSplitComponentsMap.put(e, new ArrayList<HopcroftTarjanSplitComponent<V, E>>());
				if (!virtualEdgesSplitComponentsMap.get(e).contains(splitComponent))
					virtualEdgesSplitComponentsMap.get(e).add(splitComponent);
			}
		}
	}



	/**
	 * Checks if S* is extendable
	 * It is if graph and S satisfy the following condition:
	 * G has no forbidden separation pairs
	 * For each critical separation pair {x,y} of G there exists at most one {x,y} split component having no edge of S
	 * Moreover, such {x,y} split component is either a bond if (x,y) in E or a ring otherwise
	 * 
	 * @param S
	 * @return
	 */
	@SuppressWarnings("unused")
	private boolean isIsExtendable(List<E> S){

		if (forbiddenSeparationPairs.size() > 0){
			log.info("Has forbidden separation pairs - not extendable");
			return false;
		}
		//TODO prepraviti posle ispravki separation parova - rada preko ivica
		for (E criticalSeparationPair : criticalSeparationPairs){
			List<HopcroftTarjanSplitComponent<V, E>> splitComponents = splitComponentsOfPair.get(criticalSeparationPair);
			int count = 0;
			for (HopcroftTarjanSplitComponent<V, E> splitComponent : splitComponents){
				//a component having no edge of S can only by a bond or a ring
				if (splitComponent.getType() == SplitTriconnectedComponentType.BOND || splitComponent.getType() == SplitTriconnectedComponentType.RING){
					//check if the component has at least one edge of S
					boolean hasAtLeastOne = false;
					for (E e : splitComponent.getEdges())
						if (S.contains(e)){
							hasAtLeastOne = true;
							break;
						}
					if (!hasAtLeastOne)
						count ++;
					if (count > 1)
						return false;

				}
			}
		}
		return true;

	}
}
