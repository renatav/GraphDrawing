package graph.algorithm.cycles;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPopupMenu;

public class SimpleUndirectedCyclesFinder<V extends Vertex, E extends Edge<V>> {


	private Graph<V,E> graph;

	private Map<V, Set<List<V>>> cyclesContainingVertex = new HashMap<V, Set<List<V>>>();

	public SimpleUndirectedCyclesFinder(Graph <V,E> graph){
		this.graph = graph;	
	}

	public List<List<V>> findAllCycles(){

		List<List<V>> baseCycles = new PatonSimpleCycles<V,E>(graph).findSimpleCycles();
		List<List<V>> ret = new ArrayList<List<V>>();


		//initialization
		for (List<V> cycle : baseCycles){
			for (V v : cycle){
				Set<List<V>> cycles;
				if (!cyclesContainingVertex.containsKey(v)){
					cycles = new HashSet<List<V>>();
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
		int index = 0;

		do{

			currentNew.clear();
			for (E e : graph.getEdges()){
				containingEdge.clear();
				V v1 = e.getDestination();
				V v2 = e.getOrigin();
				Set<List<V>> containingV1 = cyclesContainingVertex.get(v1);
				Set<List<V>> containingV2 = cyclesContainingVertex.get(v2);
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

						boolean join = shouldJoinCycles(cycle1, cycle2, v1, v2);
						boolean subtract = false;
						if (!join)
							subtract = shouldTryToSubtractCycles(cycle1, cycle2);

						//check if cycles should be joined
						if (!join && !subtract) 
							continue;


						List<V> resultingCycle;

						if	(join)
							resultingCycle = joinCycles(cycle1, cycle2, v1, v2);
						else{ 
							resultingCycle = subtractCycles(cycle1, cycle2, v1, v2);
							//continue;
						}
						
						if (resultingCycle == null)
							continue;

						
							System.out.println("edge: " + e);
						System.out.println(cycle1 + " and " + cycle2);

						if (join)
							System.out.println("Joined cycle: " + resultingCycle);
						if (subtract){
							System.out.println("Subtracted cycle: " + resultingCycle);
						}

						//TODO check if cycle is a permutation of an existing one!
						if (!alreadyExists(resultingCycle))
							currentNew.add(resultingCycle);


						List<List<V>> list1 = joinedCycles.get(cycle1);
						List<List<V>> list2 = joinedCycles.get(cycle2);

						if (list1 == null){
							list1 = new ArrayList<List<V>>();
							joinedCycles.put(cycle1, list1);
						}

						if (list2 == null){
							list2 = new ArrayList<List<V>>();
							joinedCycles.put(cycle2, list2);
						}


						list1.add(cycle2);
						list2.add(cycle1);

						//to avoid subtraction between joined cycle and the cycles it was made from
						//add it to the joined list as well
						list1.add(resultingCycle);
						list2.add(resultingCycle);


						for (V v : resultingCycle)
							cyclesContainingVertex.get(v).add(resultingCycle);

					}
				}
			}
			ret.addAll(currentNew);
			//System.out.println(currentNew);
			
			//TODO ODVOJITI ODUZIMANJE I JOIN-OVANJE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			//za oduzimanje treba uzeti par
			//pogledati da li se mogu oduzeti po nekoj ivici
			//i onda
			//ne traziti po tome da sadrze oba nku ivicu
			//posto nece!

		}

		while(currentNew.size() > 0);


		return ret;

	}

	private List<V> joinCycles(List<V> cycle1, List<V> cycle2, V edgeStart, V edgeEnd){

		//System.out.println("Joining cycles: " + cycle1 + " " + cycle2 + " by " + edgeStart + " " + edgeEnd);

		List<V> joined = new ArrayList<V>();

		int indexFirtst = -1;
		V first = null;
		for (int i = 0; i < cycle1.size(); i++){
			indexFirtst = i;
			if (cycle1.get(i) == edgeStart){
				first = edgeStart;
				break;
			}
			if (cycle1.get(i) == edgeEnd){
				first = edgeEnd;
				break;
			}
			joined.add(cycle1.get(i));
		}

		//now add cycle2


		V current = first;
		int currentIndex = cycle2.indexOf(first);
		V other = edgeStart == first ? edgeEnd : edgeStart;


		//see which where to go
		int nextIndex = (currentIndex + 1) % cycle2.size();
		boolean forward = false;
		if (cycle2.get(nextIndex) != other)
			forward = true;


		while (current != other){

			joined.add(current);

			if (forward)
				currentIndex = (currentIndex + 1) % cycle2.size();

			else{
				currentIndex --;
				if (currentIndex == -1)
					currentIndex = cycle2.size() - 1;
			}
			current = cycle2.get(currentIndex);
		}

		joined.add(other);

		int indexOther = cycle1.indexOf(other);

		//first vertex was edge vertex, the other one is the last one (other end of the cycle
		//go backwards
		//otherwise, it's always the next one, cannot be a previous one or it would've been
		//the one found first

		if (indexOther == cycle1.size() - 1){
			for (int i = indexOther - 1; i > indexFirtst; i--)
				joined.add(cycle1.get(i));
		}
		else{
			for (int i = indexOther + 1; i < cycle1.size(); i++)
				joined.add(cycle1.get(i));
		}


		return joined;

	}

	private List<V> subtractCycles(List<V> cycle1, List<V> cycle2, V edgeStart, V edgeEnd){

		List<V> bigger = cycle1.size() > cycle2.size() ? cycle1 : cycle2;
		List<V> smaller = bigger == cycle1 ? cycle2 : cycle1;

		List<V> ret = new ArrayList<V>(bigger);
		for (V v : smaller)
			if (v != edgeStart && v != edgeEnd)
				ret.remove(v);
		
		//check if that is a cycle
		for (int i = 0; i < ret.size(); i++){
			V v1 = ret.get(i);
			int next = (i+1) % ret.size();
			V v2 = ret.get(next);
			if (!graph.hasEdge(v1, v2))
				return null;
		}

		return ret;
	}

	private boolean shouldTryToSubtractCycles(List<V> cycle1, List<V> cycle2){
		List<V> bigger = cycle1.size() > cycle2.size() ? cycle1 : cycle2;
		List<V> smaller = bigger == cycle1 ? cycle2 : cycle1;
		
		if (bigger.size() == smaller.size())
			return false;

		for (V v : smaller)
			if (!bigger.contains(v))
				return false;
		return true;
	}



	private boolean shouldJoinCycles(List<V> cycle1, List<V> cycle2, V edgeStart, V edgeEnd){

		List<V> bigger = cycle1.size() > cycle2.size() ? cycle1 : cycle2;
		List<V> smaller = bigger == cycle1 ? cycle2 : cycle1;

		for (V v : smaller)
			if (bigger.contains(v) && v != edgeStart && v != edgeEnd)
				return false;

		return true;

	}

	private boolean alreadyExists(List<V> newCycle){

		Set<List<V>> cycles = cyclesContainingVertex.get(newCycle.get(0));

		for (int i = 1; i < newCycle.size(); i++)
			cycles.retainAll(cyclesContainingVertex.get(newCycle.get(i)));

		System.out.println("Remaining cycles: " + cycles);

		for (List<V> cycle : cycles){
			if (cycle.size() != newCycle.size())
				continue;

			//now check order of vertices
			int indexCycle = cycle.indexOf(newCycle.get(0));
			int indexNewCycle = 0;
			boolean permutation = true;

			while (indexNewCycle < newCycle.size()){
				if (cycle.get(indexCycle) != newCycle.get(indexCycle)){
					permutation = false;
					break;
				}

				indexCycle ++;
				indexNewCycle ++;
				indexCycle = indexCycle % cycle.size();
			}
			if (permutation){
			//	System.out.println(newCycle + " is permutation of " + cycle);
				return true;
			}

		}


		return false;
	}


}
