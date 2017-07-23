package graph.layout.organic;

import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.layout.AbstractJungLayouter;
import graph.layout.GraphLayoutProperties;

/**
 * Layouter which uses JUNG grapmework's ISOM layout
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type 
 */
public class JungISOMLayouter<V extends Vertex, E extends Edge<V>> extends AbstractJungLayouter<V,E>{

	@Override
	protected void createJungGraph(Graph<V,E> graph){
		if (graph.isDirected())
			jungGraph = new DirectedSparseGraph<V,E>();
		else
			jungGraph = new UndirectedSparseGraph<V,E>();
		super.createJungGraph(graph);
	}
	
	
	@Override
	protected void initLayouter(GraphLayoutProperties layoutProperties) {
		layouter = new ISOMLayout<V, E>(jungGraph);
		
	}
	

}
