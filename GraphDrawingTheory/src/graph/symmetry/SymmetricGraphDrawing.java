package graph.symmetry;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.nauty.McKayGraphLabelingAlgorithm;
import graph.nauty.Permutation;
import graph.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymmetricGraphDrawing<V extends Vertex, E extends Edge<V>> {


	private Graph<V,E> graph;
	private McKayGraphLabelingAlgorithm<V, E> nauty;

	public SymmetricGraphDrawing(Graph<V,E> graph){
		this.graph = graph;
		this.nauty = new McKayGraphLabelingAlgorithm<V, E>(graph);
	}

	public void execute(){

		//find suitable permutation
		//consider permutations which contain more than
		//one cycles of the same length
		Pair<List<List<Integer>>,Integer> cycleAndLength = findPermutation();
		List<List<Integer>> g = cycleAndLength.getKey();
		int m = cycleAndLength.getValue();
		System.out.println(g);
		System.out.println(m);
		
		//extract all cycles of the defined length
		List<List<Integer>> cycles = new ArrayList<List<Integer>>();
		Map<Integer, List<Integer>> vertexCycle = new HashMap<Integer, List<Integer>>();
		
		for (List<Integer> cycle : cycles)
			if (cycle.size() == m){
				cycles.add(cycle);
				for (Integer vIndex : cycle)
					vertexCycle.put(vIndex, cycle);
			}
		
		int bestCycleSize = 0;

	}


	
	//maximize k*m
	//k = number of cycles
	//m = length of a cycles
	private Pair<List<List<Integer>>,Integer> findPermutation(){

		List<Permutation> automorphisms = nauty.findAutomorphisms();
		List<List<Integer>> cyclicRepresentation = new ArrayList<List<Integer>>();
		int maxKM = 0;
		Integer cycleLength = null;

		Map<Integer,Integer> cyclesWithLengthsMap = new HashMap<Integer, Integer>();
		
		for (Permutation p : automorphisms){

			List<List<Integer>> currentCyclic = p.cyclicRepresenatation();
			cyclesWithLengthsMap.clear();
			for (List<Integer> cycle : currentCyclic){

				Integer length = cycle.size();
				if (length == 1)
					continue;

				if (!cyclesWithLengthsMap.containsKey(length))
					cyclesWithLengthsMap.put(length, 1);
				else {
					Integer num = cyclesWithLengthsMap.get(length);
					num++;
					cyclesWithLengthsMap.put(length, num);
				}
			}
			
			if (cyclesWithLengthsMap.size() > 0){
				int maxCurrentKM = 0;
				int currentCycleLength = 0;
				
				for (Integer key : cyclesWithLengthsMap.keySet()){
					Integer num = cyclesWithLengthsMap.get(key);
					int currentKM = key * num;
					if (currentKM > maxCurrentKM){
						maxCurrentKM = currentKM;
						currentCycleLength = num;
					}
				}
				
				if (maxCurrentKM > maxKM){
					cyclicRepresentation.clear();
					cyclicRepresentation.addAll(currentCyclic);
					cycleLength = currentCycleLength;
				}
			}


		}
		return new Pair<List<List<Integer>>,Integer>(cyclicRepresentation, cycleLength);
	}


}
