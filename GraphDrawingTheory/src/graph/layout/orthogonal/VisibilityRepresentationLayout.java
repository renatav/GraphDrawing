package graph.layout.orthogonal;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import graph.algorithms.drawing.VisibilityRepresentation;
import graph.drawing.Drawing;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.layout.AbstractLayouter;
import graph.layout.GraphLayoutProperties;

public class VisibilityRepresentationLayout<V extends Vertex, E extends Edge<V>> extends AbstractLayouter<V, E>{


	private int xDistance = 50;
	private int yDistance = 50;
	private int moveEdgeBy = 10;

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

		//there is also a possibility of having overlapping edges
		//so that should be handled
		Map<V, List<OrthogonalConnector<V>>> connectorsMap = new HashMap<V, List<OrthogonalConnector<V>>>();

		Drawing<V,E> drawing = new Drawing<V,E>();
		Map<V, Point2D> vertexMappings = drawing.getVertexMappings();
		for (V v : graph.getVertices()){
			int xMin = vXMinMap.get(v);
			int xMax = vXMaxMap.get(v);
			int xPosition = xMin * xDistance + (xMax - xMin)*xDistance/2;
			Point2D position = new Point2D.Double (xPosition, vYMap.get(v) * yDistance);
			vertexMappings.put(v, position);
			connectorsMap.put(v, new ArrayList<OrthogonalConnector<V>>());
		}

		//now route the edges
		//TODO maybe place this in a separate class to enable further usage

		Map<E, List<Point2D>> edgesMappings = drawing.getEdgeMappings();
		for (E edge : graph.getEdges()){
			V origin = edge.getOrigin();
			V destination = edge.getDestination();



			int x = eXMap.get(edge) * xDistance;
			//is the vertex still at that position (depends on how wide it is)

			int xOrigin = (int) vertexMappings.get(origin).getX();
			int xOriginWidth = origin.getSize().width;
			int originMin = xOrigin - xOriginWidth/2;
			int originMax = xOrigin + xOriginWidth/2;
			int y1 = (int) vertexMappings.get(origin).getY();

			int xDestination = (int) vertexMappings.get(destination).getX();
			int xDestinationWidth = origin.getSize().width;
			int desitnationMin = xDestination - xDestinationWidth/2;
			int destinationMax = xDestination + xDestinationWidth/2;
			int y2 = (int) vertexMappings.get(destination).getY();


			List<Point2D> nodePositions = new ArrayList<Point2D>();
			if (xOrigin == xDestination){
				Point2D node1 = new Point2D.Double(xOrigin, y1);
				Point2D node2 = new Point2D.Double(xOrigin, y2);
				nodePositions.add(node1);
				nodePositions.add(node2);

				OrthogonalConnector<V> connector1, connector2;
				if (y1 > y2){
					connector1 = createOrFindConnector(origin, EntryDirection.UP, connectorsMap);
					connector2 = createOrFindConnector(destination, EntryDirection.DOWN, connectorsMap);
				}
				else{
					connector1 = createOrFindConnector(origin, EntryDirection.DOWN, connectorsMap);
					connector2 = createOrFindConnector(destination, EntryDirection.UP, connectorsMap);
				}
				connector1.incNumber();
				connector2.incNumber();
				connector1.addEdge(nodePositions);
				connector2.addEdge(nodePositions);
			}
			else{

				if (x > originMax  || x < originMin){
					Point2D node1 = new Point2D.Double(xOrigin, y1);
					Point2D node2 = new Point2D.Double(x, y1);
					nodePositions.add(node1);
					nodePositions.add(node2);
					OrthogonalConnector<V> conn;
					if (x > originMax)
						conn = createOrFindConnector(origin, EntryDirection.RIGHT, connectorsMap);
					else
						conn = createOrFindConnector(origin, EntryDirection.LEFT, connectorsMap);
					conn.incNumber();
					conn.addEdge(nodePositions);
				}
				else{
					Point2D node = new Point2D.Double(x, y1);
					OrthogonalConnector<V> conn;
					if (y1 > y2)
						conn = createOrFindConnector(origin, EntryDirection.UP, connectorsMap);
					else
						conn = createOrFindConnector(origin, EntryDirection.DOWN, connectorsMap);
					conn.incNumber();
					conn.addEdge(nodePositions);

					nodePositions.add(node);
				}
				if (x > destinationMax  || x < desitnationMin){
					Point2D node1 = new Point2D.Double(xDestination, y2);
					Point2D node2 = new Point2D.Double(x, y2);
					nodePositions.add(node2);
					nodePositions.add(node1);

					OrthogonalConnector<V> conn;
					if (x > destinationMax)
						conn = createOrFindConnector(destination, EntryDirection.RIGHT, connectorsMap);
					else
						conn = createOrFindConnector(destination, EntryDirection.LEFT, connectorsMap);
					conn.incNumber();
					conn.addEdge(nodePositions);

				}
				else{
					Point2D node = new Point2D.Double(x, y2);
					nodePositions.add(node);

					OrthogonalConnector<V> conn;
					if (y2 > y1)
						conn = createOrFindConnector(destination, EntryDirection.UP, connectorsMap);
					else
						conn = createOrFindConnector(destination, EntryDirection.DOWN, connectorsMap);
					conn.incNumber();
					conn.addEdge(nodePositions);

				}
			}

			edgesMappings.put(edge, nodePositions);

		}

