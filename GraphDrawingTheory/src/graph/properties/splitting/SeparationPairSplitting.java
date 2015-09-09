package graph.properties.splitting;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.properties.components.HopcroftSplitComponent;
import graph.trees.DFSTree;
import graph.util.Pair;
import graph.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.log4j.Logger;

public class SeparationPairSplitting<V extends Vertex, E extends Edge<V>> {

	/**
	 * array keeping the number of descendants of vertices
	 */
	private int[] nd;
	/**
	 * array keeping the lowest reachable vertex
	 * by traversing zero or more tree arcs in the palm tree followed by at most one frond
	 * for each vertex
	 */
	private int[] lowpt1;
	/**
	 * array keeping the second lowest reachable vertex
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
	
	/**denotes the start vertex of the current path in the pathfinder phase*/ 
	private V s;
	/**denotes the last number assigned to a vertex in the pathfinder phase*/
	private int m;
	
	private int n;
	private int j;
	private DFSTree<V,E> tree;

	private Class edgeClass;

	private Logger log = Logger.getLogger(SeparationPairSplitting.class);


	public List<Pair<V,V>> findSeaparationPairs(Graph<V,E> graph,  Class edgeClass){
		List<Pair<V,V>> separationPairs = new ArrayList<Pair<V,V>>();
		this.edgeClass = edgeClass;

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
		vertices = graph.getVertices();
		this.graph = graph;

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

		log.info("first dfs traversal finished");
		tree = new DFSTree<V,E>(root, number, treeEdges, fronds, vertices);
		System.out.println(tree.toString());

		
		for (V v : vertices){
			System.out.println("Adjacent for " + v + ": " + adjacency.get(v));
		}


		//		//construct ordered adjacency lists
		constructAdjacencyLists(adjacency);
		
		for (V v : vertices){
			System.out.println("Adjacent for " + v + ": " + adjacency.get(v));
		}

		s = null;
		m = size;

		//find vertex whose number is 1, start with it
		//that will be the previously selected root vertex
		List<List<E>> paths = new ArrayList<List<E>>();
		pathfiner(root, adjacency, paths, null);

		System.out.println("number:");
		for (int i = 0; i < number.length; i++)
			System.out.print(number[i] + " ");
		System.out.println("");

		System.out.println("newnum");
		for (int i = 0; i < newnum.length; i++)
			System.out.print(newnum[i] + " ");
	
		
		
				//compute degree(v), lowpt1(v) and lowpt2(v)
				//using the new numbering
		
				tree = new DFSTree<V,E>(root, newnum, treeEdges, fronds, vertices);
				
				System.out.println("treeEdges: " + treeEdges);
				System.out.println("backEdges: " + fronds);
		
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
					inverseNumbering[newnum[vIndex] - 1] = vIndex;
		
				}
		
				log.info("seconf dfs completed, printing dfs tree");
				printDFSTree();
		
				split(adjacency, paths);

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

		int size = vertices.size();
		V v;

