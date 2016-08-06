package graph.algorithms.connectivity;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import graph.algorithms.planarity.FraysseixMendezPlanarity;
import graph.algorithms.planarity.PlanarityTestingAlgorithm;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.exception.CannotBeAppliedException;
import graph.tree.bc.BCTree;
import graph.tree.bc.BCTreeEdge;
import graph.tree.bc.BCTreeNode;
import graph.tree.bc.VertexType;

/**
 * Given a connected, but not biconnected planar graph, the algorithm
 * adds a minimum number of edges to turn it into a biconnected graph
 * while not ruining the planarity
 * This implementation is of Fialko and Mutzel's 5/3 approximation
 */
public class PlanarAugmentation<V extends Vertex, E extends Edge<V>> {

	private PlanarityTestingAlgorithm<V, E> planarityTest;
	private Logger log = Logger.getLogger(PlanarAugmentation.class);
	
	public PlanarAugmentation(){
		planarityTest = new FraysseixMendezPlanarity<V,E>();
	}
	
	public Graph<V,E> planarBiconnected(Graph<V,E> graph) throws CannotBeAppliedException{
		if (!planarityTest.isPlannar(graph))
			throw new CannotBeAppliedException("Graph is not planar");

		//technically, if it is not it can first be made connected
		//in a similar fashion to this algorithm
		//for now, only apply the algorithm if the graph is already connected
		if (!graph.isConnected())
			throw new CannotBeAppliedException("Graph is not connected");
		
		//step one
		//Construct the block tree
		//initialize P (list containing all pendants in T)
		//pendant is a block which contains only one cut vertex
		
		log.info("initialization");
		log.info("Cut vertices: " + graph.listCutVertices());
		log.info("Blocks: " + graph.listBiconnectedComponents());
		
		BCTree<V, E> bcTree = new BCTree<V,E>(graph);
		log.info(bcTree);
		
		List<Graph<V,E>> pendants = bcTree.getPendants(); 
		//initialize the list of labels by calling reduce chains
		List<PlanarAugmentationLabel<V, E>> labels = reduceChains(pendants, bcTree);
		PlanarAugmentationLabel<V, E> l1, l2;
		
		//while T(G) has c-vertices
		while(bcTree.getcVertices().size() > 0){
			//l1 - label with maximum size
			//l2 - match_labels(l1)
			
			l1 = labels.get(0); 
			for (int i = 1; i < labels.size(); i++){
				PlanarAugmentationLabel<V, E> label = labels.get(0);
				if (labels.size() > l1.size())
					l1 = label;
			}
			
			l2 = matchLabels(l1, labels); //search for a partner label for l1
			
			//if there is no label that can be matched to l1
			if (l2 == null){
				//Add L(l1) - 1 (size(l1)) edges from an outside node
				//in the first child of l1 to the outside nodes of other children of l1
				//merge all bc nodes along the path between l1 and the children of l1
				//and the children to a new pendant p'
				//f - the cut vertex found in the first pass of reduce_chains(p')
				//b - the block adjacent to f but not belonging to the path {p', f)
				//add an edge from an outside node of p'to a node of b adjacent to f
				//l' - construct in T(G) a new block from p', b and bc_nodes
				//along the path {p', b)
			}
			else{ //l1 is the partner label of l1
				//connect every child of l2 with a child of l1
				//merge all connected pendants, all bc nodes on the path
				//between them and their parents (l1 or l2), l1, l2 and all bc nodes
				//on the path {l1,l1) to a new block p'
			}
			
			if (l1.size() == l2.size()) //l1 has no more children
				labels.remove(l1); //remove l1 form the list of labels
			//if degree(p') == 1 //a new pendant occurs
				//find the parent of p' via reduce_chains
			//if (l1 is b-label) and (L(l2) == 1)
				//apply reduce chains to the remaining child of l1
		}

		
		
		Graph<V,E> biconnected = new Graph<V,E>();
		
		return biconnected;
	}
	
	/**
	 * Finds all labels in T(G). Applying this procedure to one pendant p too.
	 * That way, the first loop (first) pass) is only run for p and the second
	 * loop (second pass) only for the cut vertex c found in the first loop
	 * @param pendants
	 * @return
	 */
	private List<PlanarAugmentationLabel<V, E>> reduceChains(List<Graph<V,E>> pendants,
			BCTree<V,E> bcTree){
		
		//for each p in pendants
		for (Graph<V,E> p : pendants){
			//c - the cut vertex adjacent to p
			BCTreeNode pendantNode = bcTree.getVertexByContent(p);
			BCTreeNode c = null;
			for (BCTreeNode adj : bcTree.adjacentVertices(pendantNode)){
				if (adj.getType() == VertexType.C){
					c = adj;
					break;
				}
			}
			//while deg(c) = 2 and c is not the root of T(G)
			
			while (bcTree.vertexDegree(c) == 2 && c != bcTree.getRoot()){
				//if c is a b-vertex
				if (c.getType() == VertexType.C){
					List<BCTreeEdge> edges = bcTree.adjacentEdges(c);
					//let c1 and c2 be 2 cut vertices adjacent to b
					BCTreeEdge e1 = edges.get(0);
					BCTreeEdge e2 = edges.get(1);
					BCTreeNode c1 = e1.getOrigin() == c ? e1.getDestination() : e1.getOrigin();
					BCTreeNode c2 = e1.getOrigin() == c ? e2.getDestination() : e2.getOrigin();

					//if !planar(G+(c1,c2))
						//break;
				
				}
				//c - parent(c)
				c = c.getParent();
			}
		}
		
		return new ArrayList<PlanarAugmentationLabel<V, E>>();
	}
	
	private PlanarAugmentationLabel<V, E> matchLabels(PlanarAugmentationLabel<V, E> l1,
			List<PlanarAugmentationLabel<V, E>> labels){
		
		return labels.get(0);
		
	}
	
}
