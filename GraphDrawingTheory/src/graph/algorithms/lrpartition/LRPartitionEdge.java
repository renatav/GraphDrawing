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
	
	/**
	 * Mark the given edge as a LR partition edge and initializes
	 * lists of the edges which should be in the same and in different
	 * partition as it
	 * @param edge Edge
	 */
	public LRPartitionEdge(E edge){
		same = new ArrayList<E>();
		different = new ArrayList<E>();
		this.edge = edge;
		valid = true;
	}
	
	
	/**
	 * Marks the given edges as those that should be in the same partition
	 * as the current edge
	 * @param toBeAdded Edges that should be in the same partition as the current edges  
	 * @return {@code true} if there was no contradiction, {@code false} otherwise 
	 */
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
	
	/**
	 * Marks the given edges as those that should be in the different partition
	 * than the current edge
	 * @param toBeAdded Edges that should be in the different partition than the current edge's  
	 * @return {@code true} if there was no contradiction, {@code false} otherwise 
	 */
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

	/**
	 * @return Edge
	 */
	public E getEdge() {
		return edge;
	}

	/**
	 * @param edge Edge to set
	 */
	public void setEdge(E edge) {
		this.edge = edge;
	}

	/**
	 * @return Edges that should be in the same partition as this one
	 */
	public List<E> getSame() {
		return same;
	}

	/**
	 * @param same Edges that should be in the same partition as this one
	 */
	public void setSame(List<E> same) {
		this.same = same;
	}

	/**
	 * @return Edges that should be in the different partition than this one's
	 */
	public List<E> getDifferent() {
		return different;
	}

	/**
	 * @param different Edges that should be in the different partition than this one's
	 */
	public void setDifferent(List<E> different) {
		this.different = different;
	}

	/**
	 * @return Indicator if there was a contradiction
	 */
	public boolean isValid() {
		return valid;
	}


	/**
	 * @param valid Valid value to set
	 */
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	
	

}
