package graph.layout;

import graph.elements.Edge;
import graph.elements.Vertex;
import graph.layout.box.BoxLayouter;
import graph.layout.circle.CircleLayouter;
import graph.layout.force.directed.FruchtermanReingoldLayouter;
import graph.layout.force.directed.KamadaKawaiLayouter;
import graph.layout.force.directed.SpringLayouter;
import graph.layout.symmetric.SymmetricCircleLayouter;
import graph.layout.symmetric.TutteLayouter;
import graph.layout.tree.BalloonLayouter;
import graph.layout.tree.RadialTreeLayouter;
import graph.layout.tree.TreeLayouter;

public class LayouterFactory<V extends Vertex, E extends Edge<V>> {

	
	public AbstractLayouter<V,E> createLayouter(LayoutAlgorithms algorithm){

		AbstractLayouter<V,E> layouter = null;
		
		if (algorithm == LayoutAlgorithms.KAMADA_KAWAI)
			layouter = new KamadaKawaiLayouter<V,E>();
		else if (algorithm == LayoutAlgorithms.FRUCHTERMAN_REINGOLD)
			layouter= new FruchtermanReingoldLayouter<V,E>();
		else if (algorithm == LayoutAlgorithms.CIRCLE)
			layouter = new CircleLayouter<V,E>();
		else if (algorithm == LayoutAlgorithms.SPRING)
			layouter = new SpringLayouter<V,E>();
		else if (algorithm == LayoutAlgorithms.BALLOON)
			layouter = new BalloonLayouter<V,E>();
		else if (algorithm == LayoutAlgorithms.RADIAL_TREE)
			layouter = new RadialTreeLayouter<V,E>();
		else if (algorithm == LayoutAlgorithms.TREE)
			layouter = new TreeLayouter<>();
		else if (algorithm == LayoutAlgorithms.BOX)
			layouter = new BoxLayouter<V,E>();
		else if (algorithm == LayoutAlgorithms.TUTTE)
			layouter = new TutteLayouter<V,E>();
		else if (algorithm == LayoutAlgorithms.CONCENTRIC)
			layouter = new SymmetricCircleLayouter<V,E>();
			
		return layouter;

	}
	
	
	
	
}
