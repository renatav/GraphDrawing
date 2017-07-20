package graph.math;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class for calculating Czekanovski-Dice distances
 * With respect to Czekanovski-Dice distance,
 * two vertices are close to each other if they
 * have many common vertices
 * @author Renata
 * */

public class CzekanovskiDiceDistance<V extends Vertex, E extends Edge<V>> {
	
	/**
	 * Graph between whose vertices the distances are being calculated
	 */
	private Graph<V,E> graph;
	/**
	 * Czekanovski-Dice distances
	 */
	private Map<V, Map<V,Double>> distances;
	
	public CzekanovskiDiceDistance(Graph<V, E> graph) {
		super();
		this.graph = graph;
		distances = calculateVerticeDistances();
	}
	
	/**
	 * Calculates Czekanovski-Dice distances between all vertices of a graph
	 * @return A mapping of each vertex to a map of distances between itself
	 * and all other vertices of the graph
	 */
	public Map<V, Map<V,Double>> calculateVerticeDistances(){
		
		Map<V, Map<V,Double>> ret = new HashMap<V, Map<V,Double>>();
		
		for (V vi : graph.getVertices()){
			Map<V,Double> neighborDistance = new HashMap<V, Double>();
			for (V vj : graph.getVertices()){
				if (vi == vj)
					neighborDistance.put(vi, (double)0);
				
				//if (ret.get(vj).containsKey(vi))
					//continue;
				
				Double distance = calculateDistance(vi, vj);
				neighborDistance.put(vj, distance);
				
			}
			ret.put(vi, neighborDistance);
		}
		
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
	
	public double getDistance(V vi, V vj){
		return distances.get(vi).get(vj);
	}

	
	

}
