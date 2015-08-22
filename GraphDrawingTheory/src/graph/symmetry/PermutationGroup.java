package graph.symmetry;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PermutationGroup {

	private List<Permutation> permutations;
	private Permutation reflection, rotation;
	
	public PermutationGroup(Permutation reflection, Permutation rotation){
		permutations = new ArrayList<Permutation>();
		this.reflection = reflection;
		this.rotation = rotation;
		initPermutations();
	}
	
	private void initPermutations(){
		if (reflection != null && rotation == null){
			permutations.add(reflection);
			permutations.add(reflection.mul(reflection));
		}
		if (reflection == null && rotation != null){
			Permutation mul = new Permutation(rotation.getPermutation());
			while (!mul.isIdentityPermutation()){
				permutations.add(mul);
				mul = mul.mul(rotation);
			}
			permutations.add(mul);
		}
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
	
	/**the orbit of u under A is
	 *orbitA(u) = {β(u) | β ∈ A} */
   	public List<Integer> orbit(Integer u){
   		
   		List<Integer> ret = new ArrayList<Integer>();
   		for (Permutation perm : permutations)
   			ret.add(perm.getPermutation().get(u));
   		return ret;
	}
   	
   	/**
   	 * Group is semiregular if no permutation which is not an identity
   	 * has fixed points
   	 * @return
   	 */
   	public boolean isSemiRegular(){
   		for (Permutation p : permutations)
   			if (!p.isIdentityPermutation())
   				if (p.fix().size() > 0)
   					return false;
   		return true;
   	}
   	
   	public int size(){
   		return permutations.size();
   	}
}
