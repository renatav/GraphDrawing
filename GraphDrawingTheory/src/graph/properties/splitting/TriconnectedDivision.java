package graph.properties.splitting;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.properties.components.HopcroftTarjanSplitComponent;
import graph.properties.components.SplitComponentType;
import graph.properties.components.SplitPair;
import graph.trees.DFSTree;
import graph.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * Implementation of Hopcroft's and Tarjan's algorithm which divides a graph
 * into triconnected components and finds separation pairs
 * @author xx
 *
 */

public class TriconnectedDivision<V extends Vertex, E extends Edge<V>> {


	private Graph<V,E> graph;
	private Class<?> edgeClass;
	private List<SplitPair<V,E>> separationPairs;
	private Map<V,List<E>> adjacency;
	private Map<V, List<SplitPair<V,E>>> separationPairStartVertices;
	private Map<V, List<SplitPair<V,E>>> separationPairEndVertices;
	private List<E> treeEdges;
	private List<E> fronds;
	private List<V> vertices;
	private int[] numbering;
	private Map<Integer, List<V>> lowpt1sMap;
	private DFSTree<V, E> tree;
	private Logger log = Logger.getLogger(TriconnectedDivision.class);
	private List<V> separationPairVertices;
	private Map<E, List<HopcroftTarjanSplitComponent<V, E>>> componentsVirtualEdgesMap;
	private List<HopcroftTarjanSplitComponent<V, E>> components;


	public TriconnectedDivision(Graph<V,E> graph){
		this.graph = graph;
		edgeClass = graph.getEdges().get(0).getClass();
	
	}


	public void execute(){

		componentsVirtualEdgesMap = new HashMap<E, List<HopcroftTarjanSplitComponent<V, E>>>();
		components = new ArrayList<HopcroftTarjanSplitComponent<V, E>>();
		triconnected(graph);

		//		try {
		//			splitting.findSeaparationPairs(graph, edgeClass);
		//		} catch (AlgorithmErrorException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
	}


	private void triconnected(Graph<V,E> graph){

		//
		//		//remove all multiedges
		//		//and create triple bonds
		//
		//		Graph<V,E> gPrim= graph;
		//
		//		List<List<E>> multiedges = graph.listMultiEdges();
		//		if (multiedges.size() > 0){
		//
		//			gPrim = Util.copyGraph(graph);
		//			for (List<E> multi : multiedges){
		//
		//				HopcroftSplitComponent<V, E> tripleBond = new HopcroftSplitComponent<V,E>(SplitComponentType.TRIPLE_BOND, multi, null);
		//				splitComponents.add(tripleBond);
		//
		//				//remove all but one edge (which represents all three)
		//				for (int i = 1; i < multi.size(); i++){
		//					gPrim.removeEdge(multi.get(i));
		//				}
		//			}
		//		}

		//find biconnected components of G'
		//List<BiconnectedComponent<V, E>> biconnectedComponents = gPrim.listBiconnectedComponents();

		//for now, assume that graph is biconnected, worry about it when this works :P

		SeparationPairSplitting<V, E> separationPairSplitting = new SeparationPairSplitting<V,E>();
		try {
			separationPairSplitting.findSeaparationPairs(graph);
		} catch (AlgorithmErrorException e) {
			e.printStackTrace();
		}
		separationPairs = separationPairSplitting.getSeparationPairs();
		adjacency = separationPairSplitting.getAdjacency();
		separationPairStartVertices = separationPairSplitting.getSeparationPairStartVertices();
		separationPairEndVertices = separationPairSplitting.getSeparationPairEndVertices();
		treeEdges = separationPairSplitting.getTreeEdges();
		fronds = separationPairSplitting.getFronds();
		numbering = separationPairSplitting.getNewnum();
		vertices = graph.getVertices();
		lowpt1sMap = separationPairSplitting.getLowpt1sMap();
		tree = separationPairSplitting.getTree();
		separationPairVertices = new ArrayList<V>();

		for (SplitPair<V, E> splitPair : separationPairs){
			if (!separationPairVertices.contains(splitPair.getU()))
				separationPairVertices.add(splitPair.getU());
			if (!separationPairVertices.contains(splitPair.getV()))
				separationPairVertices.add(splitPair.getV());
		}

		List<E> virtualEdges = new ArrayList<E>();
		List<E> estack = new ArrayList<E>();
		V start = graph.getVertices().get(0);
		List<V> vstack = new ArrayList<V>();
		dfs(start, estack, vstack,  new ArrayList<E>(), virtualEdges);
		createComponentsConsistingOfVirtualEdgesOnly(virtualEdges);

	}


