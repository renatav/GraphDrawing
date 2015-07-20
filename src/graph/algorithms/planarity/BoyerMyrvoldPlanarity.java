package graph.algorithms.planarity;

import graph.algorithms.numbering.DFSNumbering;
import graph.algorithms.numbering.Numbering;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.traversal.DFSTreeTraversal;
import graph.trees.DFSTree;
import graph.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class BoyerMyrvoldPlanarity<V extends Vertex, E extends Edge<V>> extends PlanarityTestingAlgorithm<V,E>{


	/**Map whose key is a vertex and value is a list of all block whith that vertex as root*/
	private Map<V, List<Block>> allBlocksWithRoot = new HashMap<V, List<Block>>();


	/**List of all blocks*/
	private List<Block> blocks = new ArrayList<Block>();

	/**
	 * Map of all blocks and their roots. When checking for pertinents roots of a vertex
	 * just check if it is a key in this map
	 */
	private Map<E, Map<V, Block>> pertinentBlocksForEdge = new HashMap<E, Map<V, Block>>();
	/**
	 * DFS tree formed from the graph which is being analyzed 
	 */
	private DFSTree<V,E> dfsTree;
	/**
	 * Map which for every edge stores a list of blocks which will need to be merged 
	 * when it is embedded	
	 */
	private Map<E, List<Block>> blocksToMergeWhenEmbedding = new HashMap<E, List<Block>>();


	private Map<V, List<V>> vertexChildListMap = new HashMap<V, List<V>>();
	private Map<V, Integer> vertexLowpointMap = new HashMap<V, Integer>();
	private Map<V, V> parentOfVertices = new HashMap<V, V>(); //key child, value parent

	private Logger log = Logger.getLogger(BoyerMyrvoldPlanarity.class);


	@Override
	public boolean isPlannar(Graph<V, E> graph) {

		//************************
		//preprocessing
		//************************

		log.info("Preprocessing started");

		DFSTreeTraversal<V, E> traversal = new  DFSTreeTraversal<V,E>(graph);
		dfsTree = traversal.formDFSTree(graph.getVertices().get(0));


		//equip each vertex with a list called separatedDFSChildList
		//which initially contains references to all DFS children of the vertex, sorted by
		//their lowpoint values.
		//separatedDFSChild list should only contain children which belong to different blocks
		//when two blocks are joined some children may be removed from this list

		for (V v : graph.getVertices()){
			List<V> separatedDFSChildList = new ArrayList<V>();
			for (V child : dfsTree.directDescendantsOf(v)){
				separatedDFSChildList.add(child);
				vertexLowpointMap.put(child, dfsTree.lowpt(child));
				parentOfVertices.put(child, v); // v is parent of child
			}

			Collections.sort(separatedDFSChildList, new Comparator<V>(){

				@Override
				public int compare(V o1, V o2) {

					if (vertexLowpointMap.get(o1) > vertexLowpointMap.get(o2))
						return 1;
					else if (vertexLowpointMap.get(o1) < vertexLowpointMap.get(o2))
						return -1;
					else
						return 0;
				}
			});

			vertexChildListMap.put(v, separatedDFSChildList);

		}

		log.info("Preprocessing finished");

		//***************************************
		//Preprocessing finished
		//***************************************

		//*****************************************
		//Perform the algorithm
		//*****************************************

		Numbering<V,E> numbering =  new DFSNumbering<V, E>(dfsTree);

		for (V v : numbering.getOrder()){


			System.out.println("processing " + v + " " + dfsTree.getIndex(v));

			//for each DFS child c of v in G
			//Embed tree edge (vc, c) as a biconnected component in G
			for (E e : dfsTree.allOutgoingTreeEdges(v)){
				formBlock(e);
			}

			List<E> incomingBackEdges = dfsTree.allIncomingBackEdges(v);

			for (E back : incomingBackEdges){
				walkup(v, back);
			}
			
			for (E back : incomingBackEdges){
				walkdown(v, back);
			}
			
			//for each back edge of G incident to v and a descendant w
			//Walkup(G ˜, v, w)

			//for each DFS child c of v in G
			//Walkdown(G ˜, vc)

			//for each back edge of G incident to v and a descendant w
			//if (vc, w) ∈ / G ˜
			//IsolateKuratowskiSubgraph(G ˜, G, v)
			//return (NONPLANAR, G ˜)

		}



		/*
		//initially, split graph into blocks, where each block corresponds to an edge of the dfs tree
		formBlocks();

		for (V v : numbering.getOrder()){
			log.info("Processing node: " + v);
			List<E> incomingBackEdges = dfsTree.allIncomingBackEdges(v);

			//if there are no incoming back edges, don't process vertex
			if (incomingBackEdges.isEmpty())
				continue;

			//else - decode how to embed such edge

			//phase 1: path searching
			//suppose that some back edges enter v from u1...uk
			//for each uj, k = 1..k
			//the algorithms searches for a path from uj to v with some specific properties

			for (E e : incomingBackEdges){
				//find vertex u
				V u = e.getOrigin() == v ? e.getDestination() : e.getOrigin();

			}


		}cv
		 */

		return false;
	}


	//the purpose of the Walkup is to identify vertices and biconnected
	//components that are pertinent due to the given back edge (v, w).
	private void walkup(V v, E backEdge){


		V w = backEdge.getOrigin() == v ? backEdge.getDestination() : backEdge.getOrigin();

		List<Block> blocksToBeJoined = new ArrayList<Block>();
		blocksToMergeWhenEmbedding.put(backEdge, blocksToBeJoined);

		Map<V, Block> pertinentBlocksMap = new HashMap<V, Block>();
		pertinentBlocksForEdge.put(backEdge, pertinentBlocksMap);


		//find dfs tree path between vertices connected with the back edge
		List<V> dfsTreePath = dfsTree.treePathBetween(v, w);
		System.out.println("Path betwee : " + v + " and  " + w + " is :" + dfsTreePath );


		for (V pathVertex : dfsTreePath){

			//check for blocks on the path which contain the vertex
			System.out.println("Checking for vertex " + pathVertex);


			//check for blocks with path vertex as root
			List<Block> rootsBlocks = allBlocksWithRoot.get(pathVertex);
			if (rootsBlocks == null && pathVertex != dfsTreePath.get(0))
				continue;

			if (rootsBlocks != null){
				for (Block b : rootsBlocks){

					E rootEdge = b.getRootEdge();
					//check if root's child is on the path
					V other = rootEdge.getOrigin() == b.getRoot() ? rootEdge.getDestination()  : rootEdge.getOrigin();
					if (!dfsTreePath.contains(other))
						continue;

					if (!blocksToBeJoined.contains(b))
						blocksToBeJoined.add(b);

					pertinentBlocksMap.put(pathVertex, b);
					break;
				}
			}
			else{
				List<Block> blocksWhichContainVertex = blocksWhichContainVertex(pathVertex);
				//since it's not root, there should be only one block which contains it
				blocksToBeJoined.add(blocksWhichContainVertex.get(0));
			}

		}

		System.out.println(blocksToBeJoined);
		System.out.println(pertinentBlocksForEdge);
	}



	private List<Block> blocksWhichContainVertex(V v){

		List<Block> ret = new ArrayList<Block>();
		for (Block b : blocks)
			if (b.getVertices().contains(v))
				ret.add(b);

		return ret;
	}

	private boolean walkdown(V v, E backEdge){

		
		//!! pertinentBlocksForEdge treba obratiti paznju na to da se to sve moze menjati po 
		//embeddovanju ivice...
		
		//find block whose root is v and which leads to the end of the back edge
		Block current = pertinentBlocksForEdge.get(backEdge).get(v);
		List<Block> blocksToBeJoined = new ArrayList<Block>();
		List<Block> toBeFlipped = new ArrayList<Block>();
		
		V endpoint = backEdge.getOrigin() == v ? backEdge.getDestination() : backEdge.getOrigin();


		boolean direction = true;
		boolean end = false;

		while (!end){

			
			System.out.println(current);
			blocksToBeJoined.add(current);
			Block nextBlock = null;


			//analyze block
			//go in one direction, if extremely active vertex is reached, go the other way
			//until a vertex which leads to another block is reached
			boolean changeDirection = false;

			if (direction){
				for (int i = 1; i < current.getBoundaryVertices().size(); i++){ //skip root
					V currentBoundary = current.getBoundaryVertices().get(i);
					if (extremelyActive(currentBoundary, v)){
						changeDirection = true;
						break;
					}
					if (pertinentBlocksForEdge.get(backEdge).get(currentBoundary) != null){
						nextBlock = pertinentBlocksForEdge.get(backEdge).get(currentBoundary);
						break;
					}
					if (currentBoundary == endpoint){
						end = true;
						break;
					}
				}
			}
			else{
				for (int i = current.getBoundaryVertices().size() - 1; i > 0 ; i--){
					V currentBoundary = current.getBoundaryVertices().get(i);
					if (extremelyActive(currentBoundary, v)){
						changeDirection = true;
						break;
					}
					if (pertinentBlocksForEdge.get(backEdge).get(currentBoundary) != null){
						nextBlock = pertinentBlocksForEdge.get(backEdge).get(currentBoundary);
						break;
					}
					if (currentBoundary == endpoint){
						end = true;
						break;
					}
				}
			}

			if (changeDirection){
				direction = !direction;
				if (direction){
					for (int i = current.getBoundaryVertices().size() - 1; i > 0 ; i--){
						V currentBoundary = current.getBoundaryVertices().get(i);
						if (extremelyActive(currentBoundary, v)){
							return false;
						}
						if (pertinentBlocksForEdge.get(backEdge).get(currentBoundary) != null){
							nextBlock = pertinentBlocksForEdge.get(backEdge).get(currentBoundary);
							break;
						}
						if (currentBoundary == endpoint){
							end = true;
							break;
						}
					}
				}
				else{
					for (int i = 1; i < current.getBoundaryVertices().size(); i++){
						V currentBoundary = current.getBoundaryVertices().get(i);
						if (extremelyActive(currentBoundary, v)){
							return false;
						}
						if (pertinentBlocksForEdge.get(backEdge).get(currentBoundary) != null){
							nextBlock = pertinentBlocksForEdge.get(backEdge).get(currentBoundary);
							break;
						}
						if (currentBoundary == endpoint){
							end = true;
							break;
						}
					}
				}
			}
			
			if (changeDirection)
				toBeFlipped.add(current);

			current = nextBlock;

		}



		//ideja je da idemo, krecemo sre kroz listu napred ili nazad, da ne naidjemo na active
		//ako se ne moze izbeci -> stop
		return true;

	}


	/*
	   A vertex w is externally active during the processing of v if w either has a least ancestor less than
		v or if the first element in the separatedDFSChildList of w has a lowpoint less
		than v.
	 */
	private boolean extremelyActive(V w, V v){

		V leastAncestor = dfsTree.leastAncestor(w);
			
		if ((leastAncestor != null && dfsTree.getIndex(leastAncestor) < dfsTree.getIndex(v)) || 
				(vertexChildListMap.get(w).size() > 0 && dfsTree.getIndex(vertexChildListMap.get(w).get(0)) < dfsTree.getIndex(v)))
			return true;

		return false;
	}



	private void formBlock(E treeEdge){

		Block block = new Block();
		block.addVertex(treeEdge.getOrigin(), true);
		block.addVertex(treeEdge.getDestination(), true);
		block.addEdge(treeEdge, true);
		blocks.add(block);
		V root = block.getRoot();
		List<Block> rootsBlocks = allBlocksWithRoot.get(root);

		if (rootsBlocks == null){
			rootsBlocks = new ArrayList<Block>();
			allBlocksWithRoot.put(root, rootsBlocks);
		}
		rootsBlocks.add(block);
		System.out.println("Formed block: " + block);
	}


	private void formBlocks(){

		for (E treeEdge : dfsTree.getTreeEdges()){
			formBlock(treeEdge);
		}

	}

	private Block mergeBlocks(Block b1, Block b2){

		V root1 = b1.getRoot();
		V root2 = b2.getRoot();


		Block result, other;
		if (dfsTree.getIndex(root1) < dfsTree.getIndex(root2)){
			result = b1;
			other = b2;
		}
		else{
			result = b2;
			other = b1;
		}

		for (V v : other.getVertices())
			if (v != other.getRoot()) //don't add root, it's already in the other block
				result.getVertices().add(v);

		result.getEdges().addAll(other.getEdges());

		//remove merged block
		blocks.remove(other.getRoot());

		return result;

	}

	public class Block{

		private List<V> vertices;
		private List<E> edges;
		private List<V> boundaryVertices;
		private List<E> boundaryEdges;
		private V root;
		private E rootEdge;

		private Map<V, List<V>> adjacencyListsMap = new HashMap<V, List<V>>();

		/*when sign is -1 it means that vertices in a subtree rooted by endpoint of the edge
		has inverse orientation
		A planar embedding for any biconnected component can be
		recovered at any time by imposing the orientation of the biconnected component root vertex on all vertices in the biconnected component. If the product of
		the signs along the tree path from a vertex to the biconnected component root
		vertex is -1, then the adjacency list of the vertex should be inverted.*/
		private Map<E, Integer> edgeSigns = new HashMap<E, Integer>();

		public Block() {
			vertices = new ArrayList<V>();
			edges = new ArrayList<E>();
			boundaryVertices = new ArrayList<V>();
			boundaryEdges = new ArrayList<E>();

		}


		public void addVertex(V v, boolean boundary){
			vertices.add(v);
			if (boundary)
				boundaryVertices.add(v);
			if (root == null || dfsTree.getIndex(v) < dfsTree.getIndex(root))
				root = v;
			adjacencyListsMap.put(v, new ArrayList<V>());
		}

		public void addEdge(E edge, boolean boundary){
			edges.add(edge);
			if (boundary)
				boundaryEdges.add(edge);
			if (edge.getOrigin() == root || edge.getDestination() == root)
				rootEdge = edge;
			adjacencyListsMap.get(edge.getOrigin()).add(edge.getDestination());
			adjacencyListsMap.get(edge.getDestination()).add(edge.getOrigin());
			edgeSigns.put(edge, 1);
		}

		public List<V> getVertices() {
			return vertices;
		}

		public void setVertices(List<V> vertices) {
			this.vertices = vertices;
		}

		public List<E> getEdges() {
			return edges;
		}

		public void setEdges(List<E> edges) {
			this.edges = edges;
		}

		public List<V> getBoundaryVertices() {
			return boundaryVertices;
		}

		public void setBoundaryVertices(List<V> boundaryVertices) {
			this.boundaryVertices = boundaryVertices;
		}

		public List<E> getBoundaryEdges() {
			return boundaryEdges;
		}

		public void setBoundaryEdges(List<E> boundaryEdges) {
			this.boundaryEdges = boundaryEdges;
		}


		public V getRoot() {
			return root;
		}

		public void setRoot(V root) {
			this.root = root;
		}

		public E getRootEdge() {
			return rootEdge;
		}

		public void setRootEdge(E rootEdge) {
			this.rootEdge = rootEdge;
		}

		@Override
		public String toString() {
			return "Block [vertices=" + vertices + ", edges=" + edges
					+ ", boundaryVertices=" + boundaryVertices
					+ ", boundaryEdges=" + boundaryEdges + ", root=" + root
					+ ", rootEdge=" + rootEdge + "]";
		}




	}

}
