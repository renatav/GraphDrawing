package graph.nauty;

import graph.elements.Vertex;

import java.util.ArrayList;
import java.util.List;

public class OrderedPartition<V extends Vertex> {
	
	private List<List<V>> partition;
	
	@SafeVarargs
	public OrderedPartition(List<V>...vertices){
		partition = new ArrayList<List<V>>();
		for (List<V> verticeList : vertices)
			partition.add(verticeList);
	}
	
	public OrderedPartition(List<List<V>> partition){
		this.partition = partition;
	}

	public List<List<V>> getPartition() {
		return partition;
	}

	public void setPartition(List<List<V>> partition) {
		this.partition = partition;
	}
	

}