	private void dfs(V v, List<E> estack, List<V> coveredVertices, List<E> coveredEdges, List<E> virtualEdges){

		System.out.println("dfs for vertex " + v);

		for (E e : adjacency.get(v)){

			log.info("current edge " + e);

			if (coveredEdges.contains(e))
				continue;

			V w = e.getOrigin() == v ? e.getDestination() : e.getOrigin();

			if (treeEdges.contains(e)){

				estack.add(e);

				System.out.println("current estack: " + estack);

				if (separationPairEndVertices.containsKey(w)){ 

					List<SplitPair<V,E>> splitPairs = findSeparationPairsToOutput(w, separationPairStartVertices, separationPairEndVertices, estack);

					log.info("Split pairs: " + splitPairs);

					for (SplitPair<V,E> splitPair : splitPairs){
						outputComponent(splitPair, separationPairStartVertices, separationPairEndVertices, estack, coveredEdges, virtualEdges );
					}

				}

				if (!coveredVertices.contains(w)){
					coveredVertices.add(w);
					dfs(w, estack, coveredVertices, coveredEdges, virtualEdges);
				}
			}

			else {

				if (!coveredEdges.contains(e)){
					System.out.println("Back edge : " + e);
					outputComponentBackEdge(e,estack, separationPairStartVertices, separationPairEndVertices, coveredEdges, virtualEdges);

				}

			}

		}

	}



