package graph.properties.components;

import graph.elements.Edge;
import graph.elements.Vertex;

import java.util.ArrayList;
import java.util.List;

public class HopcroftTarjanSplitComponent<V extends Vertex, E extends Edge<V>> extends Component<V,E>{

	private SplitTriconnectedComponentType type;
	private SplitPair<V,E> spearaionPair;
	private List<E> virtualEdges;
	
	public SplitTriconnectedComponentType getType() {
		return type;
	}

	public void setType(SplitTriconnectedComponentType type) {
		this.type = type;
	}

	public List<E> getEdges() {
		return edges;
	}

	public void setEdges(List<E> edges) {
		this.edges = edges;
		virtualEdges = new ArrayList<E>();
	}
	
	public HopcroftTarjanSplitComponent() {
		super();
		virtualEdges = new ArrayList<E>();
	}
	

	public HopcroftTarjanSplitComponent(List<E> edges, List<E> virtualEdges,
			SplitTriconnectedComponentType type) {
		super();
		this.type = type;
		this.edges = edges;
		this.virtualEdges = virtualEdges;
	}
	
	public HopcroftTarjanSplitComponent(SplitTriconnectedComponentType type, List<E> edges) {
		super(edges);
		this.type = type;
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

	public List<E> getVirtualEdges() {
		return virtualEdges;
	}

	public void setVirtualEdges(List<E> virtualEdges) {
		this.virtualEdges = virtualEdges;
	}

	
	public void addEdge(E e){
		edges.add(e);
	}
	
	public void addVirtualEdge(E e){
		edges.add(e);
		virtualEdges.add(e);
	}
	
	
	
	
	
}
