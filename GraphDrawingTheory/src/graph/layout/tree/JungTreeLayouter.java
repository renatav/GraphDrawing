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

/**
 * A layouter which creates a drawing of a graph using JUNG franework's tree algorithm
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type 
 */
public class JungTreeLayouter<V extends Vertex, E extends Edge<V>> extends AbstractJungLayouter<V, E>{

	@Override
	protected void createJungGraph(Graph<V,E> graph){
		jungGraph = new DelegateForest<>();
		super.createJungGraph(graph);
	}

	@Override
	protected void initLayouter(GraphLayoutProperties layoutProperties) {

		
		int xDist = -1, yDist = -1;
		
		if (layoutProperties != null){

			Object xDistValue = layoutProperties.getProperty(TreeProperties.X_DISTANCE);
			Object yDistValue = layoutProperties.getProperty(TreeProperties.Y_DISTANCE);

			if (xDistValue != null)
				xDist = (int) xDistValue;
			if (yDistValue != null)
				yDist = (int) yDistValue;
		}

		TreeLayout<V,E> treeLayout;
		if (xDist != -1){
			if (yDist != -1)
				treeLayout = new TreeLayout<V, E>((Forest<V, E>) jungGraph, xDist, yDist);
			else
				treeLayout = new TreeLayout<V, E>((Forest<V, E>) jungGraph, xDist);
		}
		else 
			treeLayout = new TreeLayout<V, E>((Forest<V, E>) jungGraph);

		layouter = treeLayout;

	}

}
