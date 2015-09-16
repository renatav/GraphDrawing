package graph.properties.splitting;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.properties.components.HopcroftSplitComponent;
import graph.properties.components.SplitPair;
import graph.trees.DFSTree;
import graph.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.prefs.BackingStoreException;

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
	private int[] lowpt;
	private Map<Integer, List<V>> lowpt1sMap;
	private DFSTree<V, E> tree;
	private Logger log = Logger.getLogger(TriconnectedDivision.class);
	private List<V> separationPairVertices;


	public TriconnectedDivision(Graph<V,E> graph){
		this.graph = graph;
		edgeClass = graph.getEdges().get(0).getClass();
	}


	public void execute(Graph<V,E> graph){

		triconnected(graph);

		//		try {
		//			splitting.findSeaparationPairs(graph, edgeClass);
		//		} catch (AlgorithmErrorException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
	}


	private void triconnected(Graph<V,E> graph){

		List<HopcroftSplitComponent> splitComponents = new ArrayList<HopcroftSplitComponent>();
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
			separationPairSplitting.findSeaparationPairs(graph, edgeClass);
		} catch (AlgorithmErrorException e) {
			e.printStackTrace();
		}
		separationPairs = separationPairSplitting.getSeparationPairs();
		adjacency = separationPairSplitting.getAdjacency();
		separationPairStartVertices = separationPairSplitting.getSeparationPairStartVertices();
		separationPairEndVertices = separationPairSplitting.getSeparationPairEndVertices();
		treeEdges = separationPairSplitting.getTreeEdges();
		fronds = separationPairSplitting.getFronds();
		lowpt = separationPairSplitting.getLowpt1();
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
		dfs(start, estack, vstack,  new ArrayList<E>());

	}


	private void dfs(V v, List<E> estack, List<V> coveredVertices, List<E> coveredEdges){

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
						outputComponent(splitPair, separationPairStartVertices, separationPairEndVertices, estack, coveredEdges);
					}

				}

				if (!coveredVertices.contains(w)){
					coveredVertices.add(w);
					dfs(w, estack, coveredVertices, coveredEdges);
				}
			}

			else {
				if (separationPairEndVertices.containsKey(w)){

					SplitPair<V,E> backEdgeSplit = null;
					for (SplitPair<V,E> splitPair : separationPairEndVertices.get(w))
						if (splitPair.getV() == v){
							backEdgeSplit = splitPair;
							estack.add(e);
							break;
						}
					System.out.println("Back edge split estact " + estack);
				}
			}

		}

	}



	private void outputComponent(SplitPair<V,E> splitPair, Map<V, List<SplitPair<V, E>>> separationPairStartVertices, 
			Map<V, List<SplitPair<V, E>>> separationPairEndVertices, List<E> estack, List<E> coveredEdges) {


		V start = splitPair.getV();
		V end = splitPair.getU();

		V current = end;

		int startNumber = numbering[vertices.indexOf(start)];
		int endNumber = numbering[vertices.indexOf(end)];


		List<E> edges = new ArrayList<E>();
		List<E> componentEdges = new ArrayList<E>();
		List<V> componentVertices = new ArrayList<V>();

		for (int i = estack.size() - 1; i >= 0; i--){

			E e = estack.get(i);

			componentVertices.add(current);
			componentEdges.add(e);
			edges.add(e);
			current = e.getOrigin() == current ? e.getDestination() : e.getOrigin();
			if (current == start)
				break;

		}


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

		for (V v : toProcess){


			//	if (v != start && v != end){
			for (E e : adjacency.get(v))
				if (fronds.contains(e)){
					//	System.out.println("frond: " + e);
					V other = e.getOrigin() == v ? e.getDestination() : e.getOrigin();
					int currentNum = numbering[vertices.indexOf(other)];
					if (currentNum >= lowestpt)
						componentEdges.add(e);
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

			System.out.println("TESTING LOWPTSMAP");
			System.out.println(v);
			System.out.println(verticesWithLowpt);


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

					System.out.println("HIGH: " + high);

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

		//		//check separation pair vertices
		//		for (E e : adjacency.get(start))
		//			if (fronds.contains(e)){
		//				V other = e.getOrigin() == start ? e.getDestination() : e.getOrigin();
		//				if (other == end)
		//					continue;
		//				int currentNum = numbering[vertices.indexOf(other)];
		//				if (currentNum >= lowestpt)
		//					componentEdges.add(e);
		//			}
		//
		//		for (E e : adjacency.get(end))
		//			if (fronds.contains(e)){
		//				V other = e.getOrigin() == end ? e.getDestination() : e.getOrigin();
		//				if (other == start)
		//					continue;
		//				int currentNum = numbering[vertices.indexOf(other)];
		//				if (currentNum >= lowestpt)
		//					componentEdges.add(e);
		//			}


		HopcroftSplitComponent<V, E> newComponent = new HopcroftSplitComponent<V,E>();
		newComponent.getEdges().addAll(componentEdges);
		E virtualEdge = Util.createEdge(start, end, edgeClass);
		newComponent.getEdges().add(virtualEdge);
		System.out.println(newComponent);
		estack.add(virtualEdge);

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
}
