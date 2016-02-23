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
import java.util.Collection;
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
	private Map<SplitPair<V,E>, E>  splitPairVirtualEdgeMap;
	private Map<E, SplitPair<V,E>>  splitPairVirtualEdgeMapInverse;
	private Logger log = Logger.getLogger(ConvexDrawing.class);

	private List<SplitPair<V,E>> primeSeparationPairs;
	private List<SplitPair<V,E>> forbiddenSeparationPairs;
	private List<SplitPair<V,E>> criticalSeparationPairs;
	private Map<SplitPair<V,E>, List<HopcroftTarjanSplitComponent<V, E>>> splitComponentsOfPair;
	private PlanarityTestingAlgorithm<V, E> planarityTesting = new FraysseixMendezPlanarity<V,E>();

	private Map<E, List<HopcroftTarjanSplitComponent<V, E>>> virtualEdgesSplitComponentsMap = new HashMap<E, List<HopcroftTarjanSplitComponent<V, E>>>();

	private DijkstraAlgorithm<V, E> dijkstra = new DijkstraAlgorithm<V,E>();

	public ConvexDrawing(Graph<V,E> graph){
		this.graph = graph;
		edgeClass = graph.getEdges().get(0).getClass();
		vertexClass = graph.getVertices().get(0).getClass();
		rand = new Random();
		splitting = new Splitting<V,E>();
	}


	public Map<V, Point2D> execute(){

		Map<V, Point2D> ret = new HashMap<V, Point2D>();

		testSeparationPairs();



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

	private void setVirtualEdgesSplitPairMap(List<SplitPair<V, E>> separationPairs, Collection<E> virtualEdges){

		splitPairVirtualEdgeMap = new HashMap<SplitPair<V,E>, E>();
		splitPairVirtualEdgeMapInverse = new HashMap<E, SplitPair<V,E>>();
		boolean found;
		for (SplitPair<V,E> sp : separationPairs){
			found = false;
			for (E e : virtualEdges)
				if (e.getOrigin() == sp.getV() && e.getDestination() == sp.getU()){
					splitPairVirtualEdgeMap.put(sp, e);
					splitPairVirtualEdgeMapInverse.put(e, sp);
					found = true;
					break;
				}
			if (!found)
				splitPairVirtualEdgeMap.put(sp, null);
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

		testSeparationPairs();

		if (forbiddenSeparationPairs.size() > 0){
			throw new CannotBeAppliedException("Forbidden separation pair found. Graph doesn't have a convex drawing");
		}
		else if (criticalSeparationPairs.size() == 0){
			log.info("All facial cycles are extendable");
			allCyclesExtendable = true;
		}
		else if (criticalSeparationPairs.size() == 1){
			//TODO
			//set S based on figure 4 from Chibba's paper
			//finds those four possible cycles
		}
		else{
			//STEP 2 construct graphs G1 and G2
			//test if G2 is planar
			//if it isn't, G doesn't have a convex drawing
			//otherwise set S as v-cycle of planar graph G2  ? 
			log.info("Creating graph G1");
			Graph<V, E> G1 = Util.copyGraph(graph);

			List<V> criticalSeparationPairVertices = new ArrayList<V>();


			for (SplitPair<V,E> criticalSeparationPair : criticalSeparationPairs){
				V x = criticalSeparationPair.getV();
				V y = criticalSeparationPair.getU();

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
						dijkstra.setEdges(new ArrayList<E>(ring.getEdges()));
						List<E> edges = dijkstra.getPath(x, y).getPath();
						log.info("Edges: " + edges);
						for (E e : edges)
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
			List<V> joinedVertices = new ArrayList<V>();
			for (SplitPair<V,E> criticalSeparationPair : criticalSeparationPairs){
				V v1 = criticalSeparationPair.getV();
				V v2 = criticalSeparationPair.getU();
				if (!joinedVertices.contains(v1)){
					E e1 = Util.createEdge(v, v1, edgeClass);
					G2.addEdge(e1);
					joinedVertices.add(v1);
				}
				if (!joinedVertices.contains(v2)){
					E e2 = Util.createEdge(v, v2, edgeClass);
					G2.addEdge(e2);
					joinedVertices.add(v2);
				}
			}

			log.info("Created graph G2 " + G2.toString());

			//now check if G2 is planar
			log.info("Checking planarity of G2");
			boolean planar = planarityTesting.isPlannar(G2);
			log.info("planar? " + planar);

			if (!planar)
				throw new CannotBeAppliedException("Graph G2 is not planar. Graph doesn't have a convex drawing");

			//S is v-cycle of plane graph G2


			//STEP 3
			//Graph has a convex drawing
			//TODO naci S - v-ciklus
			//the  v-cycle is the cycle of plane subgraph of G1 of G2 which bounds the face of G1 in which v lay
			//G1 = G2 - v
			//napraviti ciklus koji spaja sve cvorove u G1 koji su spojeni sa v


			//approach based on based on theorem 3 and condition 2
			//find all cycles in G1 containing all vertices of critical separation pairs
			//then see which satisfy condition 2

			List<List<E>> extendableFacialCycles = new ArrayList<List<E>>();
			GraphTraversal<V, E> traversal = new GraphTraversal<V, E>(G1);

			//arbitrary start
			V start = criticalSeparationPairVertices.get(0);
			//finds one adjacent to it in G1
			//mark it as end
			//path containing an edge between them is not acceptable, so if it is a cycle, it would go the other way around
			//with that edge at the end, we have a cycle
			V end = G1.adjacentVertices(start).get(0);
			//G1 is biconnected, so this should not throw null pointer...

			List<Path<V,E>> paths = traversal.findAllPathsDFSContaining(start, end, criticalSeparationPairVertices);
			for (Path<V,E> path : paths){
				List<E> edges = path.getPath();
				if (isIsExtendable(edges))
					extendableFacialCycles.add(edges);
			}

			//TODO should this return all
			return extendableFacialCycles;
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
			//	for (E e : hopcroftTarjanSplitting.getVirtualEdges())
			//	formXYSplitComponents(splitComponenets, e);

			//form triconnected components

			List<HopcroftTarjanSplitComponent<V, E>> triconnectedComponents = formTriconnectedComponents(splitComponenets);
			for (HopcroftTarjanSplitComponent<V, E> triconnected : triconnectedComponents)
				System.out.println(triconnected);

		} catch (AlgorithmErrorException e) {
			// TODO Auto-generated catch block
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
							System.out.println("virtual edge to merge on " + e);
							for (HopcroftTarjanSplitComponent<V, E> component : virtualEdgesSplitComponentsMap.get(e)){
								if (processedComponents.contains(component)|| component == splitComponent || 
										component.getType() != SplitTriconnectedComponentType.TRIANGLE || 
										component.getVirtualEdges().size() == component.getEdges().size()) // from looking at the example in chiba's paper (TODO - see if this is correct)
									continue;
								merged = true;
								System.out.println("merging with component " + component);
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
			HopcroftTarjanSplitComponent<V, E> xySplitComponent = formCurrentComponent(currentBaseComponent, virtualEdge);
			//split component must contain at least one non-virtual edge
			System.out.println("Result: " + xySplitComponent);
			if(xySplitComponent.getEdges().size() == xySplitComponent.getVirtualEdges().size())
				System.out.println("Discarding component");
			else
				xySplitComponents.add(xySplitComponent);

				

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

		System.out.println("Current base component " + baseComponent);
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
		while (joinVirtualEdges.size() > 0){
			newVirtualEdges.clear();

			for (E e : joinVirtualEdges){
				System.out.println("virtual edge to merge on " + e);
				for (HopcroftTarjanSplitComponent<V, E> component : virtualEdgesSplitComponentsMap.get(e)){
					if (processedComponents.contains(component))
						continue;
					System.out.println("merging with component " + component);
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

			System.out.println(newVirtualEdges);
			joinVirtualEdges.clear();
			joinVirtualEdges.addAll(newVirtualEdges);
		}

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
		for (SplitPair<V, E> criticalSeparationPair : criticalSeparationPairs){
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
