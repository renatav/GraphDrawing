package graph.algorithms.drawing;

import graph.algorithms.planarity.FraysseixMendezPlanarity;
import graph.algorithms.planarity.PlanarityTestingAlgorithm;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Path;
import graph.elements.Vertex;
import graph.exception.CannotBeAppliedException;
import graph.properties.components.Block;
import graph.properties.components.HopcroftTarjanSplitComponent;
import graph.properties.components.SplitPair;
import graph.properties.components.SplitTriconnectedComponentType;
import graph.properties.splitting.AlgorithmErrorException;
import graph.properties.splitting.HopcroftTarjanSplitting;
import graph.properties.splitting.Splitting;
import graph.traversal.DijkstraAlgorithm;
import graph.traversal.GraphTraversal;
import graph.util.Util;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;

public class ConvexDrawing<V extends Vertex, E extends Edge<V>> {

	private Graph<V,E> graph;
	private Class<?> edgeClass, vertexClass;
	private Random rand;
	private Splitting<V, E> splitting;
	private Logger log = Logger.getLogger(ConvexDrawing.class);

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
	private Map<HopcroftTarjanSplitComponent<V, E>, Boolean> complexComponentsMap;

	public ConvexDrawing(Graph<V,E> graph){
		this.graph = graph;
		edgeClass = graph.getEdges().get(0).getClass();
		vertexClass = graph.getVertices().get(0).getClass();
		rand = new Random();
		splitting = new Splitting<V,E>();
		dijkstra = new DijkstraAlgorithm<V,E>();
		virtualEdgesSplitComponentsMap = new HashMap<E, List<HopcroftTarjanSplitComponent<V, E>>>();
		planarityTesting = new FraysseixMendezPlanarity<V,E>();
		dijkstra.setDirected(false);
		complexComponentsMap = new HashMap<HopcroftTarjanSplitComponent<V,E>, Boolean>();
	}


