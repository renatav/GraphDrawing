package graph.tree.spqr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.operations.GraphOperations;
import graph.properties.components.SplitComponent;
import graph.properties.components.SplitPair;
import graph.properties.splitting.Splitting;

/**
 * Used in the construction of SPQR trees
 * Not a very efficient implementation, needs to be rewritten
 * Can be used for smaller graphs
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type 
 */
public class ProtoSPQRTree<V extends Vertex,E extends Edge<V>> extends AbstractTree<V,E>{

	/**
	 * Original graph - reference edge
	 */
	private Graph<V,E> gPrim;


	private Logger log = Logger.getLogger(ProtoSPQRTree.class);
	
	private Splitting<V,E> splitting;


	public ProtoSPQRTree(E referenceEdge, Graph<V, E> graph) {
		super(referenceEdge, graph);
		splitting = new Splitting<V,E>();

		GraphOperations<V, E> operations = new GraphOperations<>();
		gPrim = operations.removeEdgeFromGraph(graph, referenceEdge);

		log.info("Created G' graph");

		constructTree();

	}


	//TODO za child proveriti da li je samo edge, ili nesto komplikovanije (reprezentuje podgraf).
	//pa onda napraviti i za taj podgraf rekurzivno...

	@SuppressWarnings("unchecked")
	private void constructTree(){

		log.info("Constructing Proto-SPQR tree");
		
		GraphOperations<V,E> operations = new GraphOperations<>();
		V s = referenceEdge.getOrigin();
		V t = referenceEdge.getDestination();

		//trivial case
		if (gPrimIsASingleEdge()){ //is g prim is a single edge

			log.info("Trivial case: creating Q root node");

			Skeleton<V,TreeEdgeWithContent<V,E>> skeleton = new Skeleton<>();

			for (V v : graph.getVertices())
				skeleton.addVertex(v);
			for (E e : graph.getEdges())
				skeleton.addEdge(new TreeEdgeWithContent<V,E>(e.getOrigin(), e.getDestination()));


			root = new SPQRTreeNode<V,TreeEdgeWithContent<V, E>>(NodeType.Q, skeleton);
			addVertex(root);
		}
		//series case
		else if (!gPrim.isBiconnected()){ //g prim is not a single edge and is not biconnected

			log.info("Series case: creating S root node");

			List<V> cutVertices = gPrim.listCutVertices();

			log.info("Found cut vertices og G': " + cutVertices);

			List<Graph<V, E>> blocks = gPrim.listBiconnectedComponents();

			log.info("Split G' into blocks: " +blocks);

			List<V> vertices = new ArrayList<>(cutVertices);

			organizeBlocksAndVertices(s, t, vertices, blocks);

			//create root
			Graph<V,E> cycle = operations.formCycleGraph(vertices, graph.getEdges().get(0).getClass());

			//edges of the skeleton represent the blocks - ei represent Bi
			List<TreeEdgeWithContent<V, E>> edges = new ArrayList<TreeEdgeWithContent<V,E>>();
			TreeEdgeWithContent<V, E> treeEdge;


			for (int i = 0; i < vertices.size() - 1; i++){
				E e = cycle.edgeesBetween(vertices.get(i), vertices.get(i+1)).get(0);
				treeEdge = new TreeEdgeWithContent<V,E>(e.getOrigin(), e.getDestination());
				treeEdge.setContent(blocks.get(i));
				edges.add(treeEdge);
			}


			Skeleton<V, TreeEdgeWithContent<V,E>> rootSkeleton = new Skeleton<>(vertices, edges);

			TreeEdgeWithContent<V,E> virtualEdge = new TreeEdgeWithContent<V,E>(s,t);
			rootSkeleton.addEdge(virtualEdge, true);
			root = new SPQRTreeNode<V,TreeEdgeWithContent<V,E>>(NodeType.S, rootSkeleton);
			addVertex(root);

			log.info("Constructed tree root: " + root);

			log.info("Creating children...");
			//now create its children

			/*
			 * Children of the root node are defined by graphs Gi which are constructed
			 * from the block Bi by adding edge ei
			 * ei is an edge between vi-1 and vi
			 * Children are roots of Proto-SPQR trees for the subgraphs
			 */
			Graph<V,E> currentBlock;
			SPQRTreeNode<V, TreeEdgeWithContent<V,E>> childNode;
			E referenceEdge;
			for (int i = 0; i < blocks.size(); i++ ){
				currentBlock = blocks.get(i);
				referenceEdge = cycle.getEdges().get(i);
				currentBlock.addEdge(referenceEdge);

				if (currentBlock.getEdges().size() == 1){
					Skeleton<V, TreeEdgeWithContent<V,E>> childSkeleton = new Skeleton<>();
					for (V v : currentBlock.getVertices())
						childSkeleton.addVertex(v);
					for (E e : currentBlock.getEdges())
						childSkeleton.addEdge(new TreeEdgeWithContent<V,E>(e.getOrigin(), e.getDestination()));
					childNode = new SPQRTreeNode<>(NodeType.Q, childSkeleton);

				}
				else{
					//construct Proto-SPQR tree from block and reference edge, add its root to children nodes
					ProtoSPQRTree<V, E> childTree = new ProtoSPQRTree<V, E>(referenceEdge, currentBlock);
					childNode = childTree.getRoot();

				}
				log.info("Adding child node: " + childNode);
				root.getChildren().add(childNode);
			}
		}

		else{

			log.info("G' is biconnected, finding split pairs");

			//check if vertices s and t are a split pair of G'
			List<SplitPair<V, E>> splitPairs = splitting.findAllSplitPairs(gPrim);
			SplitPair<V,E> stSplit = new SplitPair<V,E>(s, t);
			boolean isSplitPair = false;
			for (SplitPair<V,E> split : splitPairs)
				if (split.equals(stSplit)){
					isSplitPair = true;
					break;
				}

			log.info("Found split pairs: " + splitPairs);
			log.info("{s,t} is a split pair: " + isSplitPair);


			List<SplitComponent<V, E>> components = null;
			if (isSplitPair){
				components = splitting.findAllSplitComponents(gPrim, stSplit);
				log.info("Found split components: " + components);
			}

			//parallel case
			if (components != null && components.size() >= 2){

				log.info("Parallel case: creating P root node");

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
				rootSkeleton.addEdge(stEdge, true);
				//create root
				root = new SPQRTreeNode<V,TreeEdgeWithContent<V,E>>(NodeType.P, rootSkeleton);
				addVertex(root);
				log.info("Create root node: " + root);

				//add children

				/*
				 * Children are defined by graphs G1...Gk constructed from
				 * C1...Ck by adding edge ei for i=1...k
				 * all ei edges are between vertices s and t - reference edge of the this tree
				 */

				SPQRTreeNode<V, TreeEdgeWithContent<V,E>> childNode;
				SplitComponent<V, E> splitComponent;

				for (int i = 0; i < components.size(); i++){
					splitComponent = components.get(i);
					splitComponent.addEdge(referenceEdge);

					if (splitComponent.getEdges().size() == 1){
						Skeleton<V, TreeEdgeWithContent<V,E>> childSkeleton = new Skeleton<>();
						for (V v : splitComponent.getVertices())
							childSkeleton.addVertex(v);
						for (E e : splitComponent.getEdges())
							childSkeleton.addEdge(new TreeEdgeWithContent<V,E>(e.getOrigin(), e.getDestination()));
						childNode = new SPQRTreeNode<>(NodeType.Q, childSkeleton);

					}
					else{

						//construct Proto-SPQR tree from block and reference edge, add its root to children nodes
						ProtoSPQRTree<V, E> childTree = new ProtoSPQRTree<V, E>(referenceEdge, splitComponent);
						childNode = childTree.getRoot();
					}
					log.info("Adding child node: " + childNode);
					root.getChildren().add(childNode);

				}
			}

			//Rigid case - grim is biconnected and {s,t} is not a split pair
			//with two or more components
			else{

				log.info("Rigid case: creating R root node");


				//The vertices in the skeleton are s,t ad all {si, ti} from split pairs.
				//Skeleton contains st edge (virtual) and edges ei which connect si to ti
				//Each ei represents subgraph Ui - split graph
				Skeleton<V, TreeEdgeWithContent<V, E>> rootSkeleton = new Skeleton<>();
				rootSkeleton.addVertex(s,t);

				E stEdge = graph.edgeesBetween(s, t).get(0);
				List<SplitPair<V,E>> maxSplittingPairs = splitting.maximalSplitPairs(graph, stEdge);

				log.info("Maximal splitting pairs: " + maxSplittingPairs);

				List<Graph<V, E>> uGraphs = new ArrayList<Graph<V,E>>();
				List<E> edges = new ArrayList<E>();

				System.out.println("St edge: " + stEdge);
				Class<? extends Edge<V>> edgeClass = (Class<? extends Edge<V>>) graph.getEdges().get(0).getClass();
				for (SplitPair<V, E> splitPair : maxSplittingPairs){
					//split graph of splitPair with respect to {s,t} edge
					Graph<V, E> uGraph = splitting.splitGraph(splitPair, stEdge, graph);
					uGraphs.add(uGraph);
					rootSkeleton.addVertex(splitPair.getU(), splitPair.getV());
					TreeEdgeWithContent<V, E> edge = new TreeEdgeWithContent<V, E>(splitPair.getU(),
							splitPair.getV(),uGraph);
					rootSkeleton.addEdge(edge);
					edges.add(operations.createNewEdge(splitPair.getU(), splitPair.getV(), edgeClass));
				}


				TreeEdgeWithContent<V, E> stTreeEdge = new TreeEdgeWithContent<V,E>(s,t);
				rootSkeleton.addEdge(stTreeEdge, true);

				root = new SPQRTreeNode<>(NodeType.R, rootSkeleton);
				addVertex(root);

				log.info("Create root node: " + root);

				//create children
				/*
				 * children are defined by the graphs Gi, constructed from Ui by
				 * adding edge ei	
				 */
				E childReferenceEdge;
				SPQRTreeNode<V, TreeEdgeWithContent<V,E>> childNode;
				for (int i = 0; i < uGraphs.size(); i++){
					Graph<V, E> uGraph = uGraphs.get(i);

					childReferenceEdge = edges.get(i);
					uGraph.addEdge(childReferenceEdge);
					
					if (uGraph.equals(graph)) //TODO zasto se ovo desava???
						continue;

					if (uGraph.getEdges().size() == 1){
						Skeleton<V, TreeEdgeWithContent<V,E>> childSkeleton = new Skeleton<>();
						for (V v : uGraph.getVertices())
							childSkeleton.addVertex(v);
						for (E e : uGraph.getEdges())
							childSkeleton.addEdge(new TreeEdgeWithContent<V,E>(e.getOrigin(), e.getDestination()));
						childNode = new SPQRTreeNode<>(NodeType.Q, childSkeleton);

					}

					else{

						//construct Proto-SPQR tree from block and reference edge, add its root to children nodes
						ProtoSPQRTree<V, E> childTree = new ProtoSPQRTree<V, E>(childReferenceEdge, uGraph);
						childNode = childTree.getRoot();
					}

					log.info("Adding child: " + childNode);
					root.getChildren().add(childNode);
				}


			}
		}
		log.info("Finished constructing Proto-SPQR tree");
	}

