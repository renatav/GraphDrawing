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


	/**Map whose key is a vertex and value is a list of all block with that vertex as root*/
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

	private Map<V, List<V>> vertexChildListMap = new HashMap<V, List<V>>();
	private Map<V, Integer> vertexLowpointMap = new HashMap<V, Integer>();

	private Logger log = Logger.getLogger(BoyerMyrvoldPlanarity.class);


	@Override
	public boolean isPlannar(Graph<V, E> graph) {

		//************************
		//preprocessing
		//************************

		//clearing all collections, in case the method was not called for the first time
		//no need to instantiate the class again
		blocks.clear();
		allBlocksWithRoot.clear();
		pertinentBlocksForEdge.clear();
		vertexChildListMap.clear();
		vertexLowpointMap.clear();

		log.info("Preprocessing started");

		DFSTreeTraversal<V, E> traversal = new  DFSTreeTraversal<V,E>(graph);
		dfsTree = traversal.formDFSTree(graph.getVertices().get(0));

		System.out.println("DFS TREE");
		System.out.println(dfsTree);

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


			System.out.println("PROCESSING VERTEX + " + v);

			//System.out.println("processing " + v + " " + dfsTree.getIndex(v));

			//for each DFS child c of v in G
			//Embed tree edge (vc, c) as a biconnected component in G
			for (E e : dfsTree.allOutgoingTreeEdges(v)){
				formBlock(e);
			}


			List<E> incomingBackEdges = dfsTree.allIncomingBackEdges(v);


			for (E back : incomingBackEdges)
				walkup(v, back);


			//the Walkdown is invoked by the core planarity algorithm once for each DFS child
			//c of the vertex v to embed the back
			//edges from v to descendants of c.
			for (V child : dfsTree.directDescendantsOf(v)){
				if (incomingBackEdges.size() > 0){
					List<E> edgesToEmbed = new ArrayList<E>();
					for (E back : incomingBackEdges){
						V endpoint = back.getOrigin() == v ? back.getDestination() : back.getOrigin();
						if (dfsTree.allDescendantsOf(child, false).contains(endpoint)){
							edgesToEmbed.add(back);
						}
					}
					if (edgesToEmbed.size() > 0)
						if (!walkdown(v, child, edgesToEmbed))
							return false;
				}


			}
			//if the implementation needed to be expanded
			//to embed all back edges which can be embedded and to isolate
			//Kuratowski subgraph
			//don't return false, just keep going
		}
		return true;
	}



	//the purpose of the Walkup is to identify vertices and biconnected
	//components that are pertinent due to the given back edge (v, w).
	private void walkup(V v, E backEdge){

		log.info("Walkup " + backEdge);

		V w = backEdge.getOrigin() == v ? backEdge.getDestination() : backEdge.getOrigin();

		Map<V, Block> pertinentBlocksMap = new HashMap<V, Block>();
		pertinentBlocksForEdge.put(backEdge, pertinentBlocksMap);


		//find dfs tree path between vertices connected with the back edge
		List<V> dfsTreePath = dfsTree.treePathBetween(v, w);
		//	System.out.println("DFS path " + dfsTreePath);

		for (V pathVertex : dfsTreePath){

			//check for blocks on the path which contain the vertex
			//System.out.println("Checking for vertex " + pathVertex);

			//check for blocks with path vertex as root
			List<Block> rootsBlocks = allBlocksWithRoot.get(pathVertex);

			if (rootsBlocks == null && pathVertex != dfsTreePath.get(0))
				continue;

			if (rootsBlocks != null){
				for (Block b : rootsBlocks){

					//check if root has a dfs child in this block and on the path
					List<V> dfsChildren = dfsTree.directDescendantsOf(pathVertex);
					boolean contains = false;
					for (V child : dfsChildren)
						if (dfsTreePath.contains(child) && b.getVertices().contains(child)){
							contains = true;
							break;
						}
					if (!contains)
						continue;

					pertinentBlocksMap.put(pathVertex, b);
					break;
				}
			}
		}
	}



	private List<Block> blocksWhichContainVertex(V v){

		List<Block> ret = new ArrayList<Block>();
		for (Block b : blocks)
			if (b.getVertices().contains(v))
				ret.add(b);

		return ret;
	}

	private boolean walkdown(V v, V child, List<E> backEdges){

		//in this phase it is determined how to embed an edge
		//and blocks are joined
		//it is important to keep externally active vertices
		//on the external face

		//other endpoint vertices must stay on the external face!

		List<V> endpoins = new ArrayList<V>();

		for (E backEdge : backEdges){
			V endpoint = backEdge.getOrigin() == v ? backEdge.getDestination() : backEdge.getOrigin();
			endpoins.add(endpoint);
		}


		for (E backEdge : backEdges){

			System.out.println("CURRENT BLOCKS " + blocks);

			log.info("Walkdown " + v + " " + backEdge);

			List<V> externallyActive = listExternalyActive(v);
			System.out.println("Externally active " + externallyActive);

			//find block whose root is v and which leads to the end of the back edge
			Block current = pertinentBlocksForEdge.get(backEdge).get(v);
			List<Block> blocksToBeJoined = new ArrayList<Block>();

			//found the back edge ending vertex while traversing the structure
			boolean foundEnding = false;

			V endpoint = backEdge.getOrigin() == v ? backEdge.getDestination() : backEdge.getOrigin();
			List<Block> blocksWhichContainEndpoint = blocksWhichContainVertex(endpoint);

			Direction currentDirection = Direction.CLOCKWISE;
			boolean first = true;

			List<V> traversedVerticesList = new ArrayList<V>();

			while (!foundEnding){

				System.out.println("Processing block: " + current);

				traversedVerticesList.clear();

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

					System.out.println("Current boundary: " + currentBoundary);


					if (!blocksWhichContainEndpoint.contains(current)) //don't move to next block if this contains the endpoint
						if (pertinentBlocksForEdge.get(backEdge).get(currentBoundary) != null && 
						pertinentBlocksForEdge.get(backEdge).get(currentBoundary) != current){
							nextBlock = pertinentBlocksForEdge.get(backEdge).get(currentBoundary);
							break;
						}

					if (currentBoundary == endpoint){
						System.out.println("endpoint");
						foundEnding = true;
						break;
					}

					if (externallyActive.contains(currentBoundary)){
						changeDirection = true;
						System.out.println("found extremely active vertex " + currentBoundary + " changing direction");
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

						//System.out.println("Current boundary: " + currentBoundary);

						traversedVerticesList.add(currentBoundary);


						if (!blocksWhichContainEndpoint.contains(current)) //don't move to next block if this contains the endpoint
							if (pertinentBlocksForEdge.get(backEdge).get(currentBoundary) != null && 
							pertinentBlocksForEdge.get(backEdge).get(currentBoundary) != current){
								nextBlock = pertinentBlocksForEdge.get(backEdge).get(currentBoundary);
								break;
							}
						if (currentBoundary == endpoint){
							foundEnding = true;
							break;
						}

						if (externallyActive.contains(currentBoundary)){
							System.out.println("found another externally active " + currentBoundary + " - not planar!");
							return false;
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

				//update external face and flip if necessary (to keep externally active on the external face)


				//now check if there are externally active on the other side
				//but it wasn't traversed since the correct direction was chosen


				V last = traversedVerticesList.get(traversedVerticesList.size() - 1);
				
				if (changeDirection)
					current.setExternalFace(last, Direction.CLOCKWISE);
				else{

					boolean flip = false;


					int indexOfEnd = current.getBoundaryVertices().
							indexOf(traversedVerticesList.get(traversedVerticesList.size() - 1));

					boolean hasExternallyActive = false;
					boolean hasOtherToBeEmbedded = false;
					boolean hasPertiment = false;

					for (int i = current.getBoundaryVertices().size() - 1; i > indexOfEnd; i--){
						V vert = current.getBoundaryVertices().get(i);
						if (externallyActive.contains(vert)){
							hasExternallyActive = true;
							break;
						}
						if (endpoins.contains(vert)){
							hasOtherToBeEmbedded = true;
							break;
						}
						if (allBlocksWithRoot.get(vert) != null){
							hasPertiment = true;
						}
					}

					flip = hasExternallyActive || hasOtherToBeEmbedded;

//					if (!flip){
//						boolean traversedPertinent = false;
//						for(V vert : traversedVerticesList){
//							if (vert == endpoint)
//								break;
//							if (allBlocksWithRoot.get(vert) != null){
//								traversedPertinent = true;
//								break;
//							}
//						}
//						if (!traversedPertinent && hasPertiment)
//							flip = true;
//					}


					System.out.println("FLIP " + flip);

					if (flip)
						current.setExternalFace(last, Direction.COUNTERCLOCKWISE);
					else
						current.setExternalFace(last, Direction.CLOCKWISE);
				}


				System.out.println("Blok posle: " + current);

				current = nextBlock;
				if (first)
					first = false;

			}


			//now merge blocks
			//and set the external face of the new block properly

			if (blocksToBeJoined.size() > 1){
				System.out.println("Merging blocks: " + blocksToBeJoined);
				Block newBlock = mergeBlocks(blocksToBeJoined);

				//update pertiment block maps for other edges
				for (E e : backEdges){
					if (e == backEdge)
						continue;
					Map<V, Block> toBeTraversed =  pertinentBlocksForEdge.get(e);
					boolean modified = false;
					for (Block joinedBlock : blocksToBeJoined){
						if (toBeTraversed.get(joinedBlock.getRoot()) == joinedBlock){
							toBeTraversed.remove(joinedBlock);
							modified = true;
						}
					}
					if (modified)
						toBeTraversed.put(newBlock.getRoot(), newBlock);


				}
				//now embed the edge
				newBlock.addEdge(backEdge);

				System.out.println(newBlock);
				//System.out.println(blocks);
			}
		}

		return true;

	}

	/*
	   A vertex w is externally active during the processing of v if w either has a least ancestor less than
		v or if the first element in the separatedDFSChildList of w has a lowpoint less than v.
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
		if (dfsTree.getIndex(treeEdge.getOrigin()) < dfsTree.getIndex(treeEdge.getDestination())){
			block.addVertex(treeEdge.getOrigin(), true);
			block.addVertex(treeEdge.getDestination(), true);
		}
		else{
			block.addVertex(treeEdge.getDestination(), true);
			block.addVertex(treeEdge.getOrigin(), true);
		}


		block.addEdge(treeEdge);
		blocks.add(block);
		V root = block.getRoot();
		List<Block> rootsBlocks = allBlocksWithRoot.get(root);

		if (rootsBlocks == null){
			rootsBlocks = new ArrayList<Block>();
			allBlocksWithRoot.put(root, rootsBlocks);
		}
		rootsBlocks.add(block);
	}


	private Block mergeBlocks(List<Block> blocksToBeJoined){

		Block result = new Block();

		for (Block block : blocksToBeJoined){

			if (result.getRoot() == null){
				result.setRoot(block.getRoot());
			}

			for (V v : block.getVertices()){
				if (!result.getVertices().contains(v))
					result.addVertex(v, false);
			}
			for (V v : block.getBoundaryVertices()){
				if (!result.getBoundaryVertices().contains(v))
					result.getBoundaryVertices().add(v);
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

	private List<V> listExternalyActive(V v){

		List<V> ret = new ArrayList<V>();

		for (V w : dfsTree.getVertices())
			if (externallyActive(w, v))
				ret.add(w);

		return ret;
	}


	public class Block{

		private List<V> vertices;
		private List<E> edges;
		private List<V> boundaryVertices;
		private V root;


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
		}


		public void setExternalFace(V endpoint, Direction direction){
			List<V> boundaryCopy = new ArrayList<V>(boundaryVertices);
			int indexOF = boundaryVertices.indexOf(endpoint);
			boundaryVertices.clear();
			boundaryVertices.add(boundaryCopy.get(0));
			if (direction == Direction.CLOCKWISE){
				for (int i = 1; i <= indexOF; i++){
					boundaryVertices.add(boundaryCopy.get(i));
				}
			}
			else{
				for (int i = boundaryCopy.size() - 1; i >= indexOF; i--)
					boundaryVertices.add(boundaryCopy.get(i));
			}
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

		@Override
		public String toString() {
			return "Block [vertices=" + vertices + ", edges=" + edges
					+ ", boundaryVertices=" + boundaryVertices
					+  "root=" + root;
		}

	}

}
