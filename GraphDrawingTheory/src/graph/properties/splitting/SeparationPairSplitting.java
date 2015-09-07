package graph.properties.splitting;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.properties.components.HopcroftSplitComponent;
import graph.trees.DFSTree;
import graph.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class SeparationPairSplitting<V extends Vertex, E extends Edge<V>> {

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
	/**
	 * inverse of numbering, takes number as the index of the array and keeps numberings as values
	 */
	private int[] inverseNumbering;
	private int n;
	private int j;


	public List<Pair<V,V>> findSeaparationPairs(Graph<V,E> graph){
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
		inverseNumbering = new int[size];


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
			inverseNumbering[newnum[vIndex]] = vIndex;

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

	/**
	 * Finds split components of a biconnected multigraph
	 * on which the previous steps (finds lowpt1, lowpt2, etc.)
	 * were already carried out
	 * A graph is represented by a set of properly ordered adjacency list s A9V)
	 * @param tstack contains triples representing possible type 2 separation pairs
	 * @param estack contains edges backed up over during search
	 */
	public void split(Stack tstack, Stack estack){

	}

	/**
	 * Recursive procedure which repeats the pathfinding search
	 * finding separation pairs and splitting off components as it proceeds
	 * @param v current vertex in the depth-first search
	 */
	private void pathsearch(V v, Map<V,List<E>> adjacent, List<List<E>> paths, Stack<Triple>tstack, 
			List<E> estack, HopcroftSplitComponent<V, E> currentComponent, List<HopcroftSplitComponent<V, E>> components){

		int vIndex = vertices.indexOf(v);
		HopcroftSplitComponent<V, E> newComponent = null; 

		for (E e : adjacent.get(v)){

			V w = e.getOrigin() == v ? e.getDestination() : e.getOrigin();
			int wIndex = vertices.indexOf(w);

			//a vertex is represented by its number (according to newnum)
			//newnum takes index of a vertex and returns the order

			if (firstEdgeOfAPath(e, paths)){
				int y = 0;
				int b = 0; //save last deleted b
				boolean deleted = false;
				
				while (!tstack.isEmpty()){ //while (h,a,b) on tstack has a > lowpt1(w)
					Triple stackCurrent = tstack.peek();

					int a = stackCurrent.getA();
					
					if (a <= lowpt1[wIndex])
						break;

					int h = stackCurrent.getH();
					y = Math.max(y, h);
					tstack.pop(); //remove from stack
					deleted = true;
					b = stackCurrent.getB();
				}
				
				//if no triples were deleted from tstack add (w + ND(w) - 1, lowpt1(w), v) to stack
				//else max{y, w + ND(w) - 1}, lowpt1(w),b)
				Integer[] stackItem = new Integer[3];
				Triple newPair;
				if (!deleted)
					newPair = new Triple(newnum[wIndex] + nd[wIndex] - 1, lowpt1[wIndex], newnum[vIndex]);
				else 
					newPair = new Triple(Math.max(y, newnum[wIndex] + nd[wIndex] - 1), lowpt1[wIndex], b);
				tstack.push(newPair);
				//add end of stack marker to tstack - let that be (h,a,b) = (-1,-1,-1)
				tstack.push(new Triple(-1, -1, -1));
			}


			pathsearch(w, adjacent, paths, tstack, estack, currentComponent, components);
			estack.add(e); //add (v,w) to estack

			boolean flag = false;
			E savedEdge = null;

			while (newnum[vIndex] != 1){
				Triple stackItem = tstack.peek();
				int a = stackItem.getA();

				//((degree(w) = 2) and (A1(w)>w) or (h,a,b) on tstack satisfies (v=a)
				if (!((degree[wIndex] == 2 && a1[wIndex] > newnum[wIndex]) || a == newnum[vIndex]))
					break;

				//test for type 2 pairs

				int b = stackItem.getB();
				int h = stackItem.getH();
				int bIndex = inverseNumbering[b];
				int aIndex = inverseNumbering[a];

				if (a == newnum[vIndex] && father[bIndex] == aIndex) //a==v and father(b) = a
					tstack.pop(); //delete (h,a,b) from stack
				else{
					if (degree[wIndex] == 2 && newnum[a1[wIndex]] > newnum[wIndex]){ //degree(w) = 2 and A1(w) > w

						j++;

						newComponent = new HopcroftSplitComponent<V,E>();
						components.add(newComponent);
						currentComponent = newComponent;

						//add top two edges (v,w) and (w,x) on estack to new component
						E e1 = estack.get(estack.size() - 1);
						E e2 = estack.get(estack.size() - 2);
						newComponent.getEdges().add(e1);
						newComponent.getEdges().add(e2);

						//edges are (v,w) and (w,x)
						//add(v,x,j) to new component
						//find x
						V x = e.getDestination() == w ? e.getOrigin() : e.getDestination();
						Integer[] newTriple = new Integer[3];
						newTriple[0] = newnum[vIndex];
						newTriple[1] = newnum[vertices.indexOf(x)];
						newTriple[2] = j;

						//is there an edge (y,z) = (x,v) on estack?

						for (E edge : estack)
							if ((edge.getOrigin() == x && edge.getDestination() == v) ||
									(edge.getOrigin() == v && edge.getDestination() == x)){
								flag = true;
								estack.remove(edge); //delete (y,z) from stack and save
								savedEdge = edge;
								break;
							}
					}
					//continue here
					int x = 0;
					if (a == newnum[vIndex] && father[bIndex] != aIndex){ //if v=a and father(b)!=a
						j++;
						//delete (h,a,b) from tstack
						tstack.pop();
						//simulating stack - start with the last one
						List<E> toDelete = new ArrayList<E>();
						for (int i = estack.size() - 1; i >= 0; i--){ //while (x,y) on estack has (a<=x<=h) and (a<=y<=h)
							E edge = estack.get(i);

							V xVert= edge.getDestination();
							V yVert = edge.getOrigin();
							x = newnum[vertices.indexOf(xVert)];
							int y = newnum[vertices.indexOf(yVert)];

							if (!((a <= x && x <= h) && (a <= y && y <= h)))
								break; //simulating while 

							if ((x == a  && y == b) || (y == b && x == a)){ //if (x,y) == (a,b)
								flag = true;
								//delete (a,b) from tstack - Isn't it already?
								//maybe estack?
								//delete (a,b) = delete (x,y) = delete current edge and save
								toDelete.add(edge);
								savedEdge = edge;
							}
							else{
								//delete (x,y) from estack and add to current component
								toDelete.add(edge);
								currentComponent.getEdges().add(edge);
								//decrement degree(x) and degree(y)
								degree[vertices.indexOf(x)] --;
								degree[vertices.indexOf(y)] --;
							}
						}
						estack.removeAll(toDelete);

						//add (a,b,j) to new component
						Integer[] newTriple = new Integer[3];
						newTriple[0] = a;
						newTriple[1] = b;
						newTriple[2] = j;


						newComponent = new HopcroftSplitComponent<V,E>();
						components.add(newComponent);
						currentComponent = newComponent;
						newComponent.getTriples().add(newTriple);
						x = b;
					}

					if (flag){
						flag = false;
						j++;
						//add saved edges (x,v,j-1), (x,v,j) to new component

						//TODO when to create new component
						newComponent.getEdges().add(savedEdge);
						Integer[] triple1 = new Integer[3];
						triple1[0] = x;
						triple1[1] = newnum[vIndex];
						triple1[2] = j-1;

						Integer[] triple2 = new Integer[3];
						triple2[0] = x;
						triple2[1] = newnum[vIndex];
						triple2[2] = j;

						newComponent.getTriples().add(triple1);
						newComponent.getTriples().add(triple2);

						//decrement degree(x), degree(y)

						int xIndex = inverseNumbering[x];

						degree[xIndex] --;
						degree[vIndex] --;

					}

				}
			}



		}

	}




	private boolean firstEdgeOfAPath(E e, List<List<E>> paths){
		for (List<E> path : paths)
			if (path.get(0) == e)
				return true;
		return false;

	}
}