	/**
	 * Organized vertices and blocks so that vertex v is contained in blocks bi and bi+1
	 * First and last blocks
	 * @param vertices
	 * @param blocks
	 */
	private void organizeBlocksAndVertices(V s, V t, List<V> vertices, List<Graph<V, E>> blocks){

		log.info("Organizing vertices and blocks");

		/*
		 * If there v1...vk-1 are cut vertices, there are k blocks, b1 to bk
		 * Since graph is biconnected
		 * s is contained in b1
		 * t in bk
		 * A cut vertex vi is contained in b1 and bi+1
		 */

		Graph<V, E> firstBlock = blocksContainingVertex(blocks, s).get(0);
		if (firstBlock == null)
			throw new RuntimeException("S vertex not containined in any of the blocks. Error!");
		vertices.add(0, s);
		Collections.swap(blocks, 0, blocks.indexOf(firstBlock));

		Graph<V, E> lastBlock = blocksContainingVertex(blocks, t).get(0);
		if (lastBlock == null)
			throw new RuntimeException("T vertex not containined in any of the blocks. Error!");
		vertices.add(t);
		Collections.swap(blocks, blocks.size() - 1, blocks.indexOf(lastBlock));

		Graph<V, E> currentBlock = firstBlock;
		V currentVertex, previousVertex = s;

		//current block contains current vertex and another one from the list, find it
		//then find next block

		int currentIndex = 1;

		while (true){
			currentVertex = otherVertexInBlock(currentBlock, previousVertex, vertices);
			Collections.swap(vertices, currentIndex, vertices.indexOf(currentVertex));
			List<Graph<V,E>> blocksContainingVertex = blocksContainingVertex(blocks, currentVertex);
			if (blocksContainingVertex.size() != 2)
				throw new RuntimeException("Cut vertix not conatained in exactly two blocks! Error");
			for (Graph<V, E> block : blocksContainingVertex)
				if (block != currentBlock){
					currentBlock = block;
					break;
				}
			if (currentBlock == lastBlock)
				break;
			previousVertex = currentVertex;
			Collections.swap(blocks, currentIndex++, blocks.indexOf(currentBlock));

		}


	}

