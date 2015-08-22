package graph.symmetry;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.symmetry.nauty.McKayGraphLabelingAlgorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermutationAnalyzator<V extends Vertex, E extends Edge<V>> {
	
	private Graph<V,E> graph;
	private List<Permutation> permutations;

	public PermutationAnalyzator(Graph<V, E> graph) {
		super();
		this.graph = graph;
		McKayGraphLabelingAlgorithm<V, E> nauty = new McKayGraphLabelingAlgorithm<V,E>(graph);
		permutations = nauty.findAutomorphisms();
	}
	
	
	public List<PermutationGroup> findReflectionGroups(){
		
		List<PermutationGroup> ret = new ArrayList<PermutationGroup>();
		for (Permutation p : permutations)
			if (isReflectionGroupGenerator(p))
				ret.add(new PermutationGroup(p, null));
		
		return ret;
		
	}
	
	public boolean isReflectionGroupGenerator(Permutation p){
		return p.order() == 2;
	}
	
	public List<PermutationGroup> findRotationGroups(){
		List<PermutationGroup> ret = new ArrayList<PermutationGroup>();
		for (Permutation p : permutations){
			PermutationGroup group = isRotationalGroupGenerator(p);
			if (group != null)
				ret.add(group);
		}
		
		return ret;
	}
	
	
	public PermutationGroup isRotationalGroupGenerator(Permutation p){
		List<Integer> fixedPoints = p.fix();
		//rotational permutation has one fixed point at most
		if (fixedPoints.size() > 1)
			return null;
		
		PermutationGroup group = new PermutationGroup(null, p);
		if (group.size() <= 1)
			return null;
		
		if (fixedPoints.size() == 1){
			Integer fixed = fixedPoints.get(0);
			Map<Integer, Integer> pvMap = new HashMap<Integer, Integer>(p.getPermutation());
			pvMap.remove(fixed);
			PermutationGroup test = new PermutationGroup(null, new Permutation(pvMap));
			if (test.isSemiRegular())
				return group;
			else
				return null;
		}
		
		if (group.isSemiRegular())
			return group;
		return null;
	}
	

}
