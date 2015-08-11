package graph.symmetry;

import graph.elements.Vertex;

import java.util.ArrayList;
import java.util.List;

public class PermutationCycle<V extends Vertex> {
	
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
