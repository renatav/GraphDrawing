package graph.properties.splitting;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.properties.components.HopcroftSplitComponent;
import graph.trees.DFSTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.log4j.Logger;

public class HopcroftTarjanSplitting<V extends Vertex, E extends Edge<V>> {


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

	private Map<Integer, List<V>> lowpt1sMap = new HashMap<Integer, List<V>>();

	private Map<V, List<E>> adjacency;

	private Logger log = Logger.getLogger(HopcroftTarjanSplitting.class);

	private List<SeparationPair<V>> separationPairs;

	private List<HopcroftSplitComponent<V, E>> splitComponents;

	private Stack<E> estack;

	private Stack<Triple> tstack;

	private Triple endOfStackMarker;

	List<List<E>> paths = new ArrayList<List<E>>();


	public HopcroftTarjanSplitting(Graph<V,E> graph){
		this.graph = graph;
	}

	public void execute() throws AlgorithmErrorException{
		init();
		pathsearch(vertices.get(0));

	}

	/**
	 * Procedure to determine split components of a biconnected multigraph
	 * on which steps, such as dfs traversal, path finding, initialization
	 * of lowpts etc. were already carried out
	 * tstack contains triples representing possible type 2 separation pairs
	 * e stack contains edges backed up over during search
	 */
	private void pathsearch(V v){

		System.out.println("Current v: " + v);

		for (E e : adjacency.get(v)){

			int vIndex = vertices.indexOf(v);
			V w = e.getOrigin() == v ? e.getDestination() : e.getOrigin();
			int wIndex = vertices.indexOf(w);
			
			System.out.println("Current w: " + w);

			//if v -> w i.e. if e is a tree edge

			if (treeEdges.contains(e)){
				
				//TODO gde ide flag
				
				boolean flag;
				E savedEdge;

				//A:
				if (firstEdgeOfAPath(e)){
					int y = 0;

					Triple lastDeletedTriple = null;
					//while (h,a,b) on tastack has a > lowpt1(w)
					while (true){

						if (tstack.isEmpty())
							break;

						Triple triple = tstack.peek();
						int a = triple.getA();
						if (a <= lowpt1[wIndex])
							break;

						int h = triple.getH();
						y = Math.max(y, h);
						tstack.pop();
						lastDeletedTriple = triple;

					}
					//if no triples deleted from tstack add(w + ND(w)-1, lowpt1(w),v) to tstack
					if (lastDeletedTriple == null){
						Triple newTriple = new Triple(newnum[wIndex] + nd[wIndex] - 1, lowpt1[wIndex], newnum[vIndex]);
						log.info("pushing " + newTriple + " to tstack");
						tstack.push(newTriple);
					}
					//add max{y, w+nd(w)-1}, lowpt1(s),b to tstack
					else{
						Triple newTriple = new Triple
								(Math.max(y, newnum[wIndex] + nd[wIndex] - 1), lowpt1[wIndex], lastDeletedTriple.getB());
						log.info("pushing " + newTriple + " to tstack");
						tstack.push(newTriple);
					}
					//add end of stack marker to tstack
					tstack.push(endOfStackMarker);
					printTStack();
				}
				pathsearch(w);
				estack.push(e);
				//end of A

				//B:
				//while v != 1 and ((degree(w) = 2) and (A1(w) > 2) or (h,a,b) on tstack
				//stasfies (v = a) - test for type 2 pairs
				
				while(true){
					
					if (number[vIndex] == 1)
						break;
					
					//TODO obratiti paznju da li je ovo OK
					boolean firstCondition = degree[wIndex] == 2 && a1[wIndex] > newnum[wIndex];
					boolean secondCondition = false;
					
					Triple triple = null;
					if (!tstack.isEmpty())
						triple = tstack.peek();
					
					if (triple != null)
						secondCondition = newnum[vIndex] == triple.getA();
					
					if (firstCondition || secondCondition){
						//if (h,a,b) on tstack has (a=v) and father(b) = a
						int b = triple.getB();
						int bIndex = inverseNumbering[b];
						int a = triple.getA();
						if (secondCondition && newnum[father[bIndex]] == a){
							//delete (h,a,b) from stack
							log.info("removing triple " + triple + " from tstack");
							tstack.pop();
						}
						else{
							//if degree(w) = 2 and a1(w) > w
							if (degree[wIndex] == 2 && a1[wIndex] > newnum[wIndex]){
								j++;
								//add top two edges (v,w) and (w,x) on estack to new component
								E e1 = estack.get(estack.size() - 1);
								E e2 = estack.get(estack.size() - 2);
								
								int[] dir = getDirectedNodes(e2, newnum);
								int xIndex = dir[1];
								int x = newnum[xIndex];
								int vNum = newnum[vIndex];
								
								//TODO
								log.info("Add " + e1 + " " + e2 + "to new component");
								//add (v,x,j) to new component
								log.info("Add " + vNum + ", " + x + ", " + j + " to new component");
								
								
								//if (y,z) on estack has (y,z) = (x,v)
								//basically, try to find an edge (x,v)
								E xvEdge = onEstack(xIndex, vIndex);
								if (xvEdge != null){
									flag = true;
									//remove from estack and save
									estack.remove(xvEdge);
									savedEdge = xvEdge;
								}
								
								
								
							}
						}
						
					}
					
				}
			}
		}
	}


