package graph.nauty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	
	@Override
	public String toString() {
		String ret = "(";
		Set<Integer> keys = permutation.keySet(); 
		List<Integer> list = new ArrayList<Integer>(keys);
		Collections.sort(list);
		for (int i = 0; i < list.size(); i++){
			ret += list.get(list.get(i)) + "-" + permutation.get(list.get(i));
			if (i < list.size() - 1)
				ret += ", ";
		}
		ret += ")";
		
		return ret;
		
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
		
		for (Integer key : permutation.keySet()){
			if (!other.getPermutation().containsKey(key))
				return false;
			if (other.getPermutation().get(key) != permutation.get(key))
				return false;
		}
		return true;
	}


}
