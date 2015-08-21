package graph.symmetry;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PermutationGroup {

	private List<Permutation> permutations;
	
	public PermutationGroup(){
		permutations = new ArrayList<Permutation>();
	}
	
	
	/**
	 * The stabilizer of u ∈ V , denoted by stabA(u),
	 * is the set of automorphisms in A that fix u, that is,
	 * stabA(u) = {β ∈ A | β(u) = u}. (3.1)
	 * @param u
	 * @return
	 */
	public List<Permutation> stabilizers(Integer u){
		List<Permutation> ret = new ArrayList<Permutation>();
		
		for (Permutation perm : permutations){
			if (perm.getPermutation().get(u) == u)
				ret.add(perm);
		}
		
		return ret;
		
	}
	
	/**
	 * f Y subset of V , then
	 * stabA(Y ) = {β ∈ A | ∀y ∈ Y, β(y) ∈ Y }.
	 * @param Y
	 * @return
	 */
	public List<Permutation> subsetStabilizers(List<Integer> Y){
		
		List<Permutation> ret = new ArrayList<Permutation>();
		
		for (Permutation perm : permutations){
			boolean stabilizer = true;
			for (Integer y : Y){
			  if (!Y.contains(perm.getPermutation().get(y))){
				  stabilizer = false;
				  break;
			  }
			  if (stabilizer)
				  ret.add(perm);
				
			}
		}
		return ret;
	}
	
	/**For each automorphism β we denote {u ∈ V | β(u) = u} by fixβ
	 * */
	public List<Integer> fixOnePermutation(Permutation beta){
		List<Integer> ret = new ArrayList<Integer>();
		
		for (Integer key : beta.getPermutation().keySet())
			if (key == beta.getPermutation().get(key))
				ret.add(key);
		
		return ret;
	}
	
	/**
	 * The set of vertices
	 * that are fixed elementwise by every element of A is denoted by fixA, that is,
     * fixA = {v ∈ V | ∀ β ∈ A, β(v) = v }. (3.3
	 * @return
	 */
	public List<Integer> fix(){
		List<Integer> ret = new ArrayList<Integer>();
		
		Set<Integer> elements = permutations.get(0).getPermutation().keySet();
		
		for (Integer el : elements){
			boolean ok = true;
			for (Permutation perm : permutations){
				if (perm.getPermutation().get(el) != el){
					ok = false;
					break;
				}
			}
			if (ok)
				ret.add(el);
		}
		return ret;
	}
}