		for (E e : graph.getEdges()){

			System.out.println("analyzing edge " + e);

			//v is origin
			//w is destination
			int[] directedIndexes = getDirectedNodes(e, number);

			int vIndex = directedIndexes[0];
			int wIndex = directedIndexes[1];


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

				int[] directedIndexes = getDirectedNodes(e, number);
				int vIndex = directedIndexes[0];
				v = vertices.get(vIndex);
				//add w to end of A(v)
				adjacent.get(v).add(e);
			}
		}

	}

	/**
	 * Routine to generate paths in a biconnected palm tree 
	 * with specially ordered adjacency lists
	 * @param v vertex
	 * @param adjacent ordered adjacency structure
	 * @param paths list which will contain all paths
	 * @param currentPath current path being built
	 * @param treeEdges list of all tree edges (arcs)
	 */
	private void pathfiner(V v, Map<V,List<E>> adjacent, 
			List<List<E>> paths, List<E> currentPath){
		
		int vIndex = vertices.indexOf(v);
		newnum[vIndex] = m - nd[vIndex] + 1;
		
		System.out.println("setting newnum " +  v + " = " +  newnum[vIndex]);

		for (E e : adjacent.get(v)){

			V w = e.getOrigin() == v ? e.getDestination() : e.getOrigin();
			int wIndex = vertices.indexOf(w);

			if (s == null){
				s = v;
				currentPath = new ArrayList<E>();
				paths.add(currentPath);
			}
			currentPath.add(e);
			if (treeEdges.contains(e)){
				pathfiner(w, adjacent, paths, currentPath);
				m--;
			}
			else{
				if (highpt[newnum[wIndex] - 1] == 0) //-1 since numbering start from 1, indexes from 1
					highpt[newnum[wIndex] - 1] = newnum[vIndex];
				//output current path
				System.out.println("output " + currentPath);
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
	public void split(Map<V,List<E>> adjacent, List<List<E>> paths){

		int j = 0;
		boolean flag = false;
		List<HopcroftSplitComponent<V, E>> components = new ArrayList<HopcroftSplitComponent<V,E>>();
		Map<E,Integer> virtualEdges = new HashMap<E, Integer>();

		Stack<Triple> tstack = new Stack<Triple>();
		Stack<E> estack = new Stack<E>();

		pathsearch(vertices.get(inverseNumbering[1]), adjacent, paths, tstack, estack, null, components, virtualEdges, j, flag);
	}

	/**
	 * Recursive procedure which repeats the pathfinding search
	 * finding separation pairs and splitting off components as it proceeds
	 * @param v current vertex in the depth-first search
	 */
	private void pathsearch(V v, Map<V,List<E>> adjacent, List<List<E>> paths, Stack<Triple>tstack, 
			List<E> estack, HopcroftSplitComponent<V, E> currentComponent, List<HopcroftSplitComponent<V, E>> components, 
			Map<E,Integer> virtualEdges, int j, boolean flag){

		int vIndex = vertices.indexOf(v);

		//TODO oprezno sa ivicama
		//treba znati orijentaciju!
		//uzeti to u obzir kada se preuzimaju x i y

		for (E e : adjacent.get(v)){

			V w = e.getOrigin() == v ? e.getDestination() : e.getOrigin();
			int wIndex = vertices.indexOf(w);

			//if e is a tree edge
			if (treeEdges.contains(e)){

				//a vertex is represented by its number (according to newnum)
				//newnum takes index of a vertex and returns the order

				if (firstEdgeOfAPath(e, paths)){
					int y = 0;
					int b = 0; //save last deleted b
					boolean deleted = false;

					//ensure that the stack will not be empty
					//break the loop when the other conditions are not longer satisfied
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
					Triple newPair;
					if (!deleted)
						newPair = new Triple(newnum[wIndex] + nd[wIndex] - 1, lowpt1[wIndex], newnum[vIndex]);
					else 
						newPair = new Triple(Math.max(y, newnum[wIndex] + nd[wIndex] - 1), lowpt1[wIndex], b);
					tstack.push(newPair);
					//add end of stack marker to tstack - let that be (h,a,b) = (-1,-1,-1)
					tstack.push(new Triple(-1, -1, -1));
				}


				pathsearch(w, adjacent, paths, tstack, estack, currentComponent, components, virtualEdges, j, flag);
				estack.add(e); //add (v,w) to estack



				//ensure that the stack will not be empty
				//break the loop when the other conditions are not longer satisfied
				while (!tstack.isEmpty()){

					if (newnum[vIndex] == 1)
						break;


					Triple stackItem = tstack.peek();
					int a = stackItem.getA();

					//((degree(w) = 2) and (A1(w)>w) or (h,a,b) on tstack satisfies (v=a) (while condition)
					if (!(degree[wIndex] == 2 && a1[wIndex] > newnum[wIndex] || a == newnum[vIndex]))
						break;

					//flag = false;
					E savedEdge = null;


					//test for type 2 pairs

					int b = stackItem.getB();
					int h = stackItem.getH();
					int bIndex = inverseNumbering[b - 1]; //since numbering starts from 1, indexes from 0
					int aIndex = inverseNumbering[a - 1];
					int x = 0;

					if (a == newnum[vIndex] && father[bIndex] == aIndex) //a==v and father(b) = a
						tstack.pop(); //delete (h,a,b) from stack
					else{

						if (degree[wIndex] == 2 && newnum[a1[wIndex]] > newnum[wIndex]){ //degree(w) = 2 and A1(w) > w

							j++;

							log.info("creating new component");

							HopcroftSplitComponent<V,E> newComponent = new HopcroftSplitComponent<V,E>();
							components.add(newComponent);
							currentComponent = newComponent;

							//add top two edges (v,w) and (w,x) on estack to new component
							E e1 = estack.get(estack.size() - 1);
							E e2 = estack.get(estack.size() - 2);
							newComponent.getEdges().add(e1);
							newComponent.getEdges().add(e2);
							log.info("adding edges " + e1 + " " + e2);

							//edges are (v,w) and (w,x)
							//add(v,x,j) to new component
							//find x - origin or destination of e2
							V xVert = e2.getDestination() == w ? e2.getOrigin() : e2.getDestination();

							//set x
							x = newnum[vertices.indexOf(xVert)];

							Triple newTriple = new Triple(newnum[vIndex], newnum[vertices.indexOf(x)], j);
							newComponent.getTriples().add(newTriple);
							log.info("adding triple " + newTriple);

							//is there an edge (y,z) = (x,v) on estack?
							for (E edge : estack)
								if ((edge.getOrigin() == xVert && edge.getDestination() == v) ||
										(edge.getOrigin() == v && edge.getDestination() == xVert)){
									flag = true;
									estack.remove(edge); //delete (y,z) from stack and save
									savedEdge = edge;
									break;
								}
						}
					}



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
								///TODO
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
								degree[vertices.indexOf(xVert)] --;
								degree[vertices.indexOf(yVert)] --;
							}
						}
						estack.removeAll(toDelete);

						//add (a,b,j) to new component
						Triple newTriple = new Triple(a,b,j);

						HopcroftSplitComponent<V,E> newComponent = new HopcroftSplitComponent<V,E>();
						components.add(newComponent);
						currentComponent = newComponent;
						newComponent.getTriples().add(newTriple);

						log.info("Creating new component");
						log.info("adding " + newTriple);


						x = b;
					}

					if (flag){
						flag = false;
						j++;

						//TODO when to create new component? always?

						//add saved edge, (x,v,j-1), (x,v,j) to new component

						HopcroftSplitComponent<V,E> newComponent = new HopcroftSplitComponent<V,E>();
						newComponent.getEdges().add(savedEdge);
						components.add(newComponent);
						currentComponent = newComponent;

						Triple triple1 = new Triple(x, newnum[vIndex], j - 1);
						Triple triple2 = new Triple(x, newnum[vIndex], j);

						newComponent.getTriples().add(triple1);
						newComponent.getTriples().add(triple2);

						log.info("Creating new component");
						log.info("adding " + triple1);
						log.info("adding " + triple2);

						//decrement degree(x), degree(v)

						int xIndex = inverseNumbering[x - 1];

						degree[xIndex] --;
						degree[vIndex] --;

					}

					//TODO add (v,x,j) to estack ? tstack?
					//is this the virtual edge
					//what if x was not set

					int xIndex = inverseNumbering[x - 1];
					V xVert = vertices.get(xIndex);

					//Triple newTriple = new Triple(newnum[vIndex],x,j);

					//add to estack
					//TODO is it better to create a mapping, instead of using weights?
					E newEdge = Util.createEdge(v, xVert, edgeClass);
					newEdge.setWeight(j); //save j 
					estack.add(newEdge);

					//increment degree(x), degree(v)


					degree[xIndex] ++;
					degree[vIndex] ++;
					father[xIndex] = vIndex;

					//if (A1(v) ->* x then A1(v) = x

					//check if there is a path from A1(v) to x..
					//meaning that x is descendant of of v

					V a1V = vertices.get(a1[vIndex]);
					if (tree.allDescendantsOf(a1V, true).contains(xVert)){
						//a1(v) = x
						a1[vIndex] = xIndex;
					}
					w = xVert;
					wIndex = xIndex;
				}//end of while

				//test for type 1 pair
				//if lowpt2(w)>=v and (lowpt1(v)1=1 or father(v) != 1 or w > 3)
				if ((lowpt2[wIndex] >= newnum[vIndex]) && (lowpt1[vIndex] == 1 ||
						newnum[father[vIndex]] != 1) || newnum[wIndex] > 3){

					j++;

					//while (x,y) on top of estack has (w<=x<w+nd(w) or (w<=x<w+nd(w)

					List<E> toDelete = new ArrayList<E>();
					for (int i = estack.size() - 1; i >= 0; i--){

						E topEdge = estack.get(i);

						V xVert = topEdge.getOrigin();
						V yVert = topEdge.getDestination();

						int xIndex = vertices.indexOf(xVert);
						int yIndex = vertices.indexOf(yVert);
						int x = newnum[xIndex];
						int y = newnum[yIndex];

						if (!((newnum[wIndex] <= x && x <= newnum[wIndex] + nd[wIndex]) ||
								(newnum[wIndex] <= y && y <= newnum[wIndex] + nd[wIndex])))
							break;

						//delete (x,y) from estack
						toDelete.add(topEdge);
						//add(x,y) to new component
						HopcroftSplitComponent<V,E> newComponent = new HopcroftSplitComponent<V,E>();
						components.add(newComponent);
						currentComponent = newComponent;
						newComponent.getEdges().add(topEdge);

						log.info("Creating new component");
						log.info("adding " + topEdge);

						//decrement degree(x), degree(y)
						degree[xIndex] --;
						degree[yIndex] --;
					}
					estack.removeAll(toDelete);

					//add (w, lowpt1(w), j) to new component

					HopcroftSplitComponent<V,E> newComponent = new HopcroftSplitComponent<V,E>();
					components.add(newComponent);
					currentComponent = newComponent;
					Triple newTriple = new Triple(newnum[wIndex], newnum[lowpt1[wIndex]], j);
					newComponent.getTriples().add(newTriple);

					log.info("Creating new component");
					log.info("adding " + newTriple);


					if (a1[vIndex] == wIndex) //if A1(v) = w
						a1[vIndex] = inverseNumbering[lowpt1[wIndex] - 1];// a1(v) = lowpt1(w) - since it saves indexes, inverseNumbering is used

					//test for multiple edges

					//if (x,y) on top of estack has (x,y) = (v, lowpt1(w))
					if (estack.size() > 0){
						E topEdge = estack.get(estack.size() - 1);
						V xVert = topEdge.getOrigin();
						V yVert = topEdge.getDestination();
						int yIndex = vertices.indexOf(yVert);

						if (xVert == v && yIndex == lowpt1[wIndex]){

							j++;
							//add (x,y), (v,lowpt1(w),j=1), (v, lowpt1(w),j) to new component
							newComponent = new HopcroftSplitComponent<V,E>();
							components.add(newComponent);
							currentComponent = newComponent;
							newComponent.getEdges().add(topEdge);
							Triple triple1 = new Triple(newnum[vIndex], newnum[lowpt1[wIndex]],1);
							Triple triple2 = new Triple(newnum[vIndex], newnum[lowpt1[wIndex]],j);
							newComponent.getTriples().add(triple1);
							newComponent.getTriples().add(triple2);


							log.info("Creating new component");
							log.info("adding " + triple1);
							log.info("adding " + triple2);

							//decrement degree(v), degree(lowpt1(w))
							degree[vIndex] --;
							degree[lowpt1[wIndex]] --;
						}
					}

					//if lowpt1(w) != father(v)
					if (lowpt1[vIndex] != father[vIndex]){

						//TODO
						//add (w,lowpt1(w), j) to estack -- maybe search for the edge instead of creating it
						//it should exist
						E newEdge = Util.createEdge(w, vertices.get(lowpt1[wIndex]), edgeClass);
						newEdge.setWeight(j); //save j

						//decrement degree(v), degree(lowpt1(w))
						degree[vIndex] --;
						degree[lowpt1[wIndex]] --;
					}
					else{
						j++;

						//add (v, lowpt1(w), j-1), (v, lowpt1(w), j), arc (lowpt1(w), w) to new component
						Triple triple1 = new Triple(newnum[vIndex], newnum[lowpt1[wIndex]], j-1);
						Triple triple2 = new Triple(newnum[vIndex], newnum[lowpt1[wIndex]], j);
						E newEdge = Util.createEdge(vertices.get(lowpt1[wIndex]), w, edgeClass);
						newComponent = new HopcroftSplitComponent<V,E>();
						components.add(newComponent);
						currentComponent = newComponent;
						newComponent.getEdges().add(newEdge);
						newComponent.getTriples().add(triple1);
						newComponent.getTriples().add(triple2);

						log.info("Creating new component");
						log.info("adding " + triple1);
						log.info("adding " + triple2);

						//mark tree arc (lowpt1(w), v) as virtual edge j
						virtualEdges.put(newEdge, j);
					}
				}	

				//if v-> w is first edge of a path
				//delete all entries on tstack down to and including end of stack marker
				if (firstEdgeOfAPath(e, paths)){
					while (true){
						Triple triple = tstack.peek();
						if (triple.getA() == -1 && triple.getB() == -1 && triple.getH() == -1){ //end of stack marker
							tstack.pop();
							break;
						}
						tstack.pop();
					}
				}

				//while (h,a,b) on estack has highpt(v) >h 
				//delete (h,a,b) from tstack
				//TODO
				for (int i = estack.size() - 1; i >= 0; i++){
					log.info("checking for triple of ednge stack? ");
					E estackEdge = estack.get(i);
					if (estackEdge.getWeight() == 0)
						break; //not the special triple entry
					int h = newnum[vertices.indexOf(e.getOrigin())];
					if (highpt[vIndex] <= h)
						break;
					//delete (h,a,b) from tstack
					int a = newnum[vertices.indexOf(e.getDestination())];
					int b = estackEdge.getWeight();
					Triple toDelete = null;
					for (int p = 0; p < tstack.size(); p++){
						Triple t = tstack.get(p);
						if (t.getA() == a && t.getB() == b && t.getH() == h){
							toDelete = t;
							break;
						}
					}
					if (toDelete != null)
						tstack.remove(toDelete);
				}
			}
			else{ //e is a back edge

				//if v --> w is first edge of a path (and last)
				if (firstEdgeOfAPath(e, paths)){
					int y = 0;
					boolean deleted = false;
					Triple lastDeleted = null;

					//while (h,a,b) on tstack has a > w
					while (!tstack.isEmpty()){

						Triple triple = tstack.peek();
						int a = triple.getA();
						if (a <= newnum[wIndex])
							break;
						//y = max(y,h)
						y = Math.max(y, triple.getH());
						//dekete (h,a,b) from tstack
						tstack.pop();
						deleted = true;
						lastDeleted = triple;
					}

					//if no triples were deleted from tstack
					if (!deleted){
						//add (v,w,w) to tstack
						Triple triple = new Triple(newnum[vIndex], newnum[wIndex], newnum[vIndex]);
						tstack.push(triple);
					}
					else{
						//if (h,a,b) is the last triple deleted
						//add (y,w,b) to tstack
						Triple triple = new Triple(y,newnum[wIndex], lastDeleted.getB());
						tstack.push(triple);
					}
				}
				//if (w = father(v))
				if (wIndex == father[vIndex]){
					j++;

					//add (v,w) , (v,w,j), tree arc (w,v) to new component
					HopcroftSplitComponent<V,E> newComponent = new HopcroftSplitComponent<V,E>();
					components.add(newComponent);
					currentComponent = newComponent;



					currentComponent.getEdges().add(e); //(v,w)
					Triple triple = new Triple(newnum[vIndex], newnum[wIndex], j);
					currentComponent.getTriples().add(triple);
					//find tree arc (w,v)
					E wvtreeEdge = null;
					for (E treeEdge : treeEdges){
						if ((treeEdge.getOrigin() == w && treeEdge.getDestination() == v) ||
								(treeEdge.getOrigin() == v && treeEdge.getDestination() == w)){
							wvtreeEdge = treeEdge;
							break;
						}
					}
					if (wvtreeEdge != null)
						newComponent.getEdges().add(wvtreeEdge);

					log.info("Creating new component");
					log.info("adding tree edge " + wvtreeEdge);

					//decrement degree(v), degree(w)
					degree[vIndex] --;
					degree[wIndex] --;
					//mark tree arc (w,v) as virtual edge j
					virtualEdges.put(wvtreeEdge, j);
				}
				else{
					//add (v,w) to estack
					estack.add(e);;
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


	/**
	 * A dfs tree is a directed graph regardless of this property of the original graph 
	 * Method finds origin and destination of an edge which can either be a back edge (frond)
	 * or an arc (tree edge)
	 * @param edge
	 * @return ret[0] = origin ret[1] = destination
	 */
	private int[] getDirectedNodes(E edge, int[] numbering){

		//if an edge is a tree edge
		//origin is the one with lower number
		//if it is a back edge, the oposite is true
		int[] ret = new int[2];


		int originIndex = vertices.indexOf(edge.getOrigin());
		int destinationIndex = vertices.indexOf(edge.getDestination());
		int originNum = numbering[originIndex];
		int destinationNum = numbering[destinationIndex];

		if (treeEdges.contains(edge)){
			if (originNum < destinationNum){
				ret[0] = originIndex;
				ret[1] = destinationIndex;
			}
			else{
				ret[0] = destinationIndex;
				ret[1] = originIndex;
			}
		}
		else { //back edge
			if (originNum > destinationNum){
				ret[0] = originIndex;
				ret[1] = destinationIndex;
			}
			else{
				ret[0] = destinationIndex;
				ret[1] = originIndex;
			}
		}

		return ret;




	}



	public void printDFSTree(){

		for (V v : vertices){

			int vIndex = vertices.indexOf(v);

			System.out.println("Vertex " + v + " number = " + newnum[vIndex] + " (" + vertices.get(inverseNumbering[lowpt1[vIndex] - 1]) + ", " 
					+ vertices.get(inverseNumbering[lowpt2[vIndex] -1 ]) + ")");

		}
	}

}
