package graph.algorithms.planarity.dual;

import java.awt.Dimension;
import java.util.List;

import graph.elements.Edge;
import graph.elements.Vertex;


/**
 * Vertices of dual graphs are sets of faces of G, where G is a st-graph 
 * (DAG with one source and one sink)
 * @author xx
 *
 * @param <V>
 * @param <E>
 */
public class DualGraphVertex<V extends Vertex, E extends Edge<V>> implements Vertex{
	
	
	private List<E> content;
	
	public DualGraphVertex(List<E> content) {
		super();
		this.content = content;
	}

	@Override
	public List<E> getContent() {
		return content;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setContent(Object content) {
		this.content = (List<E>)content;
		
	}
	
	@Override
	public Dimension getSize() {
		return null;
	}
	
	@Override
	public void setSize(Dimension size) {
	}

	@Override
	public String toString() {
		return "DualGraphVertex [content=" + content + "]";
	}


	
}
