package graph.traversal;

import java.util.ArrayList;
import java.util.List;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.trees.DFSTree;

/**
 * Class for forming a DFS tree given a graph
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type 
 */
public class DFSTreeTraversal<V extends Vertex, E extends Edge<V>> {

	/**
	 * Graph for which a tree is being formed
	 */
	private Graph<V,E> graph;
	private int index;

	public DFSTreeTraversal(Graph<V,E> graph){
		this.graph = graph;
	}

	public DFSTree<V,E> formDFSTree(){
		return formDFSTree(graph.getVertices().get(0));
	}

	/**
	 * Forms a DFS tree
	 * @param root Vertex which should be the root of the DFS tree
	 * @return DFS tree
	 */
	public DFSTree<V,E> formDFSTree(V root){
		DFSTree<V, E> tree = new DFSTree<V,E>(root);
		index = 1;
		formDFSTree(root, null,  tree, new ArrayList<V>(), new ArrayList<E>());
		tree.formBackEdges(graph.getEdges());
		return tree;
	}

	private void formDFSTree(V current, E currentEdge, DFSTree<V,E> tree, List<V> covered, List<E> visited){

		if (covered.size() == graph.getVertices().size())
			return;
		if (covered.contains(current))
			return;

		covered.add(current);

		tree.addVertex(current, index ++);

		if (currentEdge != null){
			tree.addTreeEdge(currentEdge);
		}

		List<E> edges;
		if (graph.isDirected())
			edges = graph.outEdges(current);
		else
			edges = graph.allEdges(current);

		if (edges != null)
			for (E e : edges) {
				if (visited.contains(e)) {
					continue;
				}
				List<E> temp = new ArrayList<E>();
				temp.addAll(visited);
				temp.add(e);


				V nextVert;
				if (current == e.getOrigin()){
					nextVert = e.getDestination();
				}
				else{
					nextVert = e.getOrigin();
				}

				formDFSTree(nextVert, e, tree, covered, temp);
			}
	}

}