	private V otherVertexInBlock(Graph<V,E> block, V containedVertex, List<V> vertices){
		for (V v : vertices){
			if (v != containedVertex && block.hasVertex(v))
				return v;
		}
		return null;
	}

	private List<Graph<V,E>> blocksContainingVertex(List<Graph<V, E>> blocks, V v){

		List<Graph<V, E>> ret = new ArrayList<>();
		for (Graph<V, E> block : blocks){
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

	/**
	 * @return G'
	 */
	public Graph<V, E> getgPrim() {
		return gPrim;
	}

	/**
	 * @param gPrim G' to set
	 */
	public void setgPrim(Graph<V, E> gPrim) {
		this.gPrim = gPrim;
	}

	/**
	 * @return The reference edge
	 */
	public E getReferenceEdge() {
		return referenceEdge;
	}

	/**
	 * @param referenceEdge The reference edge to set
	 */
	public void setReferenceEdge(E referenceEdge) {
		this.referenceEdge = referenceEdge;
	}

	/**
	 * @return Graph
	 */
	public Graph<V, E> getGraph() {
		return graph;
	}

	/**
	 * @param graph Graph to set
	 */
	public void setGraph(Graph<V, E> graph) {
		this.graph = graph;
	}

	/**
	 * @return Root of the tree
	 */
	public SPQRTreeNode<V, TreeEdgeWithContent<V, E>> getRoot() {
		return root;
	}

	/**
	 * @param root The root to set
	 */
	public void setRoot(SPQRTreeNode<V, TreeEdgeWithContent<V, E>> root) {
		this.root = root;
	}


}
