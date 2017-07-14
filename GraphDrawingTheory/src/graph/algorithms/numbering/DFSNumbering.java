package graph.algorithms.numbering;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.traversal.DFSTreeTraversal;
import graph.trees.DFSTree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Represents DFS numbering of a given graph
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type
 */
public class DFSNumbering<V extends Vertex, E extends Edge<V>> extends Numbering<V,E>{

	private DFSTree<V, E> dfsTree;
	
	public DFSNumbering(DFSTree<V, E> dfsTree){
		order = new ArrayList<V>();
		this.dfsTree = dfsTree;
		formOrder();
	}

	public DFSNumbering(Graph<V,E> graph){

		order = new ArrayList<V>();

		DFSTreeTraversal<V, E> traversal = new  DFSTreeTraversal<V,E>(graph);
		dfsTree = traversal.formDFSTree(graph.getVertices().get(0));
		formOrder();
	}
	
	private void formOrder(){


		order.addAll(dfsTree.getVertices());

		Collections.sort(order, new Comparator<V>(){

			@Override
			public int compare(V o1, V o2) {

				if (dfsTree.getVerticesWithIndexes().get(o1) < dfsTree.getVerticesWithIndexes().get(o2))
					return 1;
				else if  (dfsTree.getVerticesWithIndexes().get(o1) > dfsTree.getVerticesWithIndexes().get(o2))
					return -1;
				else
					return 0;
			}
		});
	}

}
