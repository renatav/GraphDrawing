package graph.properties.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import graph.elements.Edge;
import graph.elements.Vertex;

/**
 * A component of a graph used in Hopcroft-Tarjan splitting algorithm
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
	
	/**
	 * Creates an empty component
	 */
	public HopcroftTarjanSplitComponent() {
		super();
		virtualEdges = new ArrayList<E>();
	}
	
	/**
	 * Creates a component with the specified edges and virtual edges
	 * of the given type
	 * @param edges Component's edges
	 * @param virtualEdges Component's virtual edges
	 * @param type Component's type
	 */
	public HopcroftTarjanSplitComponent(List<E> edges, List<E> virtualEdges,
			SplitTriconnectedComponentType type) {
		super();
		this.type = type;
		this.edges = edges;
		this.virtualEdges = virtualEdges;
	}
	
	/**
	 * Creates a component with the specified edges
	 * of the given type
	 * @param edges Component's edges
	 * @param type Component's type
	 */
	public HopcroftTarjanSplitComponent(SplitTriconnectedComponentType type, List<E> edges) {
		super(edges);
		this.type = type;
	}
	
	/**
	 * @return Component's type
	 */
	public SplitTriconnectedComponentType getType() {
		return type;
	}

	/**
	 * @param type Component's type to set
	 */
	public void setType(SplitTriconnectedComponentType type) {
		this.type = type;
	}

	/**
	 * @return Component's edges
	 */
	public List<E> getEdges() {
		return edges;
	}

	/**
	 * Sets edges and initializes the virtual edges list.
	 * @param edges Edges to set.
	 */
	public void setEdges(List<E> edges) {
		this.edges = edges;
		virtualEdges = new ArrayList<E>();
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
	
	/**
	 * Sets the components vertices based on the edges that it contains
	 */
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
	
	/**
	 * @return Separation pair
	 */
	public SplitPair<V, E> getSpearaionPair() {
		return spearaionPair;
	}

	/**
	 * @param spearaionPair Separation pair to set
	 */
	public void setSpearaionPair(SplitPair<V, E> spearaionPair) {
		this.spearaionPair = spearaionPair;
	}

	/**
	 * @return Virtual edges
	 */
	public List<E> getVirtualEdges() {
		return virtualEdges;
	}

	/**
	 * @param virtualEdges Virtual edges to set
	 */
	public void setVirtualEdges(List<E> virtualEdges) {
		this.virtualEdges = virtualEdges;
	}

	/**
	 * Adds an edge to the list of edges
	 * @param e Edge to be added
	 */
	public void addEdge(E e){
		edges.add(e);
	}
	
	/**
	 * Adds a virtual edge - updates both the list of edges and virtual edges
	 * @param e The virtual edge to be added
	 */
	public void addVirtualEdge(E e){
		edges.add(e);
		virtualEdges.add(e);
	}

	/**
	 * @return The component's vertices
	 */
	public List<V> getVertices() {
		return vertices;
	}
	
	@Override
	public String toString() {
		return "HopcroftSplitComponent [edges=" + edges + "]";
	}

	
	
	
}
