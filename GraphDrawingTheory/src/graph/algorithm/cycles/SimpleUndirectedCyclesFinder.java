package graph.algorithm.cycles;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleUndirectedCyclesFinder<V extends Vertex, E extends Edge<V>> {


	private Graph<V,E> graph;

	public SimpleUndirectedCyclesFinder(Graph <V,E> graph){
		this.graph = graph;	
	}

	public List<List<V>> findAllCycles(){

		List<List<V>> baseCycles = new PatonSimpleCycles<V,E>(graph).findSimpleCycles();
		List<List<V>> ret = new ArrayList<List<V>>();


		//initialization
		Map<V, List<List<V>>> cyclesContainingVertex = new HashMap<V, List<List<V>>>();
		for (List<V> cycle : baseCycles){
			for (V v : cycle){
				List<List<V>> cycles;
				if (!cyclesContainingVertex.containsKey(v)){
					cycles = new ArrayList<List<V>>();
					cyclesContainingVertex.put(v, cycles);
				}
				else
					cycles = cyclesContainingVertex.get(v);
				cycles.add(cycle);

			}
		}

		ret.addAll(baseCycles);
		List<List<V>> currentNew = new ArrayList<List<V>>();
		List<List<V>> containingEdge = new ArrayList<List<V>>();
		Map<List<V>, List<List<V>>> joinedCycles = new HashMap<List<V>, List<List<V>>>();
		
		do{

			currentNew.clear();
			for (E e : graph.getEdges()){
				containingEdge.clear();
				V v1 = e.getDestination();
				V v2 = e.getOrigin();
				List<List<V>> containingV1 = cyclesContainingVertex.get(v1);
				List<List<V>> containingV2 = cyclesContainingVertex.get(v2);
				for (List<V> cycle : containingV1){
					if (!containingV2.contains(cycle))
						continue;
					containingEdge.add(cycle);
				}
				if (containingEdge.size() < 2)
					continue; //nothing to join

				//now join two blocks
				for (int i = 0; i < containingEdge.size() - 1; i++){
					for (int j = i + 1; j < containingEdge.size(); j++){
						List<V> cycle1 =  containingEdge.get(i);
						List<V> cycle2 = containingEdge.get(j);
						
						boolean joined = false;
						
						if (joinedCycles.containsKey(cycle1)){
							if (joinedCycles.get(cycle1).contains(cycle2))
								joined = true;
						}
						if (!joined){
							if (joinedCycles.containsKey(cycle2))
								if (joinedCycles.get(cycle2).contains(cycle1))
									joined = true;
						}
						
						
						if (joined)
							continue;
						
						//check if cycles should be joined
						if (!shouldJoinCycles(cycle1, cycle2, v1, v2)) 
							continue;
						
						//join cycles
						List<V> joinedCycle = joinCycles(cycle1, cycle2, v1, v2);
						System.out.println(joinedCycle);
						
						//TODO check if cycle is a permutation of an existing one!
						currentNew.add(joinedCycle);
						
						List<List<V>> joinedList;
						if (joinedCycles.containsKey(cycle1)){
							joinedList = joinedCycles.get(cycle1);
							joinedList.add(cycle2);
						}
						else if (joinedCycles.containsKey(cycle2)){
							joinedList = joinedCycles.get(cycle2);
							joinedList.add(cycle1);
						}
						else{
							joinedList = new ArrayList<List<V>>();
							joinedList.add(cycle2);
							joinedCycles.put(cycle1, joinedList);
						}
						
						for (V v : joinedCycle)
							cyclesContainingVertex.get(v).add(joinedCycle);
						
					}
				}
			}
			ret.addAll(currentNew);
			System.out.println(currentNew);

		}
		
		while(currentNew.size() > 0);


		return ret;

	}

	private List<V> joinCycles(List<V> cycle1, List<V> cycle2, V edgeStart, V edgeEnd){
		
		System.out.println("Joining cycles: " + cycle1 + " " + cycle2 + " by " + edgeStart + " " + edgeEnd);
		
		
		List<V> joined = new ArrayList<V>();
		int indexStartCycle2 = cycle2.indexOf(edgeStart);
		int indexEndCycle2 = cycle2.indexOf(edgeEnd);

		boolean addedCycle2 = false;

		for (V v : cycle1){

			if (!joined.contains(v))
				joined.add(v);	

			if (!addedCycle2){
				if (v == edgeStart){
					//add cycle2
					//edgeStart and edgeEnd are next to each other, but in which direction
					V next;
					if (indexStartCycle2 == cycle2.size() - 1)
						next = cycle2.get(0);
					else
						next = cycle2.get(indexStartCycle2 + 1);

					boolean forward = true;
					if (next == edgeEnd)
						forward = false;

					int currentIndex = indexStartCycle2;
					int cycle2Size = cycle2.size();
					while (currentIndex != indexEndCycle2){

						if (forward){
							currentIndex ++;
							currentIndex = currentIndex % cycle2Size;
						}
						else{
							currentIndex--;
							if (currentIndex == -1)
								currentIndex = cycle2Size - 1;
						}
						
						joined.add(cycle2.get(currentIndex));

					}
					addedCycle2 = true;
				}
				else if (v == edgeEnd){
					//add cycle2
					//edgeStart and edgeEnd are next to each other, but in which direction
					V next;
					if (indexEndCycle2 == cycle2.size() - 1)
						next = cycle2.get(0);
					else
						next = cycle2.get(indexEndCycle2 + 1);

					boolean forward = true;
					if (next == edgeStart)
						forward = false;

					int currentIndex = indexEndCycle2;
					int cycle2Size = cycle2.size();

					while (currentIndex != indexStartCycle2){

						if (forward){
							currentIndex ++;
							currentIndex = currentIndex % cycle2Size;
						}
						else{
							currentIndex--;
							if (currentIndex == -1)
								currentIndex = cycle2Size - 1;
						}
						
						joined.add(cycle2.get(currentIndex));

					}
					addedCycle2 = true;
				}
			}
		}
		return joined;

	}
	
	private boolean shouldJoinCycles(List<V> cycle1, List<V> cycle2, V edgeStart, V edgeEnd){
		
		List<V> bigger = cycle1.size() > cycle2.size() ? cycle1 : cycle2;
		List<V> smaller = bigger == cycle1 ? cycle2 : cycle1;
		
		for (V v : smaller)
			if (bigger.contains(v) && v != edgeStart && v != edgeEnd)
				return false;
		
		return true;
				
		
		
	}


}
