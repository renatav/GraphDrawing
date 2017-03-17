package graph.layout;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import graph.drawing.Drawing;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

import java.awt.geom.Point2D;

public abstract class AbstractJungLayouter<V extends Vertex, E extends Edge<V>> extends AbstractLayouter<V, E>{


	protected Layout<V,E> layouter;
	protected edu.uci.ics.jung.graph.Graph<V,E> jungGraph;

	protected void createJungGraph(Graph<V,E> graph){
		
		for (V v : graph.getVertices())
			jungGraph.addVertex(v);

		for (E e : graph.getEdges())
			jungGraph.addEdge(e, e.getOrigin(), e.getDestination());
	}


	public Drawing<V,E> layout(Graph<V,E> graph, GraphLayoutProperties layoutProperties){
		createJungGraph(graph);
		initLayouter(layoutProperties);
		return createDrawing(graph);

	}

	protected Drawing<V,E> createDrawing(Graph<V,E> graph){

		//triggers layouting
		new DefaultVisualizationModel<V, E>(layouter);

		Drawing<V, E> drawing = new Drawing<>();

		for (V v : graph.getVertices()){
			Point2D p = layouter.transform(v);
			drawing.setVertexPosition(v, p);
		}
		//drawing.separate(100, 100);
		return drawing;
	}
	protected abstract void initLayouter(GraphLayoutProperties layoutProperties);
	
}