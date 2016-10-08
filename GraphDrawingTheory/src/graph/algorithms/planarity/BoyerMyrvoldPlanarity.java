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
import java.util.Map.Entry;

import org.apache.log4j.Logger;


/**
 * The Boyer-Myrvold algorithm is a planarity testing algorithm which uses reverse DFS order as numbering
 *  of the vertices of G. The general strategy is that of explicitly maintaining a 
 *  "flexible" planar embedding of each connected component of Gi with the outer vertices on the outer face.
 *  This embedding is “flexible” in the sense that each block can be flipped in constant time,
 *  whatever its size, while the permutation of the blocks around cutvertices is left undecided. 
 */

public class BoyerMyrvoldPlanarity<V extends Vertex, E extends Edge<V>> extends PlanarityTestingAlgorithm<V,E>{


	/**Map whose key is a vertex and value is a list of all block with that vertex as root*/
	private Map<V, List<Block>> allBlocksWithRoot = new HashMap<V, List<Block>>();


	/**List of all blocks*/
	private List<Block> blocks = new ArrayList<Block>();

	/**
	 * Map of all blocks and their roots. When checking for pertinent roots of a vertex
	 * just check if it is a key in this map
	 */
	private Map<E, Map<V, Block>> pertinentBlocksForEdge = new HashMap<E, Map<V, Block>>();

	/**
	 * DFS tree formed from the graph which is being analyzed 
	 */
	private DFSTree<V,E> dfsTree;

	/**Map with a vertex as a key and lists of its dfs children as values*/
	private Map<V, List<V>> vertexChildListMap = new HashMap<V, List<V>>();

	/**Map which pairs a vertex with its lowpoint index*/
	private Map<V, Integer> vertexLowpointMap = new HashMap<V, Integer>();

	/**List of endpoints of back edges used during the walkdown phase*/
	private List<V> endpoins = new ArrayList<V>();

	/**List of externally active vertices used during the walkdown phase*/
	private List<V> externallyActive;
	
	private boolean debug = false;
	
	private Graph<V,E> graph;

	private Logger log = Logger.getLogger(BoyerMyrvoldPlanarity.class);


	@Override
	public boolean isPlannar(Graph<V, E> graph) {

		this.graph = graph;
		boolean directed = graph.isDirected();
		graph.setDirected(false);
		
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

		if (debug)
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

		if (debug)
			log.info("Preprocessing finished");

		//***************************************
		//Preprocessing finished
		//***************************************

		//*****************************************
		//Perform the algorithm
		//*****************************************

		Numbering<V,E> numbering =  new DFSNumbering<V, E>(dfsTree);

		for (V v : numbering.getOrder()){


			//System.out.println("PROCESSING VERTEX + " + v);


			//for each DFS child c of v in G
			//Embed tree edge (vc, c) as a biconnected component in G
			for (E e : dfsTree.allOutgoingTreeEdges(v)){
				formBlock(e);
			}

			if (!processVertex(v)){
				graph.setDirected(directed);
				return false;
			}


		}

		//if the implementation needed to be expanded
		//to embed all back edges which can be embedded and to isolate
		//Kuratowski subgraph
		//don't return false, just keep going
		
		if (debug)
			log.info(blocks);
		
		graph.setDirected(directed);
		return true;
	}


