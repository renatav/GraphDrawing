package graph.tree.spqr;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.operations.GraphOperations;
import graph.properties.splitting.Block;
import graph.properties.splitting.Splitting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProtoSPQRTree<V extends Vertex, E extends Edge<V>> extends Graph<TreeNode<V,E>, Edge<TreeNode<V,E>>>{

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
	private TreeNode<V, E> root;

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
		GraphOperations<V, E> operations = new GraphOperations<>();
		V s = referenceEdge.getOrigin();
		V t = referenceEdge.getDestination();

		//trivial case
		if (gPrimIsASingleEdge()){ //is g prim is a single edge
			TreeNode<V, E> node = new TreeNode<>(NodeType.Q, graph);
			addVertex(node);
			root = node;
		}
		//series case
		else if (!gPrim.isBiconnected()){ //g prim is not a single edge and is not biconnected
			List<V> cutVertices = splitting.findAllCutVertices(gPrim);
			List<Block<V, E>> blocks = splitting.findAllBlocks(gPrim, cutVertices);
			List<V> vertices = new ArrayList<>(cutVertices);
			organizeBlocksAndVertices(s, t, vertices, blocks);

			//create root
			Graph<V,E> cycle = operations.formCycleGraph(vertices, graph.getEdges().get(0).getClass());
			Skeleton<V, E> rootSkeleton = new Skeleton<>(cycle.getVertices(), cycle.getEdges());
			E virtualEdge = cycle.edgeesBetween(s, t).get(0);
			rootSkeleton.addVirualEdge(virtualEdge);
			TreeNode<V,E> root = new TreeNode<>(NodeType.S, rootSkeleton);
			addVertex(root);

			//now create its children

			/*
			 * Children of the root node are defined by graphs Gi which are constructed
			 * from the block Bi by adding edge ei
			 * ei is an edge between vi-1 and vi
			 */
			E childReferenceEdge;
			for (int i = 0; i < blocks.size(); i++ ){
				childReferenceEdge = graph.edgeesBetween(vertices.get(i), vertices.get(i+1)).get(0);
				ChildGraph<V, E> child= new ChildGraph<V,E>(blocks.get(i).getVertices(),
						blocks.get(i).getEdges(),childReferenceEdge);
				child.addEdge(referenceEdge);
				root.getChildren().add(child);
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

	public TreeNode<V, E> getRoot() {
		return root;
	}

	public void setRoot(TreeNode<V, E> root) {
		this.root = root;
	}

}
