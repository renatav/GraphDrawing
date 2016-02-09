package graph.algorithms.drawing;

import graph.algorithms.planarity.FraysseixMendezPlanarity;
import graph.algorithms.planarity.PlanarityTestingAlgorithm;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Path;
import graph.elements.Vertex;
import graph.exception.CannotBeAppliedException;
import graph.properties.components.Block;
import graph.properties.components.HopcroftSplitComponent;
import graph.properties.components.SplitComponentType;
import graph.properties.components.SplitPair;
import graph.properties.splitting.Splitting;
import graph.properties.splitting.TriconnectedDivision;
import graph.traversal.DijkstraAlgorithm;
import graph.traversal.GraphTraversal;
import graph.util.Pair;
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
	private Map<SplitPair<V,E>, List<HopcroftSplitComponent<V, E>>> splitComponentsOfPair;
	private PlanarityTestingAlgorithm<V, E> planarityTesting = new FraysseixMendezPlanarity<V,E>();

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
					List<HopcroftSplitComponent<V, E>> pairComponents = splitComponentsOfPair.get(criticalSeparationPair);
					//check if there is exactly one split component which is a ring
					int ringsCount = 0;
					HopcroftSplitComponent<V, E> ring = null;
					for (HopcroftSplitComponent<V, E> splitComponent : pairComponents){
						if (splitComponent.getType() == SplitComponentType.RING){
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

		log.info("Determening types of separation pairs");

		TriconnectedDivision<V, E> triconnectedDivision = new TriconnectedDivision<V,E>(graph);
		triconnectedDivision.execute();

		List<SplitPair<V, E>> separationPairs = triconnectedDivision.getSeparationPairs();

		Map<E, List<HopcroftSplitComponent<V, E>>> splitComponentsMap = triconnectedDivision.getComponentsVirtualEdgesMap();
		Collection<E> virtualEdges = splitComponentsMap.keySet();

		setVirtualEdgesSplitPairMap(separationPairs, virtualEdges);

		Pair<List<HopcroftSplitComponent<V,E>>, List<E>> componentsAndContainedVEdges = formTriconnectedComponentsAndAnalyzeEdges(splitComponentsMap);

		//List<HopcroftSplitComponent<V,E>> triconnectedComponents = componentsAndContainedVEdges.getKey();

		List<E> containedVirtualEdges = componentsAndContainedVEdges.getValue();
		System.out.println(containedVirtualEdges);

		setPrimeSeparationPairs(containedVirtualEdges);


		//System.out.println("SPLIT COMPONENTS MAP " + splitComponentsMap);

		forbiddenSeparationPairs = new ArrayList<SplitPair<V,E>>();
		criticalSeparationPairs = new ArrayList<SplitPair<V,E>>();
		splitComponentsOfPair = new HashMap<SplitPair<V,E>, List<HopcroftSplitComponent<V,E>>>();

		for (E virtualEdge : virtualEdges){

			log.info("Finding split components for virtual edge "  + virtualEdge);


			//System.out.println("CURRENT VIRTUAL EDGE " +  virtualEdge);

			//create a separation pair represented by that edge

			SplitPair<V,E> separationPair = splitPairVirtualEdgeMapInverse.get(virtualEdge);

			List<HopcroftSplitComponent<V, E>> pairComponents = new ArrayList<HopcroftSplitComponent<V,E>>();

			for (HopcroftSplitComponent<V, E> splitComponent : splitComponentsMap.get(virtualEdge)){

				HopcroftSplitComponent<V, E> joinedComponent = formComponent(splitComponent, splitComponentsMap, splitComponentsMap.keySet(), virtualEdge);
				//System.out.println(joinedComponent);
				if (joinedComponent != null)
					pairComponents.add(joinedComponent);

			}
			splitComponentsOfPair.put(separationPair, pairComponents);

			//now analyze components and determine the pairs type

			//forbidden separation pair is a prime separation pair which has either 
			//i) at least four {x,y} split component
			//ii) three {x,y} split components none of which is either a ring or a bonds

			//critical separation pair is a prime separation pair which has either
			//i) three {x,y} split components including a ring or a bond
			//two {x,y} split components none of which is a ring

			if (primeSeparationPairs.contains(separationPair)){
				System.out.println("Checking separation pair: " + separationPair);
				System.out.println(pairComponents.size());

				if (pairComponents.size() >= 4)
					forbiddenSeparationPairs.add(separationPair);
				else if (pairComponents.size() == 3){
					boolean forbidden = true;
					for (HopcroftSplitComponent<V, E> component : pairComponents)
						if (component.getType() == SplitComponentType.BOND || component.getType() == SplitComponentType.RING){
							forbidden = false;
							break;
						}
					if (forbidden)
						forbiddenSeparationPairs.add(separationPair);
					else
						criticalSeparationPairs.add(separationPair);
				}
				else  if (pairComponents.size() == 2){
					boolean critical = true;
					for (HopcroftSplitComponent<V, E> component : pairComponents){
						System.out.println(component);
						System.out.println(component.getType());
						if (component.getType() == SplitComponentType.RING){
							critical = false;
							break;
						}
					}
					if (critical)
						criticalSeparationPairs.add(separationPair);
				}
			}

		}

		System.out.println("Forbidden separation pairs: " + forbiddenSeparationPairs);
		System.out.println("Critical separation pairs: " + criticalSeparationPairs);

	}

	private void setPrimeSeparationPairs(List<E> containedVirtualEdges){

		primeSeparationPairs = new ArrayList<SplitPair<V, E>>();

		for (SplitPair<V,E> sp : splitPairVirtualEdgeMap.keySet()){
			E virtualEdge = splitPairVirtualEdgeMap.get(sp);
			if (containedVirtualEdges.contains(virtualEdge))
				primeSeparationPairs.add(sp);
		}

		System.out.println("PRIME: " + primeSeparationPairs);
	}




	private HopcroftSplitComponent<V,E> formComponent(HopcroftSplitComponent<V, E> component, Map<E, List<HopcroftSplitComponent<V, E>>> splitComponentsMap, 
			Collection<E> virtualEdges, E virtualEdge){


		log.info("Forming split comopnent for component"  + component);

		//System.out.println("STARTING COMPONENT " + component);

		//it is also necessary to determine the components type
		//if it is a bond, the split component corresponds to edge (x,y)
		//if it is a ring it corresponds to subdivision of edge (x,y)
		//(x,y) is the separation pair whose split components we are looking for

		HopcroftSplitComponent<V, E> ret = new HopcroftSplitComponent<V,E>();
		ret.getEdges().addAll(component.getEdges());
		boolean hasNonVirtualEdge = false;

		boolean changes = true;
		List<E> newEdges = new ArrayList<E>();
		List<E> toProcess = new ArrayList<E>();
		toProcess.addAll(component.getEdges());
		List<HopcroftSplitComponent<V, E>> processedComponents = new ArrayList<HopcroftSplitComponent<V, E>>();
		processedComponents.add(component);

		Map<V,List<E>> adjacencyLists = new HashMap<V, List<E>>();
		List<E> allEdges = new ArrayList<E>();

		boolean notARing = false;

		while (changes){

			newEdges.clear();
			changes = false;
			Iterator<E> iter = toProcess.iterator();

			while (iter.hasNext()){
				E e = iter.next();
				iter.remove();


				//System.out.println("current edge " + e);
				if (!virtualEdges.contains(e) || e == virtualEdge){

					if (e != virtualEdge)
						hasNonVirtualEdge = true;

					//to determine if the graph is a ring i.e. a cycle

					allEdges.add(e);

					if (!notARing){

						V v1 = e.getOrigin();
						V v2 = e.getDestination();

						List<E> adj1 = adjacencyLists.get(v1);
						List<E> adj2 = adjacencyLists.get(v2);

						if (adj1 != null && adj1.size() == 2 && !adj1.contains(e))
							notARing = true;
						else if (adj2 != null && adj2.size() == 2 && !adj2.contains(e))
							notARing = true;
						else{

							if (adj1 == null){
								adj1 = new ArrayList<E>();
								adjacencyLists.put(v1, adj1);
							}

							if (adj2 == null){
								adj2 = new ArrayList<E>();
								adjacencyLists.put(v2, adj2);
							}

							adj1.add(e);
							adj2.add(e);
						}
					}

					continue;
				}


				//is virtual edge
				if (ret.getEdges().contains(e)){
					if (e != virtualEdge)
						ret.getEdges().remove(e);
					else
						continue;
				}

				for (HopcroftSplitComponent<V, E> componentOfEdge : splitComponentsMap.get(e)){
					if (componentOfEdge.getEdges().contains(virtualEdge))
						continue;
					if (processedComponents.contains(componentOfEdge))
						continue;
					processedComponents.add(componentOfEdge);
					ret.getEdges().addAll(componentOfEdge.getEdges());
					newEdges.addAll(componentOfEdge.getEdges());
					changes = true;
				}
			}

			toProcess.addAll(newEdges);
		}

		if (!hasNonVirtualEdge)
			return null;

		//check is it is a ring or a bond

		boolean bond = true;

		//check if the edges are same
		//if that is not a case
		//check if it is a ring


		SplitComponentType type = SplitComponentType.TRICONNECTED_GRAPH;

		for (E e : allEdges)
			if (!((e.getOrigin() == virtualEdge.getOrigin() && e.getDestination() == virtualEdge.getDestination()) ||
					(e.getDestination() == virtualEdge.getOrigin() && e.getOrigin() == virtualEdge.getDestination()))){
				bond = false;
				break;
			}
		if (bond)
			type = SplitComponentType.BOND;

		//System.out.println("not a ring " + notARing);

		//System.out.println("all edges " + allEdges);

		if (allEdges.size() == adjacencyLists.size() && !bond && !notARing){

			//System.out.println("check if it is a ring");

			//arbitrary start vertex
			V start = virtualEdge.getOrigin();
			V current = start;
			boolean first = true;

			List<V> coveredVertices = new ArrayList<V>();
			List<E> coveredEdges = new ArrayList<E>();
			boolean cycleFormed = false;
			V other;
			E currentEdge = null;
			while (!cycleFormed){
				other = null;

				for (E e : adjacencyLists.get(current)){
					other = e.getOrigin() == current ? e.getDestination() : e.getOrigin();
					currentEdge = e;
					if (!first && other == start)
						break;
					if (!coveredVertices.contains(other))
						break;
				}

				if (first)
					first = !first;

				if (other == null)
					break;

				coveredEdges.add(currentEdge);
				coveredVertices.add(other);

				if (other == start)
					cycleFormed = true;
				else
					current = other;

			}

			if (cycleFormed && coveredEdges.size() == allEdges.size())
				type = SplitComponentType.RING;
		}

		log.info("Component: " + ret);
		log.info("Component type: " +type);

		ret.setType(type);


		return ret;
	}


	//TODO spajanje u ring?
	//da li po ovoj logici dobijamo ciklus?
	//da li je dobro odradjeno to da odredimo koje mozemo spajati
	//tj. nije dovoljno da dele virutlenu ivicu nego su bas nastale od tog separation para?
	/**
	 * Joins triple bonds into a bond and triangles into a ring
	 * @return
	 */
	private Pair<List<HopcroftSplitComponent<V,E>>, List<E>> formTriconnectedComponentsAndAnalyzeEdges(Map<E, List<HopcroftSplitComponent<V, E>>> splitComponentsMap){

		log.info("Forming triconnected comopnents");

		List<E> containedVirtualEdges = new ArrayList<E>();
		List<HopcroftSplitComponent<V, E>> components = new ArrayList<HopcroftSplitComponent<V, E>>();
		Collection<E> virtualEdges = splitComponentsMap.keySet();

		List<HopcroftSplitComponent<V, E>> processedComponents = new ArrayList<HopcroftSplitComponent<V, E>>();
		for (E virtualEdge  : splitComponentsMap.keySet())
			for (HopcroftSplitComponent<V, E> component : splitComponentsMap.get(virtualEdge)){
				if (component.getType() != SplitComponentType.TRICONNECTED_GRAPH){
					if (!processedComponents.contains(component))
						formBondOrRing(component, splitComponentsMap, processedComponents, containedVirtualEdges, components);
				}
				else{
					//triconnected graph
					for (E e : component.getEdges())
						if (virtualEdges.contains(e) && !containedVirtualEdges.contains(e))
							containedVirtualEdges.add(e);
				}
			}

		return new Pair<List<HopcroftSplitComponent<V,E>>, List<E>>(components, containedVirtualEdges);

	}

	private void formBondOrRing(HopcroftSplitComponent<V, E> component, Map<E, List<HopcroftSplitComponent<V, E>>> splitComponentsMap,
			List<HopcroftSplitComponent<V, E>> processedComponents, List<E> containedVirtualEdges, List<HopcroftSplitComponent<V, E>> components){

		SplitComponentType type = component.getType();
		Collection<E> virtualEdges = splitComponentsMap.keySet();

		//edge used for joining
		E virtualEdge = component.getVirtualEdge();


		HopcroftSplitComponent<V, E> ret = new HopcroftSplitComponent<V,E>();
		if (type == SplitComponentType.TRIPLE_BOND)
			ret.setType(SplitComponentType.BOND);
		else
			ret.setType(SplitComponentType.RING);

		List<E> toProcess = new ArrayList<E>();
		toProcess.addAll(component.getEdges());
		processedComponents.add(component);


		System.out.println("current component " + component);
		System.out.println(component.getVirtualEdge());

		Iterator<E> iter = toProcess.iterator();

		ret.getEdges().addAll(component.getEdges());
		ret.getEdges().remove(virtualEdge);

		boolean joined = false;

		while (iter.hasNext()){
			E e = iter.next();
			iter.remove();

			if (virtualEdges.contains(e) && e != virtualEdge)
				containedVirtualEdges.add(e);

			if (e != virtualEdge)
				continue;


			//if e is a virtual edge used to joined two components, then it should be removed
			//other virtual edges should remain

			//System.out.println("COMPONENTS: " + splitComponentsMap.get(e));

			for (HopcroftSplitComponent<V, E> componentOfEdge : splitComponentsMap.get(e)){

				//	System.out.println(" component of edgea" + componentOfEdge);

				if (componentOfEdge == component)
					continue;

				if (componentOfEdge.getType() != type)
					continue;

				if (processedComponents.contains(componentOfEdge))
					continue;

				if (componentOfEdge.getVirtualEdge() != component.getVirtualEdge())
					continue;


				joined = true;

				ret.getEdges().addAll(componentOfEdge.getEdges());
				ret.getEdges().remove(e);

				processedComponents.add(componentOfEdge);
				for (E edge : componentOfEdge.getEdges())
					if (virtualEdges.contains(edge) && edge != virtualEdge)
						containedVirtualEdges.add(edge);

			}
		}


		if (!joined)
			ret = component;

		System.out.println("JOIN RESULT");
		System.out.println(ret);
		//System.out.println(virtualEdges);

		//add formed component to list of components
		components.add(ret);



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
			List<HopcroftSplitComponent<V, E>> splitComponents = splitComponentsOfPair.get(criticalSeparationPair);
			int count = 0;
			for (HopcroftSplitComponent<V, E> splitComponent : splitComponents){
				//a component having no edge of S can only by a bond or a ring
				if (splitComponent.getType() == SplitComponentType.BOND || splitComponent.getType() == SplitComponentType.RING){
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