	private boolean processVertex(V v){

		List<E> incomingBackEdges = dfsTree.allIncomingBackEdges(v);

		//the Walkdown is invoked by the core planarity algorithm once for each DFS child
		//c of the vertex v to embed the back
		//edges from v to descendants of c.
		for (V child : dfsTree.directDescendantsOf(v)){

			//if the vertex has incoming back edges
			if (incomingBackEdges.size() > 0){
				List<E> edgesToEmbed = new ArrayList<E>();
				for (E back : incomingBackEdges){
					V endpoint = back.getOrigin() == v ? back.getDestination() : back.getOrigin();
					if (dfsTree.allDescendantsOf(child, false).contains(endpoint)){
						edgesToEmbed.add(back);
					}
				}

				if (edgesToEmbed.size() > 0){

					endpoins.clear();
					externallyActive = listExternalyActive(v);


					//set endpoints list
					for (E backEdge : edgesToEmbed){
						V endpoint = backEdge.getOrigin() == v ? backEdge.getDestination() : backEdge.getOrigin();
						endpoins.add(endpoint);
					}



					for (E backEdge : edgesToEmbed)
						walkup(v, backEdge);

					
					while (edgesToEmbed.size() > 0){
						
						//perform walkup for every edge
						//since blocks are changed when merging and 
						//some kind of processing would have to be performed in any case
						sortEdgesToEmbed(edgesToEmbed);
						
						E backEdge = edgesToEmbed.get(0);
						//now perform walkdown and embed the edge
						//if that is not possible, return false
						if (!walkdown(v, child, backEdge, edgesToEmbed))
							return false;
						edgesToEmbed.remove(backEdge);
					}
				}
			}
		}
		return true;
	}


	private void sortEdgesToEmbed(List<E> edgesToEmbed){

		//sort the embedding order
		//if the edges are chosen randomly
		//it is possible that some of them won't be embedded
		//resulting in algorithm's failure
		//if there are more relevant vertices in a block, not just back edges
		//make sure that they are not left out of the external face
		//basically, traversing the external face of the relevant block and searching for 
		//ednpoints and pertinent vertices

		//System.out.println(edgesToEmbed);
		Collections.sort(edgesToEmbed, new Comparator<E>() {

			@Override
			public int compare(E o1, E o2) {


				V endpoint1 = dfsTree.getIndex(o1.getOrigin()) > dfsTree.getIndex(o1.getDestination()) ? o1.getOrigin() : o1.getDestination();
				V endpoint2 = dfsTree.getIndex(o2.getOrigin()) > dfsTree.getIndex(o2.getDestination()) ? o2.getOrigin() : o2.getDestination();


				Map<V,Block> pertinent1 = pertinentBlocksForEdge.get(o1);
				Map<V,Block> pertinent2 = pertinentBlocksForEdge.get(o2);

				//System.out.println("pertinent1 " + pertinent1);
				//System.out.println("pertinent2 " + pertinent2);

				List<Block> overlappingBlocks = new ArrayList<Block>(pertinent1.values());
				overlappingBlocks.retainAll(pertinent2.values());
				//System.out.println("overlapping blocks: " + overlappingBlocks);


				if (overlappingBlocks.size() > 0){

					//find block with lowest dfi (where paths part)
					Block block = overlappingBlocks.get(0);
					for (Block overlapping : overlappingBlocks)
						if (dfsTree.getIndex(overlapping.getRoot()) > dfsTree.getIndex(block.getRoot()))
							block = overlapping;

					//	System.out.println("RELEVANT BLOCK " + block);

					int index1 = -1, index2 = -1;
					int stopIndex = -1;
					for (int i = 1; i < block.getBoundaryVertices().size(); i++){
						V boundary = block.getBoundaryVertices().get(i);
						if (externallyActive.contains(boundary)){
							stopIndex = i;
							break;
						}
						if (boundary == endpoint1 || pertinent1.containsKey(boundary))
							index1 = i;
						if (boundary == endpoint2 || pertinent2.containsKey(boundary))
							index2 = i;
						if (index1 != -1 && index2 != -1)
							break;
					}

					if (stopIndex != -1){
						for (int i = block.getBoundaryVertices().size() - 1; i > stopIndex; i--){
							V boundary = block.getBoundaryVertices().get(i);

							if (boundary == endpoint1 || pertinent1.containsKey(boundary))
								index1 = i;
							if (boundary == endpoint2 || pertinent2.containsKey(boundary))
								index2 = i;
							if (index1 != -1 && index2 != -1)
								break;
						}
					}


					if (stopIndex == -1){
						if (index1 > index2){
							return 1;
						}


						if (index1 < index2){
							return -1;
						}
					}
					else{
						if (index1 > index2){
							return -1;
						}


						if (index1 < index2){
							return 1;
						}
					}

					if (dfsTree.getIndex(endpoint1) > dfsTree.getIndex(endpoint2))
						return 1;

					if (dfsTree.getIndex(endpoint1) < dfsTree.getIndex(endpoint2))
						return -1;

					return 0;

				}

				if (dfsTree.getIndex(endpoint1) > dfsTree.getIndex(endpoint2))
					return 1;

				if (dfsTree.getIndex(endpoint1) < dfsTree.getIndex(endpoint2))
					return -1;

				return 0;
			}
		});

	}




