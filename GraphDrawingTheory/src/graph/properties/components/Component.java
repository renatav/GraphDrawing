package graph.properties.components;

import graph.elements.Edge;
import graph.elements.Vertex;

import java.util.ArrayList;
import java.util.List;

/**
 * Class represent a component of a graph. Meant to be extended by other components with more semantics.
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type 
 */
public class Component<V extends Vertex, E extends Edge<V>> {

	/**
	 * Component's edges
	 */
	protected List<E> edges;

	/**
	 * Construct a component with an empty list of edges
	 */
	public Component(){
		edges = new ArrayList<E>();
	}
	
	/**
	 * Constructs a component and sets the edges
	 * @param edges Edges of the component
	 */
	public Component(List<E> edges) {
		super();
		this.edges = edges;
	}
	
	/**
	 * @return Component's edges
	 */
	public List<E> getEdges() {
		return edges;
	}
	
	/**
	 * @param edges Edges to set
	 */
	public void setEdges(List<E> edges) {
		this.edges = edges;
	}

	@Override
	public String toString() {
		return "Component [edges=" + edges + "]";
	}
	
}
