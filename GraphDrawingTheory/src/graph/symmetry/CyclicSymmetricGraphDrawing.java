package graph.symmetry;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.symmetry.nauty.McKayGraphLabelingAlgorithm;
import graph.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CyclicSymmetricGraphDrawing<V extends Vertex, E extends Edge<V>> {


	private Graph<V,E> graph;
	private McKayGraphLabelingAlgorithm<V, E> nauty;

	private Map<V, PermutationCycle<V>> vertexCycle = new HashMap<V, PermutationCycle<V>>();
	private Map<Integer, Integer> maxLenghts;

	public CyclicSymmetricGraphDrawing(Graph<V,E> graph){
		this.graph = graph;
		this.nauty = new McKayGraphLabelingAlgorithm<V, E>(graph);
	}
	
	
	private Map<Integer, List<PermutationCycle<V>>> executeForPermutation(Permutation p){
		
		
		CyclicPermutation<V> g = new CyclicPermutation<V>(graph.getVertices(), p);
		
		List<PermutationCycle<V>> cyclesNotOnPaths = new ArrayList<PermutationCycle<V>>();
		cyclesNotOnPaths.addAll(g.getCycles());

		System.out.println("Permutation " + g);

		maxLenghts = maxLenghts(g);

		//visited cycles are used and change in extend path
		List<PermutationCycle<V>> visitedCycles = new ArrayList<PermutationCycle<V>>();
		//processed cycles contains all cycles on which extend path was called
		//extend path does not have any impact on it
		List<PermutationCycle<V>> processedCycles = new ArrayList<PermutationCycle<V>>();


		for (PermutationCycle<V> cycle: g.getCycles())
			for (V v : cycle.getCycle())
				vertexCycle.put(v, cycle);


		//map containing best found paths, classified by lengths of the cycles contained within them
		Map<Integer, List<PermutationCycle<V>>> bestPathForLength = 
				new HashMap<Integer,List<PermutationCycle<V>>>();


		for (PermutationCycle<V> cycle: g.getCycles()){
			System.out.println("current cycle: " + cycle);

			Integer baseLength = cycle.size();

			//found best bath for cycles of this length
			if (bestPathForLength.get(baseLength) != null)
				if (bestPathForLength.get(baseLength).size() * baseLength == maxLenghts.get(baseLength))
					continue;

			List<PermutationCycle<V>> bestPath = new ArrayList<PermutationCycle<V>>();
			
			if (cycle.size() > 1){
				
				visitedCycles.clear();
				visitedCycles.addAll(processedCycles);
				
				boolean done = extendPath(cycle, cycle, visitedCycles, new ArrayList<PermutationCycle<V>>(), bestPath);
				
				processedCycles.add(cycle);

				if (bestPathForLength.get(baseLength) == null || bestPath.size() > bestPathForLength.get(baseLength).size()){
					bestPathForLength.put(baseLength, bestPath);
				}

				if (!done){
					maxLenghts.put(baseLength, maxLenghts.get(baseLength) - baseLength);;
				}
			}

		}
		
		//determine which cycles are not in any of the found paths
		for (Integer key : bestPathForLength.keySet())
			cyclesNotOnPaths.removeAll(bestPathForLength.get(key));
		
		//add those to the map of paths
		bestPathForLength.put(new Integer(-1), cyclesNotOnPaths);
		
		return bestPathForLength;

	}
	
	public List<List<V>> execute(Permutation p){
		return formVerticeLists(executeForPermutation(p));
	}
	

	public List<List<V>> execute(){


		//find suitable permutation
		//consider permutations which contain more than
		//one cycles of the same length
		//Pair<List<List<Integer>>,Integer> cycleAndLength = findPermutation();
		//List<List<Integer>> g = cycleAndLength.getKey();
		
		Map<Integer, List<PermutationCycle<V>>> bestFoundPaths 
			= new HashMap<Integer, List<PermutationCycle<V>>>();
		Map<Integer, List<PermutationCycle<V>>> permutationFoundPaths; 

		List<Permutation> automorphisms = nauty.findAutomorphisms();
		for (Permutation p : automorphisms){
			permutationFoundPaths = executeForPermutation(p);
			if (bestFoundPaths.size() == 0 || permutationFoundPaths.get(-1).size() < bestFoundPaths.get(-1).size()){
				bestFoundPaths.clear();
				bestFoundPaths.putAll(permutationFoundPaths);
			}
		}
		
		return formVerticeLists(bestFoundPaths);

		
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
	
	private List<List<V>> formVerticeLists(Map<Integer, List<PermutationCycle<V>>> map){
		
		List<List<V>> ret = new ArrayList<List<V>>();
		
		for (List<PermutationCycle<V>> cycles : map.values()){
			List<V> verticesinCycle = new ArrayList<V>();
			for (PermutationCycle<V> cycle : cycles)
				verticesinCycle.addAll(cycle.getCycle());
			ret.add(verticesinCycle);
		}
		
		return ret;
		
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
