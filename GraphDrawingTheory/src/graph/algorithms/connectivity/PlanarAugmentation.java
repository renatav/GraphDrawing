package graph.algorithms.connectivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import graph.algorithms.planarity.FraysseixMendezPlanarity;
import graph.algorithms.planarity.PlanarityTestingAlgorithm;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.exception.CannotBeAppliedException;
import graph.traversal.GraphTraversal;
import graph.tree.bc.BCTree;
import graph.tree.bc.BCTreeEdge;
import graph.tree.bc.BCTreeNode;
import graph.tree.bc.VertexType;
import graph.util.Util;

/**
 * Given a connected, but not biconnected planar graph, the algorithm
 * adds a minimum number of edges to turn it into a biconnected graph
 * while not ruining the planarity
 * This implementation is of Fialko and Mutzel's 5/3 approximation
 */
public class PlanarAugmentation<V extends Vertex, E extends Edge<V>> {

	private PlanarityTestingAlgorithm<V, E> planarityTest;
	private Logger log = Logger.getLogger(PlanarAugmentation.class);
	private Graph<V,E> graph;
	private BCTree<V, E> bcTree;
	private Map<BCTreeNode, Integer> deg;
	
	public PlanarAugmentation(Graph<V,E> graph){
		planarityTest = new FraysseixMendezPlanarity<V,E>();
		this.graph = graph;
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
		
		//degrees will be modified so a map which holds them is used and initialized here
		for (BCTreeNode bcNode : bcTree.getVertices()){
			deg.put(bcNode, bcTree.vertexDegree(bcNode));
		}
		
		
		List<Graph<V,E>> pendants = bcTree.getPendants(); 
		//initialize the list of labels by calling reduce chains
		List<PlanarAugmentationLabel<V, E>> labels = reduceChains(pendants);
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
				//Add L(l1) - 1 (size(l1) - 1) edges from an outside node
				//in the first child of l1 to the outside nodes of other children of l1
				//merge all bc nodes along the path between l1 and the children of l1
				//and the children to a new pendant p'
				//f - the cut vertex found in the first pass of reduce_chains(p')
				//b - the block adjacent to f but not belonging to the path {p', f)
				//add an edge from an outside node of p'to a node of b adjacent to f
				//l' - construct in T(G) a new block from p', b and bc_nodes
				//along the path {p', b)
			}
			else{ //l2 is the partner label of l1
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
	@SuppressWarnings("unchecked")
	private List<PlanarAugmentationLabel<V, E>> reduceChains(List<Graph<V,E>> pendants){
		
		//for each p in pendants
		//the first pass
		List<PlanarAugmentationLabel<V, E>> labels = 
				new ArrayList<PlanarAugmentationLabel<V,E>>();
		
		Map<BCTreeNode, PlanarAugmentationLabel<V,E>> nodeLabelsMap = 
				new HashMap<BCTreeNode, PlanarAugmentationLabel<V,E>>();
		
		for (Graph<V,E> p : pendants){
			
			log.info("Current pendant " + p );
			
			//c - the cut vertex adjacent to p
			BCTreeNode pendantNode = bcTree.getVertexByContent(p);
			BCTreeNode c = null;
			for (BCTreeNode adj : bcTree.adjacentVertices(pendantNode)){
				if (adj.getType() == VertexType.C){
					c = adj;
					break;
				}
			}
			
			log.info("Cut vertex adjacent to the pendant " + c);
			
			//while deg(c) = 2 and c is not the root of T(G)
			
			BCTreeNode lastC = null;
			
			while (deg.get(c) == 2 && c != bcTree.getRoot()){
				//if c is a b-vertex
				if (c.getType() == VertexType.B){
					List<BCTreeEdge> edges = bcTree.adjacentEdges(c);
					//let c1 and c2 be 2 cut vertices adjacent to b
					BCTreeEdge e1 = edges.get(0);
					BCTreeEdge e2 = edges.get(1);
					BCTreeNode c1 = e1.getOrigin() == c ? e1.getDestination() : e1.getOrigin();
					BCTreeNode c2 = e1.getOrigin() == c ? e2.getDestination() : e2.getOrigin();

					//if !planar(G+(c1,c2))
						//break;
					
					if (!checkPlanarity(graph, (V)c1.getContent(), (V)c2.getContent()))
							break;
				}
				else{
					//keep track of the last reached c vertex
					lastC = c;
				}
				//c - parent(c)
				c = c.getParent();
			}
			
			log.info("c: " + c);
			log.info("last cut vertex: " + lastC);
			
			//if c is a b-vertex
			if (c.getType() == VertexType.B){
				//c - the last cut vertex that we have reached
				c = lastC;
			}
			
			//c is a c-label
			//p is a child of c
			PlanarAugmentationLabel<V, E> cLabel = new PlanarAugmentationLabel<V,E>(c);
			nodeLabelsMap.put(c, cLabel);
			cLabel.addChild(p);
			labels.add(cLabel);
			log.info("Created label " + cLabel);
		}
		
		//second pass
		//for each c-label found in the first pass
		//not iterating through the list since it will be changed later
		
		log.info("second pass");
		for (PlanarAugmentationLabel<V, E> c : nodeLabelsMap.values()){
			//if deg(c) == 2 and c is adjacent to the block b with
			//degree 3 and b is adjacent to another c-label c'
			//with deg(c') == 2 and planar(c,c')

			//if it was already removed
			if (!labels.contains(c))
				continue;
			
			BCTreeNode cNode = c.getParent();
			if (deg.get(cNode) != 2)
				continue;
			
			//see if c is adjacent to a block of degree 3
			BCTreeNode block = null;
			
			for (BCTreeEdge bcEdge : bcTree.adjacentEdges(cNode)){
				BCTreeNode other = bcEdge.getOrigin() == cNode ? bcEdge.getDestination() : bcEdge.getOrigin();
				if (other.getType() == VertexType.B && deg.get(other) == 3){
					block = other;
					break;
				}
			}
			
			if (block == null)
				continue;
			
			log.info("adjacent to a block of degree 3 " + block);
			
			//is b adjacent to another c-label
			PlanarAugmentationLabel<V,E> cPrim = null;
			
			for (BCTreeEdge bcEdge : bcTree.adjacentEdges(block)){
				BCTreeNode other = bcEdge.getOrigin() == block ? bcEdge.getDestination() : bcEdge.getOrigin();
				if (other != cNode && nodeLabelsMap.containsKey(other)){
					cPrim = nodeLabelsMap.get(other);
					//also check if the label was already removed from the list
					if (labels.contains(cPrim) && deg.get(other) == 2 
							&& planar(c, cPrim, bcTree, graph))
						break;
					else
						cPrim = null;
				}
			}
			
			log.info("c' " + cPrim );
			
			if (cPrim == null)
				continue;
			
			//b is a new b-label and the 2 children of c and c'
			//are now children of b
			//add b and remove c and c' from the list of labels
			PlanarAugmentationLabel<V, E> bLabel = 
					new PlanarAugmentationLabel<V,E>(block);
			bLabel.addChild(c.getChildren().get(0));
			bLabel.addChild(cPrim.getChildren().get(0));
			labels.remove(c);
			labels.remove(cPrim);
			labels.add(bLabel);
			log.info("created b label " + bLabel);
		}
		
		return labels;
	}
	
	
	/**
	 * Tests whether two bundles represented by labels l1 and l2 can be matched planar
	 * @param l1
	 * @param l2
	 * @return true if a planar match can be made, false otherwise
	 */
	@SuppressWarnings("unchecked")
	private boolean planar(PlanarAugmentationLabel<V, E> l1, PlanarAugmentationLabel<V, E> l2, BCTree<V,E> bcTree,  Graph<V,E> graph){
		
		log.info("Checking if labels " + l1 + " and " + l2 + " can be matched planar");
		
		//if l1 and l2 are c labels
		//return planar(G+(l1,l2))
		if (l1.getParent().getType() == VertexType.C && l2.getParent().getType() == VertexType.C){
			log.info("Two c labels");
			return checkPlanarity(graph, (V) l1.getParent(), (V) l2.getParent());
		}
		
		//let v1 be a b-label and c11 and c12 cut vertices
		//adjacent to 2 children of l1 in T(G)
		//TODO da li su oni mozda adjacent oboje dece? 
		//ili pojedinacno c11 prvom, c12 drugom
		
		//at least one of them is a block
		PlanarAugmentationLabel<V, E> blockLabel = l1.getType() == VertexType.B ? l1 : l2;
		PlanarAugmentationLabel<V, E> otherLabel = blockLabel == l1 ? l2 : l1;
		
		log.info(blockLabel + " is B label");
		log.info("Finding c11 and c12");
		
		V c11 = null, c12 = null;
		for (Graph<V,E> pendant : blockLabel.getChildren()){
			BCTreeNode pendantNode = bcTree.getVertexByContent(pendant);
			for (BCTreeEdge bcEdge : bcTree.adjacentEdges(pendantNode)){
				BCTreeNode other = bcEdge.getOrigin() == pendantNode ? bcEdge.getDestination() : bcEdge.getOrigin();
				if (other.getType() == VertexType.C){
					if (c11 == null)
						c11 = (V) other.getContent();
					else
						c12 = (V) other.getContent();
					break;
				}
			}
		}
		
		log.info("c11 " + c11);
		log.info("c12 " + c12);
		
		//if l2 is a c-label
		if (otherLabel.getType() == VertexType.C){
			//if L(l2) == 1
			V cutVertex = (V) otherLabel.getParent();
			log.info("Other label of c type and size " + otherLabel.size());
			if (otherLabel.size() == 1){
				//return planar(G + (c11,l2) or planar(G + (c12,l2))
				return (checkPlanarity(graph, c11, cutVertex) || 
						checkPlanarity(graph, c12, cutVertex));
			}
			else{
				//return planar(G + (c11,l2) + (c12,l2))
				return checkPlanarity(graph, c11, cutVertex, c12, cutVertex);
			}
		}
		else{ //both l1 and l2 are b-labels
			//let c21 and c22be two cut vertices adjacent to two children of l2
			log.info("Both labels are blocks");
			
			V c21 = null, c22 = null;
			for (Graph<V,E> pendant : otherLabel.getChildren()){
				BCTreeNode pendantNode = bcTree.getVertexByContent(pendant);
				for (BCTreeEdge bcEdge : bcTree.adjacentEdges(pendantNode)){
					BCTreeNode other = bcEdge.getOrigin() == pendantNode ? bcEdge.getDestination() : bcEdge.getOrigin();
					if (other.getType() == VertexType.C){
						if (c21 == null)
							c21 = (V) other.getContent();
						else
							c22 = (V) other.getContent();
						break;
					}
				}
			}
			
			log.info("c21 " + c21);
			log.info("c22 " + c22);
			
			//return planar(G+(c11,c21) + (c12,c22)) or planar(G + (c11,c22) + c(12,c21))
			return (checkPlanarity(graph, c11,c21,c12,c22) || 
					checkPlanarity(graph, c11,c22,c12,c21));
		}
		
	}

	/**
	 * Check the planarity of graph after the two vertices v1 and v2 are connected
	 * @param graph
	 * @param v1
	 * @param v2
	 * @return true if the graph is planar, false otherwise
	 */
	@SuppressWarnings("unchecked")
	private boolean checkPlanarity(Graph<V,E> graph, V v1, V v2){
		
		boolean planar;

		//TODO use incremental planarity test once implemented
		
		E edge = Util.createEdge(v1, v2, graph.getEdges().get(0).getClass());
		graph.addEdge(edge);
		planar = planarityTest.isPlannar(graph);
		//removing the edge in any case, added it just to test the planarity
		graph.removeEdge(edge);
		
		return planar;
	}
	
	/**
	 * Check the planarity of graph after vertices are connected
	 * First two vertices form the first edge, next to the second edge etc.
	 * @param graph
	 * @param v1
	 * @param v2
	 * @return true if the graph is planar, false otherwise
	 */
	@SuppressWarnings("unchecked")
	private boolean checkPlanarity(Graph<V,E> graph, V...vertices){
		
		boolean planar;

		//TODO use incremental planarity test once implemented
		
		List<E> edges = new ArrayList<E>();
		
		for (int i = 0; i < vertices.length - 2; i += 2){
			V v1 = vertices[i];
			V v2 = vertices[i+1];
			E edge = Util.createEdge(v1, v2, graph.getEdges().get(0).getClass());
			graph.addEdge(edge);
			edges.add(edge);
	}
		
		planar = planarityTest.isPlannar(graph);
		
		//removing the edge in any case, added it just to test the planarity
		for (E edge : edges)
			graph.removeEdge(edge);
		
		return planar;
	}
	
	
	/**
	 * Checks condition con_cond for two bundles represented
	 * by labels l1 and l2
	 * @param l1
	 * @param l2
	 * @return true is condition is met, false otherwise
	 */
	private boolean conCond(PlanarAugmentationLabel<V,E> l1, PlanarAugmentationLabel<V,E> l2){
		
		log.info("Checking con cond for labels " + l1 + " and " + l2);
		
		//P - compute the path {l1,l2} in T(G)
		List<BCTreeNode> P = GraphTraversal.nonrecursiveDFSPath(bcTree, l1.getParent(), l2.getParent()).getUniqueVertices();
		log.info("P: " + P);
		
		//replace the degree of l1 by deg(l1) - L(l2) + 1
		//replace the degree of l2 by deg(l2)- L(l2) + 1
		
		//save for later
		int l1Degree = deg.get(l1.getParent());
		int l2Degree = deg.get(l2.getParent());
		
		deg.put(l1.getParent(), l1Degree - l2.size() + 1);
		deg.put(l2.getParent(), l2Degree - l2.size() + 1);
		
		
		//if P contains 2 bc nodes with degree at least 3 or 1 block
		//with degree at least 4, return true, else return false
		boolean ret = false;
		int nodesOfDegree3 = 0;
		int blocksOfDegree4 = 0;
		
		for (BCTreeNode node  : P)
			if (deg.get(node) == 3)
				nodesOfDegree3++;
			else if (node.getType() == VertexType.B && deg.get(node) == 4)
				blocksOfDegree4++;
		
		ret = nodesOfDegree3 == 2 || blocksOfDegree4 == 1;
		
		log.info("nodes of degree 3: " + nodesOfDegree3);
		log.info("blocks of degree 4: " + blocksOfDegree4);
		
		//changing back the degrees
		deg.put(l1.getParent(), l1Degree);
		deg.put(l2.getParent(), l2Degree);
		
		return ret;
	}
	
	/**
	 * Given a label l1, find a label, l2,  with which it can be matched
	 * so that planarity is not ruined
	 * l1 and l2 need to satisfy con cond
	 * @param l1
	 * @param labels
	 * @return
	 */
	private PlanarAugmentationLabel<V, E> matchLabels(PlanarAugmentationLabel<V, E> l1,
			List<PlanarAugmentationLabel<V, E>> labels){
		
		for (PlanarAugmentationLabel<V, E> l2 : labels)
			if (l2 == l1)
				continue;
			else if (conCond(l1, l2))
				return l2;
		
		return null;
		
	}
	
}
