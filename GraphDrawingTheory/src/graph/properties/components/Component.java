package graph.properties.components;

import graph.elements.Edge;
import graph.elements.Vertex;

import java.util.ArrayList;
import java.util.List;

public class Component<V extends Vertex, E extends Edge<V>> {

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
