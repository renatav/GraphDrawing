package graph.algorithms.connectivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import graph.algorithms.planarity.BoyerMyrvoldPlanarity;
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
import graph.tree.bc.BCNodeType;
import graph.util.Util;

/**
 * Given a connected, but not biconnected planar graph, the algorithm
 * adds a minimum number of edges to turn it into a biconnected graph
 * while not ruining the planarity
 * This implementation is based on Fialko and Mutzel's 5/3 approximation
 */
public class PlanarAugmentation<V extends Vertex, E extends Edge<V>> {

	/**
	 * Algorithm used to check the planarity
	 */
	private PlanarityTestingAlgorithm<V, E> planarityTest;
	/**
	 * Boyer-Myrvold planarity, used to determine outside vertices of certain blocks
	 */
	private BoyerMyrvoldPlanarity<V, E> boyerMyrvold;
	/**
	 * Block-cut vertex tree used in the algorithm
	 */
	private BCTree<V, E> bcTree;
	/**
	 * A map of nodes and their degrees
	 */
	private Map<BCTreeNode, Integer> deg;

	private boolean debug = false;

	private Logger log = Logger.getLogger(PlanarAugmentation.class);


	public PlanarAugmentation(){
		planarityTest = new FraysseixMendezPlanarity<V,E>();
		boyerMyrvold = new BoyerMyrvoldPlanarity<V,E>();
	}


