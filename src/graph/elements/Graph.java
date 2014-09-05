package graph.elements;

import graph.traversal.GraphTraversal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * /**
 * A graph consisting of a set of vertices of type <code>V</code>
 * and a set of edges of type <code>E</code>. 
 * @author xxx
 *
 * @param <V>
 * @param <E>
 */
public class Graph<V extends Vertex,E extends Edge<V>>{

	private List<V> vertices;
	private List<E> edges;
	private boolean directed = true;

	/**
	 * An adjacent list contains a list of all the edges leaving the vertex
	 */
	private Map<V, LinkedList<E>> adjacentLists;

	public Graph(){
		vertices = new ArrayList<V>();
		edges = new ArrayList<E>();
		adjacentLists = new HashMap<V, LinkedList<E>>();
	}
	
	public Graph(boolean directed){
		this();
		this.directed = directed;
	}
	
	
	@SuppressWarnings("unchecked")
	public void addVertex(V...vert){
		for (V v : vert){
			vertices.add(v);
			adjacentLists.put(v, new LinkedList<E>());
		}
	}
	
	public void addVertex(V v){
		vertices.add(v);
		adjacentLists.put(v, new LinkedList<E>());
	}
	
	public void addVertexBeginning(V v){
		vertices.add(0, v);
		adjacentLists.put(v, new LinkedList<E>());
	}

	public void removeVertex(V v){
		vertices.remove(v);
		adjacentLists.remove(v);
	}

	@SuppressWarnings("unchecked")
	public void addEdge(E...edge){
		for (E e : edge){
			edges.add(e);
			adjacentLists.get(e.getOrigin()).add(e);
		}
	}

	/**
	 * Checks if vertices v1 and v2 are connected i.e. if there is an edge between them
	 * @param v1 Source vertex
	 * @param v2 Destination vertex
	 * @return true if there is and edge between v1 and v2, otherwise false
	 */
	public boolean hasEdge(V v1, V v2){
		for (E e : adjacentLists.get(v1))
			if (e.getDestination() == v2)
				return true;
		if (!directed)
			for (E e : adjacentLists.get(v2))
				if (e.getDestination() == v1)
					return true;
		return false;
	}

	/**
	 * Returns all edges between vertices v1 and v2
	 * @param v1 Source vertex
	 * @param v2 Destination vertex
	 * @return All edges between v1 and v2
	 */
	public List<E> edgeesBetween (V v1, V v2){
		List<E> ret = new ArrayList<E>();
		for (E e : adjacentLists.get(v1))
			if (e.getDestination() == v2)
				ret.add(e);
		if (!directed)
			for (E e : adjacentLists.get(v2))
				if (e.getDestination() == v1)
					ret.add(e);
		return ret;
	}

	public void removeEdge(E e){
		edges.remove(e);
		adjacentLists.get(e.getOrigin()).remove(e);
	}

	/**
	 * All edges leaving v
	 * @param v
	 * @return
	 */
	public LinkedList<E> outEdges(V v){
		LinkedList<E> ret = new LinkedList<E>();
		ret.addAll(adjacentLists.get(v));
		return ret;
	}
	
	/**
	 * Number of edges leaving vertex v
	 * @param v
	 * @return
	 */
	public int outDegree (V v){
		return outEdges(v).size();
	}

	/**
	 * All edges entering v
	 * @param v
	 * @return
	 */
	public LinkedList<E> inEdges(V v){
		LinkedList<E> ret = new LinkedList<E>();
		for (E e : edges)
			if (e.getDestination() == v)
				ret.add(e);
		return ret;
	}
	
	/**
	 * Number of edges entering v
	 * @param v
	 * @return
	 */
	public int inDegree(V v){
		return inEdges(v).size();
	}
	
	
	/**
	 * Checks if vertex is a source (vertex with no incoming edges)
	 * @param v
	 * @return
	 */
	public boolean isSource(V v){
		return inDegree(v) == 0;
	}
	
	/**
	 * Checks if vertex is a sink (vertex with no outgoing edges)
	 * @param v
	 * @return
	 */
	public boolean isSink(V v){
		return outDegree(v) == 0;
	}
	
	/**
	 * All edges leaving or entering v
	 * @param v
	 * @return
	 */
	public LinkedList<E> allEdges(V v){
		LinkedList<E> ret = new LinkedList<E>();
		for (E e : edges)
			if (e.getDestination() == v || e.getOrigin() == v)
				ret.add(e);
		return ret;
	}

	/**
	 * All edges which connect one vertix to itself
	 * @return
	 */
	public List<E> getAllSelfLoopEdges(){
		List<E> ret = new ArrayList<E>();
		for (E e : edges){
			if (e.getOrigin() == e.getDestination())
				ret.add(e);
		}
		return ret;
	}
	
	/**
	 * Checks if graph has self loop edges 
	 * @return
	 */
	public boolean hasSelfLoopEdges(){
		for (E e : edges){
			if (e.getOrigin() == e.getDestination())
				return true;
		}
		return false;
	}

	/**
	 * Checks if graph is simple
	 * @return
	 */
	public boolean isSimple(){
		if (hasSelfLoopEdges())
			return false;
		Set<V> covered = new HashSet<V>(); 
		for (V v : vertices){
			if (covered.contains(v))
				return false;
			covered.add(v);
		}
		return false;
	}

	/**
	 * Number of edges entering or leaving v
	 * @param v
	 * @return
	 */
	public int vertexDegree(V v){
		int degree = 0;
		for (E e : edges)
			if (e.getOrigin() == v || e.getDestination() == v)
				degree ++;
		return degree;
	}

	/**
	 * Max vertex degree
	 * @return
	 */
	public int graphMaxDegree(){
		int degree = 0;
		int vertDegree;
		for (V v : vertices){
			vertDegree = vertexDegree(v);
			if (vertDegree > degree)
				degree = vertDegree;
		}
		return degree;
	}

	/**
	 * Checks is graph is connected
	 * @return
	 */
	public boolean isConnected(){
		List<Path<V, E>> paths = new ArrayList<Path<V, E>>();
		GraphTraversal<V, E> traversal = new GraphTraversal<>(this);
		for (V v1 : vertices)
			for (V v2 : vertices){
				if (v1 == v2)
					continue;
				paths.clear();
				paths = traversal.findAllPathsDFS(v1, v2);
				if (paths.size() == 0)
					return false;
			}
		return true;
	}
	
	public boolean isCyclic(){
		List<Path<V, E>> paths = new ArrayList<Path<V, E>>();
		GraphTraversal<V, E> traversal = new GraphTraversal<>(this);
		for (V v : vertices){
				paths = traversal.findAllPathsDFS(v, v);
				if (paths.size() > 0)
					return true;
			}
		return false;
	}
	
	public List<V> getAllSinks(){
		List<V> ret = new ArrayList<V>();
		for (V v : vertices)
			if (isSink(v))
				ret.add(v);
		return ret;
	}
	


	public List<V> getVertices() {
		return vertices;
	}

	public void setVertices(List<V> vertices) {
		this.vertices = vertices;
	}

	public Collection<E> getEdges() {
		return edges;
	}

	public void setEdges(List<E> edges) {
		this.edges = edges;
	}


	public boolean isDirected() {
		return directed;
	}

	public void setDirected(boolean directed) {
		this.directed = directed;
	}

	@Override
	public String toString() {
		return "Graph [vertices=" + vertices + ", edges=" + edges + "]";
	}


}