	private void outputComponent(SplitPair<V,E> splitPair, Map<V, List<SplitPair<V, E>>> separationPairStartVertices, 
			Map<V, List<SplitPair<V, E>>> separationPairEndVertices, List<E> estack, List<E> coveredEdges, List<E> virtualEdges) {


		V start = splitPair.getV();
		V end = splitPair.getU();

		V current = end;

		int startNumber = numbering[vertices.indexOf(start)];
		int endNumber = numbering[vertices.indexOf(end)];

		List<E> coveredVirtualEdges = new ArrayList<E>();


		List<E> edges = new ArrayList<E>();
		List<E> componentEdges = new ArrayList<E>();
		List<V> componentVertices = new ArrayList<V>();

		boolean formingTripleBond = false;
		boolean containsDifferentEdges = false;

		for (int i = estack.size() - 1; i >= 0; i--){

			E e = estack.get(i);

			//don't add edge which has split pair end and start vertices 
			//if some other edges were already marked as component edges
			//which don't have split pair vertices as their endpoints

			boolean alreadyContainsEdge = alreadyContainsEdge(e, start, end, componentEdges);

			if (componentVertices.size() == 1 && !alreadyContainsEdge)
				containsDifferentEdges = true;

			if (containsDifferentEdges && alreadyContainsEdge)
				break;

			componentVertices.add(current);
			componentEdges.add(e);
			//	if (!virtualEdges.contains(e))
			edges.add(e);

			if (virtualEdges.contains(e))
				coveredVirtualEdges.add(e);

			current = e.getOrigin() == current ? e.getDestination() : e.getOrigin();
			if (current == start)
				break;

		}

		if (!containsDifferentEdges)
			formingTripleBond = true;


		estack.removeAll(edges);

		int lowestpt = numbering[vertices.indexOf(start)];
		V lowestVertex = start;
		int highest = numbering[vertices.indexOf(end)];
		V highestVertex = end;

		//if a vertex is one of two vertices of the split pair
		//do not add its back edges unless some other vertex
		//is connected to endpoints of those edges as well

		List<V> addedVertices = new ArrayList<V>();

		List<V> toProcess = new ArrayList<V>();
		toProcess.addAll(componentVertices);
		toProcess.remove(start);
		toProcess.remove(end);
		toProcess.add(start);
		toProcess.add(end);

		//don't add two same edges (with the same origin and destination, or reversed origin and destination)
		//for example, a virtual edge and the same back edge
		//unless the component doesn't and will not contain any other edge - it is a triple bond 

		for (V v : toProcess){


			//	if (v != start && v != end){
			for (E e : adjacency.get(v))
				if (fronds.contains(e)){
					//	System.out.println("frond: " + e);
					V other = e.getOrigin() == v ? e.getDestination() : e.getOrigin();
					int currentNum = numbering[vertices.indexOf(other)];
					if (currentNum >= lowestpt){
						boolean alreadyContains = alreadyContainsEdge(e, start, end, componentEdges);
						if (alreadyContains && !formingTripleBond)
							continue;
						if (!alreadyContains && formingTripleBond)
							continue;
						componentEdges.add(e);
					}

					else{
						//is there a path on estack from the other vertex to one of the vertices on the stack
						List<E> traversedEdges = new ArrayList<E>();
						List<V> traversedVertices = new ArrayList<V>();
						current = lowestVertex;
						boolean ok = false;
						//System.out.println("checking vertex " + other);
						V currentLow = lowestVertex;
						for (int i = estack.size() - 1; i >= 0; i--){

							E currentEdge = estack.get(i);
							if (isSplitEdge(currentEdge, start, end))
								continue;

							traversedEdges.add(currentEdge);
							V otherLow =  currentEdge.getOrigin() == currentLow ? currentEdge.getDestination() : currentEdge.getOrigin();
							traversedVertices.add(otherLow);
							//belongs to a different separation pair, should be part of a different component
							if (separationPairStartVertices.containsKey(otherLow) || separationPairEndVertices.containsKey(otherLow))
								break;
							if (otherLow == other){
								ok = true;
								break;
							}
							currentLow = otherLow;
						}
						//System.out.println(ok);

						if (ok){
							componentEdges.addAll(traversedEdges);
							estack.removeAll(traversedEdges);
							componentEdges.add(e);
							lowestVertex = other;
							lowestpt = numbering[vertices.indexOf(other)];
							addedVertices.addAll(traversedVertices);
						}
					}
					//}
				}




			//now see if there are parts of the component
			//which are below the end vertex
			//these edges are not on estack yet
			//search for vertices which have back edges
			//which end in a vertex belonging to this component
			//there shouldn't be any other separation pair vertices
			//between vertices already in the component and the connected vertices
			//special attention is payed to start and end vertices (separation pair vertices of this component)
			//since they can belong to some other component as well
			//if there is another back edge between the found vertex and component vertices
			//other than the one that ends in start or end
			//it is ok to add it

			//TODO da se ovde ne bi stalno trazio path
			//mozda prvo popuniti estack ili neku drugu strukturu
			//pa tu gledati slicno kao i u gornjem slucaju

			List<V> verticesWithLowpt = lowpt1sMap.get(numbering[vertices.indexOf(v)]); //vertices with the highest back edges ending at vertex v

			//			System.out.println("TESTING LOWPTSMAP");
			//			System.out.println(v);
			//			System.out.println(verticesWithLowpt);


			if (verticesWithLowpt != null){
				for (V high : verticesWithLowpt){

					boolean ok = false;
					boolean separationVertexOk = false;
					if (v == start || v == end){
						int separationPairNum = v == start ? startNumber : endNumber;
						for (E e : adjacency.get(high))
							if (fronds.contains(e)){
								V other = e.getOrigin() == high ? e.getDestination() : e.getOrigin();
								int otherNum = numbering[vertices.indexOf(other)];
								if (otherNum < highest && otherNum > separationPairNum){
									separationVertexOk = true;
									break;
								}
							}
					}
					else{
						separationVertexOk = true;
					}

					if (!separationVertexOk)
						continue;

					for (E e : adjacency.get(high)){
						if (!coveredEdges.contains(e)){
							ok = true;
							break;
						}
					}

					if (!ok)
						continue;

					//System.out.println("HIGH: " + high);

					//are there any separation pairs vertices occurring before the vertex

					//TODO mozda pustiti ovde dfs ili nekako drugacije smanjiti tu pretragu putanja


					int vertexNumber = numbering[vertices.indexOf(high)];

					if (vertexNumber > highest){

						List<E> edgesToAdd = new ArrayList<E>();

						//connect this vertex with previous highest
						V next = highestVertex;
						List<E> treeEdgesBetween = tree.treeEdgesBetween(highestVertex, high);
						if (treeEdgesBetween == null){
							//different branching
							treeEdgesBetween = tree.treeEdgesBetween(end, high);
						}


						for (E e : treeEdgesBetween){
							next = e.getOrigin() == next ? e.getDestination() : e.getOrigin();
							System.out.println("Tree edge " + e);
							if (separationPairVertices.contains(next) && next != start && next != end){
								ok = false;
								break;
							}

							edgesToAdd.add(e);
							for (E e1 : adjacency.get(next)){
								if (fronds.contains(e1)){
									V other = e.getOrigin() == next ? e.getDestination() : e.getOrigin();
									int otherNum = numbering[vertices.indexOf(other)];
									if (otherNum >= startNumber)
										edgesToAdd.add(e1);
								}
							}

						}

						if (ok){
							highest = vertexNumber;
							highestVertex = high;
							componentEdges.addAll(edgesToAdd);
						}
					}
				}

			}

		}
		componentVertices.addAll(addedVertices);

		if (formingTripleBond && componentEdges.size() < 3)
			return;

		HopcroftTarjanSplitComponent<V, E> newComponent = new HopcroftTarjanSplitComponent<V,E>();
		newComponent.getEdges().addAll(componentEdges);

		E virtualEdge = createVirtualEdge(start, end, edgeClass, virtualEdges);

		newComponent.getEdges().add(virtualEdge);
		log.info("outputting new component");
		System.out.println(newComponent);

		System.out.println("SET VIRTUAL EDGE: " + virtualEdge);
		addCompnentWithMultiplevVirtualEdges(coveredVirtualEdges, newComponent, virtualEdge);
		System.out.println(newComponent.getVirtualEdge());

		estack.add(virtualEdge);
		virtualEdges.add(virtualEdge);


		for (E e : componentEdges)
			if (!coveredEdges.contains(e))
				coveredEdges.add(e);

	}