	public Map<V, Point2D> execute(){

		Map<V, Point2D> ret = new HashMap<V, Point2D>();

		try {
			convexTesting();
		} catch (CannotBeAppliedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return ret;
	}


	@SuppressWarnings("unchecked")
	public Map<V, Point2D> execute(List<V> S, List<V> Sstar){
		Map<V, Point2D> ret = new HashMap<V, Point2D>();


		//step one - for each vertex v of degree two not on S
		//replace v together with two edges incident to v with a 
		//single edge joining the vertices adjacent to v

		//leave the original graph intact - make a copy to start with
		Graph<V,E> gPrim = Util.copyGraph(graph);
		//store deleted vertices in order to position them later
		List<V> deleted = new ArrayList<V>();


		Iterator<V> iter = gPrim.getVertices().iterator();
		while (iter.hasNext()){
			V v = iter.next();
			if (!S.contains(v) && graph.vertexDegree(v) == 2){
				iter.remove();
				List<E> edges = graph.adjacentEdges(v);
				E e1 = edges.get(0);
				E e2 = edges.get(1);
				gPrim.getEdges().remove(e1);
				gPrim.getEdges().remove(e2);
				V adjV1 = e1.getOrigin() == v ? e1.getDestination() : e1.getOrigin();
				V adjV2 = e2.getOrigin() == v ? e2.getDestination() : e2.getOrigin();
				E newEdge = Util.createEdge(adjV1, adjV2, edgeClass);
				gPrim.addEdge(newEdge);
				deleted.add(v);
			}
		}

		//step 2 - call Draw on (G', S, S*) to extend S* into a convex drawing of G'



		return ret;
	}

	//when calling draw for the first time, positions should already 
	//contain position of vertices on S
	/**
	 * Extends a convex polygon S* of the outer facial cycle of a plane
	 *  graph G into a convex drawing of G where G
	 * has no vertex of degree 2 not on S
	 * @param G 2-connected plane graph
	 * @param S Outer facial cycle
	 * @param Sstar Extendable convex polygon of S
	 * @param positions
	 */
	private void draw(Graph<V,E> G, List<V> S, List<V> Sstar, Map<V,Point2D> positions){

		//if G has at most 3 vertices
		//a convex drawing has been obtained - return
		if (G.getVertices().size() <= 3)
			return;

		//select and arbitrary vertex of S* and let G' = G-v (remove v from G)
		V v = arbitraryApex(Sstar);

		List<E> vEdges = graph.adjacentEdges(v);
		List<V> connectedVerties = new ArrayList<V>();


		//find vertices v1 and vp+1 as vertices on S adjacent to v
		V v1, vp_1;
		int index = S.indexOf(v);
		int next = (index + 1) % S.size();
		int previous = index - 1;
		if (previous == -1)
			previous = S.size() - 1;

		v1 = S.get(Math.min(next, previous));
		vp_1 = S.get(Math.max(next, previous));

		E currentEdge = G.edgeBetween(v, v1);
		V currentVertex = v1;

		//remove v
		G.removeVertex(v);

		while (connectedVerties.size() < vEdges.size()){
			connectedVerties.add(currentVertex);
			V other = currentEdge.getOrigin() == currentVertex ? currentEdge.getDestination() : currentEdge.getOrigin();

			//find next edge
			List<E> allEdges = G.adjacentEdges(other);
			for (E e : allEdges)
				if (vEdges.contains(e) && e != currentEdge){
					currentEdge = e;
					break;
				}
			currentVertex = other;
		}

		//divide G into blocks and find cut vertices
		List<V> cutVertices = splitting.findAllCutVertices(graph);
		List<Block<V,E>> blocks = splitting.findAllBlocks(G, cutVertices);

		List<V> vis = new ArrayList<V>();
		vis.add(v1);
		vis.addAll(cutVertices);
		vis.add(vp_1);

		List<V> si = new ArrayList<V>();
		for (Block<V,E> bi : blocks){

			//TODO find Si
			//find Si


			//Si can't be determined that way - V(Si) - V(S) should't be empty
			//not all vertices on Si should be on S!
			//Implement the method for findinf S and S*, then come back here

			List<V> siApexes = new ArrayList<V>();

			//find vertices among v1, v2, .. vp_1 which the block contains
			V vBorder1 = null, vBorder2 = null;
			for (V border : vis){
				if (bi.getVertices().contains(border)){
					if (vBorder1 == null)
						vBorder1 = border;
					else if (vBorder2 == null){
						vBorder2 = border;
						break;
					}
				}
			}


			//Step 2 - Draw each block bi convex

			//Step 2.1 Determine Si*
			//when S* was found, positions of its vertices were found as well
			//so we only need to find position of vertices not on S
			//Locate the vertices in V(Si) - V(S) in the interios of the triangle
			//v*v1*vi+1 (cut vertices + 2 connected to v at the beginning and end)
			//in such way that the vertices adjacent to v are apices of convex polygon Si*
			//and the others are on the straight line segments

			//Step 2.2
			//recursively call procedure Draw(bi,Si, Si*)
		}



	}



	/**
	 * Determines if a 2-connected graph has a convex drawing
	 * and finds all the extendable facial cycles
	 * @throws CannotBeAppliedException 
	 */
	@SuppressWarnings("unchecked")
	public List<List<E>> convexTesting() throws CannotBeAppliedException{

		//STEP 1 
		//Find all separation pairs using Hopcroft-Tarjan triconnected division
		//Form three sets: prime separation pairs, critical separation pairs and forbidden separation pairs

		boolean allCyclesExtendable = false;
		List<V> face;

		testSeparationPairs();

		if (forbiddenSeparationPairs.size() > 0){
			throw new CannotBeAppliedException("Forbidden separation pair found. Graph doesn't have a convex drawing");
		}
		else if (criticalSeparationPairs.size() == 0){
			log.info("All facial cycles are extendable");
			allCyclesExtendable = true;
		}
		else if (criticalSeparationPairs.size() == 1){
			//set S based on figure 4 from Chibba's paper
			//finds those seven possible cycles
			//the extendable facial cycle should contain two vertices belonging to the separation pairs
			//it should be a cycle
			//there are either two or three split components of that pair
			//two triconnected graphs
			//or three components where at least one is a bond or a ring
			//to satisfy condition 2, a facial cycle must contain edges of all but one
			//split component of every critical separation pair (that one component is optional)
			//that component is either a ring or a bond
			//so, take paths from one critical pair vertex to the other one of the components, join them...
			//easy if it is a ring
			//TODO find an example where there are triconnected graphs as split components

			log.info("Graph has one critical separation pair");

			E pair = criticalSeparationPairs.get(0);
			V v1 = pair.getOrigin();
			V v2 = pair.getDestination();
			List<HopcroftTarjanSplitComponent<V, E>> splitComponents = splitComponentsOfPair.get(pair);

			List<E> totalPath = new ArrayList<E>();
			List<List<E>> mustContainPaths = new ArrayList<List<E>>();
			List<List<E>> optionalPaths = new ArrayList<List<E>>();
			face = new ArrayList<V>();

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
					System.out.println(edges);
				}
			}

			//paths now contain a path from v1 to v2 in every split component
			//joining them should result in acquiring the facial cycle

			for (List<E> path : mustContainPaths)
				for (E e : path)
					totalPath.add(e);

			//now about the other ones
			//if mustContainPaths has less than two paths, at least two, the third one is optional according to condition 2
			//for now, add them all

			for (List<E> path : optionalPaths)
				for (E e : path)
					totalPath.add(e);


			for (E e : totalPath){
				V origin = e.getOrigin();
				V dest = e.getDestination();
				if (!face.contains(origin))
					face.add(origin);
				if (!face.contains(dest))
					face.add(dest);

			}

			log.info("Face " + face);

		}
		else{
			//STEP 2 construct graphs G1 and G2
			//test if G2 is planar
			//if it isn't, G doesn't have a convex drawing
			//otherwise set S as v-cycle of planar graph G2  ? 
			log.info("Creating graph G1");
			Graph<V, E> G1 = Util.copyGraph(graph);

			List<V> criticalSeparationPairVertices = new ArrayList<V>();


			for (E criticalSeparationPair : criticalSeparationPairs){
				V x = criticalSeparationPair.getOrigin();
				V y = criticalSeparationPair.getDestination();

				if (!criticalSeparationPairVertices.contains(x))
					criticalSeparationPairVertices.add(x);

				if (!criticalSeparationPairVertices.contains(y))
					criticalSeparationPairVertices.add(y);

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
			G2.addVertex(v);

			//if one vertex belongs to two or more separation pairs, only create one edge between it and v
			//list of all vertices belonging to separation pairs was already formed

			for (V v1 :criticalSeparationPairVertices ){
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
			//S should contain of critical separation pair vertices
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


			for (E e : criticalSeparationPairs){
				System.out.println("split components of " + e);
				System.out.println(splitComponentsOfPair.get(e));
			}

			List<E> pathEdges = new ArrayList<E>();
			List<E> graphEdges = new ArrayList<E>();
			graphEdges.addAll(graph.getEdges());
			List<HopcroftTarjanSplitComponent<V, E>> complexSplitComponents = new ArrayList<HopcroftTarjanSplitComponent<V,E>>();
			List<E> searchEdges = new ArrayList<E>();

			for (E criticalPair : criticalSeparationPairs){
				V v1 = criticalPair.getOrigin();
				V v2 = criticalPair.getDestination();
				for (HopcroftTarjanSplitComponent<V, E> splitComponent : splitComponentsOfPair.get(criticalPair)){
					//does the split component contain other critical pair separation vertices and not just the ones of the current pair
					//is it complex in other words, was the base component joined with other components
					if (complexComponentsMap.get(splitComponent)){
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
							pathEdges.addAll(searchEdges);
							log.info("Adding path edges: " + searchEdges);
						}
						else if (splitComponent.getType() == SplitTriconnectedComponentType.TRICONNECTED_GRAPH){
							dijkstra.setEdges(searchEdges);
							List<E> path = dijkstra.getPath(v1, v2).getPath();
							pathEdges.addAll(path);
							log.info("Adding path edges: " + searchEdges);
						}
						
						for (E e : splitComponent.getEdges()){
							if (e != criticalPair)
								graphEdges.remove(e);
						}
						log.info("Remaining graph edges: " + graphEdges);
					}

				}
			}
			
			//which edges remain, do they just connect simple components, meaning that they connect separation pair vertices
			//or is it more complex?

			return null;
		}

		return null;

	}

