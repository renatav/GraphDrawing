package graph.properties.splitting;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.properties.components.BiconnectedComponent;
import graph.properties.components.HopcroftSplitComponent;
import graph.properties.components.SplitComponentType;
import graph.trees.DFSTree;
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


	/**
	 * array keeping the number of descendants of vertices
	 */
	private int[] nd;
	/**
	 * array keeping the lowest reachable vertex (represented by its index in the list of vertices)
	 * by traversing zero or more tree arcs in the palm tree followed by at most one frond
	 * for each vertex
	 */
	private int[] lowpt1;
	/**
	 * array keeping the second lowest reachable vertex (represented by its index in the list of vertices)
	 * by traversing zero or more tree arcs in the palm tree followed by at most one frond
	 * for each vertex
	 */
	private int[] lowpt2;
	/**
	 * numbering of vertices set during the first dfs
	 */
	private int[] number;
	/**
	 * array keeping the information regarding the father vertex
	 * for each vertex 
	 */
	private int[] father;
	/**
	 * new numbering set during the path finding stage (when search is performed
	 * with the ordered adjacency structure)
	 */
	private int[] newnum;
	/**
	 * array keeping the highest reachable vertex (represented by its index in the list of vertices)
	 * by traversing zero or more tree arcs in the palm tree
	 * for each vertex
	 */
	private int[] highpt;
	/**
	 * array keeping number of edges incident to each vertex
	 */
	private int[] degree;
	/**
	 * help variable used to avoid examining one edge more than once
	 */
	private boolean[] flag;
	/**
	 *Graph whose triconnected division shiould be performed 
	 */
	private Graph<V,E> graph;
	/**
	 * List of graph's vertices
	 */
	private List<V> vertices;
	/**
	 * List of tree edges of the dfs tree found during dfs
	 */
	private List<E> treeEdges;
	/**
	 * List of fronds or back edges
	 */
	private List<E> fronds;
	/**
	 * array of first sons of vertices - the first entry in the adjacency list
	 */
	private int[] a1;
	private int n;


	public TriconnectedDivision(Graph<V,E> graph){
		this.graph = graph;
		this.vertices = graph.getVertices();
	}

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

	public List<Pair<V,V>> findSeaparationPairs(){
		List<Pair<V,V>> separationPairs = new ArrayList<Pair<V,V>>();

		//step one: perform a depth-first search on the multigraph converting in
		//into a palm tree
		int size = graph.getVertices().size();
		lowpt1 = new int[size];
		lowpt2 = new int[size];
		nd = new int[size];
		father = new int[size];
		newnum = new int[size];
		highpt = new int[size];
		degree = new int[size];
		a1 = new int[size];


		number = new int[size];
		flag = new boolean[size];
		for (int i = 0; i < size; i++){
			flag[i] = true;
			//number is initially all zeros
		}
		treeEdges = new ArrayList<E>();
		fronds = new ArrayList<E>();

		Map<V, List<E>> adjacency = new HashMap<V, List<E>>();
		for (V v : graph.getVertices()){
			adjacency.put(v, new ArrayList<E>(graph.adjacentEdges(v)));
		}



		//the search starts at vertex s
		V root = graph.getVertices().get(0);
		dfs(root,null, adjacency);
		
		//construct ordered adjacency lists
		constructAdjacencyLists(adjacency);
		
		V s = null;
		int m = size;
		
		//find vertex whose number is 1, start with it
		//that will be the previously selected root vertex
		List<List<E>> paths = new ArrayList<List<E>>();
		pathfiner(root, s, m, adjacency, paths, null);

		System.out.println("number:");
		for (int i = 0; i < number.length; i++)
			System.out.print(number[i] + " ");
		System.out.println("");
		System.out.println(number);
		System.out.println("newnum");
		for (int i = 0; i < newnum.length; i++)
			System.out.print(newnum[i] + " ");
		
		
		//compute degree(v), lowpt1(v) and lowpt2(v)
		//using the new numbering
		
		DFSTree<V,E> tree = new DFSTree<V,E>(root, newnum, treeEdges, fronds, vertices);

		for (V v : vertices){
		
			int vIndex = vertices.indexOf(v);
			int[] lowpts = tree.lowpts(v);
			lowpt1[vIndex] = lowpts[0];
			lowpt2[vIndex] = lowpts[1];
			
			System.out.println(newnum[vIndex]);
			System.out.println("lowpt1: " + lowpt1[vIndex]);
			System.out.println("lowpt2: " + lowpt2[vIndex]);
			
			degree[vIndex] = adjacency.get(v).size();
			a1[vIndex] = adjacency.get(v).size();
			
		}


		return separationPairs;

	}
	

	/**
	 * Routine for depth-first search of a multigraph represented by adjacency list
	 * A(v). Variable n denote the last number assigned to a vertex
	 *  U is the father of vertex v in the spanning tree being constructed
	 *  The graph to be searched is represented by adjacency lists A(v)
	 *  This will be implemented with a map vertex - list of edges 
	 */
	private void dfs(V v, V u, Map<V,List<E>> adjacency){
		int vIndex = vertices.indexOf(v);
		
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
				treeEdges.add(e); //(v,w) is a tree arc
				dfs(w, v, adjacency);

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

	/**
	 * Constructs ordered adjacency lists
	 * @param adjacent
	 */
	private void constructAdjacencyLists(Map<V,List<E>> adjacent){
		Map<Integer, List<E>> bucket = new HashMap<Integer, List<E>>();
		List<V> vertices = graph.getVertices();
		int size = vertices.size();
		V v, w;
		
		for (E e : graph.getEdges()){

			System.out.println("analyzing edge " + e);

			

			//see which one comes first
			//v will be the one with smaller dfs index
			//w with bigger
			if (number[vertices.indexOf(e.getOrigin())] < number[vertices.indexOf(e.getDestination())]){
				v = e.getOrigin();
				w = e.getDestination();
			}
			else{
				v = e.getDestination();
				w = e.getOrigin();
			}

			int vIndex = vertices.indexOf(v);
			int wIndex = vertices.indexOf(w);

			//compute Fi(v,w)
			Integer fi;
			if (fronds.contains(e))
				fi = 2 * number[wIndex] + 1;
			else if (lowpt2[wIndex] < number[vIndex]) //and is tree edge
				fi = 2 * lowpt1[wIndex];
			else
				fi = 2*lowpt1[wIndex] + 1;

			System.out.println("Fi " + fi);

			List<E> list = bucket.get(fi);
			if (list == null){
				list = new ArrayList<E>();
				bucket.put(fi, list);
			}

			list.add(e); // add (v,w) to bucket(fi(v,w))
		}
		
		//clear adjacent
		for (V vert : vertices)
			adjacent.get(vert).clear();
		
		System.out.println("Buket: " + bucket);
		
		for (int i = 1; i <= 2*size + 1; i++){
			if (!bucket.containsKey(i))
				continue;
			for (E e : bucket.get(i)){
				//add w to the end of A(v)

				if (number[vertices.indexOf(e.getOrigin())] < number[vertices.indexOf(e.getDestination())]){
					v = e.getOrigin();
					w = e.getDestination();
				}
				else{
					v = e.getDestination();
					w = e.getOrigin();
				}
				adjacent.get(v).add(e);
			}
		}

	}
	
	/**
	 * Routine to generate paths in a biconnected palm tree 
	 * with specially ordered adjacency lists
	 * @param v vertex
	 * @param s denotes the start vertex of the current path
	 * @param m denotes the last number assigned to a vertex
	 * @param adjacent ordered adjacency structure
	 * @param paths list which will contain all paths
	 * @param currentPath current path being built
	 * @param treeEdges list of all tree edges (arcs)
	 */
	private void pathfiner(V v, V s, int m, Map<V,List<E>> adjacent, 
			List<List<E>> paths, List<E> currentPath){
		int vIndex = vertices.indexOf(v);
		newnum[vIndex] = m - nd[vIndex] + 1;
		
		for (E e : adjacent.get(v)){
			
			V w = e.getOrigin() == v ? e.getDestination() : e.getOrigin();
			int wIndex = vertices.indexOf(w);
			
			if (s == null){
				s = v;
				currentPath = new ArrayList<E>();
			}
			currentPath.add(e);
			if (treeEdges.contains(e)){
				pathfiner(w, s, m, adjacent, paths, currentPath);
				m--;
			}
			else{
				if (highpt[newnum[wIndex] - 1] == 0) //-1 since numbering start from 1, indexes from 1
					highpt[newnum[wIndex] - 1] = newnum[vIndex];
				paths.add(currentPath); //output current path
				s = null;
			}
		}
	}


}
