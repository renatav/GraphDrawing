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
		 List<V> sinkVertices = copy.getAllSinks();
		 List<V> nextSinKVertices = new ArrayList<V>();
		 List<E> inEdges;
		 int n = 0;
		 do{
			 
			nextSinKVertices.clear();
			 for (V v : sinkVertices){
				 T.put(v, n);
				 inEdges = copy.inEdges(v);
				 
				 if (inEdges != null){
					 for (E e : inEdges){
						 V u = e.getOrigin();
						 if (copy.outDegree(u) - 1 == 0)
							 nextSinKVertices.add(u);
					 }
				 }
				 copy.removeVertex(v);
				 n++;
			 }
			 sinkVertices.clear();
			 sinkVertices.addAll(nextSinKVertices);
		 }
		 while (sinkVertices.size() > 0);
			 
		 return T;
	}


}
