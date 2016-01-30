package graph.layout;

import graph.drawing.Drawing;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

public abstract class AbstractLayouter <V extends Vertex,E extends Edge<V>> {
	

	protected boolean oneGraph = true;
	protected boolean positionsEdges = false;
	
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
