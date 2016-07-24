package graph.properties;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.properties.splitting.BiconnectedSplitting;
import graph.traversal.DFSTreeTraversal;
import graph.traversal.DijkstraAlgorithm;
import graph.trees.DFSTree;

import java.util.ArrayList;
import java.util.List;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

public class GraphProperties<V extends Vertex,E extends Edge<V>>{

	private Graph<V,E> graph;

	public GraphProperties(Graph<V,E> graph){
		this.graph = graph;
	}

	
	/**
	 * Checks is graph is connected
	 * @return
	 */
	public boolean isConnected(){
		
		int size = graph.getVertices().size();
		Boolean[] visited = new Boolean[size];

		dfs(graph, graph.getVertices().get(0), 0, visited,  new Integer[size], new Integer[size], 
				new Integer[size], new Integer[size], new Boolean[size]);
		
		for (Boolean v : visited)
			if (v == null)
				return false;
		return true;
		
	}

	

	public List<V> getCutVertices(){
		List<V> ret = new ArrayList<V>();
		int size = graph.getVertices().size();
		Boolean[] visited = new Boolean[size];
		Integer[] depth = new Integer[size];
		Integer[] low = new Integer[size];
		Integer[] parent = new Integer[size];
		Integer[] childCount = new Integer[size];
		Boolean[] isArticulation = new Boolean[size];

		dfs(graph, graph.getVertices().get(0), 0, visited, depth, low, parent, childCount, isArticulation);
		
		for (int i = 0; i < size; i++)
			if ((parent[i] != null && isArticulation[i]) || (parent[i] == null && childCount[i] > 1))
				ret.add(graph.getVertices().get(i));
		
		
		return ret;
	}

	public boolean isBiconnected(){
		return getCutVertices().size() == 0;
	}

	
	private void dfs(Graph<V,E> graph, V current, int d,
			Boolean[] visited, Integer[] depth, Integer[] low, Integer[] parent, 
			Integer[] childCount, Boolean[] isArticulation){

		int i = graph.getVertices().indexOf(current);

		visited[i] = true;
		depth[i] = d;
		low[i] = d;
		childCount[i] = 0;
		isArticulation[i] = false;

		for (V adjacent : graph.adjacentVertices(current)){
			int ni = graph.getVertices().indexOf(adjacent);
			if (visited[ni] == null){
				parent[ni] = i;
				dfs(graph, adjacent, d + 1, visited, depth, low, parent, childCount, isArticulation);
				childCount[i] ++;
				if (low[ni] >= depth[i])
					isArticulation[i] = true;
				low[i] = Math.min(low[i], low[ni]);
			}
			else if (parent[i] == null || ni != parent[i])
				low[i] = Math.min(low[i], depth[ni]);
		}

	}
	
	public List<Double> getEigenValues(){
		int[][] adjacencyMatrix = graph.adjacencyMatrix();
		double[][] values = new double[graph.getVertices().size()][graph.getVertices().size()];
		for (int i = 0; i <adjacencyMatrix.length; i++)
			for (int j = 0; j <adjacencyMatrix.length; j++)
				values[i][j] = (double)adjacencyMatrix[i][j];

		Matrix m = new Matrix(values);
		EigenvalueDecomposition decomposition= m.eig();
		List<Double> ret = new ArrayList<Double>();
		for (Double d : decomposition.getRealEigenvalues())
			ret.add(d);

		return ret;

	}
	
	/**
	 * Checks is graph is connected
	 * @return
	 */
	public boolean isConnected(List<V> excluding){
		DijkstraAlgorithm<V, E> dijkstra = new DijkstraAlgorithm<V,E>(graph);
		for (V v1 : graph.getVertices()){
			if (excluding.contains(v1))
				continue;
			for (V v2 : graph.getVertices()){
				if (v1 == v2)
					continue;
				if (excluding.contains(v2))
					continue;
				if (dijkstra.getPath(v1, v2, excluding) == null) 
					return false;
			}
		}
		return true;
	}

	public boolean isCyclic(){
		//TODO umesto pravljenja celog stabla, prekinuti kada se naidje na back ivivu
		//proveriti da li je ok i za directed i za undirected 
		DFSTreeTraversal<V, E> traversal = new  DFSTreeTraversal<V,E>(graph);
		DFSTree<V, E> tree = traversal.formDFSTree(graph.getVertices().get(0));
		return tree.getBackEdges().size() > 0;
	}
	
	/**
	 * Checks if the graph is a tree
	 * @return true, if it is a tree, false otherwise
	 */
	public boolean isTree(){
		return !isCyclic() && isConnected();
	}
	
	/**
	 * Lists all tree leaves (presumes that the graph is a tree)
	 * @return List of leaves
	 */
	public List<V> treeLeaves(V root){
		List<V> ret = new ArrayList<V>();
		for (V v : graph.getVertices()){
			if (v != root && graph.getAdjacentLists().get(v).size() == 1)
				ret.add(v);
		}
		return ret;
	}
	

	
	public List<List<E>> listMultiEdges(){
		
		List<List<E>> ret = new ArrayList<List<E>>();
		
		for (int i = 0; i < graph.getVertices().size(); i++)
			for (int j = i; j < graph.getVertices().size(); j++){
				List<E> edgesBetween = graph.edgeesBetween(graph.getVertices().get(i), graph.getVertices().get(j));
				if (edgesBetween.size() > 1)
					ret.add(edgesBetween);
			}
		
		
		return ret;
	}
	
	public List<Graph<V, E>> listBiconnectedComponents(){
		BiconnectedSplitting<V,E> biconnected = new BiconnectedSplitting<V,E>(graph);
		return biconnected.findBiconnectedComponents();
	}
	
	public boolean isRing(){
		//a graph is a ring if it is basically one cycle
		
		//rings have as many vertices as edges
		if (graph.getVertices().size() != graph.getEdges().size())
			return false;
		
		List<E> traversedEdges = new ArrayList<E>();
		List<V> traversedVertices = new ArrayList<V>();
		
		E currentEdge = graph.getEdges().get(0);
		
		while (traversedEdges.size() < graph.getEdges().size()){
			V next = currentEdge.getDestination();
			if (traversedVertices.contains(next))
				next = currentEdge.getOrigin();
			
			traversedVertices.add(next);
			traversedEdges.add(currentEdge);
			
			List<E> adjacent = graph.adjacentEdges(next);
			if (adjacent.size() != 2)
				return false;
			
			if (adjacent.get(0) == currentEdge)
				currentEdge = adjacent.get(1);
			else
				currentEdge = adjacent.get(0);
				
		}
		
		if (traversedVertices.size() == graph.getVertices().size())
			return true;
		
		return false;
	}
	
}