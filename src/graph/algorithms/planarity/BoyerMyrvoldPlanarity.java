package graph.algorithms.planarity;

import graph.algorithms.numbering.DFSNumbering;
import graph.algorithms.numbering.Numbering;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.traversal.DFSTreeTraversal;
import graph.trees.DFSTree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class BoyerMyrvoldPlanarity<V extends Vertex, E extends Edge<V>> extends PlanarityTestingAlgorithm<V,E>{

	private List<Block> blocks = new ArrayList<Block>();
	private DFSTree<V,E> dfsTree;
	private Logger log = Logger.getLogger(BoyerMyrvoldPlanarity.class);
	private Map<V, List<V>> vertexChildListMap = new HashMap<V, List<V>>();
	private Map<V, Integer> vertexLowpointMap = new HashMap<V, Integer>();
	private Map<V, V> parentOfVertices = new HashMap<V, V>(); //key child, value parent


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
			
			//for each DFS child c of v in G
			//Embed tree edge (vc, c) as a biconnected component in G
			for (E e : dfsTree.allOutgoingTreeEdges(v)){
				formBlock(e);
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


		}
			 */

		return false;
	}
	
	/*
	   A vertex w is externally active during the processing of v if w either has a least ancestor less than
		v or if the first element in the separatedDFSChildList of w has a lowpoint less
		than v.
	 */
	private boolean extremelyActive(V w, V v){
		
		V leastAncestor = dfsTree.leastAncestor(w);
		if (dfsTree.getIndex(leastAncestor) < dfsTree.getIndex(v) || 
				dfsTree.getIndex(vertexChildListMap.get(w).get(0)) < dfsTree.getIndex(v))
			return true;
		
		return false;
	}


	
	private void formBlock(E treeEdge){

		Block block = new Block();
		block.addVertex(treeEdge.getOrigin(), true);
		block.addVertex(treeEdge.getDestination(), true);
		block.addEdge(treeEdge, true);
		blocks.add(block);
		System.out.println(block);
	}
	
	
	private void formBlocks(){

		for (E treeEdge : dfsTree.getTreeEdges()){
			formBlock(treeEdge);
		}

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
