package graph.layout.automatic;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.layout.LayoutAlgorithms;
import graph.tree.binary.BinaryTree;

import java.util.List;

public class LayoutPicker<V extends Vertex, E extends Edge<V>> {
	
	private double balloonFactor = 0.75;

	@SuppressWarnings("unchecked")
	public LayoutAlgorithms pickAlgorithm(List<V> vertices, List<E> edges){
		Graph<V,E> graph = new Graph<V,E>();
		for (V v : vertices)
			graph.addVertex(v);
		for (E e : edges)
			graph.addEdge(e);
		return pickAlgorithm(graph);
	}
	
	public LayoutAlgorithms pickAlgorithm(Graph<V,E> graph){
		//check graph properties
		//TODO kada koji algoritam za stablo, kada kruzni
		//recimo, ako je puno povezano sa jednim centralnim
		//staviti njega u centar, ostalo poredjati okolo
		//kada recimo radial tree, kada balloon, kada level based (obicno)
		//da li je simetricno itd.
		//nepovezani - box
		
		if (graph.isTree()){
			//see which of the tree algorithms would be the best choice
			
			//Balloon layout produces a circular "balloon-tree" layout of a tree. 
			//This layout places children nodes radially around their parent
			//if there are a lot of leaf nodes 
			//and not too many nodes that are not connected to some leaf nodes
			//this algorithm seem like a good choice
			
			//it's not really that important if the root has only one edge
			List<V> leaves = graph.getTreeLeaves(null);
			if (leaves.size() >= (int)balloonFactor*graph.getVertices().size())
				return LayoutAlgorithms.BALLOON;
			else if (new BinaryTree<V,E>(graph).isCanBeConstructed()){
					return LayoutAlgorithms.TREE; //level based approach, this won't be too wide
			}
			else return LayoutAlgorithms.NODE_LINK_TREE;
				
			
		}
		else
			return LayoutAlgorithms.KAMADA_KAWAI;
	
	}
}
