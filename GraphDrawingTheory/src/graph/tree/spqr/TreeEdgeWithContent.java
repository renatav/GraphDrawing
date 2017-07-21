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
	
	public TreeEdgeWithContent() {
		super();
	}

	public TreeEdgeWithContent(V origin, V destination) {
		super();
		this.origin = origin;
		this.destination = destination;
	}

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


	public Graph<V, E> getContent() {
		return content;
	}


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
		// TODO Auto-generated method stub
		
	}
	

}
