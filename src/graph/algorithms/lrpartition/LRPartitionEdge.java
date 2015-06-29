package graph.algorithms.lrpartition;

import graph.elements.Edge;
import graph.elements.Vertex;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;



public class LRPartitionEdge <V extends Vertex, E extends Edge<V>>{
	
	private E edge;
	private List<E> same;
	private List<E> different;
	private boolean valid;
	
	
	public LRPartitionEdge(E edge){
		same = new ArrayList<E>();
		different = new ArrayList<E>();
		this.edge = edge;
		valid = true;
	}
	
	
	public boolean addToSame(Set<E> toBeAdded){
		for (E e : toBeAdded){
			if (e == edge)
				continue;
			if (different.contains(e)){
				//System.out.println("Ne moze same " + toBeAdded);
				valid = false;
				return false;
			}
			same.add(e);
		}
		return true;
	}
	
	
	public boolean addToDifferent(Set<E> toBeAdded){
		for (E e : toBeAdded){
			if (e == edge)
				continue;
			if (same.contains(e)){
				//System.out.println("Ne moze diff " + toBeAdded);
				valid = false;
				return false;
			}
			different.add(e);
		}
		return true;
	}


	public E getEdge() {
		return edge;
	}


	public void setEdge(E edge) {
		this.edge = edge;
	}


	public List<E> getSame() {
		return same;
	}


	public void setSame(List<E> same) {
		this.same = same;
	}


	public List<E> getDifferent() {
		return different;
	}


	public void setDifferent(List<E> different) {
		this.different = different;
	}


	public boolean isValid() {
		return valid;
	}


	public void setValid(boolean valid) {
		this.valid = valid;
	}
	
	

}
