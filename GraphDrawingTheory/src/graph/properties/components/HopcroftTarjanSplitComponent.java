package graph.properties.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import graph.elements.Edge;
import graph.elements.Vertex;

/**
 * A xomponent of a graph used in Hopcroft-Tarjan splitting algorithm
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type 
 */
public class HopcroftTarjanSplitComponent<V extends Vertex, E extends Edge<V>> extends Component<V,E>{

	/**
	 * Type of the component
	 */
	private SplitTriconnectedComponentType type;
	/**
	 * Separation pair that lead to the creation of the component
	 */
	private SplitPair<V,E> spearaionPair;
	/**
	 * Virtual edges belonging to the component
	 */
	private List<E> virtualEdges;
	/**
	 * Vertices belonging to the component
	 */
	private List<V> vertices;
	
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
	
	/**
	 * Finds adjacency map of the component. Skips virtual edges.
	 * @return Mapping of component's vertices to a list of edges they're adjacent to
	 */
	public Map<V,List<E>> adjacencyMap(){
		Map<V,List<E>> ret = new HashMap<V,List<E>>();
		for (E e : edges){
			if (virtualEdges.contains(e))
				continue;
			List<E> list;
			if (!ret.containsKey(e.getOrigin())){
				list = new ArrayList<E>();
				ret.put(e.getOrigin(), list);
			}
			else
				list = ret.get(e.getOrigin());
			list.add(e);
			
			if (!ret.containsKey(e.getDestination())){
				list = new ArrayList<E>();
				ret.put(e.getDestination(), list);
			}
			else
				list = ret.get(e.getDestination());
			list.add(e);
		}
		return ret;
	}
	
	public void setVertices(){
		vertices = new ArrayList<V>();
		for (E e : edges){
			V v1 = e.getOrigin();
			V v2 = e.getDestination();
			if (!vertices.contains(v1))
				vertices.add(v1);
			if (!vertices.contains(v2))
				vertices.add(v2);
		}
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

	public List<V> getVertices() {
		return vertices;
	}
	
	
	
	
	
}
