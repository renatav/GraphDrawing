package graph.symmetry;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.nauty.McKayGraphLabelingAlgorithm;
import graph.nauty.Permutation;
import graph.util.Pair;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymmetricGraphDrawing<V extends Vertex, E extends Edge<V>> {


	private Graph<V,E> graph;
	private McKayGraphLabelingAlgorithm<V, E> nauty;

	private Integer bestCycleSize = 0, bestGCD;
	private List<Integer> baseCycle;
	private List<V> path = new ArrayList<V>();
	private List<V> bestPath = new ArrayList<V>();
	private Map<V, List<Integer>> vertexCycle = new HashMap<V, List<Integer>>();
	private List<V> vertices;
	private Map<Integer, Integer> maxLenghts;
	private List<List<V>> paths = new ArrayList<List<V>>();

	public SymmetricGraphDrawing(Graph<V,E> graph){
		this.graph = graph;
		this.nauty = new McKayGraphLabelingAlgorithm<V, E>(graph);
		vertices = graph.getVertices();
	}

	public void execute(){


		System.out.println("executing");

		List<List<Integer>> visitedCycles = new ArrayList<List<Integer>>();

		//find suitable permutation
		//consider permutations which contain more than
		//one cycles of the same length
		//Pair<List<List<Integer>>,Integer> cycleAndLength = findPermutation();
		//List<List<Integer>> g = cycleAndLength.getKey();

		List<Permutation> automorphisms = nauty.findAutomorphisms();
		for (Permutation p : automorphisms){
			List<List<Integer>> g = p.cyclicRepresenatation();

			System.out.println("Permutation " + g);

			maxLenghts = maxLenghts(g);

			//extract all cycles of the defined length


			for (List<Integer> cycle : g)
				for (Integer vIndex : cycle)
					vertexCycle.put(vertices.get(vIndex), cycle);

			for (List<Integer> cycle : g){
				System.out.println("current cycle: " + cycle);
				baseCycle = cycle;
				int baseLength = cycle.size();
				if (baseLength > 1){

					visitedCycles.add(cycle);
					//begin a new path
					V v = vertices.get(cycle.get(0));
					path.clear();
					path.add(v);
					boolean done = extendPath(v, null, baseLength, visitedCycles);
					paths.add(path);
					System.out.println(done);
					System.out.println(bestPath);
					maxLenghts.put(baseLength, maxLenghts.get(baseLength) - baseLength);
				}
			}
		}

	}

	private boolean extendPath(V u, V previous, int baseLength, List<List<Integer>> visitedCycles){

		System.out.println("current vertex: " + u);
		List<E> allEdges = graph.allEdges(u);
		if (previous != null){
			E returnEdge = graph.edgeBetween(u, previous);
			if (allEdges.contains(returnEdge)){
				allEdges.remove(returnEdge);
				allEdges.add(returnEdge);
			}

		}

		for (E e : allEdges){
			System.out.println("current edge: " + e);

			V v = e.getDestination() == u ? e.getOrigin() : e.getDestination();
			List<Integer> cycle = vertexCycle.get(v);
			System.out.println("linked cycle: " + cycle);
			Integer d = cycle.size(); //length of the cycle
			if (d == baseLength){
				if (!visitedCycles.contains(cycle)){
					System.out.println("adding "+ v);
					path.add(v);
					visitedCycles.add(cycle);
					System.out.println("calling for " + v);
					if (extendPath(v, u,baseLength, visitedCycles))
						return true;
					//else reset for next iteration
					visitedCycles.remove(cycle);
					System.out.println("removing " + cycle);
					path.remove(v);
				}
				else if (cycle == baseCycle){
					//back to the starting cycle
					System.out.println("back to base cycle");
					if (v == vertices.get(baseCycle.get(0))){
						System.out.println("starting vertex");
						//starting point on the first return to the base cycle
						//don't accept these cycles unless there is nothing longer
						if (path.size() < bestCycleSize)
							continue; //try next v
						if (path.size() == 2)
							continue; //ignore a cycle of length 2
						bestPath.clear();
						bestPath.addAll(path);
						bestGCD = d;
						bestCycleSize = bestPath.size();
					}
					int gcd = BigInteger.valueOf(d).gcd(BigInteger.valueOf(baseLength)).intValue();
					int lengthOfGrandCycle = path.size() * baseLength / gcd;
					if (lengthOfGrandCycle < bestCycleSize)
						continue; //too short - ignore
					if (lengthOfGrandCycle == bestCycleSize)
						if (gcd < bestGCD)
							continue;
					bestPath.clear();
					bestPath.addAll(path);
					bestGCD = gcd;
					bestCycleSize = lengthOfGrandCycle;
					if (lengthOfGrandCycle == maxLenghts.get(d))
						return true;
				}
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

	private Map<Integer, Integer> maxLenghts(List<List<Integer>> permutation){
		Map<Integer, Integer> ret = new HashMap<Integer, Integer>();

		for (List<Integer> cycle : permutation){
			Integer length = cycle.size();
			if (ret.containsKey(length)){
				Integer num = ret.get(length);
				ret.put(length, num++);
			}
			ret.put(length, 1);
		}
		for (Integer length : ret.keySet())
			ret.put(length, length * ret.get(length));

		return ret;

	}


}
