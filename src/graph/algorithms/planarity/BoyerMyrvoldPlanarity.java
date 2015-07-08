package graph.algorithms.planarity;

import graph.algorithms.numbering.DFSNumbering;
import graph.algorithms.numbering.Numbering;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.traversal.DFSTreeTraversal;
import graph.trees.DFSTree;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class BoyerMyrvoldPlanarity<V extends Vertex, E extends Edge<V>> extends PlanarityTestingAlgorithm<V,E>{

	private List<Block> blocks = new ArrayList<Block>();
	private DFSTree<V,E> dfsTree;
	private Logger log = Logger.getLogger(BoyerMyrvoldPlanarity.class);
	
	
	@Override
	public boolean isPlannar(Graph<V, E> graph) {
		
		
		DFSTreeTraversal<V, E> traversal = new  DFSTreeTraversal<V,E>(graph);
		dfsTree = traversal.formDFSTree(graph.getVertices().get(0));
		
		Numbering<V,E> numbering =  new DFSNumbering<V, E>(dfsTree);
		
		//initially, split graph into blocks, where each block corresponds to an edge of the dfs tree
		formBlocks();
		
		for (V v : numbering.getOrder()){
			log.info("Processing node: " + v);
			List<E> incomingBackEdges = dfsTree.allIncomingBackEdges(v);
			
			//if there are no incoming back edges, don't process vertex
			if (incomingBackEdges.isEmpty())
				continue;
			
		}
		
		
		return false;
	}
	
	
	@SuppressWarnings("unchecked")
	private void formBlocks(){
		
		for (E treeEdge : dfsTree.getTreeEdges()){
			
			
			Block block = new Block();
			block.addVertex(treeEdge.getOrigin(), true);
			block.addVertex(treeEdge.getDestination(), true);
			block.addEdge(treeEdge, true);
			blocks.add(block);
			System.out.println(block);
			
		}
		
	}
	
	
	public class Block{
		
		private List<V> vertices;
		private List<E> edges;
		private List<V> boundaryVertices;
		private List<E> boundaryEdges;
		
		public Block() {
			vertices = new ArrayList<V>();
			edges = new ArrayList<E>();
			boundaryVertices = new ArrayList<V>();
			boundaryEdges = new ArrayList<E>();
			
		}

		public Block(List<V> vertices, List<E> edges, List<V> boundaryVertices,
				List<E> boundaryEdges) {
			this.vertices = vertices;
			this.edges = edges;
			this.boundaryVertices = boundaryVertices;
			this.boundaryEdges = boundaryEdges;
		}
		
		public void addVertex(V v, boolean boundary){
			vertices.add(v);
			if (boundary)
				boundaryVertices.add(v);
		}
		
		public void addEdge(E edge, boolean boundary){
			edges.add(edge);
			if (boundary)
				boundaryEdges.add(edge);
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
		
		@Override
		public String toString() {
			return "Block [vertices=" + vertices + ", edges=" + edges
					+ ", boundaryVertices=" + boundaryVertices
					+ ", boundaryEdges=" + boundaryEdges + "]";
		}
		
		
		
	}

}