	//the purpose of the Walkup is to identify vertices and biconnected
	//components that are pertinent due to the given back edge (v, w).
	private void walkup(V v, E backEdge){

		if (debug)
			log.info("Walkup " + backEdge);

		V w = backEdge.getOrigin() == v ? backEdge.getDestination() : backEdge.getOrigin();

		Map<V, Block> pertinentBlocksMap = new HashMap<V, Block>();
		pertinentBlocksForEdge.put(backEdge, pertinentBlocksMap);


		//find dfs tree path between vertices connected with the back edge
		List<V> dfsTreePath = dfsTree.treePathBetween(v, w);
		//System.out.println("DFS path " + dfsTreePath);


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

	/*in this phase it is determined how to embed an edge
	  and blocks are joined
	  it is important to keep externally active vertices
	  on the external face*/
	private boolean walkdown(V v, V child, E backEdge,  List<E> backEdges){

		if (debug)
			log.info("Walkdown " + v + " " + backEdge);

		if (debug){
			log.info("CURRENT BLOCKS " + blocks);
			log.info("PERTINENT " + pertinentBlocksForEdge.get(backEdge));
			log.info("Externally active " + externallyActive);
		}

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

			if (debug)
				log.info("Processing block: " + current);

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

				//System.out.println("Current boundary: " + currentBoundary);


				if (!blocksWhichContainEndpoint.contains(current)) //don't move to next block if this contains the endpoint
					if (pertinentBlocksForEdge.get(backEdge).get(currentBoundary) != null && 
					pertinentBlocksForEdge.get(backEdge).get(currentBoundary) != current){
						//System.out.println("FOUND PERTINENT " + currentBoundary);
						nextBlock = pertinentBlocksForEdge.get(backEdge).get(currentBoundary);
						break;
					}

				if (currentBoundary == endpoint){
					//System.out.println("endpoint");
					foundEnding = true;
					break;
				}

				if (externallyActive.contains(currentBoundary)){
					changeDirection = true;
					//System.out.println("found extremely active vertex " + currentBoundary + " changing direction");
					break;
				}


				if (currentDirection == Direction.CLOCKWISE){
					index ++;
					if (index == current.getBoundaryVertices().size()){
						break;
					}
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
			//this is when the second traversal is performed:
			//check if there are externally active on the other side
			//but it wasn't traversed since the correct direction was chosen
			//also check if there are other endpoints (to make sure that it will be possible to embed them as well
			//and if there is no reason to flip at that point
			//see if there are any pertinent vertices on the other side - leave them on the external face


			V last = traversedVerticesList.get(traversedVerticesList.size() - 1);

			if (changeDirection){
				if (currentDirection == Direction.COUNTERCLOCKWISE)
					current.setExternalFace(last, Direction.CLOCKWISE);
				else
					current.setExternalFace(last, Direction.COUNTERCLOCKWISE);

			}
			else{

				boolean flip = false;

				int indexOfEnd = current.getBoundaryVertices().
						indexOf(traversedVerticesList.get(traversedVerticesList.size() - 1));

				boolean hasExternallyActive = false;
				boolean hasOtherToBeEmbedded = false;
				boolean hasPertiment = false;

				if (currentDirection == Direction.CLOCKWISE){

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
						if (allBlocksWithRoot.get(vert) != null && allBlocksWithRoot.get(vert).size() > 0){
							hasPertiment = true;
						}
					}
				}
				else{
					for (int i = 1; i < indexOfEnd; i++){
						V vert = current.getBoundaryVertices().get(i);
						if (externallyActive.contains(vert)){
							hasExternallyActive = true;
							break;
						}
						if (endpoins.contains(vert)){
							hasOtherToBeEmbedded = true;
							break;
						}
						if (allBlocksWithRoot.get(vert) != null && allBlocksWithRoot.get(vert).size() > 0){
							hasPertiment = true;
						}
					}
				}

				if (debug){
					log.info("Has externally active: " + hasExternallyActive);
					log.info("Has other to be embedded " + hasOtherToBeEmbedded);
				}
				flip = hasExternallyActive || hasOtherToBeEmbedded;

				if (!flip){
					boolean traversedPertinent = false;
					for(V vert : traversedVerticesList){
						if (vert == last)
							break;
						if (allBlocksWithRoot.get(vert) != null && allBlocksWithRoot.get(vert).size() > 0){
							traversedPertinent = true;
							//System.out.println(vert  + " je pertinent");
							break;
						}
					}
					if (!traversedPertinent && hasPertiment)
						flip = true;
				}


				//System.out.println("FLIP " + flip);

				Direction toSet = currentDirection;
				if (flip){
					if (currentDirection == Direction.CLOCKWISE)
						toSet = Direction.COUNTERCLOCKWISE;
					else
						toSet = Direction.CLOCKWISE;
				}

				current.setExternalFace(last, toSet);
			}


			//System.out.println("Blok posle: " + current);

			current = nextBlock;
			if (first)
				first = false;

		}



		//now merge blocks
		//and set the external face of the new block properly
		if (blocksToBeJoined.size() > 1){
			if (debug)
				log.info("Merging blocks: " + blocksToBeJoined);
			Block newBlock = mergeBlocks(blocksToBeJoined);

			newBlock.addEdge(backEdge);
			
			if (debug)
				log.info(newBlock);
			endpoins.remove(endpoint);
			
			//update pertinent map
			for (E edge : backEdges){
				Map<V, Block> pertinentForEdge = pertinentBlocksForEdge.get(edge);
				Iterator<Entry<V,Block>> entries = pertinentForEdge.entrySet().iterator();
				boolean replaced = false;
				while (entries.hasNext()){
					Entry<V, Block> entry = entries.next();
					if (blocksToBeJoined.contains(entry.getValue())){
							entries.remove();
							replaced = true;
					}
				}
				if (replaced)
					pertinentForEdge.put(newBlock.getRoot(), newBlock);
			}
			

		}
		
		pertinentBlocksForEdge.remove(backEdge);
		backEdges.remove(backEdge);

		return true;

	}

