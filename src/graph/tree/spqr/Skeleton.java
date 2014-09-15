package graph.tree.spqr;

import java.util.ArrayList;
import java.util.List;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

/**
 * Each node is associated with a special graph which is called a skeleton of the node
 * It is a simplified version of the original graph where some subgraphs were replaces
 * by single edges 
 * @author xxx
 *
 * @param <V>
 * @param <E>
 */
public class Skeleton<V extends Vertex,E extends Edge<V>> extends Graph<V,E>{
	
	private List<E> virtualEdges;

	public Skeleton() {
		super();
		virtualEdges = new ArrayList<>();
	}
	
	
	public Skeleton(List<V> vertices, List<E> edges) {
		super(vertices, edges);
		virtualEdges = new ArrayList<>();
	}

	public Skeleton(List<V> vertices, List<E> edges, List<E> virtualEdges) {
		super(vertices, edges);
		this.virtualEdges = virtualEdges;
	}
	
	@SuppressWarnings("unchecked")
	public void addEdge(E e, boolean virtual){
		addEdge(e);
		if (virtual)
			addVirualEdge(e);
	}
	
	public void addVirualEdge(E e){
		if (!virtualEdges.contains(e))
			virtualEdges.add(e);
	}
	
	public boolean isVirtualEdge(E e){
		return virtualEdges.contains(e);
	}

	public List<E> getVirtualEdges() {
		return virtualEdges;
	}

	public void setVirtualEdges(List<E> virtualEdges) {
		this.virtualEdges = virtualEdges;
	}


	@Override
	public String toString() {
		return "Skeleton [vertices="+ vertices + ",\n edges=" + edges + ",\n Virtual edges: " +
				virtualEdges + "]";
	}

	
	
	
	
	
	

}
