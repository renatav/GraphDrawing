package graph.algorithms.lrpartition;

import graph.elements.Edge;
import graph.elements.Vertex;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * Used to implement LR planar testing algorithms
 * Saves lists of edges which should be in the same and in different partitions
 * than the given edge
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type
 */
public class LRPartitionEdge <V extends Vertex, E extends Edge<V>>{
	
	/**
	 * Edge for which the division is calculated
	 */
	private E edge;
	/**
	 * A list of edges that should be in the same partition as the given edge
	 */
	private List<E> same;
	/**
	A list of edges that should be in a different partition than the given edge
	*/
	private List<E> different;
	/**
	 * Detects contradiction - if an edge is in both lists
	 */
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
