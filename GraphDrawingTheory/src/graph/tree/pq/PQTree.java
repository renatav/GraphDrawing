package graph.tree.pq;

import java.util.List;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

/**
 * Tree used in some algorithms for planarity testing
 * P nodes are cut vertices
 * Q nodes are nonseparable components
 * Leaves are virtual vertices (vertices on the other side of edges where one vertex is on subgraph Gk and the
 * other one is in V-Vk)
 * @author xx
 *
 */
public class PQTree <V extends Vertex, E extends Edge<V>> extends Graph<PQTreeNode, PQTreeEdge> {

	private List<PQTreeNode> PNodes, QNodes, leaves;
}
