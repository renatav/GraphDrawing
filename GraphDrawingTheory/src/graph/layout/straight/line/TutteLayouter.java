package graph.layout.straight.line;

import graph.algorithms.drawing.TutteEmbedding;
import graph.algorithms.planarity.BoyerMyrvoldPlanarity;
import graph.drawing.Drawing;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.layout.GraphLayoutProperties;
import graph.layout.PropertyEnums.TutteProperties;
import graph.layout.symmetric.SymmetricLayouter;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;

public class TutteLayouter <V extends Vertex, E extends Edge<V>> extends SymmetricLayouter<V, E>{


	@Override
	public Drawing<V, E> layout(Graph<V, E> graph,
			GraphLayoutProperties layoutProperties) {

		if (layoutProperties.getProperty(TutteProperties.DISTANCE) != null)
			distance =  (Double) layoutProperties.getProperty(TutteProperties.DISTANCE);
		if (layoutProperties.getProperty(TutteProperties.CENTER) != null)
			center = (Point2D) layoutProperties.getProperty(TutteProperties.CENTER);

		init(graph);

		BoyerMyrvoldPlanarity<V, E> boyerMyrvold = new BoyerMyrvoldPlanarity<V,E>();
		boyerMyrvold.isPlannar(graph);
		List<V> face = boyerMyrvold.getOutsideFace();

		TutteEmbedding<V, E> tutteEmbedding = new TutteEmbedding<V, E>(graph);

		Drawing<V,E> drawing = new Drawing<V,E>();

		Map<V, Point2D> vertexPositions;
		vertexPositions = tutteEmbedding.execute(face, center, distance);
		drawing.getVertexMappings().putAll(vertexPositions);

		return drawing;
	}
}