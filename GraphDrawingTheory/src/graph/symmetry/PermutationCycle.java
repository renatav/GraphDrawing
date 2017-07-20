package graph.symmetry;

import graph.elements.Vertex;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents one permutation cycle
 * @author Renata
 * @param <V> The vertex type
 */
public class PermutationCycle<V extends Vertex> {
	
	/**
	 * A list of vertices that form a permutation cycle
	 */
	private List<V> cycle = new ArrayList<V>();
	
	public PermutationCycle(List<V> cycle){
		this.cycle = cycle;
	}
	
	public PermutationCycle (List<V> vertices, List<Integer> indexes){
		for (Integer index : indexes)
			cycle.add(vertices.get(index));
	}


	public List<V> getCycle() {
		return cycle;
	}


	public void setCycle(List<V> cycle) {
		this.cycle = cycle;
	}
	
	public int size(){
		return cycle.size();
	}


	@Override
	public String toString() {
		return cycle.toString();
	}

}
