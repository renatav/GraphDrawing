package graph.algorithms.lrpartition;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.traversal.DFSTreeTraversal;
import graph.trees.DFSTree;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * A partition B = L U R of DFS oriented graph's 
 * back edges into two classes, referred to as left and right, is called left-right
 * partition, or LR partition for short
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type
 */
public class LRPartition<V extends Vertex, E extends Edge<V>> {

	private boolean canBeCreated;
	private List<E> right, left;
	private Graph<V,E> graph;
	private Logger log = Logger.getLogger(LRPartition.class);
	private boolean debug = true;

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
	 * @return {@code true} if it can be partitioned, {@code false} otherwise
	 */
	public boolean createLRPartition(){
		if (debug)
			log.info("creating traversal");
		DFSTreeTraversal<V, E> traversal = new  DFSTreeTraversal<V,E>(graph);
		if (debug){
			log.info("finished creating traversal");
			log.info("creating dfs tree");
		}
		DFSTree<V, E> tree = traversal.formDFSTree(graph.getVertices().get(0));
		if (debug){
			log.info("finished creating dfs tree");
			log.info(tree);
		}

		return createLRPartition(tree);
	}


	/**
	 * 
	 * @param tree Tree whose back edges are being partitioned
	 * @return {@code true} if it can be partitioned, {@code false} otherwise
	 */
	public boolean createLRPartition(DFSTree<V, E> tree){
		canBeCreated = true;
		right.clear();
		left.clear();

		if (debug)
			log.info(tree);

		LRPartitionSet<V, E> partitionSet = new LRPartitionSet<V,E>(tree);

		createLRPartitionNew(tree.getRoot(), partitionSet, tree);


		if (!partitionSet.organizePartitions())
			return false;
		
		partitionSet.printPartitions();

		return true;

	}

	private void createLRPartitionNew(V current, LRPartitionSet<V, E> partitionSet, DFSTree<V,E> tree){


		//log.info("\nProcessing " + current);

		List<E> outgoingEdges = tree.allOutgoingEdges(current);


		for (int i = 0; i < outgoingEdges.size(); i++)
			for (int j = i + 1; j < outgoingEdges.size(); j++){


				E e1 = outgoingEdges.get(i);
				E e2 = outgoingEdges.get(j);


				//log.info("processing edge pair: " + e1 + ", " + e2);

				int lowptE1 = tree.lowpt(e1);
				int lowptE2 = tree.lowpt(e2);

				List<E> class1 = new ArrayList<E>();
				List<E> class2 = new ArrayList<E>();

				//edges in each class should belong to the same (L or R) partition
				//and should be in different partitions than those in the other class


				for (E e : tree.returningEdges(e1)){

					//destination vertex is the one with lower index, regardless of which one was connected first;
					int index = Math.min(tree.getIndex(e.getDestination()), tree.getIndex(e.getOrigin()));
					if (index > lowptE2)
						class1.add(e);

				}

				for (E e : tree.returningEdges(e2)){
					//destination vertex is the one with lower index, regardless of which one was connected first;
					int index = Math.min(tree.getIndex(e.getDestination()), tree.getIndex(e.getOrigin()));
					if (index > lowptE1)
						class2.add(e);
				}

				partitionSet.add(class1, class2);
			}


		for (V v : tree.directDescendantsOf(current))
			createLRPartitionNew(v, partitionSet, tree);

	}


	/**
	 * Returns the right partition
	 * @return A list of edges in the right partition
	 */
	public List<E> getRight() {
		return right;
	}


	/**
	 * @param right Edges in the right partition
	 */
	public void setRight(List<E> right) {
		this.right = right;
	}


	/**
	 * Returns the left partition
	 * @return A list of edges in the left partition
	 */
	public List<E> getLeft() {
		return left;
	}

	/**
	 * @param left Edges in the left partition
	 */
	public void setLeft(List<E> left) {
		this.left = left;
	}

	public void printPartition(){
		System.out.println("in right partition: ");
		for (E e : right)
			System.out.println(" " + e);
		System.out.println("in left partition: ");
		for (E e : left)
			System.out.println(" " + e);
	}

	@Override
	public String toString() {
		if (!canBeCreated)
			System.out.println("LRPartition not created");
		return "LRPartition [right=" + right
				+ ", left=" + left + "]";
	}


}
