package graph.tree.binary;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

import java.util.ArrayList;
import java.util.List;

public class BinaryTree<V extends Vertex, E extends Edge<V>> {
	
	
	private BinaryTreeNode<V> root;
	private List<BinaryTreeNode<V>> nodes = new ArrayList<BinaryTreeNode<V>>();
	
	public BinaryTree(Graph<V,E> graph){
		formBinaryTree(graph);
	}

	private void formBinaryTree(Graph<V, E> graph) {
		//start with the leaves and go upwards
		//or just traverse the vertices, form a map of 
		//created nodes - vertices
		//if the vertex is encountered again, grab the node from the map
		//avoid two traversal that way
		
	}
	
	
	

}
