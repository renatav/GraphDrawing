package graph.layout;

import graph.drawing.Drawing;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

public abstract class AbstractLayouter <V extends Vertex,E extends Edge<V>> {
	

	public abstract Drawing<V,E> layout(Graph<V,E> graph, GraphLayoutProperties layoutProperties);

}
