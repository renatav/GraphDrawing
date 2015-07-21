package graph.algorithms.planarity;

import graph.algorithms.numbering.DFSNumbering;
import graph.algorithms.numbering.Numbering;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.enumerations.Direction;
import graph.traversal.DFSTreeTraversal;
import graph.trees.DFSTree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
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

	private Logger log = Logger.getLogger(BoyerMyrvoldPlanarity.class);


	@Override
	public boolean isPlannar(Graph<V, E> graph) {

		//************************
		//preprocessing
		//************************

		log.info("Preprocessing started");

		DFSTreeTraversal<V, E> traversal = new  DFSTreeTraversal<V,E>(graph);
		dfsTree = traversal.formDFSTree(graph.getVertices().get(0));
		boolean planar = true;


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
				planar = planar && walkdown(v, back);
			}

		//	for (E back : incomingBackEdges){
			//	walkdown(v, back);
			//}

			//for each back edge of G incident to v and a descendant w
			//Walkup(G ˜, v, w)

			//for each DFS child c of v in G
			//Walkdown(G ˜, vc)

			//for each back edge of G incident to v and a descendant w
			//if (vc, w) ∈ / G ˜
			//IsolateKuratowskiSubgraph(G ˜, G, v)
			//return (NONPLANAR, G ˜)

		}

		return planar;
	}


	//the purpose of the Walkup is to identify vertices and biconnected
	//components that are pertinent due to the given back edge (v, w).
	private void walkup(V v, E backEdge){

		
		System.out.println("/n");
		log.info("Walkup " + backEdge);
		
		System.out.println(blocks);

		V w = backEdge.getOrigin() == v ? backEdge.getDestination() : backEdge.getOrigin();

		List<Block> blocksToBeJoined = new ArrayList<Block>();
		blocksToMergeWhenEmbedding.put(backEdge, blocksToBeJoined);

		Map<V, Block> pertinentBlocksMap = new HashMap<V, Block>();
		pertinentBlocksForEdge.put(backEdge, pertinentBlocksMap);


		//find dfs tree path between vertices connected with the back edge
		List<V> dfsTreePath = dfsTree.treePathBetween(v, w);
		System.out.println("Path between : " + v + " and  " + w + " is :" + dfsTreePath );

		//TODO ovde proveriti da li ne nalazi nekada kada treba blok
		
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

		System.out.println("/n************walkdown***************");
		log.info("Walkdown " + v + " " + backEdge);
		printExternalyActive(v);

		//!! pertinentBlocksForEdge treba obratiti paznju na to da se to sve moze menjati po 
		//embeddovanju ivice...

		//find block whose root is v and which leads to the end of the back edge
		Block current = pertinentBlocksForEdge.get(backEdge).get(v);
		List<Block> blocksToBeJoined = new ArrayList<Block>();

		V endpoint = backEdge.getOrigin() == v ? backEdge.getDestination() : backEdge.getOrigin();


		//found the back edge ending vertex while traversing the structure
		boolean foundEnding = false;

		List<Block> blocksWhichContainEndpoint = blocksWhichContainVertex(endpoint);
		
		Direction currentDirection = Direction.CLOCKWISE;
		boolean first = true;

		while (!foundEnding){

			System.out.println("Processing block: " + current);
			
			List<V> traversedVerticesList = new ArrayList<V>();
			
			blocksToBeJoined.add(current);
			Block nextBlock = null;

			//analyze block
			//go in one direction, if extremely active vertex is reached, go the other way
			//until a vertex which leads to another block is reached
			boolean changeDirection = false;


			int index = 1;
			if (currentDirection == Direction.COUNTERCLOCKWISE)
				index = current.getBoundaryVertices().size() -1;

			while (true){ //simulating for loop in order to avoid writing the same code twice 

				V currentBoundary = current.getBoundaryVertices().get(index);
				traversedVerticesList.add(currentBoundary);
				
				if (externallyActive(currentBoundary, v)){
					changeDirection = true;
					System.out.println("found extremely active vertex " + v + " changing direction");
					break;
				}

				if (!blocksWhichContainEndpoint.contains(current)) //don't move to next block if this contains the endpoint
					if (pertinentBlocksForEdge.get(backEdge).get(currentBoundary) != null){
						nextBlock = pertinentBlocksForEdge.get(backEdge).get(currentBoundary);
						break;
					}

				if (currentBoundary == endpoint){
					foundEnding = true;
					break;
				}

				if (currentDirection == Direction.CLOCKWISE){
					index ++;
					if (index == current.getBoundaryVertices().size())
						break;
				}
				else{
					index --;
					if (index == 0)
						break;
				}
			}



			if (changeDirection){
				
				
				currentDirection = currentDirection == Direction.CLOCKWISE ? Direction.COUNTERCLOCKWISE : Direction.CLOCKWISE;

				index = 1;
				if (currentDirection == Direction.COUNTERCLOCKWISE)
					index = current.getBoundaryVertices().size() -1;
				
				traversedVerticesList.clear();

				while (true){

					V currentBoundary = current.getBoundaryVertices().get(index);
					traversedVerticesList.add(currentBoundary);
					
					if (externallyActive(currentBoundary, v)){
						return false;
					}

					if (!blocksWhichContainEndpoint.contains(current)) //don't move to next block if this contains the endpoint
						if (pertinentBlocksForEdge.get(backEdge).get(currentBoundary) != null){
							nextBlock = pertinentBlocksForEdge.get(backEdge).get(currentBoundary);
							break;
						}
					if (currentBoundary == endpoint){
						foundEnding = true;
						break;
					}

					if (currentDirection == Direction.CLOCKWISE){
						index ++;
						if (index == current.getBoundaryVertices().size())
							break;
					}
					else{
						index --;
						if (index == 0)
							break;
					}
				}
			}
			
			//update external face and flip if necessary
			if (changeDirection && !first)
				current.setExternalFaceVertices(traversedVerticesList, false);
			else
				current.setExternalFaceVertices(traversedVerticesList, true);

			current = nextBlock;

		}
		
		
		//now merge blocks
		System.out.println("Merging blocks: " + blocksToBeJoined);
		Block newBlock = mergeBlocks(blocksToBeJoined);
		//now embed the edge
		newBlock.addEdge(backEdge);
		
		System.out.println(newBlock);
		System.out.println(blocks);
		
		return true;

	}


	/*
	   A vertex w is externally active during the processing of v if w either has a least ancestor less than
		v or if the first element in the separatedDFSChildList of w has a lowpoint less
		than v.
	 */
	private boolean externallyActive(V w, V v){

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
		block.addEdge(treeEdge);
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


	private Block mergeBlocks(List<Block> blocksToBeJoined){
		
		Block result = new Block();
		
		for (Block block : blocksToBeJoined){
			
			if (result.getRoot() == null){
				result.setRoot(block.getRoot());
				result.setRootEdge(block.getRootEdge());
			}
			
			for (V v : block.getVertices()){
				if (!result.getVertices().contains(v)){
					if (block.getBoundaryVertices().contains(v))
						result.addVertex(v, true);
					else
						result.addVertex(v, false);
						
				}
			}
			
			result.getEdges().addAll(block.getEdges());
			
			blocks.remove(block);
			allBlocksWithRoot.get(block.getRoot()).remove(block);
		}
		
		blocks.add(result);
		allBlocksWithRoot.get(result.getRoot()).add(result);
		
		//update dfsSeparatedChildList
		//since some of the vertices aren't separated any more
		for (V v : result.getVertices()){
			
			List<V> separatedDFSChildList = vertexChildListMap.get(v);
			Iterator<V> separatedListIterator = separatedDFSChildList.iterator();
			while (separatedListIterator.hasNext()){
				V next = separatedListIterator.next();
				if (result.getVertices().contains(next))
					separatedListIterator.remove();
			}
		}
		
		return result;
		
	}
	
	private void printExternalyActive(V v){
		System.out.println("Listing externally active");
		for (V w : dfsTree.getVertices())
			if (externallyActive(w, v))
				System.out.println(w);
	}
	

	public class Block{

		private List<V> vertices;
		private List<E> edges;
		private List<V> boundaryVertices;
		private V root;
		private E rootEdge;


		public Block() {
			vertices = new ArrayList<V>();
			edges = new ArrayList<E>();
			boundaryVertices = new ArrayList<V>();
		}


		public void addVertex(V v, boolean boundary){
			vertices.add(v);
			if (boundary)
				boundaryVertices.add(v);
			if (root == null || dfsTree.getIndex(v) < dfsTree.getIndex(root))
				root = v;
		}

		public void addEdge(E edge){
			edges.add(edge);
			if (edge.getOrigin() == root || edge.getDestination() == root)
				rootEdge = edge;
		}
		
		/**
		 * Updates external faces vertices by either setting the passed list as face vertices or
		 * by setting those which are not in the list (except for the fist and last vertex)
		 *  based on the value of <code>containing</code>
		 * @param newVertices
		 * @param containing
		 */
		public void setExternalFaceVertices(List<V> newVertices, boolean containing){
			if (containing){
				boundaryVertices.clear();
				boundaryVertices.addAll(newVertices);
			}
			else{
				for (V v : boundaryVertices){
					if (!newVertices.contains(v))
						boundaryVertices.add(v);
				}
				boundaryVertices.add(newVertices.get(newVertices.size() - 1));
			}
			
			boundaryVertices.add(0, root);
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
					+  "root=" + root;
		}

	}

}