	private void testSeparationPairs(){

		log.info("Determining types of separation pairs");

		HopcroftTarjanSplitting<V, E> hopcroftTarjanSplitting = new HopcroftTarjanSplitting<V,E>(graph, false);

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

		if (joinVirtualEdges.size() == 0){
			complexComponentsMap.put(current, false);
			return current;
		}

		List<E> newVirtualEdges = new ArrayList<E>();
		List<HopcroftTarjanSplitComponent<V, E>> processedComponents = new ArrayList<HopcroftTarjanSplitComponent<V,E>>();
		processedComponents.add(baseComponent);
		while (joinVirtualEdges.size() > 0){
			newVirtualEdges.clear();

			for (E e : joinVirtualEdges){
				//System.out.println("virtual edge to merge on " + e);
				for (HopcroftTarjanSplitComponent<V, E> component : virtualEdgesSplitComponentsMap.get(e)){
					if (processedComponents.contains(component))
						continue;
					//System.out.println("merging with component " + component);
					if (component.getType() == SplitTriconnectedComponentType.TRICONNECTED_GRAPH)
						current.setType(SplitTriconnectedComponentType.TRICONNECTED_GRAPH);
					for (E componentEdge : component.getEdges())
						if (!component.getVirtualEdges().contains(componentEdge)){
							current.addEdge(componentEdge);
						}
						else if (componentEdge != virtualEdge && componentEdge != e)
							newVirtualEdges.add(componentEdge);
					processedComponents.add(component);
				}

			}

			//System.out.println(newVirtualEdges);
			joinVirtualEdges.clear();
			joinVirtualEdges.addAll(newVirtualEdges);
		}

		complexComponentsMap.put(current, true);
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

	private V arbitraryApex(List<V> Sstar){
		int i = rand.nextInt(Sstar.size());
		return Sstar.get(i);

	}


}
