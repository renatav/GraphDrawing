package graph.layout.tree;

import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.Forest;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.layout.AbstractJungLayouter;
import graph.layout.GraphLayoutProperties;

public class TreeLayouter<V extends Vertex, E extends Edge<V>> extends AbstractJungLayouter<V, E>{

	public TreeLayouter(Graph<V, E> graph,
			GraphLayoutProperties layoutProperties) {
		super(graph, layoutProperties);
	}

	@Override
	protected void createJungGraph(){
		jungGraph = new DelegateForest<>();
	}
	
	@Override
	protected void initLayouter() {
		TreeLayout<V,E> treeLayout = new TreeLayout<V, E>((Forest<V, E>) jungGraph);
		layouter = treeLayout;
		
	}

	

}