	/**
	 * 
	 * @param graph Graph that should be connected and planar that is to be turned into a biconnected graph
	 * @return Edges to be added
	 * @throws CannotBeAppliedException
	 */
	@SuppressWarnings("unchecked")
	public List<E> planarBiconnected(Graph<V,E> graph) throws CannotBeAppliedException{
		if (!planarityTest.isPlannar(graph))
			throw new CannotBeAppliedException("Graph is not planar");

		//technically, if it is not it can first be made connected
		//in a similar fashion to this algorithm
		//for now, only apply the algorithm if the graph is already connected
		if (!graph.isConnected())
			throw new CannotBeAppliedException("Graph is not connected");



		//keep a track of all created edges between vertices of blocks
		//and add them when merging the blocks
		List<E> addedEdges = new ArrayList<E>();

		if (graph.isBiconnected())
			return addedEdges;

		//step one
		//Construct the block tree
		//initialize P (list containing all pendants in T)
		//pendant is a block which contains only one cut vertex

		if (debug){
			log.info("initialization");
			log.info("Cut vertices: " + graph.listCutVertices());
			log.info("Blocks: " + graph.listBiconnectedComponents());
		}

		bcTree = new BCTree<V,E>(graph);
		
		if (debug)
			log.info(bcTree);

		Graph<V,E> ret = Util.copyGraph(graph);

		//degrees will be modified so a map which holds them is used and initialized here
		deg = new HashMap<BCTreeNode, Integer>();

		for (BCTreeNode bcNode : bcTree.getVertices()){
			deg.put(bcNode, bcTree.vertexDegree(bcNode));
		}


		List<Graph<V,E>> pendants = bcTree.getPendants(); 
		//initialize the list of labels by calling reduce chains
		List<PlanarAugmentationLabel<V, E>> labels = reduceChains(graph, pendants);
		PlanarAugmentationLabel<V, E> l1, l2;

		Set<BCTreeNode> pathNodes = new HashSet<BCTreeNode>();

		BCTreeNode pPrimNode;
		Set<BCTreeNode> cutVertices = new HashSet<BCTreeNode>();

		//while T(G) has c-vertices
		while(bcTree.getcVertices().size() > 0){
			//l1 - label with maximum size
			//l2 - match_labels(l1)

			l1 = labels.get(0); 
			for (int i = 1; i < labels.size(); i++){
				PlanarAugmentationLabel<V, E> label = labels.get(i);
				if (labels.size() > l1.size())
					l1 = label;
			}

			if (debug)
				log.info("l1: " + l1);

			l2 = matchLabels(l1, labels); //search for a partner label for l1

			if (debug)
				log.info("l2: " + l2);

			//if there is no label that can be matched to l1
			if (l2 == null){
				//Add L(l1) - 1 (size(l1) - 1) edges from an outside node
				//in the first child of l1 to the outside nodes of other children of l1
				//merge all bc nodes along the paths between l1 and the children of l1
				//and the children themselves to a new pendant p'

				//don't connect cut vertices, connect vertices inside blocks
				//all children are pendants, which means that 
				//they only have one cut vertex and one edge

				BCTreeNode c1 = l1.getParent();
				Graph<V,E> firstChild = l1.getChildren().get(0);
				boyerMyrvold.isPlannar(firstChild);
				List<V> outsideFace = boyerMyrvold.getOutsideFace();
				BCTreeEdge edge = bcTree.adjacentEdges(bcTree.getVertexByContent(firstChild)).get(0);
				BCTreeNode cutNode = edge.getOrigin() == bcTree.getVertexByContent(firstChild) ? edge.getDestination() : edge.getOrigin();
				outsideFace.remove(cutNode.getContent());
				V v1 = outsideFace.get(0);

				pathNodes.clear();

				for (int i = 1; i < l1.getChildren().size(); i++){
					Graph<V,E> otherChild = l1.getChildren().get(i);
					boyerMyrvold.isPlannar(otherChild);
					outsideFace = boyerMyrvold.getOutsideFace();
					edge = bcTree.adjacentEdges(bcTree.getVertexByContent(otherChild)).get(0);
					cutNode = edge.getOrigin() == bcTree.getVertexByContent(firstChild) ? edge.getDestination() : edge.getOrigin();
					outsideFace.remove(cutNode.getContent());
					V v2 = outsideFace.get(0);
					E newEdge = Util.createEdge(v1, v2, graph.getEdges().get(0).getClass());
					addedEdges.add(newEdge);
					ret.addEdge(newEdge);
					if (debug)
						log.info("added edge " + newEdge); 
					List<BCTreeNode> currentPath = GraphTraversal.nonrecursiveDFSPath(bcTree, bcTree.getVertexByContent(firstChild), 
							bcTree.getVertexByContent(otherChild)).getUniqueVertices();
					pathNodes.addAll(currentPath);
				}

				//now add path between the children themselves
				//and also between l1 and the children
				List<BCTreeNode> currentPath;
				currentPath = GraphTraversal.nonrecursiveDFSPath(bcTree, bcTree.getVertexByContent(firstChild), 
						l1.getParent()).getUniqueVertices();
				pathNodes.addAll(currentPath);
				for (int i = 0; i < l1.getChildren().size(); i++){
					Graph<V,E> child1 = l1.getChildren().get(i);
					currentPath = GraphTraversal.nonrecursiveDFSPath(bcTree, l1.getParent(), bcTree.getVertexByContent(child1)).pathVertices();
					pathNodes.addAll(currentPath);

					//if a nodes on the path is not a child (and it can't be since only pendants are children of a label)
					//and since the nodes are supposed to be merged to form a new pendant
					//the formed graph (block) must be biconnected
					//that means that these non-pendant blocks on the path also need to be connected
					//to other blocks
					if (debug){
						log.info("l1 child " + child1 + " path: " + currentPath);
						log.info("Connect blocks on this path");
					}
					for (int j = 1; j < currentPath.size() - 2; j+=2){
						Graph<V,E> block1 = (Graph<V, E>) currentPath.get(j).getContent();
						Graph<V,E> block2 = (Graph<V, E>) currentPath.get(j + 2).getContent();
						boyerMyrvold.isPlannar(block1);
						V cutVertex = (V) currentPath.get(j + 1).getContent();
						List<V> face1 = boyerMyrvold.getOutsideFace();
						face1.remove(cutVertex);
						boyerMyrvold.isPlannar(block2);
						List<V> face2 = boyerMyrvold.getOutsideFace();
						face2.remove(cutVertex);
						V block1V = face1.get(0);
						V block2V = face2.get(0);
						E newEdge = Util.createEdge(block1V, block2V, graph.getEdges().get(0).getClass());
						if (debug)
							log.info("added edge: " + newEdge);
						addedEdges.add(newEdge);
						ret.addEdge(newEdge);
					}
					if (i == 0)
						continue;

					if (i < l1.getChildren().size() - 1)
						for (int j = 2; j < l1.getChildren().size(); j++){
							Graph<V,E> child2 = l1.getChildren().get(j);
							currentPath = GraphTraversal.nonrecursiveDFSPath(bcTree, bcTree.getVertexByContent(child1), 
									bcTree.getVertexByContent(child2)).getUniqueVertices();
							pathNodes.addAll(currentPath);
						}
				}

				if (debug)
					log.info("All nodes on paths: "  + pathNodes);

				//merge all bc nodes on the paths to one new pendant p'
				Graph<V,E> pPrim = new Graph<V,E>();
				mergeNodes(pathNodes, pPrim);
				for (E e : addedEdges)
					pPrim.addEdge(e);

				if (debug)
					log.info("P' " + pPrim);

				//after merging the nodes, the bc tree should be updated
				//the old nodes removed, the new ones added
				//if l1 is c1-label
				//c1 is the only cut vertex of the new block
				pPrimNode = new BCTreeNode(pPrim);
				//set parent of the node
				pPrimNode.setParent(l1.getParent());
				pPrimNode.setType(BCNodeType.B);

				bcTree.addVertex(pPrimNode);
				//connect the new node to c1
				BCTreeEdge pPrimEdge = new BCTreeEdge(c1, pPrimNode);
				bcTree.addEdge(pPrimEdge);

				if (debug){
					log.info("BCTree after merging blocks: ");
					log.info(bcTree);
				}

				//f - the cut vertex found in the first pass of reduce_chains(p')
				BCTreeNode f = reduceChainsFirstPass(graph, pPrim);

				if (debug)
					log.info("f: " + f);
				//b - the block adjacent to f but not belonging to the path {p', f)
				List<BCTreeNode> pPrimFPath = GraphTraversal.nonrecursiveDFSPath(bcTree, pPrimNode, f).getUniqueVertices();

				if (debug)
					log.info("p' f path " + pPrimFPath);

				BCTreeNode b = null;
				for (BCTreeEdge bcEdge : bcTree.adjacentEdges(f)){
					BCTreeNode otherNode = bcEdge.getOrigin() == f ? bcEdge.getDestination() : bcEdge.getOrigin();
					if (otherNode.getType() == BCNodeType.B)
						if (!pPrimFPath.contains(otherNode)){
							b = otherNode;
							break;
						}
				}

				//add an edge from an outside node of p' to a node of b adjacent to f
				//choose a node of p' that is adjacent to c1

				v1 = pPrim.adjacentVertices((V)c1.getContent()).get(0);

				Graph<V,E> bBlock = (Graph<V, E>) b.getContent();
				V v2 = bBlock.adjacentVertices((V) f.getContent()).get(0);
				E newEdge = Util.createEdge(v1, v2, graph.getEdges().get(0).getClass());

				if (debug)
					log.info("Adding new edge " + newEdge);
				addedEdges.add(newEdge);
				ret.addEdge(newEdge);

				pathNodes.clear();
				List<BCTreeNode> pPrimBPath = GraphTraversal.nonrecursiveDFSPath(bcTree, pPrimNode, b).getUniqueVertices();
				pathNodes.add(pPrimNode);
				pathNodes.add(b);
				pathNodes.addAll(pPrimBPath);

				//p' - construct in T(G) a new block from p', b and bc_nodes along the path {p', b)
				if (debug)
					log.info("Forming new p' from " + pathNodes);

				initCutVerticesOnPath(pathNodes, cutVertices);


				Graph<V,E> pPrimNew = new Graph<V,E>();
				mergeNodes(pathNodes, pPrimNew);
				pPrimNew.addEdge(newEdge);
				pPrimNode = new BCTreeNode(pPrimNew);
				bcTree.addVertex(pPrimNode);
				pPrimNode.setType(BCNodeType.B);

				setNewParents(cutVertices, pathNodes, pPrimNode);

				//new root of the tree
				if (pPrimNode.getParent() == null)
					bcTree.setRoot(pPrimNode);

				if (debug){
					log.info("Created new p' node: " + pPrimNode);
					log.info("BC tree after creation of a new p' node " + bcTree);
				}
			}


			else{ //l2 is the partner label of l1
				//connect every child of l2 with a child of l1
				//so, not with every child of l1
				//let's merge all of the with the first child of l1
				//because it is not specified with which child
				//revise this if the result is not satisfactory

				List<V> outsideFace1, outsideFace2;
				addedEdges.clear();
				Graph<V,E> l1Child = l1.getChildren().get(0);
				boyerMyrvold.isPlannar(l1Child);
				outsideFace1 = boyerMyrvold.getOutsideFace();
				BCTreeEdge edge = bcTree.adjacentEdges(bcTree.getVertexByContent(l1Child)).get(0);
				BCTreeNode cutNode = edge.getOrigin() == bcTree.getVertexByContent(l1Child) ? edge.getDestination() : edge.getOrigin();
				outsideFace1.remove(cutNode.getContent());
				V v1 = outsideFace1.get(0);


				if (debug){
					log.info("labels l1 " + l1 + " and l2 " + l2);
					log.info("choose child " + l1Child + " of l1 to merge");
				}

				//list of l1 and l2 children to be merged
				List<BCTreeNode> l1ChildNodes = new ArrayList<BCTreeNode>(), l2ChildNodes = new ArrayList<BCTreeNode>();
				l1ChildNodes.add(bcTree.getVertexByContent(l1Child));

				for (Graph<V,E> l2Child : l2.getChildren()){
					l2ChildNodes.add(bcTree.getVertexByContent(l2Child));
					boyerMyrvold.isPlannar(l2Child);
					outsideFace2 = boyerMyrvold.getOutsideFace();
					edge = bcTree.adjacentEdges(bcTree.getVertexByContent(l2Child)).get(0);
					cutNode = edge.getOrigin() == bcTree.getVertexByContent(l2Child) ? edge.getDestination() : edge.getOrigin();
					outsideFace2.remove(cutNode.getContent());
					V v2 = outsideFace2.get(0);
					E newEdge = Util.createEdge(v1, v2, graph.getEdges().get(0).getClass());
					addedEdges.add(newEdge);
					if (debug)
						log.info("adding edge " + newEdge);
					ret.addEdge(newEdge);
				}

				pathNodes.clear();
				//merge all connected pendants, all bc nodes on the path
				//between them and their parents (l1 or l2), l1, l2 and all bc nodes
				//on the path {l1,l2) to a new block p'

				BCTreeNode l1Parent = l1.getParent();
				BCTreeNode l2Parent = l2.getParent();

				for (BCTreeNode node : l1ChildNodes){
					List<BCTreeNode> currentPath = GraphTraversal.nonrecursiveDFSPath(bcTree, node, l1Parent).getUniqueVertices();
					currentPath.remove(l1Parent);
					pathNodes.addAll(currentPath);
				}

				for (BCTreeNode node : l2ChildNodes){
					List<BCTreeNode> currentPath = GraphTraversal.nonrecursiveDFSPath(bcTree, node, l2Parent).getUniqueVertices();
					pathNodes.addAll(currentPath);
				}

				List<BCTreeNode> currentPath = GraphTraversal.nonrecursiveDFSPath(bcTree, l1Parent, l2Parent).getUniqueVertices();
				pathNodes.addAll(currentPath);

				if (debug)
					log.info("Should merge all on path " + pathNodes);


				//now to determine how to connect it
				//try the same logic as before

				initCutVerticesOnPath(pathNodes, cutVertices);


				Graph<V,E> pPrim = new Graph<V,E>();
				mergeNodes(pathNodes, pPrim);
				for (E e : addedEdges)
					pPrim.addEdge(e);

				pPrimNode = new BCTreeNode(pPrim);
				pPrimNode.setType(BCNodeType.B);
				bcTree.addVertex(pPrimNode);

				setNewParents(cutVertices, pathNodes, pPrimNode);

				if (pPrimNode.getParent() == null)
					bcTree.setRoot(pPrimNode);

				if (debug){
					log.info("p' node: " + pPrimNode);
					log.info("BC tree after creation of a new p' node " + bcTree);
				}
			}


			//Updates after the main part is completed

			//update degrees
			deg.clear();
			for (BCTreeNode bcNode : bcTree.getVertices()){
				deg.put(bcNode, bcTree.vertexDegree(bcNode));
			}
			//update pendants and labels
			bcTree.updatePendants();
			pendants = bcTree.getPendants();
			labels = reduceChains(ret, pendants);

			if (l2 != null && l1.size() == l2.size()) //l1 has no more children
				labels.remove(l1); //remove l1 form the list of labels

		}

		return addedEdges;
	}

