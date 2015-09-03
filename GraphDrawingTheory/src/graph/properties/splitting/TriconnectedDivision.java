package graph.properties.splitting;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.properties.components.BiconnectedComponent;
import graph.properties.components.HopcroftSplitComponent;
import graph.properties.components.SplitComponentType;
import graph.util.Pair;
import graph.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Implementation of Hopcroft's and Tarjan's algorithm which divides a graph
 * into triconnected components and finds separation pairs
 * @author xx
 *
 */

public class TriconnectedDivision<V extends Vertex, E extends Edge<V>> {

	
	private int[] nd;
	private int[] lowpt1;
	private int[] lowpt2;
	private int[] number;
	private int[] father;
	private boolean[] flag;
 	private List<V> vertices;
	private int n;
	
	private void triconnected(Graph<V,E> graph){
		
		List<HopcroftSplitComponent> splitComponents = new ArrayList<HopcroftSplitComponent>();
		
		//remove all multiedges
		//and create triple bonds
		
		Graph<V,E> gPrim= graph;
		
		List<List<E>> multiedges = graph.listMultiEdges();
		if (multiedges.size() > 0){
			
			gPrim = Util.copyGraph(graph);
			for (List<E> multi : multiedges){
				
				HopcroftSplitComponent<V, E> tripleBond = new HopcroftSplitComponent<V,E>(SplitComponentType.TRIPLE_BOND, multi, null);
				splitComponents.add(tripleBond);
				
				//remove all but one edge (which represents all three)
				for (int i = 1; i < multi.size(); i++){
					gPrim.removeEdge(multi.get(i));
				}
			}
			
		}
		
		//find biconnected components of G'
		List<BiconnectedComponent<V, E>> biconnectedComponents = gPrim.listBiconnectedComponents();
		
		
		
		
		
		
	}
	
	public List<Pair<V,V>> findSeaparationPairs(Graph<V,E> graph){
		List<Pair<V,V>> separationPairs = new ArrayList<Pair<V,V>>();
		
		//step one: perform a depth-first search on the multigraph converting in
		//into a palm tree
		int size = graph.getVertices().size();
		lowpt1 = new int[size];
		lowpt2 = new int[size];
		nd = new int[size];
		father = new int[size];
		
		
	
		
		number = new int[size];
		flag = new boolean[size];
		for (int i = 0; i < size; i++){
			flag[i] = true;
			//number is initially all zeros
		}
		List<E> treeEdges = new ArrayList<E>();
		List<E> fonds = new ArrayList<E>();
		
		Map<V, List<E>> adjacency = new HashMap<V, List<E>>();
		for (V v : graph.getVertices()){
			adjacency.put(v, new ArrayList<E>(graph.adjacentEdges(v)));
		}
		
		
		
		//the search starts at vertex s
		V s = graph.getVertices().get(0);
		dfs(s,null, adjacency, treeEdges, fonds);
	
		
		return separationPairs;
		
	}
	
	/**
	 * Routine for depth-first search of a multigraph represented by adjacency list
	 * A(v). Variable n denote the last number assigned to a vertex
	 *  U is the father of vertex v in the spanning tree being constructed
	 *  The graph to be searched is represented by adjacency lists A(v)
	 *  This will be implemented with a map vertex - list of edges 
	 */
	private void dfs(V v, V u, Map<V,List<E>> adjacency, List<E> treeEdges, List<E> fronds){
		int vIndex = vertices.indexOf(v);
		int uIndex = vertices.indexOf(u);
		
		//n:= number(v):=n+1
		n++;
		number[vIndex] = n;
		
		//a:
		lowpt1[vIndex] = number[vIndex];
		lowpt2[vIndex] = number[vIndex];
		nd[vIndex] = 1;
		
		for (E e : adjacency.get(v)){
			V w = e.getOrigin() == v ? e.getDestination() : e.getOrigin();
			int wIndex = vertices.indexOf(w);
			if (number[wIndex] == 0){
				//w in as new vertex
				treeEdges.add(e); //(v,w) is a tree ar
				dfs(w, v, adjacency, treeEdges, fronds);
				
				//b:
				if (lowpt1[wIndex] < lowpt1[vIndex]){
					lowpt2[vIndex] = Math.min(lowpt1[vIndex], lowpt2[wIndex]);
					lowpt1[vIndex] = lowpt1[wIndex];
				}
				else if (lowpt1[wIndex] == lowpt1[vIndex])
					lowpt2[vIndex] = Math.min(lowpt2[vIndex], lowpt2[wIndex]);
				else
					lowpt2[vIndex] = Math.min(lowpt2[vIndex], lowpt1[wIndex]);
				nd[vIndex] += nd[wIndex];
				father[wIndex] = vIndex;
				
			}
			else if ((number[wIndex] < number[vIndex]) && ((w != u) || !flag[vIndex])){
				//the test in necessary to avoid exploring the edge
				//in both direction. Flag(v) becomes false when the entry in A(v) corresponding
				//to the tree arc (u,v) is examined
				
				//mark (v,w) as frond
				fronds.add(e);
				
				//c:
				if (number[wIndex] < lowpt1[vIndex]){
					lowpt2[vIndex] = lowpt1[vIndex];
					lowpt1[vIndex] = number[wIndex];
				}
				else if (number[wIndex] > lowpt1[vIndex])
					lowpt2[vIndex] = Math.min(lowpt2[vIndex], number[wIndex]);
			}
			if (w == u)
				flag[vIndex] = false;
		}
	}
	
	
}
