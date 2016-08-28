package graph.ordering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.exception.CannotBeAppliedException;
import graph.util.Util;

/**
 * A topological ordering T(G) is an assignment of integer values T(v) to each
 * vertex v in V such that for every directed edge (u,v) in E T(u)<T(v) 
 *
 * @param <V>
 * @param <E>
 */
public class TopologicalOrdering {


	public static <V extends Vertex,E extends Edge<V>>  Map<V,Integer> calculateOrdering(Graph<V,E> graph) throws CannotBeAppliedException{
		
		if (!graph.isDirected() || graph.isCyclic())
			throw new CannotBeAppliedException("Canonical ordering is only for directed acyclic graphs");
		
		 Map<V, Integer> T = new HashMap<V, Integer>();
		 
		 Graph<V,E> copy = Util.copyGraph(graph);
		 List<V> sourceVertices = copy.getAllSources();
		 List<V> nextSourceVertices = new ArrayList<V>();
		 List<E> outEdges;
		 int n = 0;
		 do{
			 
			 System.out.println("Current sources: " + sourceVertices );
			 
			 nextSourceVertices.clear();
			 for (V v : sourceVertices){
				 T.put(v, n);
				 outEdges = copy.outEdges(v);
				 
				 if (outEdges != null){
					 for (E e : outEdges){
						 V u = e.getDestination();
						 if (copy.inDegree(u) - 1 == 0)
							 nextSourceVertices.add(u);
					 }
				 }
				 copy.removeVertex(v);
				 n++;
			 }
			 sourceVertices.clear();
			 sourceVertices.addAll(nextSourceVertices);
		 }
		 while (sourceVertices.size() > 0);
			 
		 return T;
	}


}
