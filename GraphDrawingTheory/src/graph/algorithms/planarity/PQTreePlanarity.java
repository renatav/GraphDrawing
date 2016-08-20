package graph.algorithms.planarity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import graph.algorithms.numbering.STNumbering;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.traversal.DijkstraAlgorithm;
import graph.tree.pq.PQNodeType;
import graph.tree.pq.PQTree;
import graph.tree.pq.PQTreeEdge;
import graph.tree.pq.PQTreeNode;
import graph.tree.pq.PQTreeReduction;

public class PQTreePlanarity<V extends Vertex, E extends Edge<V>> extends PlanarityTestingAlgorithm<V, E>{

	private List<V> stOrder;
	private Graph<V,E> graph;

	/**
	 * A map containing graph (subgraphs G1,G2...Gn)
	 * and the lists of virtual edges turning them into
	 * Gk'
	 */
	private Map<Graph<V,E>, List<E>> gPrimMap; 

	private DijkstraAlgorithm<PQTreeNode, PQTreeEdge> dijkstra = new DijkstraAlgorithm<PQTreeNode, PQTreeEdge>();

	private Logger log = Logger.getLogger(PQTreePlanarity.class);

	@SuppressWarnings("unchecked")
	@Override
	public boolean isPlannar(Graph<V, E> graph) {

		this.graph = graph;
		gPrimMap = new HashMap<Graph<V,E>, List<E>>();
		
		//assign st-numbers to all vertices of G
		//s and t should be connected, but it is not stated
		//that they should meet any special condition
		//so, let st be the first edge
		E st = graph.getEdges().get(0);
		V s = st.getOrigin();
		V t = st.getDestination();
		
		STNumbering<V, E> stNumbering = new STNumbering<V,E>(graph, s, t);
		stOrder = stNumbering.getOrder();
		final Map<V, Integer> stMapping = stNumbering.getNumbering();

		log.info("s " + s);
		log.info("t " + t);
		log.info("st order: " + stOrder);

		//construct a PQ-tree corresponding to G1'
		Graph<V,E> g = constructGk(1);
		PQTree<V, E> T = new PQTree<>(g, gPrimMap.get(g), stMapping);

		//S - the set of edges whose higher-numbered vertex is j
		List<PQTreeNode> S = new ArrayList<PQTreeNode>();
		//S' - the set of edges whose lower-numbered vertex is j
		List<E> Sprim = new ArrayList<E>();

		PQTreeReduction<V, E> treeReduction = new PQTreeReduction<V,E>();

		List<PQTreeNode> fullChildren = new ArrayList<PQTreeNode>();

		List<PQTreeNode> children = new ArrayList<PQTreeNode>();

		//S sets should contains leaves
		//leaves have higher indexes
		//if j is higher, add j node
		//if j is lower, add the other node 

		List<PQTreeNode> nodesWithContent = new ArrayList<PQTreeNode>();
		for (int j = 1; j < stOrder.size() - 1; j++){

			log.info("Current j: " + j);
			log.info("Current tree:" + T);
			//pq nodes adjacent to the j vertex will be added to S

			//pay attention to the fact that there could be more leaves that contain the same vertex

			nodesWithContent.clear();
			for (PQTreeNode node : T.getVertices())
				if (node.getContent() == stOrder.get(j))
					nodesWithContent.add(node);

			S.clear();
			Sprim.clear();

			for (PQTreeNode jNode : nodesWithContent){
				System.out.println("jNode " + jNode);
				V content = (V) jNode.getContent();
				//don't just search the vertices of the tree, S' also needs to be populated
				for (E edge : graph.adjacentEdges(content)){
					V other = edge.getOrigin() == content ? edge.getDestination() : edge.getOrigin();
					if (stMapping.get(other) < j){
						if (!S.contains(jNode))
							S.add(jNode);
					}
					else{
						if (!Sprim.contains(edge))
							Sprim.add(edge);
					}
				}
			}

			log.info("S' " + Sprim);

			//sort S so that lower indexes are processed later
			//bottom up

			Collections.sort(S, new Comparator<PQTreeNode>() {

				@Override
				public int compare(PQTreeNode node1, PQTreeNode node2) {


					PQTreeNode parent1 = node1.getParent();
					PQTreeNode parent2 = node2.getParent();

					while (parent1.getContent() == null){
						parent1 = parent1.getParent();
					}

					while (parent2.getContent() == null){
						parent2 = parent2.getParent();
					}

					//parent are now P nodes
					V v1 = (V) parent1.getContent();
					V v2 = (V) parent2.getContent();


					if  (stMapping.get(v1) < stMapping.get(v2))
						return 1;
					else if  (stMapping.get(v1) == stMapping.get(v2))
						return 0;
					else return -1;

				}
			});

			log.info("S: " + S);

			PQTreeNode pertRoot = pertinentSubtreeRoot(T, S);
			log.info("PERTINENT SUBTREE ROOT: " + pertRoot);

			//bubble(T,S)
			//reduce(T,S)
			treeReduction.bubble(T, S);
			if (!treeReduction.reduce(T, S, pertRoot))
				return false;

			//S' - the set of edges whose lower-numbered index is i
			//if root(T,S) is a Q-node
			//that is, the check if the root of the pertinent subtree is a Q-node
			//a pertinent subtree is a tree of minimal height whose frontier contains all S nodes


			PQTreeNode newNode = constructTSprim(stOrder.get(j), Sprim);
			log.info("Constructed new node T(S',S')");
			log.info(newNode);

			T.addVertex(newNode);
			if (newNode.getType() != PQNodeType.LEAF){
				for (PQTreeNode child : newNode.getChildren()){
					T.addVertex(child);
					T.addEdge(new PQTreeEdge(newNode, child));
				}
			}

			//when removing certain nodes, it is not only important
			//to update the tree
			//but also list of children of nodes

			pertRoot = pertinentSubtreeRoot(T, S);
			if (pertRoot.getType() == PQNodeType.Q){
				//replace the full children of root(T,S) and their
				//descendants by T(S', S')
				log.info("Replace the full children of root(T,s) and their descentants by T(S', S')");
				
				System.out.println("CHILDREN: " + pertRoot.getChildren());


				//to prevent concurrent modification exception
				fullChildren.clear();
				fullChildren.addAll(pertRoot.getFullChildren());
				log.info("Full children: " + fullChildren);

				List<PQTreeNode> descendants = new ArrayList<PQTreeNode>();
				//find the minimal index of a full child
				int index = -1;
				for (PQTreeNode fullChild : fullChildren){
					descendants.addAll(T.allDescendantsOf(fullChild));
					int currentIndex = pertRoot.getChildren().indexOf(fullChild); 
					if (index == -1 || currentIndex < index)
						index = currentIndex;
					pertRoot.removeChild(fullChild);
				}
				if (index == -1)
					index = 0;
				
				//now add the full children
				descendants.addAll(fullChildren);
				log.info("All descendants: " + descendants);
				//all nodes in the descendants list should be replaced by
				//T(S',S')


				for (PQTreeNode descendant : descendants)
					T.removeVertex(descendant);

				//the order is important, so don't
				//just add the child at the end
				//replace the removed children
				
				pertRoot.addChild(newNode, index);
				T.addEdge(new PQTreeEdge(pertRoot, newNode));

				if (pertRoot.childrenCount() == 2){
					pertRoot.setType(PQNodeType.P);
					T.getqNodes().remove(pertRoot);
					T.getpNodes().add(pertRoot);
					PQTreeNode parent = pertRoot.getParent();
					if (parent != null){
						parent.getPartialChildren().remove(pertRoot);
					}
						
				}


				System.out.println(T);
			}
			else{
				//else replace root(T,S) and its descendants by T(S', S')
				List<PQTreeNode> descendants = T.allDescendantsOf(pertRoot);
				descendants.add(pertRoot);


				//remove all children of pertinent root
				if (pertRoot.getChildren() != null){
					children.clear();
					children.addAll(pertRoot.getChildren());
					for (PQTreeNode child : children)
						pertRoot.removeChild(child);
				}

				for (PQTreeNode descendant : descendants)
					T.removeVertex(descendant);		

				PQTreeNode parent = pertRoot.getParent();

				if (parent == null)
					T.setRoot(newNode);
				else{
					parent.addChild(newNode);
					T.addEdge(new PQTreeEdge(parent, newNode));
					parent.removeChild(pertRoot);
				}
			}

		}



		return true;
	}


