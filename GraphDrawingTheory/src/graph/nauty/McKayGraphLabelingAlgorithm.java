package graph.nauty;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

public class McKayGraphLabelingAlgorithm<V extends Vertex, E extends Edge<V>> {
	
	
	private OrderedPartition<V> patition;
	private Graph<V,E> graph;
	
	
	public McKayGraphLabelingAlgorithm(Graph<V,E> graph){
		this.graph = graph;
	}
	
	
	private void refinementProcedure(OrderedPartition<V> pi){
		OrderedPartition<V> tau = new OrderedPartition<V>(pi.getPartition());
		while (true){
			//find B, if it's empty - break
			//else find new vi, update tau
		}
		
	}

}
