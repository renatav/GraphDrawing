package graph.algorithms.planarity;

import graph.algorithms.numbering.STNumbering;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.tree.pq.PQNodeType;
import graph.tree.pq.PQTree;
import graph.tree.pq.PQTreeEdge;
import graph.tree.pq.PQTreeNode;
import graph.tree.pq.PQTreeReduction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PQTreePlanarity<V extends Vertex, E extends Edge<V>> extends PlanarityTestingAlgorithm<V, E>{

	private List<V> stOrder;
	private Graph<V,E> graph;
	/**
	 * A map containing graph (subgraphs G1,G2...Gn)
	 * and the lists of virtual edges turning them into
	 * Gk'
	 */
	private Map<Graph<V,E>, List<E>> gPrimMap; 

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

		System.out.println("s " + s);
		System.out.println("t " + t);

		//construct a PQ-tree corresponding to G1'
		Graph<V,E> g = constructGk(1);
		PQTree<V, E> T = new PQTree<>(g, gPrimMap.get(g));
		System.out.println(T);
		List<E> U = gPrimMap.get(g); //the list of edges whose lower numbered vertex is 1

		//S - the set of edges whose higher-numbered vertex is i
		List<PQTreeNode> S = new ArrayList<PQTreeNode>();
		//S' - the set of edges whose lower-numbered vertex is i
		List<PQTreeNode> Sprim = new ArrayList<PQTreeNode>();

		//S sets should contains leaves
		//leaves have higher indexes
		//if i is higher, add i node
		//if i is lower, add the other node 

		List<PQTreeNode> nodesWithContent = new ArrayList<PQTreeNode>();
		for (int i = 1; i < stOrder.size(); i++){

			//pq nodes adjacent to the j vertex will be added to S

			//pay attention to the fact that there could be more leaves that contain the same vertex

			nodesWithContent.clear();
			for (PQTreeNode node : T.getVertices())
				if (node.getContent() == stOrder.get(i))
					nodesWithContent.add(node);


			for (PQTreeNode iNode : nodesWithContent){
				System.out.println(iNode);
				for (PQTreeEdge edge : T.adjacentEdges(iNode)){
					PQTreeNode other = edge.getOrigin() == iNode ? edge.getDestination() : edge.getOrigin();
					System.out.println("other" + other);
					if (stOrder.indexOf(((V)other.getContent())) < i)
						S.add(iNode);
					else
						Sprim.add(other);
				}
			}
			System.out.println("S: " + S);

			//bubble(T,S)
			//reduce(T,S)
			PQTreeReduction<V, E> treeReduction = new PQTreeReduction<V,E>();
			if (!treeReduction.bubble(T, S) ||
					!treeReduction.reduce(T, S))
				return false;

			//TODO pogledati kada se desava da je root Q cvor
			//i srediti postavljanje root-a
			//inicijalno kada se kontruise je P-covr
			//a onda kasnije kako se menja, u redukciji?
			//ima tamo u onom kodu JGraphEd nesto sa menjanjem root-a
			//pogledati
			
			//S' - the set of edges whose lower-numbered index is i
			//if root(T,S) is a Q-node
			if (T.getRoot().getType() == PQNodeType.Q){
				//replace the full children of root(T,S) and their
				//descendants by T(S', S')
				System.out.println("Replace the full children of root(T,s) and their descentants by T(S', S')");
			}
			else{
				//else replace root(T,S) and its descendants by T(S', S')
				//basically, construct a new tree
				constructGk(i + 1);
				//U = U - S union S'
			}

		}



		return false;
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
}
