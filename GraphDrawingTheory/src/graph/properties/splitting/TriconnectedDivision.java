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


/**
 * Implementation of Hopcroft's and Tarjan's algorithm which divides a graph
 * into triconnected components and finds separation pairs
 * @author xx
 *
 */

public class TriconnectedDivision<V extends Vertex, E extends Edge<V>> {



	private Graph<V,E> graph;
	private Class<?> edgeClass;

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
		List<SplitPair<V,E>> separationPairs = separationPairSplitting.getSeparationPairs();
		Map<V,List<E>> adjacency = separationPairSplitting.getAdjacency();
		Map<V, List<SplitPair<V,E>>> separationPairStartVertices = separationPairSplitting.getSeparationPairStartVertices();
		Map<V, List<SplitPair<V,E>>> separationPairEndVertices = separationPairSplitting.getSeparationPairEndVertices();
		
		
		System.out.println("start " + separationPairStartVertices);
		System.out.println("end " + separationPairEndVertices);
		
		List<E> virtualEdges = new ArrayList<E>();
		List<E> estack = new ArrayList<E>();
		V start = graph.getVertices().get(0);
		dfs(start, separationPairs, adjacency, estack, separationPairStartVertices, separationPairEndVertices, new ArrayList<V>());
		
	}
	
	
	private void dfs(V v, List<SplitPair<V,E>> separationPairs,
			Map<V,List<E>> adjacency, List<E> estack, Map<V, List<SplitPair<V,E>>> separationPairStartVertices, 
			Map<V, List<SplitPair<V,E>>> separationPairEndVertices, List<V> coveredVertices){
		
		System.out.println("dfs for vertex " + v);
		for (E e : adjacency.get(v)){
			V w = e.getOrigin() == v ? e.getDestination() : e.getOrigin();
			
			estack.add(e);
			//check if w is ending vertex of a split pair
			if (separationPairEndVertices.containsKey(w)){
				System.out.println("contains");
				outputComponent(w, separationPairStartVertices, estack);
				System.out.println(w);
				System.out.println(estack);
			}
			
			if (!coveredVertices.contains(w)){
				coveredVertices.add(w);
				dfs(w, separationPairs, adjacency, estack, separationPairStartVertices, separationPairEndVertices, coveredVertices);
			}
			
	}
  

}


	private void outputComponent(V w,
			Map<V, List<SplitPair<V, E>>> separationPairStartVertices,
			List<E> estack) {
		
			V previous = w;
			V current = null;
			List<E> edges = new ArrayList<E>();
			boolean ok = false;
			for (int i = estack.size() - 1; i >= 0; i--){
				E e = estack.get(i);
				edges.add(e);
				current = e.getOrigin() == previous ? e.getDestination() : e.getOrigin();
				if (separationPairStartVertices.containsKey(current)){
					for (SplitPair<V, E> separationPair : separationPairStartVertices.get(current))
						if (separationPair.getU() == w){
							ok = true;
							break;
						}
					if (ok)
						break;
				}
			}
			
			if (ok){
				estack.removeAll(edges);
				HopcroftSplitComponent<V, E> newComponent = new HopcroftSplitComponent<V,E>();
				newComponent.getEdges().addAll(edges);
				estack.removeAll(edges);
				System.out.println(newComponent);
				E virtualEdge = Util.createEdge(current, w, edgeClass);
				estack.add(virtualEdge);
			}
			
		}
		
	}
