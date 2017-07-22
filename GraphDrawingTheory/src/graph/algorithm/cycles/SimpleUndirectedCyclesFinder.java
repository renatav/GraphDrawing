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

/**
 * Finds all cycles of a graph
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type
 */
public class SimpleUndirectedCyclesFinder<V extends Vertex, E extends Edge<V>> {


	private Graph<V,E> graph;

	private Map<V, Set<List<V>>> cyclesContainingVertex = new HashMap<V, Set<List<V>>>();

	public SimpleUndirectedCyclesFinder(Graph <V,E> graph){
		this.graph = graph;	
	}

	/**
	 * Finds all cycles of a graph. A graph can have a very large number of cycles,
	 * and the algorithm is only effective if that is not the case. 
	 * @return All cycles of a graph, where each cycles is represented as a list of
	 * vertices it contains
	 */
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


				if (containingEdge.size() >= 2)
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


							//is v1, v2 edge in cycle 1

							boolean isEdgeInCycle1 = false;
							boolean isEdgeInCycle2 = false;

							int v1index = cycle1.indexOf(v1);
							int v2index = cycle1.indexOf(v2);
							int dif = Math.abs(v1index - v2index);
							if (dif == 1 || dif == cycle1.size() - 1)
								isEdgeInCycle1 = true;

							v1index = cycle2.indexOf(v1);
							v2index = cycle2.indexOf(v2);
							dif = Math.abs(v1index - v2index);
							if (dif == 1 || dif == cycle2.size() - 1)
								isEdgeInCycle2 = true;

							if (!isEdgeInCycle1 && !isEdgeInCycle2)
								continue;

							List<V> resultingCycle = null;

							if (isEdgeInCycle1 && isEdgeInCycle2){

								//the cycles share and edge
								//try to join them

								if (!shouldJoinCycles(cycle1, cycle2, v1, v2))
									continue;

								resultingCycle = joinCycles(cycle1, cycle2, v1, v2);

							}

							else{

								//one has vertices as an edge
								//the other has them too, but they are not directly linked
								//see if these cycles should be subtracted
								//the one that does not contain the edge should be bigger
								//and formed by joining the one that contains it with
								//another that contains it
								List<V> bigger = cycle1;
								List<V> smaller = cycle2;
								if (!isEdgeInCycle2){
									bigger = cycle2;
									smaller = cycle1;
								}

								if (shouldTryToSubtractCycles(bigger, smaller)){
									resultingCycle = subtractCycles(bigger, smaller, v1, v2);
								}

							}

							if (resultingCycle == null)
								continue;

							List<V> existing = alreadyExists(resultingCycle);

							if (existing == null){
								currentNew.add(resultingCycle);
								existing = resultingCycle;
							}


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
							list1.add(existing);
							list2.add(existing);


							for (V v : existing)
								cyclesContainingVertex.get(v).add(existing);
						}
					}



			}
			ret.addAll(currentNew);
			//System.out.println(currentNew);

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

		//System.out.println("edge " + edgeStart + " " + edgeEnd);
		//System.out.println("Subtracting " + cycle1 + " and " + cycle2);


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

	private boolean shouldTryToSubtractCycles(List<V> bigger, List<V> smaller){

		if (bigger.size() <= smaller.size())
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

	private List<V> alreadyExists(List<V> newCycle){

		Set<List<V>> cycles = cyclesContainingVertex.get(newCycle.get(0));
		Set<List<V>> result = new HashSet<List<V>>(cycles);

		for (int i = 1; i < newCycle.size(); i++)
			result.retainAll(cyclesContainingVertex.get(newCycle.get(i)));

		//		System.out.println("Remaining cycles: " + cycles);


		for (List<V> cycle : cycles){
			if (cycle.size() != newCycle.size())
				continue;

			//now check order of vertices
			int indexCycle = (cycle.indexOf(newCycle.get(0)) + 1) % cycle.size();
			int indexNewCycle = 1;
			boolean permutation = true;
			boolean reverse = false;
			boolean first = true;
			
			//System.out.println("Comparing " + newCycle + "and " + cycle);
			
			while (indexNewCycle < newCycle.size()){
				
				if (cycle.get(indexCycle) != newCycle.get(indexNewCycle)){
					if (first){
						//reset and try the other way
						reverse = true;
						first = false;
						indexCycle = cycle.indexOf(newCycle.get(0));
						indexNewCycle = 0;
						continue;
					}
					else{
						permutation = false;
						break;
					}
				}
				first = false;
				indexNewCycle ++;
				if (!reverse){
					indexCycle ++;	
					indexCycle = indexCycle % cycle.size();
				}
				else{
					indexCycle --;
					if (indexCycle == -1)
						indexCycle = cycle.size() - 1;
				}
			}

			if (permutation){
				//System.out.println(newCycle + " is permutation of " + cycle);
				return cycle;
			}
		}

		return null;
	}



}
