package graph.algorithms.planarity;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.traversal.DFSTree;
import graph.traversal.DFSTreeTraversal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * A partition
 * B = L U R of its back edges into two classes, referred to as left and right, is called left-right
 * partition , or LR partition for short, i
 * @author xxx
 */
public class LRPartition<V extends Vertex, E extends Edge<V>> {

	private boolean canBeCreated;
	private List<E> right, left;
	private Graph<V,E> graph;

	public LRPartition(Graph<V,E> graph){
		right = new ArrayList<E>();
		left = new ArrayList<E>();
		this.graph = graph;
	}

	/**
	 * An LR partition is a partition of the back edges into Left and Right so
	 * that for every fork all return edges of e ending strictly
	 * higher than lowpt(e2) belong to one partition, and all return edges of e2 ending strictly
	 * higher than lowpt(e1) belong to the other.
	 * @param graph Graph that should be partitioned
	 * @return true if it can be partitioned, false otherwise
	 */
	public boolean createLRPartition(){
		DFSTreeTraversal<V, E> traversal = new  DFSTreeTraversal<V,E>(graph);
		DFSTree<V, E> tree = traversal.formDFSTree(graph.getVertices().get(0));
		createLRPartition(tree);
		System.out.println(toString());
		return canBeCreated;
	}


	/**
	 * 
	 * @param tree Tree whose back edges are being partitioned
	 * @return true if it can be partitioned, false otherwise
	 */
	public boolean createLRPartition(DFSTree<V, E> tree){
		canBeCreated = true;
		right.clear();
		left.clear();

		//System.out.println(tree.getBackEdges());

		Map<E, LRPartitionEdge<V,E>> edgesMap = new HashMap<E, LRPartitionEdge<V,E>>();
		createLRPartition(tree.getRoot(), edgesMap, tree);

		if (!canBeCreated)
			return false;

		//formed edges map, now iterate through it and divide back edges into two classes
		for (E e : tree.getBackEdges())
			placeInPartition(e, edgesMap);

		return true;

	}

	private void createLRPartition(V current, Map<E, LRPartitionEdge<V,E>> edgesMap, DFSTree<V, E> tree){
		if (!canBeCreated)
			return;

		List<E> outgoingEdges = tree.allOutgoingEdges(current);

		//for every pair of outgoing edges, see if edges can be put in two distinct classes

		E e1, e2;
		Set<E> edgesToBePlacedE1 = new HashSet<E>();
		Set<E> edgesToBePlacedE2 = new HashSet<E>();


		for (int i = 0; i < outgoingEdges.size(); i++)
			for (int j = i + 1; j < outgoingEdges.size(); j++){

				e1 = outgoingEdges.get(i);
				e2 = outgoingEdges.get(j);
				
				int lowptE1 = tree.lowpt(e1);
				int lowptE2 = tree.lowpt(e2);
				int highptE1 = tree.highpt(e1);
				int highptE2 = tree.highpt(e2);

				edgesToBePlacedE1.clear();
				edgesToBePlacedE2.clear();

				for (E retEdgeE1 : tree.returningEdges(e1)){
					int edgeEndIndex = Math.min(tree.getIndex(retEdgeE1.getOrigin()), tree.getIndex(retEdgeE1.getDestination()));
					if (edgeEndIndex < lowptE2){ //ends higher
						edgesToBePlacedE1.add(retEdgeE1);
					}
					else if (tree.getBackEdges().contains(e2) && edgeEndIndex > lowptE2 && edgeEndIndex < highptE2){
						edgesToBePlacedE1.add(retEdgeE1);
						edgesToBePlacedE2.add(e2);
					}
				}
				


				for (E retEdgeE2 : tree.returningEdges(e2)){
					int edgeEndIndex = Math.min(tree.getIndex(retEdgeE2.getOrigin()), tree.getIndex(retEdgeE2.getDestination()));
					if (edgeEndIndex < lowptE1) //ends higher
						edgesToBePlacedE2.add(retEdgeE2);		
					else if (tree.getBackEdges().contains(e1) && edgeEndIndex > lowptE1 && edgeEndIndex < highptE1){
						edgesToBePlacedE2.add(retEdgeE2);
						edgesToBePlacedE1.add(e1);
					}
				}
				
				System.out.println("E1: " + e1);
				System.out.println("E2: " + e2);
				System.out.println(edgesToBePlacedE1);
				System.out.println(edgesToBePlacedE2);
				


				//for every edge form lists of edges
				//that must and can't be in the same class
				//which will later be used form classes 

				LRPartitionEdge<V,E> lrPartitionEdge;
				for (E e : edgesToBePlacedE1){
					lrPartitionEdge = edgesMap.get(e);
					if (lrPartitionEdge == null){
						lrPartitionEdge = new LRPartitionEdge<V,E>(e);
						edgesMap.put(e, lrPartitionEdge);
					}

					if (!lrPartitionEdge.addToSame(edgesToBePlacedE1)){
						canBeCreated = false;
						return;
					}

					if (!lrPartitionEdge.addToDifferent(edgesToBePlacedE2)){
						canBeCreated = false;
						return;
					}

				}

				for (E e : edgesToBePlacedE1){
					lrPartitionEdge = edgesMap.get(e);
					if (lrPartitionEdge == null){
						lrPartitionEdge = new LRPartitionEdge<V,E>(e);
						edgesMap.put(e, lrPartitionEdge);
					}

					if (!lrPartitionEdge.addToSame(edgesToBePlacedE1)){
						canBeCreated = false;
						return;
					}

					if (!lrPartitionEdge.addToDifferent(edgesToBePlacedE2)){
						canBeCreated = false;
						return;
					}

				}

				for (E e : edgesToBePlacedE2){
					lrPartitionEdge = edgesMap.get(e);
					if (lrPartitionEdge == null){
						lrPartitionEdge = new LRPartitionEdge<V,E>(e);
						edgesMap.put(e, lrPartitionEdge);
					}

					if (!lrPartitionEdge.addToSame(edgesToBePlacedE2)){
						canBeCreated = false;
						return;
					}

					if (!lrPartitionEdge.addToDifferent(edgesToBePlacedE1)){
						canBeCreated = false;
						return;
					}
				}


			}

		for (V desc : tree.directDescendantsOf(current))
			createLRPartition(desc, edgesMap, tree);
	}


	private void placeInPartition(E edge, Map<E, LRPartitionEdge<V, E>> edgesMap){
		if (edgesMap.get(edge) != null){
			List<E> sameList = edgesMap.get(edge).getSame();
			List<E> differentList = edgesMap.get(edge).getDifferent();


			if (sameList.size() < differentList.size()){
				for (E e : sameList){
					if (left.contains(e)){ //place in left
						left.add(edge);
						return;
					}
					if (right.contains(e)){
						right.add(edge);
						return;
					}
				}
			}

			//check different know
			for (E e : differentList){
				if (left.contains(e)){ //place in right
					right.add(edge);
					return;
				}
				if (right.contains(e)){
					left.add(edge);
					return;
				}
			}

		}
		
		
		//doesn't matter, place anywhere
		left.add(edge);

	}


	public List<E> getRight() {
		return right;
	}


	public void setRight(List<E> right) {
		this.right = right;
	}


	public List<E> getLeft() {
		return left;
	}


	public void setLeft(List<E> left) {
		this.left = left;
	}

	@Override
	public String toString() {
		if (!canBeCreated)
			System.out.println("LRPartition not created");
		return "LRPartition [right=" + right
				+ ", left=" + left + "]";
	}


}
