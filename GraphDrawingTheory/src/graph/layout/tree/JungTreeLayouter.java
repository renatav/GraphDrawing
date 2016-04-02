package graph.layout.tree;

import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.Forest;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.layout.AbstractJungLayouter;
import graph.layout.GraphLayoutProperties;
import graph.layout.PropertyEnums.TreeProperties;

public class JungTreeLayouter<V extends Vertex, E extends Edge<V>> extends AbstractJungLayouter<V, E>{

	@Override
	protected void createJungGraph(Graph<V,E> graph){
		jungGraph = new DelegateForest<>();
		super.createJungGraph(graph);
	}

	@Override
	protected void initLayouter(GraphLayoutProperties layoutProperties) {

		
		Integer xDist = 50, yDist = 50;
		
		if (layoutProperties != null){
			layoutProperties.getProperty(TreeProperties.X_DISTANCE);


			Object xDistValue = layoutProperties.getProperty(TreeProperties.X_DISTANCE);
			Object yDistValue = layoutProperties.getProperty(TreeProperties.Y_DISTANCE);

			if (xDistValue instanceof Double)
				xDist = ((Double) xDistValue).intValue();

			if (yDistValue instanceof Double)
				yDist = ((Double) xDistValue).intValue();
		}

		TreeLayout<V,E> treeLayout;
		if (xDist != null){
			if (yDist != null)
				treeLayout = new TreeLayout<V, E>((Forest<V, E>) jungGraph, xDist, yDist);
			else
				treeLayout = new TreeLayout<V, E>((Forest<V, E>) jungGraph, xDist);
		}
		else 
			treeLayout = new TreeLayout<V, E>((Forest<V, E>) jungGraph);

		layouter = treeLayout;

	}

}
