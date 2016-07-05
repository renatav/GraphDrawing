package graph.elements;

import graph.properties.GraphProperties;

import java.util.ArrayList;
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

	protected List<V> vertices;
	protected List<E> edges;
	protected boolean directed = false;
	protected GraphProperties<V,E> properties;

	//TODO mozda u svakom slucaju 2 adjacency liste
	//po jedna  za direcred i undirected
	//iako je directed, nekada treba obradjivati sve susedne...

	/**
	 * An adjacent list contains a list of all the edges leaving the vertex
	 */
	protected Map<V, LinkedList<E>> adjacentLists;

	/**
	 * Vertex by content map
	 */
	protected Map<Object, V> vertexByContentMap;

	public Graph(){
		vertices = new ArrayList<V>();
		edges = new ArrayList<E>();
		adjacentLists = new HashMap<V, LinkedList<E>>();
		vertexByContentMap = new HashMap<Object,V>();
		properties = new GraphProperties<V, E>(this);
	}


	@SuppressWarnings("unchecked")
	public Graph(List<V> vertices, List<E> edges){
		this();
		for (V v : vertices)
			addVertex(v);
		for (E e : edges)
			addEdge(e);

	}

	public Graph(boolean directed){
		this();
		this.directed = directed;
	}

	public boolean hasVertex(V v){
		return vertices.contains(v);
	}


	@SuppressWarnings("unchecked")
	public void addVertex(V...vert){
		for (V v : vert){
			if (!vertices.contains(v)){
				vertices.add(v);
				adjacentLists.put(v, new LinkedList<E>());
				vertexByContentMap.put(v.getContent(), v);
			}
		}
	}

	public void addVertex(V v){
		if (vertices.contains(v))
			return;
		vertices.add(v);
		adjacentLists.put(v, new LinkedList<E>());
		vertexByContentMap.put(v.getContent(), v);
	}

	public void addVertexBeginning(V v){
		vertices.add(0, v);
		adjacentLists.put(v, new LinkedList<E>());
	}

	public void removeVertex(V v){
		vertices.remove(v);
		List<E> adjacent = new ArrayList<E>();
		adjacent.addAll(adjacentLists.get(v));
		for (E e : adjacent){
			removeEdge(e);
		}
		adjacentLists.remove(v);
		
	}


	@SuppressWarnings("unchecked")
	public void addEdge(E...edge){

		for (E e : edge){
			if (edges.contains(e))
				continue;
			edges.add(e);
			if (adjacentLists.get(e.getOrigin()) != null){
				adjacentLists.get(e.getOrigin()).add(e);
			}
			if (!directed && (adjacentLists.get(e.getDestination()) != null)){
				adjacentLists.get(e.getDestination()).add(e);
			}

		}
	}

	/**
	 * Checks if vertices v1 and v2 are connected i.e. if there is an edge between them
	 * @param v1 Source vertex
	 * @param v2 Destination vertex
	 * @return true if there is and edge between v1 and v2, otherwise false
	 */
	public boolean hasEdge(V v1, V v2){

		for (E e : adjacentLists.get(v1)){
			V other =  e.getOrigin() == v1 ?  e.getDestination() : e.getOrigin();
			if (other == v2)
				return true;
		}
		if (!directed)
			for (E e : adjacentLists.get(v2)){
				V other =  e.getOrigin() == v2 ?  e.getDestination() : e.getOrigin();
				if (other == v1)
					return true;
			}
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

		if (adjacentLists.get(v1) != null)
			for (E e : adjacentLists.get(v1))
				if (e.getDestination() == v2)
					ret.add(e);
		if (!directed)
			if (adjacentLists.get(v2) != null)
				for (E e : adjacentLists.get(v2))
					if (e.getDestination() == v1){
						ret.add(e);
					}

		return ret;
	}

	public E edgeBetween(V v1, V v2){
		List<E> edges = edgeesBetween(v1, v2);
		if (edges.size() == 0)
			return null;
		else 
			return edges.get(0);
	}

	public void removeEdge(E e){
		edges.remove(e);
		if (adjacentLists.get(e.getOrigin()) != null)
			adjacentLists.get(e.getOrigin()).remove(e);
		if (adjacentLists.get(e.getDestination()) != null)
			adjacentLists.get(e.getDestination()).remove(e);
	}

	public List<E> adjacentEdges(V v){
		return adjacentLists.get(v);
	}

	/**
	 * All edges leaving v
	 * @param v
	 * @return
	 */
	public LinkedList<E> outEdges(V v){
		return adjacentLists.get(v);
	}

	/**
	 * All vertices adjacent to the given one
	 * @param v
	 * @return
	 */
	public List<V> adjacentVertices(V v){
		List<V> ret = new ArrayList<V>();
		if (adjacentLists.get(v) != null)
			for (E e : adjacentLists.get(v)){
				V other = e.getDestination() != v ? e.getDestination() : e.getOrigin();
				if (!ret.contains(other))
					ret.add(other);
			}
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
		return adjacentLists.get(v).size();
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
	 * @return true if the graph is connected, false otherwise
	 */
	public boolean isConnected(){
		return properties.isConnected();
	}
	
	/**
	 * Check if the graph is a tree
	 * @return true if the graph is a tree, false otherwise
	 */
	public boolean isTree(){
		return properties.isTree();
	}
	
	/**
	 * @param root Root of the tree
	 * @return list of vertices which are tree leaves 
	 * (presuming that the graph is a tree)
	 */
	public List<V> getTreeLeaves(V root){
		return properties.treeLeaves(root);
	}

	/**
	 * Checks is graph is connected presumed that certain vertices are removed
	 * @return
	 */
	public boolean isConnected(List<V> excluding){
		return properties.isConnected(excluding);
	}

	public boolean isCyclic(){
		return properties.isCyclic();
	}

	public List<V> getAllSinks(){
		List<V> ret = new ArrayList<V>();
		for (V v : vertices)
			if (isSink(v))
				ret.add(v);
		return ret;
	}
	
	public List<List<E>> listMultiEdges(){
		return properties.listMultiEdges();
	}
	
	public boolean isRing(){
		return properties.isRing();
	}
	

	/**
	 * Checks if a graph is biconnected. 
	 * A graph is biconnected if and only if any vertex is deleted, the graph remains connected.
	 * @return true if graph is biconnected, otherwise false
	 */
	public boolean isBiconnected(){
		return properties.isBiconnected();
	}
	
	public List<V> listCutVertices(){
		return properties.getCutVertices();
	}
	
	public List<Graph<V,E>>listBiconnectedComponents(){
		return properties.listBiconnectedComponents();
	}

	public int[][] adjacencyMatrix(){
		int[][] ret = new int[vertices.size()][vertices.size()];
		for (int i = 0; i < vertices.size(); i++)
			for (int j = 0; j < vertices.size(); j++){
				if (hasEdge(vertices.get(i), vertices.get(j)))
					ret[i][j] = 1;
				else
					ret[i][j] = 0;

			}
		return ret;
	}

	public void printAdjacencyMatrix(){
		int[][] adjMatrix = adjacencyMatrix();
		for (int[] row : adjMatrix){
			System.out.print("[");
			for (int j = 0; j < row.length; j++){
				System.out.print(row[j]);
				if (j < row.length - 1)
					System.out.print(", ");
			}
			System.out.print("],");

		}
		System.out.println("");
	}

	public V getVertexByContent(Object content){
		return vertexByContentMap.get(content);
	}


	public List<V> getVertices() {
		return vertices;
	}

	public void setVertices(List<V> vertices) {
		this.vertices = vertices;
	}

	public List<E> getEdges() {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (directed ? 1231 : 1237);
		result = prime * result + ((edges == null) ? 0 : edges.hashCode());
		result = prime * result
				+ ((vertices == null) ? 0 : vertices.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Graph<?,?> other = (Graph<?,?>) obj;
		if (vertices != null && (other.getVertices()) == null )
			return false;
		if (vertices == null && (other.getVertices()) != null )
			return false;
		if (vertices != null && other.getVertices() != null){
			if (vertices.size() != other.getVertices().size())
				return false;
			for (V v1 : vertices){
				boolean found = false;
				for (Object v2 : other.getVertices()){
					if (v1.equals(v2)){
						found = true;
						break;
					}
				}
				if (!found){
					return false;
				}
			}
		}

		if (edges != null && (other.getEdges()) == null )
			return false;
		if (edges == null && (other.getEdges()) != null )
			return false;
		if (edges != null && other.getEdges() != null){
			if (edges.size() != other.getEdges().size())
				return false;
			for (E e1 : edges){
				boolean found = false;
				for (Object e2 : other.getEdges()){
					if (e1.equals(e2)){
						found = true;
						break;
					}
				}
				if (!found){
					return false;
				}
			}
		}
		return true;
	}


	/**
	 * @return the adjacentLists
	 */
	public Map<V, LinkedList<E>> getAdjacentLists() {
		return adjacentLists;
	}


}