	private List<SplitPair<V,E>> findSeparationPairsToOutput(V end, Map<V, List<SplitPair<V, E>>> separationPairStartVertices, 
			Map<V, List<SplitPair<V, E>>> separationPairEndVertices, List<E> estack){

		log.info("finding separation pairs for splitting ");
		List<SplitPair<V,E>> ret = new ArrayList<SplitPair<V,E>>();

		V current = end;
		for (int i = estack.size() - 1; i >= 0; i--){

			E e = estack.get(i);

			current = e.getOrigin() == current ? e.getDestination() : e.getOrigin();

			if (separationPairStartVertices.containsKey(current)){
				for (SplitPair<V,E> splitPair : separationPairEndVertices.get(end)){
					if (splitPair.getV() == current){
						ret.add(splitPair);
						break;
					}
				}
			}

		}

		return ret;
	}

	/*
	 * Does adding the back edge form a path from split pair start to split pair end vertex
	 * Such back edge is either directly between the two split pair vertices
	 * Or is below the end edge
	 * If end was not yet reach, it can't close the path
	 * When end is reached, all back edges that should be in that component are added
	 * Which means that we are searching for components which will contain the virtual edge
	 * between start and end vertex
	 * That means that the back edge should end in a split pair start vertex
	 * Otherwise we would have to traverse up, and that is not possible since those tree edges should 
	 * already belong to a different component
	 */
	private void outputComponentBackEdge(E backEdge, List<E> estack, Map<V, List<SplitPair<V, E>>> separationPairStartVertices, 
			Map<V, List<SplitPair<V, E>>> separationPairEndVertices, List<E> coveredEdges, List<E> virtualEdges){

		//check if this edge is split edge - its endpoints form a split pair

		V v1 = backEdge.getOrigin();
		V v2 = backEdge.getDestination();

		List<E> coveredVirtualEdges = new ArrayList<E>();

		SplitPair<V,E> edgeSplitPair = splitEdgeOf(backEdge);
		if (edgeSplitPair != null){

			V start = edgeSplitPair.getV();
			V end = edgeSplitPair.getU();

			//try to form a triple bond
			System.out.println("formin triple bond");
			List<E> virtualBetween = findAllVirutalEdgesBetween(start, end, virtualEdges);
			if (virtualBetween.size() == 0)
				return;

			HopcroftTarjanSplitComponent<V, E> newComponent = new HopcroftTarjanSplitComponent<V,E>();
			newComponent.getEdges().add(virtualBetween.get(0));
			newComponent.getEdges().add(backEdge);
			E virtualEdge = virtualBetween.get(0);
			newComponent.getEdges().add(virtualEdge);
			//add virtual edge to virtual edges list might be used to form another triple bond)
			virtualEdges.add(virtualEdge);
			
			log.info("outputting new component - triple bond");
			System.out.println(newComponent);
			estack.add(virtualEdge);
			coveredEdges.add(backEdge);
			addComponent(virtualEdge, newComponent, true);
		}
		else{

			//this case is about connecting already found virtual edges 
			//forming a component from edges on estack
			//passed back edge
			//and all back edges that are found to be between vertices of this component
			//in this case, a virtual edge is not created

			V backEdgeEnd, backEdgeStart;

			if (numbering[vertices.indexOf(v1)] < numbering[vertices.indexOf(v2)]){
				backEdgeEnd = v1;
				backEdgeStart = v2;
			}
			else{
				backEdgeStart = v1;
				backEdgeEnd = v2;
			}


			V current = backEdgeEnd;


			List<E> componentEdges = new ArrayList<E>();
			List<V> componentVertices = new ArrayList<V>();

			componentVertices.add(backEdgeStart);

			for (int i = estack.size() - 1; i >= 0; i--){

				E e = estack.get(i);

				System.out.println("current edge "+ e);


				current = e.getDestination() == current ? e.getOrigin() : e.getDestination();
				componentVertices.add(current);

				componentEdges.add(e);

				if (virtualEdges.contains(e))
					coveredVirtualEdges.add(e);

				if (current == backEdgeEnd)
					break;

			}

			//add all back edges
			for (V v : componentVertices){
				for (E e : adjacency.get(v)){

					if (fronds.contains(e) && !coveredEdges.contains(e)){
						V other = e.getOrigin() == v ? e.getDestination() : e.getOrigin();
						if (componentVertices.contains(other)){
							componentEdges.add(e);
							coveredEdges.add(e);
						}
					}
				}
			}

			estack.removeAll(componentEdges);

			HopcroftTarjanSplitComponent<V, E> newComponent = new HopcroftTarjanSplitComponent<V,E>();
			newComponent.getEdges().addAll(componentEdges);
			
			//E virtualEdge = Util.createEdge(start, end, edgeClass);
			//already contains the virtual edge
			//newComponent.getEdges().add(virtualEdge); //TODO is it possible that this edge wasn't on estack?
			//estack.add(virtualEdge);
			log.info("outputting new component");
			System.out.println(newComponent);
			
			//TODO kako znamo koji je to tacno separation par? koja je to ivica? da li moze biti vise virtuelnih
			
			addCompnentWithMultiplevVirtualEdges(coveredVirtualEdges, newComponent, coveredVirtualEdges.get(0));

			//TODO should be be done like this (otherwise, the triple bond containing edges can't be formed...)
			virtualEdges.addAll(coveredVirtualEdges);
			
			coveredEdges.add(backEdge);
		}

	}