	/**
	 * Sets new parents of bc tree nodes after nodes on the path are merged into a new node
	 * @param cutVertices
	 * @param pathNodes
	 * @param pPrimNode
	 */
	private void setNewParents(Set<BCTreeNode> cutVertices, Set<BCTreeNode> pathNodes, BCTreeNode pPrimNode){

		//the edges in the bc trees are between cut vertices and blocks that contain them
		//if a cut vertex is on a lower level than the block, the block is its parent
		//otherwise, the vertex is the parent
		//so, some cut vertices of this block will have it as the parent
		//the cut vertex that has none of the blocks of the path as its parent
		//should be the new parent of the block

		for (BCTreeNode cNode : cutVertices){
			BCTreeEdge newPPrimEdge = new BCTreeEdge(pPrimNode, cNode);
			bcTree.addEdge(newPPrimEdge);
			if (cNode.getParent() == null || !pathNodes.contains(cNode.getParent()))
				pPrimNode.setParent(cNode);
			else if (cNode.getParent() != null)
				cNode.setParent(pPrimNode); //also updating the parent of the cut vertex since its parent was merged

		}
	}

	/**
	 * All bc tree nodes on the path are to be merged
	 * Some of the cut vertices will no longer be that
	 * If all blocks that contained them are merged and connected
	 * This method finds the vertices which are still cut vertices
	 * That is, checks if a c node is connected to some other block
	 * not on the path
	 * @param pathNodes Nodes on the path
	 * @param cutVertices A set if cut vertices that is initialized
	 */
	private void initCutVerticesOnPath(Set<BCTreeNode> pathNodes, Set<BCTreeNode> cutVertices){
		cutVertices.clear();
		for (BCTreeNode pathNode : pathNodes){
			if (pathNode.getType() == BCNodeType.B){
				for (BCTreeEdge bcEdge : bcTree.adjacentEdges(pathNode)){
					BCTreeNode other = bcEdge.getOrigin() == pathNode ? bcEdge.getDestination() : bcEdge.getOrigin();
					if (other.getType() == BCNodeType.C){
						//see if this vertex has an edge to a block not on the current path
						for (BCTreeEdge cutVertexEdge : bcTree.adjacentEdges(other)){
							BCTreeNode connected = cutVertexEdge.getOrigin() == other ? cutVertexEdge.getDestination() : cutVertexEdge.getOrigin();
							if (!pathNodes.contains(connected)){
								cutVertices.add(other);
								break;
							}
						}
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void mergeNodes(Set<BCTreeNode> nodes, Graph<V,E> constructedGraph){
		if (debug)
			log.info("Merge nodes " + nodes);
		if (nodes.size() == 1)
			return;
		for (BCTreeNode pathNode : nodes){
			if (debug)
				log.info("curent node: " + pathNode);
			if (pathNode.getType() == BCNodeType.B){
				Graph<V,E> currentPendant = (Graph<V, E>) pathNode.getContent();
				if (debug)
					log.info("curent pendant: " + currentPendant);
				for (V v : currentPendant.getVertices())
					constructedGraph.addVertex(v);
				for (E e : currentPendant.getEdges())
					constructedGraph.addEdge(e);
				//removing the node from the tree
				bcTree.removeVertex(pathNode);
			}
		}

		//if node is a c node
		//and it is not adjacent to any edges other than the ones that were on the path
		//it is no longer a cut vertex, so remove it 
		for (BCTreeNode pathNode : nodes)
			if (pathNode.getType() == BCNodeType.C && bcTree.adjacentEdges(pathNode).size() == 0)
				bcTree.removeVertex(pathNode);
	}


	@SuppressWarnings("unchecked")
	private BCTreeNode reduceChainsFirstPass(Graph<V,E> graph, Graph<V,E> p){
		//c - the cut vertex adjacent to p
		BCTreeNode pendantNode = bcTree.getVertexByContent(p);
		BCTreeNode c = null;
		for (BCTreeNode adj : bcTree.adjacentVertices(pendantNode)){
			if (adj.getType() == BCNodeType.C){
				c = adj;
				break;
			}
		}

		if (debug)
			log.info("Cut vertex adjacent to the pendant " + c);

		//while deg(c) = 2 and c is not the root of T(G)

		BCTreeNode lastC = null;

		while (c != bcTree.getRoot() && deg.get(c) == 2){

			//if c is a b-vertex
			if (c.getType() == BCNodeType.B){
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

		if (debug){
			log.info("c: " + c);
			log.info("last cut vertex: " + lastC);
		}

		//if c is a b-vertex
		if (c.getType() == BCNodeType.B){
			//c - the last cut vertex that we have reached
			c = lastC;
		}

		return c;

	}

	/**
	 * Finds all labels in T(G). Applying this procedure to one pendant p too.
	 * That way, the first loop (first) pass) is only run for p and the second
	 * loop (second pass) only for the cut vertex c found in the first loop
	 * @param pendants
	 * @param labels List of labels to be updated or formed
	 */
	private List<PlanarAugmentationLabel<V, E>> reduceChains(Graph<V,E> graph, List<Graph<V,E>> pendants){

		//for each p in pendants
		//the first pass

		List<PlanarAugmentationLabel<V, E>> labels = 
				new ArrayList<PlanarAugmentationLabel<V,E>>();

		Map<BCTreeNode, PlanarAugmentationLabel<V,E>> nodeLabelsMap = 
				new HashMap<BCTreeNode, PlanarAugmentationLabel<V,E>>();
		for (PlanarAugmentationLabel<V,E> label : labels)
			nodeLabelsMap.put(label.getParent(), label);

		for (Graph<V,E> p : pendants){

			if (debug)
				log.info("Current pendant " + p );

			BCTreeNode c = reduceChainsFirstPass(graph, p);

			//c is a c-label
			//p is a child of c
			PlanarAugmentationLabel<V, E> cLabel = new PlanarAugmentationLabel<V,E>(c);
			nodeLabelsMap.put(c, cLabel);
			cLabel.addChild(p);
			labels.add(cLabel);

			if (debug)
				log.info("Created label " + cLabel);
		}

		//second pass
		//for each c-label found in the first pass
		//not iterating through the list since it will be changed later

		if (debug)
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
				if (other.getType() == BCNodeType.B && deg.get(other) == 3){
					block = other;
					break;
				}
			}

			if (block == null)
				continue;

			if (debug)
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

			if (debug)
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

			if (debug)
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

		if (debug)
			log.info("Checking if labels " + l1 + " and " + l2 + " can be matched planar");

		//if l1 and l2 are c labels
		//return planar(G+(l1,l2))
		if (l1.getParent().getType() == BCNodeType.C && l2.getParent().getType() == BCNodeType.C){
			if (debug)
				log.info("Two c labels");
			return checkPlanarity(graph, (V) l1.getParent().getContent(), (V) l2.getParent().getContent());
		}

		//let v1 be a b-label and c11 and c12 cut vertices
		//adjacent to 2 children of l1 in T(G)

		//at least one of them is a block
		PlanarAugmentationLabel<V, E> blockLabel = l1.getType() == BCNodeType.B ? l1 : l2;
		PlanarAugmentationLabel<V, E> otherLabel = blockLabel == l1 ? l2 : l1;

		if (debug){
			log.info(blockLabel + " is B label");
			log.info("Finding c11 and c12");
		}

		V c11 = null, c12 = null;
		for (Graph<V,E> pendant : blockLabel.getChildren()){
			BCTreeNode pendantNode = bcTree.getVertexByContent(pendant);
			for (BCTreeEdge bcEdge : bcTree.adjacentEdges(pendantNode)){
				BCTreeNode other = bcEdge.getOrigin() == pendantNode ? bcEdge.getDestination() : bcEdge.getOrigin();
				if (other.getType() == BCNodeType.C){
					if (c11 == null)
						c11 = (V) other.getContent();
					else
						c12 = (V) other.getContent();
					break;
				}
			}
		}

		if (debug){
			log.info("c11 " + c11);
			log.info("c12 " + c12);
		}

		//if l2 is a c-label
		if (otherLabel.getType() == BCNodeType.C){
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
					if (other.getType() == BCNodeType.C){
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

		if (debug)
			log.info("Checking con cond for labels " + l1 + " and " + l2);

		//P - compute the path {l1,l2} in T(G)
		List<BCTreeNode> P = GraphTraversal.nonrecursiveDFSPath(bcTree, l1.getParent(), l2.getParent()).getUniqueVertices();
		
		if (debug)
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
			else if (node.getType() == BCNodeType.B && deg.get(node) == 4)
				blocksOfDegree4++;

		ret = nodesOfDegree3 == 2 || blocksOfDegree4 == 1;

		if (debug){
			log.info("nodes of degree 3: " + nodesOfDegree3);
			log.info("blocks of degree 4: " + blocksOfDegree4);
		}

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
