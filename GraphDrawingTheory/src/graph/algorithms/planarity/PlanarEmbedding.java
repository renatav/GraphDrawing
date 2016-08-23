package graph.algorithms.planarity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import graph.algorithms.drawing.NotPlanarException;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.exception.CannotBeAppliedException;

public class PlanarEmbedding {

	/**
	 * Calculates a planar embedding of a graph based on the work of Chiba, Nishizeki, Abe and Ozava
	 * @param graph
	 * @return
	 * @throws CannotBeAppliedException
	 */
	public static <V extends Vertex, E extends Edge<V>> Map<V,List<E>> emedGraph(Graph<V,E> graph) throws NotPlanarException{
		Map<V,List<E>> embedding = new HashMap<V, List<E>>();

		//get upwards embedding
		PQTreePlanarity<V, E> pqPlanarity = new PQTreePlanarity<V,E>();
		if (!pqPlanarity.isPlannar(graph))
			throw new NotPlanarException();

		//copy upwards embedding
		Map<V, List<E>> upwardsEmbedding = pqPlanarity.getUpwardsEmbedding();

		for (V v : upwardsEmbedding.keySet()){
			List<E> list = new ArrayList<E>();
			list.addAll(upwardsEmbedding.get(v));
			embedding.put(v, list);
		}

		List<V> covered = new ArrayList<V>();

		List<V> stOrder = pqPlanarity.getStOrder();
		dfs(stOrder.get(stOrder.size() - 1), covered, upwardsEmbedding, embedding);

		return embedding;

	}

	private static <V extends Vertex, E extends Edge<V>> void dfs(V y, List<V> covered, Map<V,List<E>> upwardsEmbedding, Map<V,List<E>> embedding){
		//mark vertex y as old - adding  it to the list
		covered.add(y);

		//for each vertex v in Au(y) insert y to the top of Au(v)
		//modified this a little and adding edges

		if (upwardsEmbedding.containsKey(y))

			for (E e : upwardsEmbedding.get(y)){
				V v = e.getOrigin() == y ? e.getDestination() : e.getOrigin();
				List<E> list = embedding.get(v);
				if (list == null){
					list = new ArrayList<E>();
					embedding.put(v, list);
				}
				list.add(e);
				//if v is new - not in the list
				if (!covered.contains(v))
					dfs(v, covered, upwardsEmbedding, embedding);
			}

	}


}
