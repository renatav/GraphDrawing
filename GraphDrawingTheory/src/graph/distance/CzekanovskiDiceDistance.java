package graph.distance;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** With respect to Czekanovski-Dice distance,
 * two vertices are close to each other if they
 * have many common vertices*/

public class CzekanovskiDiceDistance<V extends Vertex, E extends Edge<V>> {
	
	private Graph<V,E> graph;
	
	public CzekanovskiDiceDistance(Graph<V, E> graph) {
		super();
		this.graph = graph;
	}
	

	public Map<V, Map<V,Double>> calculateVerticeDistances(){
		
		Map<V, Map<V,Double>> ret = new HashMap<V, Map<V,Double>>();
		
		for (V vi : graph.getVertices()){
			Map<V,Double> neighborDistance = new HashMap<V, Double>();
			for (V vj : graph.getVertices()){
				if (vi == vj)
					continue;
				
				//if (ret.get(vj).containsKey(vi))
					//continue;
				
				Double distance = calculateDistance(vi, vj);
				neighborDistance.put(vj, distance);
				
			}
			ret.put(vi, neighborDistance);
		}
		
		System.out.println(ret);
		return ret;
		
	}
	
	private double calculateDistance(V vi, V vj){
		
		List<V> Ni = graph.adjacentVertices(vi);
		Ni.add(vi);
		List<V> Nj = graph.adjacentVertices(vj);
		Nj.add(vj);
		
		List<V> difference =symmetricalDifference(Ni, Nj);
		
		double d = (double) difference.size() / (double)(Ni.size() + Nj.size());
		
		return d;
		
	}
	
	private List<V> symmetricalDifference(List<V> A, List<V> B){
		
		List<V> ret = new ArrayList<V>();
		
		for (V a : A)
			if (!B.contains(a))
				ret.add(a);
		for (V b : B)
			if (!A.contains(b))
				ret.add(b);
		
		return ret;
		
	}

	
	

}
