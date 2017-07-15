package graph.layout;

import graph.drawing.Drawing;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

/**
 * Abstract layouter class meant to be extended by all layouters.
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type 
 */
public abstract class AbstractLayouter <V extends Vertex,E extends Edge<V>> {
	
	/**
	 * Indicates if the given algorithm layouts the whole graph, even if it
	 * consists of more than one 1-connected component
	 */
	protected boolean oneGraph = true;
	/**
	 * Indicates if the algorithm also routes the edges
	 */
	protected boolean positionsEdges = false;
	
	/**
	 * Layouts the graph, taking into account given properties
	 * @param graph Graph that should be laid out
	 * @param layoutProperties Properties of the layout algorithm 
	 * @return Drawing
	 */
	public abstract Drawing<V,E> layout(Graph<V,E> graph, GraphLayoutProperties layoutProperties);

	public boolean isOneGraph() {
		return oneGraph;
	}

	public void setOneGraph(boolean oneGraph) {
		this.oneGraph = oneGraph;
	}

	public boolean isPositionsEdges() {
		return positionsEdges;
	}

	public void setPositionsEdges(boolean positionsEdges) {
		this.positionsEdges = positionsEdges;
	}

}
