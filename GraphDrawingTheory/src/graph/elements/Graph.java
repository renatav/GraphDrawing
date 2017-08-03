package graph.elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import graph.properties.GraphProperties;

/**
 * /**
 * A graph consisting of a set of vertices of type <code>V</code>
 * and a set of edges of type <code>E</code>. 
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type 
 */
public class Graph<V extends Vertex,E extends Edge<V>>{

	protected List<V> vertices;
	protected List<E> edges;
	protected boolean directed = false;
	protected GraphProperties<V,E> properties;

	/**
	 * An adjacent list contains a list of all the edges leaving the vertex
	 */
	protected Map<V, List<E>> adjacentLists;

	/**
	 * Map of all edges leaving vertices
	 */
	protected Map<V, List<E>> outgoingEdges;
	
	/**
	 * Map of all edges entering vertices
	 */
	protected Map<V, List<E>> incomingEdges;

	/**
	 * Vertex by content map
	 */
	protected Map<Object, V> vertexByContentMap;

	/**
	 * Creates a graph by creating empty lists of edges, vertices and other properties
	 * By default, the graph is undirected
	 */
	public Graph(){
		vertices = new ArrayList<V>();
		edges = new ArrayList<E>();
		adjacentLists = new HashMap<V, List<E>>();
		vertexByContentMap = new HashMap<Object,V>();
		properties = new GraphProperties<V, E>(this);
		outgoingEdges = new HashMap<V, List<E>>();
		incomingEdges = new HashMap<V, List<E>>();
	}


	@SuppressWarnings("unchecked")
	/**
	 * Creates a graph and sets vertices and edges
	 * By default, the graph is undirected
	 * @param vertices A list of vertices
	 * @param edges A list of edges
	 */
	public Graph(List<V> vertices, List<E> edges){
		this();
		for (V v : vertices)
			addVertex(v);
		for (E e : edges)
			addEdge(e);

	}

	/**
	 * Creates a directed or undirected graph, depending on the provided parameter value
	 * @param directed {@code true} if the graph should be directed, {@code false} otherwise
	 */
	public Graph(boolean directed){
		this();
		this.directed = directed;
	}

	/**
	 * Checks if the graph contains a certain vertex
	 * @param v Vertex
	 * @return {@code true} if the graph contains {@code v}, {@code false} otherwise
	 */
	public boolean hasVertex(V v){
		return vertices.contains(v);
	}


	@SuppressWarnings("unchecked")
	/**
	 * Adds a desired number of vertices to the graph and updates
	 * all relevant structures
	 * @param vert One or more vertices to add
	 */
	public void addVertex(V...vert){
		for (V v : vert){
			if (!vertices.contains(v)){
				vertices.add(v);
				adjacentLists.put(v, new ArrayList<E>());
				vertexByContentMap.put(v.getContent(), v);
			}
		}
	}

	/**
	 * Add one vertex to the graph
	 * @param v Vertex to add
	 */
	public void addVertex(V v){
		if (vertices.contains(v))
			return;
		vertices.add(v);
		adjacentLists.put(v, new ArrayList<E>());
		vertexByContentMap.put(v.getContent(), v);
	}

	/**
	 * Adds vertex as to the graph before any other vertex
	 * @param v Vertex to add
	 */
	public void addVertexBeginning(V v){
		vertices.add(0, v);
		adjacentLists.put(v, new ArrayList<E>());
	}

	/**
	 * Removes a vertex from the graph, thus updating all relevant structures
	 * and also removing the edges it was a part of
	 * @param v Vertex to remove
	 */
	public void removeVertex(V v){
		vertices.remove(v);
		List<E> adjacent = new ArrayList<E>();
		adjacent.addAll(adjacentLists.get(v));
		for (E e : adjacent){
			removeEdge(e);
		}
		adjacentLists.remove(v);
		outgoingEdges.remove(v);
		incomingEdges.remove(v);

	}


