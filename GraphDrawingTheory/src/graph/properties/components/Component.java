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
	
	public Component(){
		edges = new ArrayList<E>();
	}
	
	
	public Component(List<E> edges) {
		super();
		this.edges = edges;
	}
	
	public List<E> getEdges() {
		return edges;
	}
	public void setEdges(List<E> edges) {
		this.edges = edges;
	}


	@Override
	public String toString() {
		return "Component [edges=" + edges + "]";
	}
	
}
