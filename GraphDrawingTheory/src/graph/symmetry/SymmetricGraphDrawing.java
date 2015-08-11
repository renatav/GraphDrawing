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

	private Map<V, PermutationCycle<V>> vertexCycle = new HashMap<V, PermutationCycle<V>>();
	private Map<Integer, Integer> maxLenghts;
	private List<List<PermutationCycle<V>>> paths = new ArrayList<List<PermutationCycle<V>>>();

	public SymmetricGraphDrawing(Graph<V,E> graph){
		this.graph = graph;
		this.nauty = new McKayGraphLabelingAlgorithm<V, E>(graph);
	}

	public void execute(){


		System.out.println("executing");

		List<PermutationCycle<V>> visitedCycles = new ArrayList<PermutationCycle<V>>();

		//find suitable permutation
		//consider permutations which contain more than
		//one cycles of the same length
		//Pair<List<List<Integer>>,Integer> cycleAndLength = findPermutation();
		//List<List<Integer>> g = cycleAndLength.getKey();

		List<Permutation> automorphisms = nauty.findAutomorphisms();
		for (Permutation p : automorphisms){
			
			CyclicPermutation<V> g = new CyclicPermutation<V>(graph.getVertices(), p);

			System.out.println("Permutation " + g);

			maxLenghts = maxLenghts(g);
			
			System.out.println("Max lenghts : " +maxLenghts);

			//extract all cycles of the defined length


			for (PermutationCycle<V> cycle: g.getCycles())
				for (V v : cycle.getCycle())
					vertexCycle.put(v, cycle);

			
			Map<Integer, List<PermutationCycle<V>>> bestPathForLength = 
					new HashMap<Integer,List<PermutationCycle<V>>>();

			
			//TODO 
			//kako naci najbolju kombinaciju, recimo, mozda mogu sve
			//ako se krene od nekog drugog ciklusa, drugim redosledom...
			//tipa krenuti u obilazak
			//kada se dodje drugi put do nekog, pogledati za njega da li je 
			//to najbolja moguca putanja, i tako
			
			for (PermutationCycle<V> cycle: g.getCycles()){
				System.out.println("current cycle: " + cycle);
				
				Integer baseLenght = cycle.size();
				
				if (bestPathForLength.get(baseLenght) != null)
					continue;
				
				List<PermutationCycle<V>> bestPath = new ArrayList<PermutationCycle<V>>(); 
				if (cycle.size() > 1 && !visitedCycles.contains(cycle)){
					boolean done = extendPath(cycle, cycle, visitedCycles, new ArrayList<PermutationCycle<V>>(), bestPath);
					if (!done){
						visitedCycles.add(cycle);
						maxLenghts.put(baseLenght, maxLenghts.get(baseLenght) - baseLenght);;
					}
					else{
						bestPathForLength.put(baseLenght, bestPath);
					}
						
				}
					
			}
		}

	}
	
	
	
	private boolean extendPath(PermutationCycle<V> currentCycle, PermutationCycle<V> baseCycle, 
			List<PermutationCycle<V>> visitedCycles, List<PermutationCycle<V>> path, List<PermutationCycle<V>> bestPath){
		
		
		path.add(currentCycle);
		
		//there is only one cycle
		if (currentCycle.size() == maxLenghts.get(currentCycle.size())){
			bestPath.add(currentCycle);
			return true;
		}
		
		System.out.println("Current path: " + path);
		
		
		System.out.println("visiting cycle " + currentCycle);
		
		List<PermutationCycle<V>> linkedCycles = new ArrayList<PermutationCycle<V>>();
		
		//find links between this cycle and others
		for (V v : currentCycle.getCycle()){
			for (E e : graph.allEdges(v)){
				V linked = e.getDestination() == v ? e.getOrigin() : e.getDestination();
				if (vertexCycle.get(linked) == currentCycle)
					continue;
				PermutationCycle<V> cycle = vertexCycle.get(linked);
				if (!linkedCycles.contains(cycle) && !visitedCycles.contains(cycle))
					linkedCycles.add(cycle);
			}
		}
		
		System.out.println("Linked cycles: " + linkedCycles);
		
		for (PermutationCycle<V> linkedCycle : linkedCycles){
			
			if (linkedCycle.size() != baseCycle.size())
				continue;
			
			
			
			if (linkedCycle == baseCycle){
				System.out.println("back to base cycle");
				
				
				if (path.size() == 2) //ignore cycle of lenght 2
					continue;
				
				//save this as best path if it's better than the current one
				if (path.size() > bestPath.size()){
					bestPath.clear();
					bestPath.addAll(path);
					System.out.println("Best path: " + bestPath);
				}
				
				
				//see if the maximal covered path was taken
				int covered = path.size() * baseCycle.size();
				if (covered == maxLenghts.get(baseCycle.size())){
						return true;
				}
				
			}
			
			else{
				visitedCycles.add(linkedCycle);
				if (extendPath(linkedCycle, baseCycle, visitedCycles, path, bestPath))
					return true;
				
				System.out.println("removing " + linkedCycle);
				
				//else reset for next iteration
				visitedCycles.remove(linkedCycle);
				path.remove(linkedCycle);
			}
		}
		
		
		return false;
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

	private Map<Integer, Integer> maxLenghts(CyclicPermutation<V> permutation){
		Map<Integer, Integer> ret = new HashMap<Integer, Integer>();

		for (PermutationCycle<V> cycle : permutation.getCycles()){
			Integer length = cycle.size();
			if (ret.containsKey(length)){
				Integer num = ret.get(length);
				ret.put(length, num + 1 );
			}
			else
				ret.put(length, 1);
		}
		for (Integer length : ret.keySet()){
			ret.put(length, length * ret.get(length));
		}
		return ret;
	}


}
