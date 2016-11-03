package graph.layout.orthogonal;

import java.awt.geom.Point2D;
import java.util.Map;

import graph.algorithms.drawing.VisibilityRepresentation;
import graph.drawing.Drawing;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.layout.AbstractLayouter;
import graph.layout.GraphLayoutProperties;
import graph.layout.router.OrthogonalEdgeRouter;

public class VisibilityRepresentationLayout<V extends Vertex, E extends Edge<V>> extends AbstractLayouter<V, E>{


	private int xDistance = 50;
	private int yDistance = 50;

	public VisibilityRepresentationLayout(){
		super();
		this.positionsEdges = true;
	}

	@Override
	public Drawing<V, E> layout(Graph<V, E> graph, GraphLayoutProperties layoutProperties) {

		VisibilityRepresentation<V, E> visibilityRepresentation = new VisibilityRepresentation<>(graph);

		Map<V, Integer> vYMap = visibilityRepresentation.getvYMap();
		Map<V, Integer> vXMinMap = visibilityRepresentation.getvXMinMap();
		Map<V, Integer> vXMaxMap = visibilityRepresentation.getvXMaxMap();
		Map<E, Integer> eXMap = visibilityRepresentation.geteXMap();
		//	Map<E, Integer> eYMaxMap = visibilityRepresentation.geteYMaxMap();
		//	Map<E, Integer> eYMinMap = visibilityRepresentation.geteYMinMap();

		//position vertices based on the calculated values

		//first idea
		//calculate the values as (max-min)/2

		Drawing<V,E> drawing = new Drawing<V,E>();
		Map<V, Point2D> vertexMappings = drawing.getVertexMappings();
		for (V v : graph.getVertices()){
			int xMin = vXMinMap.get(v);
			int xMax = vXMaxMap.get(v);
			int xPosition = xMin * xDistance + (xMax - xMin)*xDistance/2;
			Point2D position = new Point2D.Double (xPosition, vYMap.get(v) * yDistance);
			vertexMappings.put(v, position);
		
		}

		//now route the edges
		
		OrthogonalEdgeRouter<V, E> orthogonalRouter = new OrthogonalEdgeRouter<>(graph.getEdges(), vertexMappings, eXMap, xDistance);
		drawing.setEdgeMappings(orthogonalRouter.routeEdges());
	

		return drawing;
	}

	

}
