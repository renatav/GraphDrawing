package graph.layout.symmetric;

import java.awt.geom.Point2D;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.layout.AbstractLayouter;
import graph.layout.GraphLayoutProperties;
import graph.layout.PropertyEnums.SymmetricProperties;
import graph.layout.circle.CircleLayoutCalc;
import graph.symmetry.Permutation;

public abstract class SymmetricLayouter <V extends Vertex, E extends Edge<V>> extends AbstractLayouter<V, E>{

	protected Permutation p;
	protected Double distance;
	protected CircleLayoutCalc<V> calc = new CircleLayoutCalc<V>();
	protected Point2D center;
	
	public SymmetricLayouter(Graph<V, E> graph,
			GraphLayoutProperties layoutProperties) {
		super(graph, layoutProperties);
		init();
	}
	
	protected void init(){
		
		if (layoutProperties.getProperty(SymmetricProperties.DISTANCE) != null)
			distance =  (Double) layoutProperties.getProperty(SymmetricProperties	.DISTANCE);
		if (layoutProperties.getProperty(SymmetricProperties.PERMUTATION)!= null)
			p = (Permutation) layoutProperties.getProperty(SymmetricProperties.PERMUTATION);
		if (layoutProperties.getProperty(SymmetricProperties.CENTER) != null)
			p = (Permutation) layoutProperties.getProperty(SymmetricProperties.CENTER);
		else
			center = new Point2D.Double(0,0);
			
		
		if (distance == null){
			//find largest element by x or y
			distance = 0D;
			for (V v : graph.getVertices()){

				if (v.getSize().getHeight() > distance)
					distance = v.getSize().getHeight();

				if (v.getSize().getWidth() > distance)
					distance = v.getSize().getWidth();
			}
			distance *= 1.3;
		}
	}

}
