package graph.tree.spqr;

import java.util.ArrayList;
import java.util.List;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

/**
 * Each node is associated with a special graph which is called a skeleton of the node
 * It is a simplified version of the original graph where some subgraphs were replaces
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type 
 */
public class Skeleton<V extends Vertex,E extends Edge<V>> extends Graph<V,E>{
	
	private List<E> virtualEdges;

	/**
	 * Constructs the skeleton and initializes an empty list of virtual edges
	 */
	public Skeleton() {
		super();
		virtualEdges = new ArrayList<>();
	}
	
	/**
	 * Constructs the skeleton and sets lists of vertices and edges. Initializes
	 * an empty list of virtual edges. 
	 * @param vertices A list of vertices
	 * @param edges A list of edges
	 */
	public Skeleton(List<V> vertices, List<E> edges) {
		super(vertices, edges);
		virtualEdges = new ArrayList<>();
	}

	/**
	 * Constructs the skeleton and sets lists of vertices, edges and virtual edges.
	 * @param vertices A list of vertices
	 * @param edges A list of edges
	 * @param virtualEdges A list of virtual edges
	 */
	public Skeleton(List<V> vertices, List<E> edges, List<E> virtualEdges) {
		super(vertices, edges);
		this.virtualEdges = virtualEdges;
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * Adds a new edge to the skeleton
	 * @param e Edge
	 * @param virtual Indicator if it is a virtual edge
	 */
	public void addEdge(E e, boolean virtual){
		addEdge(e);
		if (virtual)
			addVirualEdge(e);
	}
	
	/**
	 * Adds a new virtual edge
	 * @param e Virtual edge
	 */
	public void addVirualEdge(E e){
		if (!virtualEdges.contains(e))
			virtualEdges.add(e);
	}
	
	/**
	 * Check of the edge is virtual
	 * @param e Edge
	 * @return {@code true} if {@code e} is a virtual edge, {@code false} otherwise
	 */
	public boolean isVirtualEdge(E e){
		return virtualEdges.contains(e);
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

	@Override
	public String toString() {
		return "Skeleton [vertices="+ vertices + ",\n edges=" + edges + ",\n Virtual edges: " +
				virtualEdges + "]";
	}
}