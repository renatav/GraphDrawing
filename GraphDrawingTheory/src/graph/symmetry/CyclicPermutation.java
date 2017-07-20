package graph.symmetry;

import graph.elements.Vertex;

import java.util.ArrayList;
import java.util.List;

/**
 * Class represents a permutation in the cyclic representation
 * @author Renata
 * @param <V> The vertex type
 */
public class CyclicPermutation<V extends Vertex> {
	
	/**
	 * Cycles forming the permutation
	 */
	private List<PermutationCycle<V>> cycles;	
	
	public CyclicPermutation(List<V> vertices, Permutation permutation){
		
		cycles = new ArrayList<PermutationCycle<V>>();
		
		List<List<Integer>> cyclicPermutation = permutation.cyclicRepresenatation();
		
		for (List<Integer> indexes : cyclicPermutation){
			cycles.add(new PermutationCycle<V>(vertices, indexes));
		}
	}

	public List<PermutationCycle<V>> getCycles() {
		return cycles;
	}


	public void setCycles(List<PermutationCycle<V>> cycles) {
		this.cycles = cycles;
	}


	@Override
	public String toString() {
		return "CyclicPermutation [cycles=" + cycles + "]";
	}
}