		//now move the last segments if necessary to prevent overlapping of edges
		for (V  v : graph.getVertices()){
			for (OrthogonalConnector<V> conn : connectorsMap.get(v)){
				int number = conn.getNumber();
				if (number > 1){
					List<List<Point2D>> edgesWithNodePositions = conn.getEdgesWithNodePositions();
					Point2D position = vertexMappings.get(v);
					int ySize = (int) v.getSize().getHeight();
					int xSize = (int) v.getSize().getWidth();
					
					//TODO
					//see where the edge later goes and position the edges accordingly
					//to avoid intersections
					//also take care of edges overlapping on other segments, not just here
					//but this is the most important aspect

					int yDist, yStart, xDist, xStart;
					int parts;
					//if there the number of the edges is even, divide the element into num + 2 components
					//otherwise into num + 1
					if (number % 2 == 0)
						parts = number + 2;
					else
						parts = number + 1;

					if (conn.getEntryDirection() == EntryDirection.LEFT || conn.getEntryDirection() == EntryDirection.RIGHT){

						yDist = ySize / parts;
						yStart = (int) position.getY() - ySize/2;
						xDist = 0;
						xStart = (int) position.getX();
					}
					else{
						xDist = xSize / parts;
						xStart = (int)position.getY() - xSize/2;
						yDist = 0;
						yStart = (int) position.getY();
					}
					
					int counter = 0;
					int half = 0;
					int numOfEdges = edgesWithNodePositions.size();
					if (numOfEdges % 2 == 0)
						half = numOfEdges /2;
					int yDistTotal = yDist;
					int xDistTotal = xDist;
					for (List<Point2D> nodes : edgesWithNodePositions){

						counter ++;
						Point2D node1, node2;
						

						if (nodes.get(0).getX() == position.getX() && nodes.get(0).getY() == position.getY()){
							node1 = nodes.get(0);
							node2 = nodes.get(1);
						}
						else{
							node1 = nodes.get(nodes.size() - 1);
							node2 = nodes.get(nodes.size() - 2);
						}
						
						node1.setLocation(xStart + xDistTotal, yStart + yDistTotal);
						yDistTotal += yDist;
						xDistTotal += xDist;
						if (counter == half){
							yDistTotal += yDist;
							xDistTotal += xDist;
						}
						if (xDist == 0)
							node2.setLocation(node2.getX(), node1.getY());
						else
							node2.setLocation(node1.getY(), node2.getX());
						
					}
				}
			}
		}

	return drawing;
}

private OrthogonalConnector<V> createOrFindConnector(V v, EntryDirection dir, Map<V, List<OrthogonalConnector<V>>> connectorsMap){
	for (OrthogonalConnector<V> conn : connectorsMap.get(v))
		if (conn.getEntryDirection() == dir)
			return conn;
	OrthogonalConnector<V> conn =  new OrthogonalConnector<V>(v, dir, 0);
	connectorsMap.get(v).add(conn);
	return conn;
}

}
