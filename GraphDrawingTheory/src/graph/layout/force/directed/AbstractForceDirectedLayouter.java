package graph.layout.force.directed;

import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.layout.AbstractJungLayouter;
import graph.layout.GraphLayoutProperties;

public abstract class AbstractForceDirectedLayouter<V extends Vertex, E extends Edge<V>> extends AbstractJungLayouter<V,E>{
	
	
	public AbstractForceDirectedLayouter(Graph<V, E> graph, GraphLayoutProperties layoutProperties) {
		super(graph, layoutProperties);
	}

	@Override
	protected void createJungGraph(){
		jungGraph = new UndirectedSparseGraph<V,E>();
		super.createJungGraph();
	}
	protected abstract void initLayouter();
		



}