	private boolean alreadyContainsEdge(E e, V start, V end, List<E> edges){
		if (isSplitEdge(e, start, end))
			return true;
		if (edges.contains(e))
			return true;
		for (E anEdge : edges){
			if ((anEdge.getDestination() == e.getDestination() && e.getOrigin() == anEdge.getOrigin()) || 
					(anEdge.getOrigin() == e.getDestination() && e.getOrigin() == anEdge.getDestination()))
				return true;

		}
		return false;

	}

	/*
	 * Checks if there is already a virtual edge between vertices
	 * if it does not exist, create one
	 * Important so that we could more easily later check 
	 * which components share the same virtual edge
	 */
	private E createVirtualEdge(V start, V end, Class<?> edgeClass, List<E> virtualEdges) {

		E virtualEdge = findVirtualEdgeBetween(start, end, virtualEdges);
		if (virtualEdge == null)
			virtualEdge = Util.createEdge(start, end, edgeClass);

		return virtualEdge;
	}

	private E findVirtualEdgeBetween(V start, V end, List<E> virtualEdges){
		for (E e : virtualEdges)
			if (isSplitEdge(e, start, end))
				return e;
		return null;
	}

	private List<E> findAllVirutalEdgesBetween(V start, V end, List<E> virtualEdges){
		List<E> ret = new ArrayList<E>();

		for (E e : virtualEdges)
			if (isSplitEdge(e, start, end))
				ret.add(e);

		return ret;
	}

