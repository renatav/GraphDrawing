package graph.ordering.circular;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

import java.util.ArrayList;
import java.util.List;

public class GraphCopy<V extends Vertex, E extends Edge<V>> extends Graph<V, E> {


	private List<TriangulatedEdge<V>> triangulatedEdges = new ArrayList<TriangulatedEdge<V>>();


	public GraphCopy(){
		super();
	}

	public GraphCopy(List<V> vertices, List<E> edges) {
		super(vertices, edges);
	}

	public TriangulatedEdge<V> addTriangulatedEdge(V v1, V v2){
		TriangulatedEdge<V> te = new TriangulatedEdge<V>(v1, v2);
		triangulatedEdges.add(te);
		return te;
	}

	@Override
	public int vertexDegree(V v){
		int degree = 0;
		for (E e : edges)
			if (e.getOrigin() == v || e.getDestination() == v)
				degree ++;
		for (TriangulatedEdge<V> te : triangulatedEdges)
			if (te.getOrigin() == v || te.getDestination() == v)
				degree ++;
		return degree;
	}


	@Override
	public boolean hasEdge(V v1, V v2){
		for (E e : adjacentLists.get(v1))
			if (e.getDestination() == v2)
				return true;
		if (!directed)
			for (E e : adjacentLists.get(v2))
				if (e.getDestination() == v1)
					return true;

		for (TriangulatedEdge<V> te : triangulatedEdges){
			if ((te.getDestination() == v1 && te.getOrigin() == v2)
					|| (te.getOrigin() == v1 && te.getDestination() == v2))
				return true;
		}
		return false;
	}


	@SuppressWarnings("unchecked")
	public void removeEdgeWithTriangulated(Edge<V> e){
		if (e instanceof TriangulatedEdge<?>)
			triangulatedEdges.remove(e);
		else 
			removeEdge((E) e);
	}

	public List<V> adjacentVerticesWithTriangulated(V v){
		List<V> ret = new ArrayList<V>();
		if (adjacentLists.get(v) != null)
			for (E e : adjacentLists.get(v)){
				V other = e.getDestination() != v ? e.getDestination() : e.getOrigin();
				if (! ret.contains(other))
					ret.add(other);
			}

		for (TriangulatedEdge<V> te : triangulatedEdges){
			if (te.getDestination() == v ||  te.getOrigin() == v){
				V other = te.getDestination() != v ? te.getDestination() : te.getOrigin();
				if (! ret.contains(other))
					ret.add(other);
			}
		}
		return ret;
	}

	public List<Edge<V>> allAdjacentEdgesWithTriangulated(V v){
		List<Edge<V>> ret = new ArrayList<Edge<V>>();
		if (adjacentLists.containsKey(v))
			ret.addAll(adjacentLists.get(v));
		for (TriangulatedEdge<V> te : triangulatedEdges)
			if (te.getDestination() == v ||  te.getOrigin() == v)
				ret.add(te);

		return ret;
	}

	public List<Edge<V>> edgeesBetweenWithTriangulated (V v1, V v2){
		List<Edge<V>> ret = new ArrayList<Edge<V>>();

		for (E e : adjacentLists.get(v1))
			if (e.getDestination() == v2)
				ret.add(e);
		if (!directed)
			for (E e : adjacentLists.get(v2))
				if (e.getDestination() == v1){
					ret.add(e);
				}
		for (TriangulatedEdge<V> te : triangulatedEdges){
			if ((te.getDestination() == v1 && te.getOrigin() == v2)
					|| (te.getOrigin() == v1 && te.getDestination() == v2))
				ret.add(te);
		}
		return ret;
	}

}
