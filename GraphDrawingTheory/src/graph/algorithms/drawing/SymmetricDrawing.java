package graph.algorithms.drawing;

import graph.algorithms.planarity.BoyerMyrvoldPlanarity;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.symmetry.PermutationGroup;
import graph.symmetry.nauty.McKayGraphLabelingAlgorithm;

import java.util.List;

public class SymmetricDrawing<V extends Vertex, E extends Edge<V>> {

	private PermutationGroup permutationGroup;
	private Graph<V,E> graph;
	
	
	public SymmetricDrawing(PermutationGroup permutationGroup, Graph<V, E> graph) {
		this.permutationGroup = permutationGroup;
		this.graph = graph;
	}
	
	
	public void execute() throws NotPlanarException{
		
		BoyerMyrvoldPlanarity<V, E> planarity = new BoyerMyrvoldPlanarity<V,E>();
		if (!planarity.isPlannar(graph))
			throw new NotPlanarException();
		
		List<V> outsideFace = planarity.getOutsideFace();
		
		//which permutations fix the outside face
		
		McKayGraphLabelingAlgorithm<V, E> nauty = new McKayGraphLabelingAlgorithm<V,E>(graph);
		
		
		
	}
	
	
}