	private Graph<V,E> constructGk(int k){
		//vertices in the subgraph
		List<V> vertices = stOrder.subList(0,k); //the second index is exclusive

		Graph<V,E> Gk = graph.subgraph(vertices);
		List<E> virtualEdges = new ArrayList<E>();
		for (E e : graph.getEdges())
			if (vertices.contains(e.getOrigin()) && !vertices.contains(e.getDestination()) || 
					vertices.contains(e.getDestination()) && !vertices.contains(e.getOrigin()))
				virtualEdges.add(e);

		gPrimMap.put(Gk, virtualEdges);
		return Gk;

	}

	private PQTreeNode constructTSprim(V v, List<E> Sprim){

		PQTreeNode newNode;

		if (Sprim.size() > 1){
			newNode = new PQTreeNode(PQNodeType.P, v);
			for (E edge : Sprim){
				V other = edge.getOrigin() == v ? edge.getDestination() : edge.getOrigin();
				PQTreeNode leafNode = new PQTreeNode(PQNodeType.LEAF, other);
				leafNode.setVirtualEdge(edge);
				newNode.addChild(leafNode);
			}
		}
		else{
			E edge = Sprim.get(0);
			V other = edge.getOrigin() == v ? edge.getDestination() : edge.getOrigin();
			newNode = new PQTreeNode(PQNodeType.LEAF, other);
			newNode.setVirtualEdge(edge);
		}

		return newNode;

	}

	/**
	 * Find the root of the subtree of minimum height that contains all nodes of S
	 * @param T Tree
	 * @param S Set of nodes that should be contained
	 * @return root of the pertinent subtree
	 */
	private PQTreeNode pertinentSubtreeRoot(PQTree<V,E> T, List<PQTreeNode> S){

		log.info("Finding the pertinent subtree root");

		//trivial case
		if (S.size() == 1)
			return S.get(0);


		//find path from each of the nodes of the set to the root of the tree and
		//see where they come together
		dijkstra.setEdges(T.getEdges());
		List<List<PQTreeNode>> paths = new ArrayList<List<PQTreeNode>>();

		//System.out.println(T.getEdges());

		for (PQTreeNode node : S){
			//System.out.println("node " + node);
			List<PQTreeNode> path = dijkstra.getPath(node, T.getRoot()).pathVertices();
			//System.out.println("path " + path);
			paths.add(path);
		}

		List<PQTreeNode> aPath = paths.get(0);

		PQTreeNode root = null;
		PQTreeNode current = null;

		for (int i = aPath.size() - 1; i > 0; i--){
			boolean doesnContain = false;
			current = aPath.get(i);

			for (List<PQTreeNode> path : paths){
				if (!path.contains(current)){
					doesnContain = true;
					break;
				}
			}

			if (doesnContain)
				break;
			root = current;
		}

		return root;
	}

}
