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
		for (Permutation p : permutations){
			PermutationGroup group = isReflectionGroupGenerator(p);
			if (group != null)
				ret.add(group);
		}

		return ret;

	}

	public PermutationGroup isReflectionGroupGenerator(Permutation p){
		if (p.order() == 2)
			return new PermutationGroup(p, null);
		return null;
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


	public List<PermutationGroup> findDihedralGroups(){

		List<PermutationGroup> ret = new ArrayList<PermutationGroup>();
		for (Permutation p1 : permutations)
			for (Permutation p2 : permutations){
				PermutationGroup group = isDehidralGroupGenerator(p1, p2);
				if (group != null)
					ret.add(group);
			}

		return ret;

	}

	public PermutationGroup isDehidralGroupGenerator(Permutation p1, Permutation p2){
		Permutation rotation, axial;


		//one of the permutations must be a rotational permutation
		//the other one must be axial, of order 2
		//<p1> intersection <p2> must be trivial
		//axial * rotational = rotational ^ -1 * axial

		if (p1.order() == 2){
			axial = p1;
			rotation = p2;
		}
		else if (p2.order() == 2){
			axial = p2;
			rotation = p1;
		}
		else
			return null;

		if (isRotationalGroupGenerator(rotation) == null)
			return null;

		Permutation prod1 = axial.mul(rotation);
		Permutation prod2 = (rotation.inverse()).mul(axial);
		if (!prod1.equals(prod2))
			return null;

		PermutationGroup g1 = new PermutationGroup(null, rotation);
		PermutationGroup g2 = new PermutationGroup(axial, null);

		for (Permutation p : g1.getPermutations())
			if (!p.isIdentityPermutation() && g2.getPermutations().contains(p))
				return null;

		return new PermutationGroup(axial, rotation);

	}




}
