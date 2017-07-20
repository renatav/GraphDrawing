package graph.symmetry;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Represent a permutation group and contains methods for its analysis
 * @author Renata
 */
public class PermutationGroup {

	/**
	 * Permutations forming the group
	 */
	private List<Permutation> permutations;
	
	/**
	 * Reflection and rotation permutations
	 */
	private Permutation reflection, rotation;
	
	public PermutationGroup(Permutation reflection, Permutation rotation){
		permutations = new ArrayList<Permutation>();
		this.reflection = reflection;
		this.rotation = rotation;
		initPermutations();
	}
	
	private void initPermutations(){
		if (reflection != null){
			permutations.add(reflection);
			permutations.add(reflection.mul(reflection));
		}
		if (rotation != null){
			Permutation mul = new Permutation(rotation.getPermutation());
			while (!mul.isIdentityPermutation()){
				permutations.add(mul);
				mul = mul.mul(rotation);
			}
			permutations.add(mul);
		}
		if (reflection != null && rotation != null) {
			Permutation prod = rotation.mul(reflection);
			Permutation mul = new Permutation(prod.getPermutation());
			while (!mul.isIdentityPermutation()){
				permutations.add(mul);
				mul = mul.mul(prod);
			}
		}
	}
	
	/**
	 * @param u Vertex
	 * @return A list of all stabilizers with respect to some given {@code u}
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
	 * @param Y Subset
	 * @return A list of stabilizers with respect to a subset {@code Y}
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
	 * @return A list of vertices fixed by the permutations
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
	
	/**
	 * @param u Vertex
	 * @return An orbit (list of vertices) of the group with respect to u
	 */
   	public List<Integer> orbit(Integer u){
   		
   		List<Integer> ret = new ArrayList<Integer>();
   		for (Permutation perm : permutations)
   			ret.add(perm.getPermutation().get(u));
   		return ret;
	}
   	
   	/**
   	 * Checks if the group is semi-regular
   	 * Group is semi-regular if no permutation which is not an identity
   	 * has fixed points
   	 * @return {@code true} if the group is semi-regular {@code false} otherwise
   	 */
   	public boolean isSemiRegular(){
   		for (Permutation p : permutations)
   			if (!p.isIdentityPermutation())
   				if (p.fix().size() > 0)
   					return false;
   		return true;
   	}
   	
   	/**
   	 * @return Size of the permutation group
   	 */
   	public int size(){
   		return permutations.size();
   	}
   	
	public List<Permutation> getPermutations() {
		return permutations;
	}

	public void setPermutations(List<Permutation> permutations) {
		this.permutations = permutations;
	}
	
	@Override
	public String toString() {
		String ret = "";
		if (reflection != null && rotation == null)
			ret += "Reflection ";
		else if (reflection == null && rotation != null)
			ret += "Rotation ";
		else
			ret += "Dihedral ";
		
		ret += permutations;
		
		return ret;
	}

}
