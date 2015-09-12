package graph.properties.splitting;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.properties.components.SplitPair;
import graph.trees.DFSTree;
import graph.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	private Map<Integer, List<V>> lowpt1sMap = new HashMap<Integer, List<V>>();

	private Class edgeClass;

	private Logger log = Logger.getLogger(SeparationPairSplitting.class);


	public List<SplitPair<V, E>> findSeaparationPairs(Graph<V,E> graph,  Class edgeClass) throws AlgorithmErrorException{
		List<SplitPair<V, E>> separationPairs = new ArrayList<SplitPair<V, E>>();
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
		System.out.println(tree);
	
		constructAdjacencyLists(adjacency);


		s = null;
		m = size;

		//find vertex whose number is 1, start with it
		//that will be the previously selected root vertex
		List<List<E>> paths = new ArrayList<List<E>>();
		pathfiner(root, adjacency, paths, null);

		log.info("second dfs completed");

		System.out.println("CHECKING ADJACENCY: " + checkAdjacencyValidity(adjacency, newnum, treeEdges));

		if (!checkAdjacencyValidity(adjacency, newnum, treeEdges))
			throw new AlgorithmErrorException("Error: adjacency structure not valid");

		tree = new DFSTree<V,E>(root, newnum, treeEdges, fronds, vertices);

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

		//printDFSTree();

		
		findTypeOneSeparationPairs(separationPairs);
		findTypeTwoSeparationPairs(paths, separationPairs);



		return separationPairs;

	}

	/**
	 * If there are distinct vertices r!=a,b and s!=a,b such that b->r, lowpt1(r) = a, lowpt2(r)>=b
	 * and s is not a descendant of r, then (a,b) is a separation pair of type 1
	 * @return
	 */
	private List<SplitPair<V,E>> findTypeOneSeparationPairs(List<SplitPair<V, E>> separationPairs){
		List<Integer> containedBs = new ArrayList<Integer>();
		
		
		for (int a : lowpt1sMap.keySet()){

			int aIndex = inverseNumbering[a - 1];
			V aVert = vertices.get(aIndex);
			containedBs.clear();
			System.out.println("a: " + aVert);

			List<V> lowptList = lowpt1sMap.get(a); //all vertices whose lowpt1 = a - possible r
			if (lowptList == null)
				continue;

			for (V rVert : lowptList){
				if (rVert == aVert)
					continue;

				int rIndex = vertices.indexOf(rVert);
				int r = newnum[rIndex];

				System.out.println("r " + rVert);

				if (r == 1) // //all vertices are descendants of the first vertex, therefore there isn't s that satisfied the condition
					continue;

				int bIndex = father[rIndex];
				if (containedBs.contains(bIndex))
					continue;
				if (lowpt2[rIndex] >= newnum[bIndex]){

					V bVert = vertices.get(bIndex);
					System.out.println("b " + bVert);

					List<V> descendants = tree.allDescendantsOf(rVert, true); //TODO save all outgoing edges when creating dfs tree?

					if (vertices.size() - descendants.size() <= 2)// && !descendants.contains(aVert))
						continue;

					log.info("Detected separation pair (" + aVert + ", " + bVert + ")");
					SplitPair<V,E> newPair = new SplitPair<V,E>(aVert, bVert, 1);
					separationPairs.add(newPair);
					containedBs.add(bIndex);
				}
			}
		}

		return separationPairs;
	}

	/**
	 * If there is a vertex r!=b such that a->r-*>b; b is a first descendant of r (i.e. a,r and b lie on a common generated path);
	 * a!=1; every frond x-->y with r<=x<b has a<=y; every frond x-->y with a<y<b and b->w-*>x has 
	 * lowpt1(w)>=a, (a,b) is a separation pair of type 2
	 * @return
	 */
	private List<SplitPair<V,E>> findTypeTwoSeparationPairs(List<List<E>> paths, List<SplitPair<V, E>> separationPairs){


		List<V> rList = new ArrayList<V>();
		List<V> bList = new ArrayList<V>();
		
		for (List<E> path : paths){

			if (path.size() == 1)
				continue;

			//take an edge
			//assume it's a->r
			//go from there

			rList.clear();
			bList.clear();
			
			
			for (E e : path){

				int[] indexes = getDirectedNodes(e, newnum);
				int aIndex = indexes[0];
				int aNum = newnum[aIndex];
				if (aNum == 1)
					continue;

				int rIndex = indexes[1];
				int rNum = newnum[rIndex];
				V rVert = vertices.get(rIndex);
				V aVert = vertices.get(aIndex);

				V current = rVert;
				V bVert;
				for (int i = path.indexOf(e) + 1; i < path.size() - 1; i++){
					E anEdge = path.get(i);
					bVert = anEdge.getOrigin() == current ? anEdge.getDestination() : anEdge.getOrigin();
					int bNum = newnum[vertices.indexOf(bVert)];
					if (bNum == rNum)
						continue;
					
					
					System.out.println("Testing " + aVert + " " + bVert);
					boolean satisfiedAll = true;

					//check the back edges
					for (E backEdge : fronds){
						
						indexes = getDirectedNodes(backEdge, newnum);
						int xIndex = indexes[0];
						int yIndex = indexes[1];
						
						int xNum = newnum[xIndex];
						int yNum = newnum[yIndex];
						
						V xVert = vertices.get(xIndex);
						
						// r<=x<b has a<=y; 
						
						if (rNum <= xNum && xNum < bNum){
							if (aNum > yNum){
								satisfiedAll = false;
								break;
							}
						}
						
						if (aNum < yNum && yNum < bNum){ 
							//b->w-*>x
							for (V w : tree.directDescendantsOf(bVert)){
								int wIndex = vertices.indexOf(w);
								if (tree.allDescendantsOf(w, true).contains(xVert)){
									if (lowpt1[wIndex] < aNum){
										satisfiedAll = false;
										break;
									}
								}
							}
						}
						
					}
					if (satisfiedAll){
						log.info("separation pair "+ aVert + " " + bVert);
						SplitPair<V,E> newPair = new SplitPair<V,E>(aVert, bVert, 2);
						if (!separationPairs.contains(newPair))
							separationPairs.add(newPair);
					}
					
					current = bVert;
				}
				


			}
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

		//System.out.println("setting newnum " +  v + " = " +  newnum[vIndex]);

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
				if (highpt[newnum[wIndex] - 1] == 0) //-1 since numbering starts from 1, indexes from 1
					highpt[newnum[wIndex] - 1] = newnum[vIndex];
				//output current path
				System.out.println("output " + currentPath);
				s = null;
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



	private void printDFSTree(){

		for (V v : vertices){

			int vIndex = vertices.indexOf(v);

			System.out.println("Vertex " + v + " number = " + newnum[vIndex] + " (" + vertices.get(inverseNumbering[lowpt1[vIndex] - 1]) + ", " 
					+ vertices.get(inverseNumbering[lowpt2[vIndex] -1 ]) + ")");

		}
	}


}
