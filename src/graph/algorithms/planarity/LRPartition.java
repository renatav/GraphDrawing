package graph.algorithms.planarity;

import java.util.ArrayList;
import java.util.List;

import graph.elements.Edge;
import graph.elements.Vertex;
import graph.traversal.DFSTree;


/**
 * A partition
 * B = L U R of its back edges into two classes, referred to as left and right, is called left-right
 * partition , or LR partition for short, i
 * @author xxx
 */
public class LRPartition<V extends Vertex, E extends Edge<V>> {

	private DFSTree<V, E> tree;
	private boolean canBeCreated;
	private List<E> right, left;
	private enum Partition {LEFT, RIGHT};
	private Partition partition;


	public LRPartition(){
		right = new ArrayList<E>();
		left = new ArrayList<E>();
	}


	/**
	 * An LR partition is a partition of the back edges into Left and Right so
	 * that for every fork all return edges of e ending strictly
	 * higher than lowpt(e2) belong to one partition, and all return edges of e2 ending strictly
	 * higher than lowpt(e1) belong to the other.
	 * @param tree
	 */
	public boolean createLRPartition(DFSTree<V, E> tree){
		this.tree = tree;
		canBeCreated = true;
		right.clear();
		left.clear();

		return true;


	}

	private void createLRPartition(V current){

		if (!canBeCreated)
			return;

		List<E> outgoingEdges = tree.allOutgoingTreeEdges(current);


		//for every pair of outgoing edges, see if edges can be put in two distinct classes

		E e1, e2;
		List<E> edgesToBePlacedE1 = new ArrayList<E>();
		List<E> edgesToBePlacedE2 = new ArrayList<E>();
		
		for (int i = 0; i < outgoingEdges.size(); i++)
			for (int j = i + 1; j < outgoingEdges.size(); j++){

				Partition partition;
				
				e1 = outgoingEdges.get(i);
				e2 = outgoingEdges.get(j);

				int lowptE1 = tree.lowpt(e1);
				int lowptE2 = tree.lowpt(e2);
				
				edgesToBePlacedE1.clear();

				for (E retEdgeE1 : tree.returningEdges(e1)){
					int edgeEndIndex = Math.min(tree.getIndex(retEdgeE1.getOrigin()), tree.getIndex(retEdgeE1.getDestination()));
					if (edgeEndIndex < lowptE2) //ends higher
						edgesToBePlacedE1.add(retEdgeE1);							
				}
				
				for (E retEdgeE2 : tree.returningEdges(e2)){
					int edgeEndIndex = Math.min(tree.getIndex(retEdgeE2.getOrigin()), tree.getIndex(retEdgeE2.getDestination()));
					if (edgeEndIndex < lowptE1) //ends higher
						edgesToBePlacedE2.add(retEdgeE2);							
				}
				
				//treba smisliti kako se sada to sve rasporedi
				//da ne bude da se tako stavi samo random negde
				//pa posle ne moze zbog toga
				//a da je nekako drugacije, moglo bi
				
				

				//check if 
				
				}
			}






	public DFSTree<V, E> getTree() {
		return tree;
	}


	public void setTree(DFSTree<V, E> tree) {
		this.tree = tree;
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


}