	@SuppressWarnings("unchecked")
	/**
	 * Adds a desired number of edges to the graph and updates
	 * all relevant structures
	 * @param edge One or more edges to add
	 */
	public void addEdge(E...edge){

		for (E e : edge){
			if (edges.contains(e))
				continue;
			edges.add(e);

			V origin = e.getOrigin();
			V destination = e.getDestination();

			if (adjacentLists.get(origin) != null){
				adjacentLists.get(origin).add(e);
			}
			//add it even if the graph is directed
			if (adjacentLists.get(e.getDestination()) != null){
				adjacentLists.get(e.getDestination()).add(e);
			}

			if (!incomingEdges.containsKey(destination))
				incomingEdges.put(destination, new ArrayList<E>());

			if (!outgoingEdges.containsKey(origin))
				outgoingEdges.put(origin, new ArrayList<E>());

			incomingEdges.get(destination).add(e);
			outgoingEdges.get(origin).add(e);

		}
	}

	/**
	 * Checks if vertices v1 and v2 are connected i.e. if there is an edge between them
	 * @param v1 Source vertex
	 * @param v2 Destination vertex
	 * @return {@code true} if there is and edge between v1 and v2, otherwise {@code false}
	 */
	public boolean hasEdge(V v1, V v2){

		if (!directed){
			for (E e : adjacentLists.get(v1)){
				V other =  e.getOrigin() == v1 ?  e.getDestination() : e.getOrigin();
				if (other == v2)
					return true;
			}
			for (E e : adjacentLists.get(v2)){
				V other =  e.getOrigin() == v2 ?  e.getDestination() : e.getOrigin();
				if (other == v1)
					return true;
			}
		}
		else{
			if (outgoingEdges.containsKey(v1))
				for (E e : outgoingEdges.get(v1)){
					V other =  e.getOrigin() == v1 ?  e.getDestination() : e.getOrigin();
					if (other == v2)
						return true;
				} 
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

		if (!directed){
			if (adjacentLists.get(v1) != null)
				for (E e : adjacentLists.get(v1))
					if (e.getDestination() == v2 || e.getOrigin() == v2)
						ret.add(e);
		}
		else{
			if (outgoingEdges.containsKey(v1))
				for (E e : outgoingEdges.get(v1))
					if (e.getDestination() == v2)
						ret.add(e);
		}

		return ret;
	}

	/**
	 * Checks if there is an edge between the two given graph vertices and return is
	 * it if exists
	 * @param v1 The first vertex
	 * @param v2 The second vertex
	 * @return Edge between {@code v1} and {@code v2} if it exists, @{code null} otherwise
	 */
	public E edgeBetween(V v1, V v2){
		List<E> edges = edgeesBetween(v1, v2);
		if (edges.size() == 0)
			return null;
		else 
			return edges.get(0);
	}

	/**
	 * Removes an edge from the graph and updates all relevants structures
	 * @param e Edge to be removed
	 */
	public void removeEdge(E e){
		edges.remove(e);
		if (adjacentLists.get(e.getOrigin()) != null)
			adjacentLists.get(e.getOrigin()).remove(e);
		if (adjacentLists.get(e.getDestination()) != null)
			adjacentLists.get(e.getDestination()).remove(e);
		if (outgoingEdges.get(e.getOrigin()) != null)
			outgoingEdges.get(e.getOrigin()).remove(e);
		if (incomingEdges.get(e.getDestination()) != null)
			incomingEdges.get(e.getDestination()).remove(e);
	}

	public List<E> adjacentEdges(V v){
		return adjacentLists.get(v);
	}

	/**
	 * All edges leaving v
	 * @param v Vertex
	 * @return A list of all edges leaving graph vertex {@code v}
	 */
	public List<E> outEdges(V v){
		return outgoingEdges.get(v);
	}

	/**
	 * All vertices adjacent to the given one
	 * @param v Vertex
	 * @return A list of all vertices adjacent to graph vertex {@code v}
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
	 * Out degree of the given node
	 * @param v Vertex
	 * @return Out degree of {@code v}
	 */
	public int outDegree (V v){
		if (!outgoingEdges.containsKey(v))
			return 0;
		return outEdges(v).size();
	}

	/**
	 * All edges entering v
	 * @param v Vertex
	 * @return A list of all edges entering vertex {@code v}
	 */
	public List<E> inEdges(V v){
		return incomingEdges.get(v);
	}

	/**
	 * In degree of the given vertex
	 * @param v Vertex
	 * @return In degree of vertex {@code v}
	 */
	public int inDegree(V v){
		if (!incomingEdges.containsKey(v))
			return 0;
		return inEdges(v).size();
	}


	/**
	 * Checks if vertex is a source (vertex with no incoming edges)
	 * @param v Vertex
	 * @return {@code true} if vertex {@code v} is a source, {@code false} otherwise
	 */
	public boolean isSource(V v){
		return inDegree(v) == 0;
	}

	/**
	 * Checks if vertex is a sink (vertex with no outgoing edges)
	 * @param v Vertex
	 * @return {@code true} if vertex {@code v} is a sink, {@code false} otherwise
	 */
	public boolean isSink(V v){
		return outDegree(v) == 0;
	}

	/**
	 * All edges leaving or entering the given vertex
	 * @param v Vertex
	 * @return A list of all edges leaving or entering {@code v}
	 */
	public LinkedList<E> allEdges(V v){
		LinkedList<E> ret = new LinkedList<E>();
		for (E e : edges)
			if (e.getDestination() == v || e.getOrigin() == v)
				ret.add(e);
		return ret;
	}

	/**
	 * Finds all edges between certain vertices
	 * @param vertices A list of vertices
	 * @return A list of edges between vertices belonging to the {@code vertices} list
	 */
	public List<E> edgesBetween(List<V> vertices){
		List<E> ret = new ArrayList<E>();
		for (V v : vertices)
			for (E e : adjacentEdges(v)){
				if (!ret.contains(e)){
					V other = e.getOrigin() == v ? e.getDestination() : e.getOrigin();
					if (vertices.contains(other))
						ret.add(e);
				}
			}

		return ret;
	}

	/**
	 * All edges which connect one vertex to itself - loops
	 * @return A list of loops
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
	 * Checks if graph has loops
	 * @return {@code true} if the graph has loops, {@code false} otherwise
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
	 * @return {@code true} if the graph is simple, {@code false} otherwise
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
	 * @param v Vertex
	 * @return Vertex degree of {@code v}
	 */
	public int vertexDegree(V v){
		return adjacentLists.get(v).size();
	}

	/**
	 * Max vertex degree
	 * @return Maximum degree of any of the graph vertices
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
	 * @return {@code true} if the graph is connected, {@code false} otherwise
	 */
	public boolean isConnected(){
		return properties.isConnected();
	}

	/**
	 * Check if the graph is a tree
	 * @return {@code true} if the graph is a tree, {@code false} otherwise
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
	 * @param excluding Vertices without whom the graph should still be connected 
	 * @return {@code true} if the graph without vertices belonging to {@code excluding}
	 * is connected, {@code false} otherwise
	 */
	public boolean isConnected(List<V> excluding){
		return properties.isConnected(excluding);
	}
	
	/**
	 * Check if the graph is cyclic
	 * @return {@code true} if the graph is a tree, {@code false} otherwise
	 */
	public boolean isCyclic(){
		return properties.isCyclic();
	}

	/**
	 * Finds all sinks in the graph
	 * @return A list of sinks
	 */
	public List<V> getAllSinks(){
		List<V> ret = new ArrayList<V>();
		for (V v : vertices)
			if (isSink(v))
				ret.add(v);
		return ret;
	}

	/**
	 * Finds all sources in the graph
	 * @return A list of sources
	 */
	public List<V> getAllSources(){
		List<V> ret = new ArrayList<V>();
		for (V v : vertices)
			if (isSource(v))
				ret.add(v);
		return ret;
	}

	/**
	 * Finds all multiedges of the graph
	 * @return All multiedges. Group so that all edges between the same two vertices
	 * are in a separate list, which is then added to the another list which is returned 
	 */
	public List<List<E>> listMultiEdges(){
		return properties.listMultiEdges();
	}

	/**
	 * Check if the graph is a ring
	 * @return {@code true} if the graph is a tree, {@code false} otherwise
	 */
	public boolean isRing(){
		return properties.isRing();
	}

	/**
	 * Checks if a graph is biconnected. 
	 * A graph is biconnected if and only if any vertex is deleted, the graph remains connected.
	 * @return {@code true} if graph is biconnected, otherwise {@code false}
	 */
	public boolean isBiconnected(){
		return properties.isBiconnected();
	}

	/**
	 * Finds all cut vertices of a graph
	 * @return A list of graph's cut vertices
	 */
	public List<V> listCutVertices(){
		return properties.getCutVertices();
	}

	/**
	 * Finds all biconnected components of the graph
	 * @return A list of graph's biconnected components
	 */
	public List<Graph<V,E>>listBiconnectedComponents(){
		return properties.listBiconnectedComponents();
	}

	/**
	 * Creates a subgraph of the graph containing the
	 * given vertices
	 * @param subgraphVertices Vertices that should be in the subgraph
	 * @return Subgraph containing vertices @{code subgraphVertices}
	 */
	@SuppressWarnings("unchecked")
	public Graph<V,E> subgraph(List<V> subgraphVertices){
		Graph<V,E> subgraph = new Graph<V,E>();
		for (V v : subgraphVertices)
			subgraph.addVertex(v);

		for (E e : edges)
			if (subgraphVertices.contains(e.getDestination()) && 
					subgraphVertices.contains(e.getOrigin()))
				subgraph.addEdge(e);

		return subgraph;
	}

	/**
	 * Calculates the adjacency matrix of the graph
	 * @return Adjacency matrix
	 */
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

	/**
	 * Prints adjacency matrix
	 */
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

	/**
	 * Return a vertex with the provided content
	 * @param content Searched content
	 * @return A vertex whose content is @{code content}
	 */
	public V getVertexByContent(Object content){
		return vertexByContentMap.get(content);
	}

	/**
	 * @return Graph's vertices
	 */
	public List<V> getVertices() {
		return vertices;
	}

	/**
	 * @param vertices Vertices to set
	 */
	public void setVertices(List<V> vertices) {
		this.vertices = vertices;
	}

	/**
	 * @return Graph's edges
	 */
	public List<E> getEdges() {
		return edges;
	}

	/**
	 * @param edges Edges to set
	 */
	public void setEdges(List<E> edges) {
		this.edges = edges;
	}

	/**
	 * @return {@code true} if graph is directed, {@code false} otherwise
	 */
	public boolean isDirected() {
		return directed;
	}

	/**
	 * @param directed Value of directed property to set
	 */
	public void setDirected(boolean directed) {
		this.directed = directed;
	}
	
	/**
	 * @return Better formatted string representation of the graph. 
	 */
	public String printFormat(){
		String verticesStr = "V={";
		for (int i = 0; i < vertices.size(); i++){
			verticesStr += vertices.get(i);
			if( i < vertices.size() - 1)
				verticesStr += ", ";
		}
		verticesStr += "}";
		String edgesStr = "E={ ";
		for (int i = 0; i < edges.size(); i++){
			edgesStr += "(" + edges.get(i).getOrigin() + ", " + edges.get(i).getDestination() + ")";
			if( i < edges.size() - 1)
				edgesStr += ", ";
		}
		edgesStr += "}";
		
		return verticesStr + ", " + edgesStr;
		
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
	public Map<V, List<E>> getAdjacentLists() {
		return adjacentLists;
	}


}