	private void init() throws AlgorithmErrorException{
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
		//separationPairEndVertices = new HashMap<V,List<SplitPair<V, E>>>();
		//separationPairStartVertices = new HashMap<V,List<SplitPair<V, E>>>();
		separationPairs = new ArrayList<SeparationPair<V>>();
		splitComponents = new ArrayList<HopcroftSplitComponent<V, E>>();
		estack = new Stack<E>();
		tstack = new Stack<Triple>();
		endOfStackMarker = new Triple(-1,-1,-1);
		j = 0;

		number = new int[size];
		flag = new boolean[size];
		for (int i = 0; i < size; i++){
			flag[i] = true;
			//number is initially all zeros
		}
		treeEdges = new ArrayList<E>();
		fronds = new ArrayList<E>();

		adjacency = new HashMap<V, List<E>>();
		for (V v : graph.getVertices()){
			adjacency.put(v, new ArrayList<E>(graph.adjacentEdges(v)));
		}

		//step one: perform a depth-first search on the multigraph converting in
		//into a palm tree

		//the search starts at vertex s
		V root = graph.getVertices().get(0);
		dfs(root,null);

		log.info("first dfs traversal finished");
		tree = new DFSTree<V,E>(root, number, treeEdges, fronds, vertices);
		//System.out.println(tree);

		constructAdjacencyLists(adjacency);

		s = null;
		m = size;

		//find vertex whose number is 1, start with it
		//that will be the previously selected root vertex
		pathfiner(root,paths, null);

		log.info("second dfs completed");

		System.out.println("CHECKING ADJACENCY: " + checkAdjacencyValidity(adjacency, newnum, treeEdges));

		if (!checkAdjacencyValidity(adjacency, newnum, treeEdges))
			throw new AlgorithmErrorException("Error: adjacency structure not valid");

		tree = new DFSTree<V,E>(root, newnum, treeEdges, fronds, vertices);
		System.out.println(tree.toString());

		log.info("setting lowpts, inverse numbering etc.");


		for (V v : vertices){

			int vIndex = vertices.indexOf(v);
			int[] lowpts = tree.lowpts(v);
			lowpt1[vIndex] = lowpts[0];
			lowpt2[vIndex] = lowpts[1];

			degree[vIndex] = adjacency.get(v).size();
			a1[vIndex] = adjacency.get(v).size();
			inverseNumbering[newnum[vIndex] - 1] = vIndex;

			Integer lowpt1Val = lowpt1[vIndex];

			List<V> verticesWithLowpt;
			if (!lowpt1sMap.containsKey(lowpt1Val)){
				verticesWithLowpt = new ArrayList<V>();
				lowpt1sMap.put(lowpt1Val, verticesWithLowpt);
			}
			else
				verticesWithLowpt = lowpt1sMap.get(lowpt1Val);
			verticesWithLowpt.add(v);

			degree[vIndex] = adjacency.get(v).size();
			a1[vIndex] = adjacency.get(v).size();
			inverseNumbering[newnum[vIndex] - 1] = vIndex;

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
	private void pathfiner(V v, 
			List<List<E>> paths, List<E> currentPath){

		int vIndex = vertices.indexOf(v);
		newnum[vIndex] = m - nd[vIndex] + 1;

		//System.out.println("setting newnum " +  v + " = " +  newnum[vIndex]);

		for (E e : adjacency.get(v)){

			V w = e.getOrigin() == v ? e.getDestination() : e.getOrigin();
			int wIndex = vertices.indexOf(w);

			if (s == null){
				s = v;
				currentPath = new ArrayList<E>();
				paths.add(currentPath);
			}
			currentPath.add(e);
			if (treeEdges.contains(e)){
				pathfiner(w, paths, currentPath);
				m--;
			}
			else{
				if (highpt[newnum[wIndex] - 1] == 0) //-1 since numbering starts from 1, indexes from 1
					highpt[newnum[wIndex] - 1] = newnum[vIndex];
				//output current path
				//System.out.println("output " + currentPath);
				s = null;
			}
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

			//System.out.println("analyzing edge " + e);

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

			//System.out.println("Fi " + fi);

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

		//System.out.println("Buket: " + bucket);

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
	 * Routine for depth-first search of a multigraph represented by adjacency list
	 * A(v). Variable n denote the last number assigned to a vertex
	 *  U is the father of vertex v in the spanning tree being constructed
	 *  The graph to be searched is represented by adjacency lists A(v)
	 *  This will be implemented with a map vertex - list of edges 
	 */
	private void dfs(V v, V u){
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
				dfs(w, v);

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




	/**
	 * Checks if the adjacency structure is valid according to the following lemma:
	 * Let A(u) be the adjacency list of vertex u. Let u->v and u -> w be tree arcs
	 * with v occurring before w in A(u). Then u<w<v.
	 * @param adjacency
	 * @param numbering
	 * @return true if adjacency structure is valid, false otherwise 
	 */
	private boolean checkAdjacencyValidity(Map<V,List<E>> adjacency, int[] numbering, List<E> treeEdges){

		for (V u : adjacency.keySet()){
			int uIndex = vertices.indexOf(u);
			List<E> adjacent = adjacency.get(u);
			for (int i = 0; i < adjacent.size() - 1; i++){
				E e1 = adjacent.get(i);
				if(!treeEdges.contains(e1))
					continue;
				V v = e1.getOrigin() == u ? e1.getDestination() : e1.getOrigin();
				int vIndex = vertices.indexOf(v);
				for (int j = i + 1; j < adjacent.size(); j++){
					E e2 = adjacent.get(j);
					if(!treeEdges.contains(e2))
						continue;
					V w = e2.getOrigin() == u ? e2.getDestination() : e2.getOrigin();
					int wIndex = vertices.indexOf(w);
					if (!((numbering[uIndex] < numbering[wIndex]) && (numbering[wIndex] < numbering[vIndex])))
						return false;
				}
			}
		}
		return true;

	}

	
	private E onEstack(int index1, int index2){
		//TODO da li je dobro sa tim direkcijama
		for (int i = 0; i < estack.size(); i++){
			E e = estack.get(i);
			int[] directedNodes = getDirectedNodes(e, newnum);
			
			if (directedNodes[0] == index1 && directedNodes[1] == index2)
				return e;
		}
		
		return null;
		
	}

	private boolean firstEdgeOfAPath(E e){
		for (List<E> path : paths)
			if (path.get(0) == e)
				return true;
		return false;
	}

	private void printTStack(){
		System.out.println("TSTACK:");
		for (Triple t : tstack){
			System.out.println(t);
		}
	}

}
