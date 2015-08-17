package graph.symmetry;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Path;
import graph.elements.Vertex;
import graph.traversal.GraphTraversal;

import java.util.List;

public class MaxPAGTriconnected< V extends Vertex, E extends Edge<V>>{

	private Graph<V,E> graph;
	
	public MaxPAGTriconnected(Graph<V,E> graph){
		
		this.graph = graph;
		
		//first step - find face F of graph G so that the stabilizator stab_A(f) is maximized
		//optimally, use Fontet's algorithm
		//but I can't find its description anywhere :(
		//when Boyer-Myrvold is tested more, maybe use its results
		//at the moment, implementing this lema: 
		//Let G be a three-connected planar graph and let C be a cycle.
		//Let G/C be the graph formed by contracting C down to a point. 
		//Then C is a face of the planar graph if and only if G/C is two-connected.
		
		GraphTraversal<V, E> traversal = new GraphTraversal<V,E>(graph);
		List<Path<V,E>> cycles = traversal.findAllCycles();
		
	}
	
}
