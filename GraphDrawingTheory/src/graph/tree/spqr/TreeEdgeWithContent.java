package graph.tree.spqr;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

/**
 * Class which implements the {@code Edge} interface and adds the content attribute
 * Used for constructing SPQR-trees
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type 
 */
public class TreeEdgeWithContent<V extends Vertex, E extends Edge<V>> implements Edge<V> {

	private V origin, destination;
	private Graph<V,E> content;
	
	/**
	 * Construct an empty edge
	 */
	public TreeEdgeWithContent() {
		super();
	}

	/**
	 * Constructs an edge between the given two vertices
	 * @param origin Origin vertex
	 * @param destination Destination vertex
	 */
	public TreeEdgeWithContent(V origin, V destination) {
		super();
		this.origin = origin;
		this.destination = destination;
	}

	/**
	 * Constructs an edge between the given two vertices
	 * @param origin Origin vertex
	 * @param destination Destination vertex
	 * @param content The edge's content
	 */
	public TreeEdgeWithContent(V origin, V destination, Graph<V, E> content) {
		this(origin, destination);
		this.content = content;
	}

	@Override
	public int getWeight() {
		return 1;
	}


	@Override
	public V getOrigin() {
		return origin;
	}

	@Override
	public void setOrigin(V origin) {
		this.origin = origin;
	}

	@Override
	public V getDestination() {
		return destination;
	}

	@Override
	public void setDestination(V destination) {
		this.destination = destination;
	}

	/**
	 * @return Edge's content
	 */
	public Graph<V, E> getContent() {
		return content;
	}

	/**
	 * @param content Content to set
	 */
	public void setContent(Graph<V, E> content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "TreeEdgeWithContent [origin=" + origin + ", destination="
				+ destination + ", content=" + content + "]";
	}

	@Override
	public void setWeight(int weight) {
	}
	

}
