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
			
			System.out.println(yDistValue);

			if (xDistValue != null)
				xDist = (int) xDistValue;
			else
				xDist = 50;
			if (yDistValue != null)
				yDist = (int) yDistValue;
			else
				yDist = 50;
		}

		TreeLayout<V,E> treeLayout = new TreeLayout<V, E>((Forest<V, E>) jungGraph, xDist, yDist);

		layouter = treeLayout;

	}

}
