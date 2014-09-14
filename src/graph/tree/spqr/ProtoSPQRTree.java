package graph.tree.spqr;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.operations.GraphOperations;
import graph.properties.splitting.Block;
import graph.properties.splitting.SplitComponent;
import graph.properties.splitting.SplitPair;
import graph.properties.splitting.Splitting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProtoSPQRTree<V extends Vertex, E extends Edge<V>> extends Graph<TreeNode<V,TreeEdgeWithContent<V,E>>, Edge<TreeNode<V,TreeEdgeWithContent<V,E>>>>{

	/**
	 * Original graph - reference edge
	 */
	private Graph<V,E> gPrim;

	private E referenceEdge;

	/**
	 * Original graph. Must be biconnected
	 */
	private Graph<V,E> graph; 

	/**
	 * Root of the tree
	 */
	private TreeNode<V,TreeEdgeWithContent<V,E>> root;

	//TODO mozda napraviti neki AbstractTree

	public ProtoSPQRTree(Graph<V,E> graph, E referenceEdge){
		this.referenceEdge = referenceEdge;
		this.graph = graph;
		GraphOperations<V, E> operations = new GraphOperations<>();
		gPrim = operations.removeEdgeFromGraph(graph, referenceEdge);
		constructTree();

	}

	@SuppressWarnings("unchecked")
	private void constructTree(){

		Splitting<V, E> splitting = new Splitting<>();
		GraphOperations<V, TreeEdgeWithContent<V,E>> operations = new GraphOperations<>();
		V s = referenceEdge.getOrigin();
		V t = referenceEdge.getDestination();

		//trivial case
		if (gPrimIsASingleEdge()){ //is g prim is a single edge
			Skeleton<V,TreeEdgeWithContent<V,E>> skeleton = new Skeleton<>();
			
			for (V v : graph.getVertices())
				skeleton.addVertex(v);
			for (E e : graph.getEdges())
				skeleton.addEdge(new TreeEdgeWithContent<V,E>(e.getOrigin(), e.getDestination()));
			
			
			root = new TreeNode<V,TreeEdgeWithContent<V, E>>(NodeType.Q, skeleton);
			addVertex(root);
		}
		//series case
		else if (!gPrim.isBiconnected()){ //g prim is not a single edge and is not biconnected
			List<V> cutVertices = splitting.findAllCutVertices(gPrim);
			List<Block<V, E>> blocks = splitting.findAllBlocks(gPrim, cutVertices);
			List<V> vertices = new ArrayList<>(cutVertices);
			organizeBlocksAndVertices(s, t, vertices, blocks);

			//create root
			Graph<V,TreeEdgeWithContent<V,E>> cycle = operations.formCycleGraph(vertices, TreeEdgeWithContent.class);
			Skeleton<V, TreeEdgeWithContent<V,E>> rootSkeleton = new Skeleton<>(cycle.getVertices(), cycle.getEdges());
			TreeEdgeWithContent<V,E> virtualEdge = cycle.edgeesBetween(s, t).get(0);
			rootSkeleton.addVirualEdge(virtualEdge);
			root = new TreeNode<V,TreeEdgeWithContent<V,E>>(NodeType.S, rootSkeleton);
			addVertex(root);

			//now create its children

			/*
			 * Children of the root node are defined by graphs Gi which are constructed
			 * from the block Bi by adding edge ei
			 * ei is an edge between vi-1 and vi
			 */
			TreeEdgeWithContent<V, E> childReferenceEdge;
			Block<V,E> current;
			for (int i = 0; i < blocks.size(); i++ ){
				E edge = graph.edgeesBetween(vertices.get(i), vertices.get(i+1)).get(0);
				childReferenceEdge = new TreeEdgeWithContent<V,E> (edge.getOrigin(), edge.getDestination());
				ChildGraph<V, TreeEdgeWithContent<V,E>> child= new ChildGraph<V, TreeEdgeWithContent<V,E>>();
				current = blocks.get(i);
				for (V v : current.getVertices())
					child.addVertex(v);
				for (E e : current.getEdges())
					child.addEdge(new TreeEdgeWithContent<V, E>(e.getOrigin(), e.getDestination()));
				child.setReferenceEdge(childReferenceEdge);
				
				child.addEdge(childReferenceEdge);
				root.getChildren().add(child);
			}
		}

		else{
			//check if vertices s and t are a split pair of G'
			List<SplitPair<V, E>> splitPairs = splitting.findAllSplitPairs(gPrim);
			SplitPair<V,E> stSplit = new SplitPair<V,E>(s, t);
			boolean isSplitPair = false;
			for (SplitPair<V,E> split : splitPairs)
				if (split.equals(stSplit)){
					isSplitPair = true;
					break;
				}
			
			
			List<SplitComponent<V, E>> components = null;
			if (isSplitPair)
				components = splitting.findAllSplitComponents(gPrim, stSplit);
			
			//parallel case
			if (components != null && components.size() >= 2){
			
				//firstly, create the root node
				
				/*
				 *Root node is a P-node whose skeleton consists of two vertices - s and t
				 *and the edges e1...ek+1
				 *e1...ek are components ci...ck, ek+1 is the virtual edge s-t
				 */
				Skeleton<V, TreeEdgeWithContent<V, E>> rootSkeleton = new Skeleton<>();
				rootSkeleton.addVertex(s,t);
				for (SplitComponent<V, E> splitComponent : components){
					rootSkeleton.addEdge(new TreeEdgeWithContent<V,E>(s, t, splitComponent));
				}
				//add virtual edge
				TreeEdgeWithContent<V, E> stEdge = new TreeEdgeWithContent<V,E>(s,t);
				rootSkeleton.addEdge(stEdge);
				rootSkeleton.addVirualEdge(stEdge);
				//create root
				root = new TreeNode<V,TreeEdgeWithContent<V,E>>(NodeType.P, rootSkeleton);
				addVertex(root);
				
				//add children
				
				/*
				 * Children are defined by graphs G1...Gk constructed from
				 * C1...Ck by adding edge ei for i=1...k
				 */
				for (int i = 0; i < components.size(); i++){
					ChildGraph<V, TreeEdgeWithContent<V,E>> child = new ChildGraph<>();
					SplitComponent<V, E> splitComponent = components.get(i);
					TreeEdgeWithContent<V, E> childReferenceEdge = root.getSkeleton().getEdges().get(i);
					for (V v : splitComponent.getVertices())
						child.addVertex(v);
					for (E e : splitComponent.getEdges())
						child.addEdge(new TreeEdgeWithContent<V, E>(e.getOrigin(), e.getDestination()));
					
					child.setReferenceEdge(childReferenceEdge);
					
					child.addEdge(childReferenceEdge);
					root.getChildren().add(child);
				}
			}

			//Rigid case - grim is biconnected and {s,t} is not a split pair
			//with two or more components
			else{
				
				//The vertices in the skeleton are s,t ad all {si, ti} from split pairs.
				//Skeleton contains st edge (virtual) and edges ei which connect si to ti
				//Each ei represents subgraph Ui - split graph
				Skeleton<V, TreeEdgeWithContent<V, E>> rootSkeleton = new Skeleton<>();
				rootSkeleton.addVertex(s,t);
				
				E stEdge = graph.edgeesBetween(s, t).get(0);
				List<SplitPair<V,E>> maxSplittingPairs = splitting.maximalSplitPairs(graph, stEdge);
				List<Graph<V, E>> uGraphs = new ArrayList<Graph<V,E>>();
				List<TreeEdgeWithContent<V, E>> edges = new ArrayList<TreeEdgeWithContent<V, E>>();
				for (SplitPair<V, E> splitPair : maxSplittingPairs){
					//split graph of splitPair with respect to {s,t} edge
					Graph<V, E> uGraph = splitting.splitGraph(splitPair, stEdge, graph);
					uGraphs.add(uGraph);
					rootSkeleton.addVertex(splitPair.getU(), splitPair.getV());
					TreeEdgeWithContent<V, E> edge = new TreeEdgeWithContent<V, E>(splitPair.getU(),
							splitPair.getV(),uGraph);
					rootSkeleton.addEdge(edge);
					edges.add(edge);
				}
				
				
				TreeEdgeWithContent<V, E> stTreeEdge = new TreeEdgeWithContent<V,E>(s,t);
				rootSkeleton.addEdge(stTreeEdge);
				rootSkeleton.addVirualEdge(stTreeEdge);
				
				root = new TreeNode<>(NodeType.R, rootSkeleton);
				addVertex(root);
				
				//create children
				/*
				 * children are defined by the graphs Gi, constructed from Ui by
				 * adding edge ei				 
				 */
				/*
				 * Children are defined by graphs G1...Gk constructed from
				 * C1...Ck by adding edge ei for i=1...k
				 */
				for (int i = 0; i < uGraphs.size(); i++){
					ChildGraph<V, TreeEdgeWithContent<V,E>> child = new ChildGraph<>();
					Graph<V, E> uGraph = uGraphs.get(i);
					TreeEdgeWithContent<V, E> childReferenceEdge = edges.get(i);
					for (V v : uGraph.getVertices())
						child.addVertex(v);
					for (E e : uGraph.getEdges())
						child.addEdge(new TreeEdgeWithContent<V, E>(e.getOrigin(), e.getDestination()));
					
					child.setReferenceEdge(childReferenceEdge);
					
					child.addEdge(childReferenceEdge);
					root.getChildren().add(child);
				}
				
				
			}
		}
	}

	/**
	 * Organized vertices and blocks so that vertex v is contained in blocks bi and bi+1
	 * First and last blocks
	 * @param vertices
	 * @param blocks
	 */
	private void organizeBlocksAndVertices(V s, V t, List<V> vertices, List<Block<V, E>> blocks){

		/*
		 * If there v1...vk-1 are cut vertices, there are k blocks, b1 to bk
		 * Since graph is biconnected
		 * s is contained in b1
		 * t in bk
		 * A cut vertex vi is contained in b1 and bi+1
		 */

		Block<V, E> firstBlock = blocksContainingVertex(blocks, s).get(0);
		if (firstBlock == null)
			throw new RuntimeException("S vertex not containined in any of the blocks. Error!");
		vertices.add(0, s);
		Collections.swap(blocks, 0, blocks.indexOf(firstBlock));

		Block<V, E> lastBlock = blocksContainingVertex(blocks, t).get(0);
		if (lastBlock == null)
			throw new RuntimeException("T vertex not containined in any of the blocks. Error!");
		vertices.add(t);
		Collections.swap(blocks, blocks.size() - 1, blocks.indexOf(lastBlock));

		Block<V, E> currentBlock = firstBlock;
		V currentVertex, previousVertex = s;

		//current block contains current vertex and another one from the list, find it
		//then find next block

		int currentIndex = 1;

		while (true){
			currentVertex = otherVertexInBlock(currentBlock, previousVertex, vertices);
			Collections.swap(vertices, currentIndex, vertices.indexOf(currentVertex));
			List<Block<V,E>> blocksContainingVertex = blocksContainingVertex(blocks, currentVertex);
			if (blocksContainingVertex.size() != 2)
				throw new RuntimeException("Cut vertix not conatained in exactly two blocks! Error");
			for (Block<V, E> block : blocksContainingVertex)
				if (block != currentBlock){
					currentBlock = block;
					break;
				}
			if (currentBlock == lastBlock)
				break;
			Collections.swap(blocks, currentIndex++, blocks.indexOf(currentBlock));

		}


	}

	private V otherVertexInBlock(Block<V,E> block, V containedVertex, List<V> vertices){
		for (V v : vertices){
			if (v != containedVertex && block.hasVertex(v))
				return v;
		}
		return null;
	}

	private List<Block<V,E>> blocksContainingVertex(List<Block<V, E>> blocks, V v){

		List<Block<V, E>> ret = new ArrayList<>();
		for (Block<V, E> block : blocks){
			if (block.hasVertex(v))
				ret.add(block);
		}

		if (ret.size() == 0)
			ret.add(null);
		return ret;
	}



	private boolean gPrimIsASingleEdge(){
		if (gPrim.getEdges().size() == 1 && gPrim.getVertices().size() == 2)
			return true;
		return false;

	}

	public Graph<V, E> getgPrim() {
		return gPrim;
	}

	public void setgPrim(Graph<V, E> gPrim) {
		this.gPrim = gPrim;
	}

	public E getReferenceEdge() {
		return referenceEdge;
	}

	public void setReferenceEdge(E referenceEdge) {
		this.referenceEdge = referenceEdge;
	}

	public Graph<V, E> getGraph() {
		return graph;
	}

	public void setGraph(Graph<V, E> graph) {
		this.graph = graph;
	}

	public TreeNode<V, TreeEdgeWithContent<V, E>> getRoot() {
		return root;
	}

	public void setRoot(TreeNode<V, TreeEdgeWithContent<V, E>> root) {
		this.root = root;
	}


}