	private boolean isSplitEdge(E e, V start, V end){
		if ((e.getOrigin() == start && e.getDestination() == end)
				|| (e.getDestination() == start && e.getOrigin() == end))
			return true;
		return false;
	}

	private SplitPair<V,E> splitEdgeOf(E e){
		V v1 = e.getOrigin();
		V v2 = e.getDestination();

		if (separationPairStartVertices.containsKey(v1))
			for (SplitPair<V, E> sp : separationPairStartVertices.get(v1))
				if (sp.getU() == v2)
					return sp;
		//not else if, because both of them can be start vertices, but only one of them can be start vertex
		//of the pair that contains both of them
		if (separationPairStartVertices.containsKey(v2))
			for (SplitPair<V, E> sp : separationPairStartVertices.get(v2))
				if (sp.getU() == v1)
					return sp;

		return null;
	}

	private void addCompnentWithMultiplevVirtualEdges(List<E> virtualEdges,HopcroftTarjanSplitComponent<V, E> newComponent, E virtualEdge){
		addComponent(virtualEdge, newComponent, true);
		for (E v2 : virtualEdges)
			if (v2 == virtualEdge)
				continue;
			else
				addComponent(v2, newComponent, false);
	}

	private void addComponent(E virtualEdge, HopcroftTarjanSplitComponent<V, E> newComponent, boolean setVirtualEdge) {

		components.add(newComponent);

		List<HopcroftTarjanSplitComponent<V, E>> virtutalEdgeComponents;

		if (!componentsVirtualEdgesMap.containsKey(virtualEdge)){
			virtutalEdgeComponents = new ArrayList<HopcroftTarjanSplitComponent<V,E>>();
			componentsVirtualEdgesMap.put(virtualEdge, virtutalEdgeComponents);
		}
		else 
			virtutalEdgeComponents = componentsVirtualEdgesMap.get(virtualEdge);
		
		
		//determine type of the component
		if (newComponent.getEdges().size() > 3)
			newComponent.setType(SplitComponentType.TRICONNECTED_GRAPH);
		else{
			//check if it is a bond or a triangle
			SplitComponentType type = SplitComponentType.TRIANGLE;
			E e1 = newComponent.getEdges().get(0);
			E e2 = newComponent.getEdges().get(1);
			if (e1 == e2 || (e1.getDestination() == e2.getDestination() && e1.getOrigin() == e2.getOrigin()) || (e1.getOrigin() == e2.getDestination() && e1.getDestination() == e2.getOrigin()))
				type = SplitComponentType.TRIPLE_BOND;
			newComponent.setType(type);
		}
		
		if (setVirtualEdge)
			newComponent.setVirtualEdge(virtualEdge);
		
		if (!virtutalEdgeComponents.contains(newComponent))
			virtutalEdgeComponents.add(newComponent);

	}

	/*
	 * Analyzing virtual edges, see if there are 3 or more same virtual edges
	 * and form a triple bond containing them
	 */
	private void createComponentsConsistingOfVirtualEdgesOnly(List<E> virtualEdges){

		Map<E, Integer> occurancesMap = new HashMap<E, Integer>();

		System.out.println(virtualEdges);
		for (E e : virtualEdges){

			Integer num = occurancesMap.get(e);
			if (num == null)
				occurancesMap.put(e, 1);
			else
				occurancesMap.put(e, num + 1);
		}
		

		for (E e : occurancesMap.keySet()){

			Integer num = occurancesMap.get(e);
			if (num >= 3){
				int count = 1;
				while (count <= num){
					if (count % 3 == 0){
						//form triple bond
						HopcroftTarjanSplitComponent<V, E> newComponent = new HopcroftTarjanSplitComponent<V,E>();
						newComponent.getEdges().add(e);
						newComponent.getEdges().add(e);
						newComponent.getEdges().add(e);
						addComponent(e, newComponent, true);
						log.info("Creating triple bond composed of virtual edges");
						System.out.println(newComponent);
					}
					count ++;
				}
			}

		}
	}
	
	
	public Map<E, List<HopcroftTarjanSplitComponent<V, E>>> getComponentsVirtualEdgesMap() {
		return componentsVirtualEdgesMap;
	}


	public List<SplitPair<V, E>> getSeparationPairs() {
		return separationPairs;
	}


	public List<HopcroftTarjanSplitComponent<V, E>> getComponents() {
		return components;
	}
}
