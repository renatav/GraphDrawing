package graph.algorithm.cycles;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

import java.util.List;

public class SimpleCyclesFinder<V extends Vertex, E extends Edge<V>> {
	
	public SimpleCyclesFinder(){
		
	}
	
	public List<List<V>> findCycles(Graph<V,E> graph){
		
		if (graph.isDirected())
			return new JohnsonSimpleCycles<V,E>(graph).findSimpleCycles();
		else
			return new PatonSimpleCycles<V,E>(graph).findSimpleCycles();
		
	}

}
