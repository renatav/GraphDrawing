package graph.symmetry.nauty;

import graph.elements.Vertex;

import java.util.ArrayList;
import java.util.List;

/**
 * Ordered partition, used in graph labeling algorithm
 * @author Renata
 * @param <V> The vertex type
 */
public class OrderedPartition<V extends Vertex> {
	
	/**
	 * The ordered partition, composed of a list of parts, which are lists of vertices
	 */
	private List<List<V>> partition;
	
	
	public OrderedPartition() {
		partition = new ArrayList<List<V>>();
	}
	
	@SafeVarargs
	public OrderedPartition(List<V>...vertices){
		partition = new ArrayList<List<V>>();
		for (List<V> verticeList : vertices)
			partition.add(verticeList);
	}
	
	public OrderedPartition(List<List<V>> partition){
		this.partition = new ArrayList<List<V>>();
		this.partition.addAll(partition);
	}
	
	/**
	 * Replaces one part of the partition with the given list of components
	 * @param Vi To be replaced
	 * @param replacements Replacements of {@code Vi}
	 */
	public void replace(List<V> Vi, List<List<V>> replacements){
		int i = partition.indexOf(Vi);
		partition.remove(i);
		for (int j = replacements.size() - 1; j >=0; j--)
			partition.add(i,replacements.get(j));
		
	}
	
	/**
	 * @param u Vertex
	 * @return A part which contains {@code u}
	 */
	public List<V> partContainingVertex(V u){
		for (List<V> part : partition)
			if (part.contains(u))
				return part;
		return null;
	}
	
	/**
	 * @return The first nontrivial part of the ordered partition
	 */
	public List<V> getFirstNontrivialPart(){
		for (List<V> part : partition)
			if (part.size() > 1)
				return part;
		return null;
	}
	
	public List<V> getVerticesInOrder(){
		List<V> ret = new ArrayList<V>();
		for (List<V> part : partition)
			ret.addAll(part);
		return ret;
	}
	

	public List<List<V>> getPartition() {
		return partition;
	}

	public void setPartition(List<List<V>> partition) {
		this.partition = partition;
	}
	
	public void addPart(List<V> part){
		partition.add(part);
	}

	@Override
	public String toString() {
		String ret = "";
		for (List<V> list : partition){
			ret += "( ";
			for (V v : list)
				ret += (v + " ");
			ret += " ) ";
		}
		return ret;
	}
	
	
	
	

}
