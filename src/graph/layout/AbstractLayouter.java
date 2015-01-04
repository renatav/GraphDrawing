package graph.layout;

import graph.drawing.Drawing;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

public abstract class AbstractLayouter <V extends Vertex,E extends Edge<V>> {

	/**
	 * Graph to be layouted
	 */
	protected Graph<V,E> graph;
	
	
	public AbstractLayouter(Graph<V,E> graph){
		this.graph = graph;
	}
	
	public abstract Drawing<V,E> layout();

}
