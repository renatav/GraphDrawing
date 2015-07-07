package graph.algorithms.planarity;

import graph.algorithms.numbering.DFSNumbering;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

public class ShihShuPlanarity<V extends Vertex, E extends Edge<V>> extends PlanarityTestingAlgorithm<V,E>{

	@Override
	public boolean isPlannar(Graph<V, E> graph) {
		
		
		new DFSNumbering<V, E>(graph);
		
		
		return false;
	}

}
