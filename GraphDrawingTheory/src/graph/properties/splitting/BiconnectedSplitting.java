package graph.properties.splitting;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Finds all biconnected components of a graph
 * efficiently, by using the depth-first search 
 * Since the components should have the same features as block
 * Like removal of vertex which also removed the edge, being able to find its cut vertices etc. 
 * There is no specific class to represent the component, it is also a graph
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type 
 */
public class BiconnectedSplitting<V extends Vertex, E extends Edge<V>> {

	private boolean[] visited;
	private int[] parent, d, low;
	private int count;
	private Stack<E> stack;
	private Graph<V,E> graph;
	private List<V> vertices;


	public BiconnectedSplitting(Graph<V,E> graph){
		this.graph = graph;
	}
	
	/**
	 * @return A list of all biconnected components
	 */
	public List<Graph<V,E>> findBiconnectedComponents(){

		//initialization
		vertices = graph.getVertices();
		int n = vertices.size();
		visited = new boolean[n];
		parent = new int[n];
		d = new int[n];
		low = new int[n];
		stack = new Stack<E>();
		count = 0;

		List<Graph<V,E>> components = new ArrayList<Graph<V,E>>();
		vertices = graph.getVertices();

		for (V u : vertices)
			if (!visited[vertices.indexOf(u)])
				dfsVisit(u, components);

		return components;
	}

	private void dfsVisit(V u, List<Graph<V,E>> components){
		int uIndex = vertices.indexOf(u);
		visited[uIndex] = true;
		count ++;
		d[uIndex] = count;
		low[uIndex] = d[uIndex];
		for (E e : graph.adjacentEdges(u)){
			V v = e.getOrigin() == u ? e.getDestination() : e.getOrigin();
			int vIndex = vertices.indexOf(v);

			if (!visited[vIndex]){
				stack.push(e);
				parent[vIndex] = uIndex;
				dfsVisit(v, components);
				if (low[vIndex] >= d[uIndex])
					components.add(formComponent(e));
				low[uIndex] = Math.min(low[uIndex],low[vIndex]);
			}
			else if (parent[uIndex] != vIndex && d[vIndex] < d[uIndex]){
				//(u,v) is a back edge from u to its ancestor
				stack.push(e);
				low[uIndex] = Math.min(low[uIndex], d[vIndex]);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private Graph<V,E> formComponent(E e){
		Graph<V,E> component = new Graph<V,E>();
		E pop = null;
		do {
			pop = stack.pop();
			component.addVertex(pop.getOrigin(), pop.getDestination());
			component.addEdge(pop);
		}
		while (pop != e);

		return component;
	}


}
