package graph.layout.circle;

import java.awt.geom.Point2D;
import java.util.List;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import graph.drawing.Drawing;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.layout.AbstractLayouter;
import graph.layout.GraphLayoutProperties;
import graph.ordering.circular.Circular;

public class CircleLayouter<V extends Vertex, E extends Edge<V>> extends AbstractLayouter<V, E>
{

	public CircleLayouter(Graph<V, E> graph, GraphLayoutProperties layoutProperties) {
		super(graph, layoutProperties);
	}


	@Override
	public Drawing<V, E> layout() {
		Circular<V,E> circular = new Circular<V,E>(graph);
		List<V> ordering = circular.circularOrdering();
		//just changes the order
		graph.setVertices(ordering);

		UndirectedSparseGraph<V, E> jungGraph =  new UndirectedSparseGraph<V, E>();

		for (V v : graph.getVertices())
			jungGraph.addVertex(v);

		for (E e : graph.getEdges())
			jungGraph.addEdge(e, e.getOrigin(), e.getDestination());


		CircleLayout<V,E> layouter = new CircleLayout<V,E>(jungGraph);
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
}
