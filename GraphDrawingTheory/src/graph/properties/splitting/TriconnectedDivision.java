package graph.properties.splitting;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.properties.components.HopcroftSplitComponent;
import graph.properties.components.SplitPair;
import graph.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.prefs.BackingStoreException;


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

		List<E> virtualEdges = new ArrayList<E>();
		List<E> estack = new ArrayList<E>();
		V start = graph.getVertices().get(0);
		List<V> vstack = new ArrayList<V>();
		dfs(start, estack, vstack, new ArrayList<V>(), new ArrayList<E>());

	}


	private void dfs(V v, List<E> estack, List<V> vstack, List<V> coveredVertices, List<E> coveredEdges){

		System.out.println("dfs for vertex " + v);
		if (separationPairStartVertices.containsKey(v) || separationPairEndVertices.containsKey(v))
			vstack.add(v);
		
		System.out.println("current vstack" + vstack);

		for (E e : adjacency.get(v)){

			if (coveredEdges.contains(e))
				continue;

			V w = e.getOrigin() == v ? e.getDestination() : e.getOrigin();



			if (treeEdges.contains(e)){

				estack.add(e);

				System.out.println("current estack: " + estack);

				if (separationPairEndVertices.containsKey(w)){ 
					outputComponent(w, separationPairStartVertices, separationPairEndVertices, vstack, estack);

				}


				//check if w is ending vertex of a split pair
				//			if (separationPairEndVertices.containsKey(w)){
				//				System.out.println("contains");
				//				outputComponent(w, separationPairStartVertices, estack);
				//				System.out.println(w);
				//				System.out.println(estack);
				if (!coveredVertices.contains(w)){
					coveredVertices.add(w);
					dfs(w, estack, vstack, coveredVertices, coveredEdges);
				}

			}


		}

	}



	private void outputComponent(V w, Map<V, List<SplitPair<V, E>>> separationPairStartVertices, 
			Map<V, List<SplitPair<V, E>>> separationPairEndVertices, List<V> vstack, List<E> estack) {


		//System.out.println("ending: " + w);
		//find start vertex
		V start = null;

		//TODO ovo poraviti, nije dovoljno
		//treba pogledati po ivicama, kada se dodje do pocetka...
		for (SplitPair<V,E> splitPair : separationPairEndVertices.get(w)){ 
			start = splitPair.getV();
			System.out.println(splitPair);
			if (vstack.contains(start))
				break;
		}

//		vstack.remove(start);
//		vstack.remove(w);

		V current = w;

		//System.out.println("Start: " + start);


		List<E> edges = new ArrayList<E>();
		List<E> componentEdges = new ArrayList<E>();
		List<V> componentVertices = new ArrayList<V>();

		for (int i = estack.size() - 1; i >= 0; i--){

			E e = estack.get(i);
			//System.out.println("current edge: " + e);

			componentVertices.add(current);
			componentEdges.add(e);
			edges.add(e);
			current = e.getOrigin() == current ? e.getDestination() : e.getOrigin();
			//System.out.println("current: " + current);
		
			if (current == start){
				break;
			}

		}

		estack.removeAll(edges);

		int lowestpt = numbering[vertices.indexOf(start)];
		V lowestVertex = start;
		
		//if a vertex is one of two vertices of the split pair
		//do not add its back edges unless some other vertex
		//is connected to endpoints of those edges as well
		
		List<V> addedVertices = new ArrayList<V>();
		for (V v : componentVertices){
			if (v == start || v == w)
				continue;
			
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
							System.out.println(currentEdge);
							traversedEdges.add(currentEdge);
							V otherLow =  currentEdge.getOrigin() == currentLow ? currentEdge.getDestination() : currentEdge.getOrigin();
							traversedVertices.add(otherLow);
							if (vstack.contains(otherLow))
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
				}
			
			
		}
		componentVertices.addAll(addedVertices);
		
		//check separation pair vertices
		for (E e : adjacency.get(start))
			if (fronds.contains(e)){
				V other = e.getOrigin() == start ? e.getDestination() : e.getOrigin();
				int currentNum = numbering[vertices.indexOf(other)];
				if (currentNum >= lowestpt)
					componentEdges.add(e);
			}
		
		for (E e : adjacency.get(w))
			if (fronds.contains(e)){
				V other = e.getOrigin() == w ? e.getDestination() : e.getOrigin();
				int currentNum = numbering[vertices.indexOf(other)];
				if (currentNum >= lowestpt)
					componentEdges.add(e);
			}
		
		
			


		//zapamtiti koje su su svi cvorovi na ivicama estack-a
		//pogledati da li neki cvor direktno u komponenti ima povratnu ivicu
		//koja se zavrsava u tim cvorovima na estack-u
		//zapamtiti lowpt takvog cvora 
		//dodati sve ivice sa estack-a od njega do kraja
		//ali sta sa onima koji su posle?
		//imamo back ivicu ka njima...
		//dodati negde nekako na kraju?
		//kada se pojavi pridruziti u neku komponentu?
		//ili proci kroz adjacency end cvora pa tu nesto pogledati
		//mozda gore ne ignorisati povratne ivice

		HopcroftSplitComponent<V, E> newComponent = new HopcroftSplitComponent<V,E>();
		newComponent.getEdges().addAll(componentEdges);
		E virtualEdge = Util.createEdge(start, w, edgeClass);
		newComponent.getEdges().add(virtualEdge);
		System.out.println(newComponent);

		estack.add(virtualEdge);

	}

}
