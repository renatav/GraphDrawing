package graph.layout.force.directed;

import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.layout.AbstractJungLayouter;
import graph.layout.GraphLayoutProperties;

public abstract class AbstractForceDirectedLayouter<V extends Vertex, E extends Edge<V>> extends AbstractJungLayouter<V,E>{
	

	@Override
	protected void createJungGraph(Graph<V,E> graph){
		jungGraph = new UndirectedSparseGraph<V,E>();
		super.createJungGraph(graph);
	}
	
	
	protected abstract void initLayouter(GraphLayoutProperties layoutProperties);
		



}
