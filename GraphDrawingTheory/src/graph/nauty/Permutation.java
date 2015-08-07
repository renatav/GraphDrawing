package graph.nauty;

import java.util.HashMap;
import java.util.Map;

public class Permutation {
	
	private Map<Integer, Integer> permutation;
	
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

	public Map<Integer, Integer> getPermutation() {
		return permutation;
	}

	public void setPermutation(Map<Integer, Integer> permutation) {
		this.permutation = permutation;
	}

}