	/**
	 * @param v vertex
	 * @return List of block which contain the vertex
	 */
	private List<Block> blocksWhichContainVertex(V v){

		List<Block> ret = new ArrayList<Block>();
		for (Block b : blocks)
			if (b.getVertices().contains(v))
				ret.add(b);

		return ret;
	}


	/**
	   A vertex w is externally active during the processing of v if w either has a least ancestor less than
		v or if the first element in the separatedDFSChildList of w has a lowpoint less than v.
		Checks if vertex w is externally active during the processing of v
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
	
	public List<V> getOutsideFace(){
		List<V> ret = new ArrayList<V>();
		for (Block b : blocks)
			ret.addAll(b.getBoundaryVertices());
		return ret;
	}
	
	public List<E> getExternalFaceEdges(){
		List<V> externalFaceVertices = getOutsideFace();
		List<E> externalFace = new ArrayList<E>();
		for (int i = 0; i < externalFaceVertices.size(); i++){
			V v1 = externalFaceVertices.get(i);
			V v2;
			if (i == externalFaceVertices.size() - 1)
				v2 = externalFaceVertices.get(0);
			else
				v2 = externalFaceVertices.get(i + 1);
			externalFace.add(graph.edgeBetween(v1, v2));
		}
		return externalFace;
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
					+  "root=" + root + "\n";
		}

	}

}
