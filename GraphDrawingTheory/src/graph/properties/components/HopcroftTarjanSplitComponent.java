package graph.properties.components;

import graph.elements.Edge;
import graph.elements.Vertex;
import graph.properties.splitting.Triple;

import java.util.ArrayList;
import java.util.List;

public class HopcroftTarjanSplitComponent<V extends Vertex, E extends Edge<V>> extends Component<V,E>{

	private SplitComponentType type;
	private SplitPair<V,E> spearaionPair;
	private E virtualEdge;

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

	public HopcroftTarjanSplitComponent(SplitComponentType type, List<E> edges) {
		super(edges);
		this.type = type;
		triples = new ArrayList<Triple>();
	}
	
	public HopcroftTarjanSplitComponent(){
		super();
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

	public SplitPair<V, E> getSpearaionPair() {
		return spearaionPair;
	}

	public void setSpearaionPair(SplitPair<V, E> spearaionPair) {
		this.spearaionPair = spearaionPair;
	}

	public E getVirtualEdge() {
		return virtualEdge;
	}

	public void setVirtualEdge(E virtualEdge) {
		this.virtualEdge = virtualEdge;
	}
	
	
	
	
	
}
