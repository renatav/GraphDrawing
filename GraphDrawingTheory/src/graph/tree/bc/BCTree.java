package graph.tree.bc;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.properties.components.Block;
import graph.properties.splitting.Splitting;

import java.util.List;


/**
 * Block-Cut vertex tree
 * Let B be the set of blocks and C be the set of cut vertices of a separable graph G. Construct a
 * graph H with vertex set B U C in which adjacencies are defined as follows: 
 * ci in C is adjacent to bj in B if and only if the block bj of G contains the cut vertex ci of G 
 * @assume graph is separable
 * @author xxx
 *
 */
public class BCTree<V extends Vertex, E extends Edge<V>> extends Graph<BCTreeNode, BCTreeEdge>{

	private Graph<V,E> graph;
	private Splitting<V, E> splitting = new Splitting<V,E>();

	public BCTree(Graph<V,E> graph){
		this.graph = graph;
		constructTree();

	}

	private void constructTree(){
		List<V> cutVertices = splitting.findAllCutVertices(graph);
		List<Block<V,E>> blocks = splitting.findAllBlocks(graph, cutVertices);

		for (V cutVertex : cutVertices)
			addVertex(new BCTreeNode(cutVertex));

		for (Block<V,E> block : blocks){
			addVertex(new BCTreeNode(block));
			for (V cutVertex : cutVertices){
				if (block.hasVertex(cutVertex))
					addEdge(new BCTreeEdge(getVertexByContent(cutVertex),getVertexByContent(block)));
			}
		}
	}

}
