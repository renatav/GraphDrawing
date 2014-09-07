package graph.layout;

import java.awt.Dimension;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

public abstract class AbstractLayout<V extends Vertex,E extends Edge<V>> {
	
	/**
	 * Graph to be layouted
	 */
	protected Graph<V,E> graph;
	
	protected int xOffset, yOffset;
	
	
	public AbstractLayout(Graph<V,E> graph){
		this.graph = graph;
	}
	
	public AbstractLayout(Graph<V, E> graph, int xOffset, int yOffset) {
		super();
		this.graph = graph;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}

	/**
	 * Dimensions of the graph after layouting is complete
	 */
	public abstract Dimension getDimension();
	
	/**
	 * Performs the actual layouting
	 */
	public abstract void layout();

	public Graph<V, E> getGraph() {
		return graph;
	}

	public void setGraph(Graph<V, E> graph) {
		this.graph = graph;
	}

	public int getxOffset() {
		return xOffset;
	}

	public void setxOffset(int xOffset) {
		this.xOffset = xOffset;
	}

	public int getyOffset() {
		return yOffset;
	}

	public void setyOffset(int yOffset) {
		this.yOffset = yOffset;
	}
	
	
	
	
	
	
	

}
