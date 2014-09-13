package graph.properties.splitting;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.operations.GraphOperations;

import java.util.ArrayList;
import java.util.List;

public class Splitting<V extends Vertex, E extends Edge<V>> {


	/**
	 * A pair {u,v} of vertices is a split pair if it is an edge in the graph
	 * or if it is a separation pair (it increases the number of connected components)
	 * in the graph (graph is no longer connected if it is removed)
	 * @param graph
	 * @return
	 */
	public List<SplitPair<V,E>> findAllSplitPairs(Graph<V,E> graph){

		List<SplitPair<V,E>> ret = new ArrayList<SplitPair<V,E>>();
		List<V> excluding = new ArrayList<V>();

		List<V> vertices =  graph.getVertices();

		for (int i = 0; i <vertices.size(); i++)
			for (int j = i+1; j < vertices.size(); j++){
				V v1 = vertices.get(i);
				V v2 = vertices.get(j);
				if (graph.hasEdge(v1, v2))
					ret.add(new SplitPair<V,E>(v1, v2));
				else{
					excluding.clear();
					excluding.add(v1);
					excluding.add(v2);
					if (!graph.isConnected(excluding)){
						ret.add(new SplitPair<V,E>(v1, v2));
					}
				}
			}

		return ret;
	}


	@SuppressWarnings("unchecked")
	public List<SplitComponent<V, E>> findAllSplitComponents(Graph<V,E> graph, SplitPair<V, E> splitPair){

		List<E> coveredEdges = new ArrayList<E>();
		List<SplitComponent<V, E>> ret = new ArrayList<SplitComponent<V,E>>();
		V u = splitPair.getU();
		V v = splitPair.getV();
		for (E e : graph.allEdges(u)){
			SplitComponent<V, E> component = new SplitComponent<>(splitPair, graph);
			ret.add(component);
			coveredEdges.add(e);
			component.addVertex(u);
			component.addEdge(e);
			V other = e.getDestination() == u ? e.getOrigin() : e.getDestination();
			if (other == v) //just add split pair vertices and the edge
				component.addVertex(v);

			else
				formSplitComponent(u, v, other, coveredEdges, new ArrayList<V>(), component, graph);
		}

		return ret;

	}

	@SuppressWarnings("unchecked")
	private void formSplitComponent(V u, V  v, V current, List<E> coveredEdges, List<V> coveredVertices,
			SplitComponent<V, E> component, Graph<V,E> graph){

		if (coveredVertices.contains(current))
			return;
		coveredVertices.add(current);

		component.addVertex(current);
		for (E e : graph.allEdges(current)){
			if (coveredEdges.contains(e))
				continue;

			component.addEdge(e);
			V other = e.getDestination() == current ? e.getOrigin() : e.getDestination();
			if (other != u && other != v){
				formSplitComponent(u, v, other, coveredEdges, coveredVertices, component, graph);
			}
			else
				component.addVertex(other);
		}

	}

	/**
	 * All components should have two vertices in common: split pair vertices
	 * and no edges
	 * @param components
	 * @return
	 */
	public boolean testSplitComponents(List<SplitComponent<V, E>> components, SplitPair<V,E> splitPair){
		GraphOperations<V, E> operations = new GraphOperations<>();
		for (int i = 0; i <components.size(); i++)
			for (int j = i+1; j < components.size(); j++){
				SplitComponent<V,E> com1 = components.get(i);
				SplitComponent<V, E> com2 = components.get(j);
				List<V> verticesInCommon = operations.verticesInCommon(com1, com2);
				if (!verticesInCommon.contains(splitPair.getU())  || 
						! verticesInCommon.contains(splitPair.getV()) || verticesInCommon.size() != 2)
					return false;
				List<E> edgesInCommon = operations.edgesInCommon(com1, com2);
				if (edgesInCommon.size() > 0)
					return false;

			}



		return true;
	}




}
