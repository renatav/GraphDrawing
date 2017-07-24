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
	
	/**
	 * Constructs a cycle given a list of vertices it consists of
	 * @param cycle Cycle's vertices
	 */
	public PermutationCycle(List<V> cycle){
		this.cycle = cycle;
	}
	
	/**
	 * Constructs a permutation cycle given a list of vertices and indexes
	 * of those which should be a part of the cycle
	 * @param vertices A list of vertices
	 * @param indexes Indexes of vertices which should be included in the cycle
	 */
	public PermutationCycle (List<V> vertices, List<Integer> indexes){
		for (Integer index : indexes)
			cycle.add(vertices.get(index));
	}

	/**
	 * @return The cycle
	 */
	public List<V> getCycle() {
		return cycle;
	}

	/**
	 * @param cycle Cycle's vertices to set
	 */
	public void setCycle(List<V> cycle) {
		this.cycle = cycle;
	}
	
	/**
	 * @return Size (number of elements) of the cycle
	 */
	public int size(){
		return cycle.size();
	}

	@Override
	public String toString() {
		return cycle.toString();
	}

}
