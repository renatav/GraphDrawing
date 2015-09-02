package graph.properties.slitting.triconnected;

import graph.elements.Edge;
import graph.elements.Vertex;

import java.util.List;

public class HopcroftSplitComponent<V extends Vertex, E extends Edge<V>> {

	private SplitComponentType type;
	private List<E> edges;
	private List<E> virtualEdges;
	
	public SplitComponentType getType() {
		return type;
	}

	public void setType(SplitComponentType type) {
		this.type = type;
	}

	public List<E> getEdges() {
		return edges;
	}

	public void setEdges(List<E> edges) {
		this.edges = edges;
	}

	public List<E> getVirtualEdges() {
		return virtualEdges;
	}

	public void setVirtualEdges(List<E> virtualEdges) {
		this.virtualEdges = virtualEdges;
	}

	public HopcroftSplitComponent(SplitComponentType type, List<E> edges,
			List<E> virtualEdges) {
		super();
		this.type = type;
		this.edges = edges;
		this.virtualEdges = virtualEdges;
	}
	
	
	
	
	
}
