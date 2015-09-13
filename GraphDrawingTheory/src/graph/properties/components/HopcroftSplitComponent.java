package graph.properties.components;

import graph.elements.Edge;
import graph.elements.Vertex;
import graph.properties.splitting.Triple;

import java.util.ArrayList;
import java.util.List;

public class HopcroftSplitComponent<V extends Vertex, E extends Edge<V>> extends Component<V,E>{

	private SplitComponentType type;
	private List<E> virtualEdges;
	private List<Triple> triples;
	
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
		super(edges);
		this.type = type;
		this.virtualEdges = virtualEdges;
		triples = new ArrayList<Triple>();
	}
	
	public HopcroftSplitComponent(){
		super();
		virtualEdges = new ArrayList<E>();
		triples = new ArrayList<Triple>();
	}

	public List<Triple> getTriples() {
		return triples;
	}

	public void setTriples(List<Triple> triples) {
		this.triples = triples;
	}

	@Override
	public String toString() {
		return "HopcroftSplitComponent [edges=" + edges + "]";
	}
	
	
	
	
	
}
