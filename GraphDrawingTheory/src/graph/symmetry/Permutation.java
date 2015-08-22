package graph.symmetry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Permutation {
	

	private Map<Integer, Integer> permutation;
	
	
	public Permutation(){
		permutation = new HashMap<Integer, Integer>();
	}
	
	public Permutation(Map<Integer, Integer> permutation){
		this.permutation = permutation;
	}
	
	public Permutation inverse(){
	
		//basically, switch keys and values
		Map<Integer, Integer> inversePermutation = new HashMap<Integer, Integer>();
		
		for (Integer key : permutation.keySet())
			inversePermutation.put(permutation.get(key), key);
		
		return new Permutation(inversePermutation);
		
	}
	
	public Permutation mul(Permutation other){
		
		Map<Integer, Integer> product = new HashMap<Integer, Integer>();
		
		for (Integer key : permutation.keySet()){
			Integer composite = other.getPermutation().get(permutation.get(key));
			product.put(key, composite);
		}
		return new Permutation(product);
		
	}
	
	public int order(){
		int order = 1;
		Permutation result = new Permutation(new HashMap<Integer,Integer>(this.getPermutation()));
		
		while (!result.isIdentityPermutation()){
			result = result.mul(this);
			order ++;
		}
		return order;
		
	
	}
	
	public boolean isIdentityPermutation(){
		for (Integer key : permutation.keySet())
			if (permutation.get(key) != key)
				return false;
		return true;
	}
	
	public List<List<Integer>> cyclicRepresenatation(){
		List<List<Integer>> ret = new ArrayList<List<Integer>>();
		List<Integer> covered = new ArrayList<Integer>();
		for (Integer key : permutation.keySet()){
			if (covered.contains(key))
				continue;
			
			//start a cycle and add all
			List<Integer> cycle = new ArrayList<Integer>();
			cycle.add(key);
			Integer next = permutation.get(key);
			while (next != key){
				cycle.add(next);
				covered.add(next);
				next = permutation.get(next);
			}
			ret.add(cycle);
		}
		return ret;
	}
	
	/**For each automorphism β we denote {u ∈ V | β(u) = u} by fixβ*/
	public List<Integer> fix(){
		List<Integer> ret = new ArrayList<Integer>();
		
		for (Integer key : permutation.keySet())
			if (key ==permutation.get(key))
				ret.add(key);
		
		return ret;
	}
	
	/**
	 * If β ∈ A and u ∈ V , then the orbit of u under β, denoted by orbitβ(u), is the set of
	 * images of u under <β>, that is, orbit β(u) = {βi(u) | 0 ≤ i < k}
	 * @param u
	 * @return
	 */
	public List<Integer> orbit(Integer u){
		List<Integer> ret = new ArrayList<Integer>();
		int k = order();
		Permutation degree = new Permutation(new HashMap<Integer, Integer>(permutation));
		for (int i = 0; i < k; i++){
			if (i == 0)
				ret.add(u);
			else if (i == 1)
				ret.add(permutation.get(u));
			else{
				degree = degree.mul(this);
				ret.add(degree.getPermutation().get(u));
			}
		}
		return ret;
	}
	
	
	public Map<Integer, Integer> getPermutation() {
		return permutation;
	}

	public void setPermutation(Map<Integer, Integer> permutation) {
		this.permutation = permutation;
	}
	
	@Override
	public String toString() {
		return cyclicRepresenatation() + "";
		
	}
	
	
	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Permutation other = (Permutation) obj;
		
		if (other.getPermutation().size() != permutation.size())
			return false;
		
		for (Integer key : permutation.keySet()){
			if (!other.getPermutation().containsKey(key))
				return false;
			if (other.getPermutation().get(key) != permutation.get(key))
				return false;
		}
		return true;
	}


}
