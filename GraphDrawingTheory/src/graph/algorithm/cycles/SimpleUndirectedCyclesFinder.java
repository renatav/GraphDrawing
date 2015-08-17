package graph.algorithm.cycles;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

import java.util.ArrayList;
import java.util.List;

public class SimpleUndirectedCyclesFinder<V extends Vertex, E extends Edge<V>> {
	
	
	private Graph<V,E> graph;
	
	public SimpleUndirectedCyclesFinder(Graph <V,E> graph){
		this.graph = graph;	
	}
	
	public List<List<V>> findAllCycles(){
		
		List<List<V>> baseCycles = new PatonSimpleCycles<V,E>(graph).findSimpleCycles();
		List<List<V>> ret = new ArrayList<List<V>>();
		
		
			
		return ret;
		
	}

}
