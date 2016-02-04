package graph.drawing;

import graph.elements.Edge;
import graph.elements.Vertex;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A drawing Γ of a graph G = (V, E) is  a mapping of each vertex v in V to a
distinct point Γ(v) and of each edge e = (u, v) in E to a simple open Jordan curve Γ(e),
represented here with a list of its nodes' positions,
which has Γ(u) and Γ(v) as its endpoints. 
 * @author xxx
 *
 * @param <V>
 * @param <E>
 */
public class Drawing<V extends Vertex, E extends Edge<V>> {

	private Map<V, Point2D> vertexMappings;
	private Map<E, List<Point2D>> edgeMappings;
	private int reursiveLinkDistance = 20;


	public Drawing(){
		vertexMappings = new HashMap<V, Point2D>();
		edgeMappings = new HashMap<E, List<Point2D>>();
	}

	public Drawing(Map<V, Point2D> vertexMappings,
			Map<E, List<Point2D>> edgeMappings) {
		super();
		this.vertexMappings = vertexMappings;
		this.edgeMappings = edgeMappings;
	}

	
	public void positionEdges(List<E> edges){
		
		//initialize mappings
		for (E e : edges){
			
			List<Point2D> edgeNodePoints = new ArrayList<Point2D>();
			
			//check if the edge's origin and destination vertices are the same
			
			if (e.getDestination() == e.getOrigin()){
				
				//position edge nodes near the top edge of the vertex, to the side, and near the bottom
				
				int vertexHeight = (int) e.getOrigin().getSize().getHeight();
				int vertexWidth = (int) e.getOrigin().getSize().getWidth();
				Point2D position = vertexMappings.get(e.getOrigin());
				int xPosition = (int) position.getX();
				int yPosition = (int) position.getY();
				
				//first node
				Point2D node1 = new Point(xPosition, (int) (yPosition - vertexHeight/3));
				Point2D node2 = new Point((int) (xPosition - vertexWidth - reursiveLinkDistance), (int) (yPosition - vertexHeight/3));
				Point2D node3 = new Point((int) (xPosition - vertexWidth - reursiveLinkDistance), (int) (yPosition + vertexHeight/3));
				Point2D node4 = new Point(xPosition, (int) (yPosition + vertexHeight/3));
				
				edgeNodePoints.add(node1);
				edgeNodePoints.add(node2);
				edgeNodePoints.add(node3);
				edgeNodePoints.add(node4);
				
			}
			//else if the link isn't recursive
			else{ 
				Point2D originPosition = vertexMappings.get(e.getOrigin());
				Point2D destinationPosition = vertexMappings.get(e.getDestination());
				
				edgeNodePoints.add(new Point((int) originPosition.getX(), (int) originPosition.getY()));
				edgeNodePoints.add(new Point((int) destinationPosition.getX(), (int) destinationPosition.getY()));
			}
			
			edgeMappings.put(e, edgeNodePoints);
		}
		
		//now check for multiple links
		List<E> processedEdges = new ArrayList<E>();
		for (E e : edges){
			if (processedEdges.contains(e))
				continue;
			
			processedEdges.add(e);
			List<E> multipleEdges = findMultipleEdgesForEdge(e);
			if (multipleEdges.size() == 0)
				continue;
			
			int count = multipleEdges.size();
			int originWidth = (int) e.getOrigin().getSize().getWidth();
			int destinationWidth = (int) e.getDestination().getSize().getWidth();
			
			
			int distanceOrigin = originWidth/(count * 2);
			int distanceDestination = destinationWidth/(count * 2);
			
			int distanceMultiplicity = 1;
			
			for (int i = 0; i <  multipleEdges.size(); i++){
				
				E multEedge = multipleEdges.get(i);
				Point2D originPosition = edgeMappings.get(multEedge).get(0);
				Point2D destinationPosition = edgeMappings.get(multEedge).get(1);
				
				if (i < (int) multipleEdges.size()/2){
					originPosition.setLocation((int)(originPosition.getX() - distanceMultiplicity * distanceOrigin),
							originPosition.getY());
					destinationPosition.setLocation((int)(destinationPosition.getX() - distanceMultiplicity * distanceDestination),
							destinationPosition.getY());
				}
				
				else{
					originPosition.setLocation((int)(originPosition.getX() + distanceMultiplicity * distanceOrigin),
							originPosition.getY());
					destinationPosition.setLocation((int)(destinationPosition.getX() + distanceMultiplicity * distanceDestination),
							destinationPosition.getY());
				}
				
				if (i == (int) multipleEdges.size()) //change side from left to right
					distanceMultiplicity = 1;
				else
					distanceMultiplicity ++;
					
			}
			
			processedEdges.addAll(multipleEdges);
			
			
		}
	}
	
	private List<E> findMultipleEdgesForEdge(E edge){
		List<E> ret = new ArrayList<E>();
	
		for (E e : edgeMappings.keySet()){
			if (e == edge)
				continue;
			if (e.getOrigin() == edge.getOrigin() && e.getDestination() == edge.getDestination())
				ret.add(e);
		}
		
		return ret;
	}

	public void separate(int minXDistance, int minYDistance){
		
		List<V> covered = new ArrayList<V>();

		//separate on y axis
		while (covered.size() <= vertexMappings.size()){
			V top = findTopExcluding(covered);
			covered.add(top);
			for (V other : vertexMappings.keySet()){

				if (covered.contains(other))
					continue;

				int yDist = calcDistances(top, other)[1];
				int xDist = calcDistances(top, other)[0];
				
				if (yDist < minYDistance && xDist < 0){
					
					int toBeMoved = - yDist + minYDistance;
					Point2D position = vertexMappings.get(other);
					position.setLocation(position.getX(), position.getY() + toBeMoved);
				}

			}
		}

		//separate on x axis
		covered.clear();
		while (covered.size() <= vertexMappings.size()){
			V leftmost = findLeftmostExcluding(covered);
			covered.add(leftmost);
			for (V other : vertexMappings.keySet()){

				if (covered.contains(other))
					continue;

				int xDist = calcDistances(leftmost, other)[0];
				int yDist = calcDistances(leftmost, other)[1];
				
				if (xDist < minXDistance && yDist < 0){
					int toBeMoved = - xDist + minXDistance;
					Point2D position = vertexMappings.get(other);
					position.setLocation(position.getX() + toBeMoved, position.getY());
				}

			}
		}

	}

	public int findTop(){
		V top =  findTopExcluding(null);
		return (int) (vertexMappings.get(top).getY() - top.getSize().getHeight()/2);
	}

	public int findBottom(){
		V bottom =  findBottomExcluding(null);
		return (int) (vertexMappings.get(bottom).getY() + bottom.getSize().getHeight()/2);
	}

	public int findMiddle(){
		return (findBottom() - findTop())/2;
	}

	private V findTopExcluding(List<V> excluding){
		V top = null;
		for (V v : vertexMappings.keySet()){
			if (excluding != null && excluding.contains(v))
				continue;
			if (top == null || vertexMappings.get(v).getY() - v.getSize().getHeight()/2 < 
					vertexMappings.get(top).getY() - top.getSize().getHeight()/2)
				top = v;
		}
		return top;
	}

	private V findBottomExcluding(List<V> excluding){
		V bottom = null;
		for (V v : vertexMappings.keySet()){
			if (excluding != null && excluding.contains(v))
				continue;
			if (bottom == null || vertexMappings.get(v).getY() + v.getSize().getHeight()/2 > 
					vertexMappings.get(bottom).getY() + bottom.getSize().getHeight()/2)
				bottom = v;
		}
		return bottom;
	}

	public int findLeftmostPosition(){
		V leftmost = findLeftmostExcluding(null);
		return (int) (vertexMappings.get(leftmost).getX() - leftmost.getSize().getWidth()/2);
	}

	private V findLeftmostExcluding(List<V> excluding){
		V leftmost = null;
		for (V v : vertexMappings.keySet()){
			if (excluding != null && excluding.contains(v))
				continue;
			if (leftmost == null || vertexMappings.get(v).getX() < vertexMappings.get(leftmost).getX())
				leftmost = v;
		}
		return leftmost;
	}

	private int[] calcDistances(V v1, V v2){
		int v1X = (int) vertexMappings.get(v1).getX() + (int) v1.getSize().getWidth()/2;
		int v2X = (int) vertexMappings.get(v2).getX() - (int) v2.getSize().getWidth()/2;
		int v1Y = (int) vertexMappings.get(v1).getY() + (int) v1.getSize().getHeight()/2;
		int v2Y = (int) vertexMappings.get(v2).getY() - (int) v2.getSize().getHeight()/2;

		int[] ret = new int[2];
		ret[0] = -(v1X - v2X);
		ret[1] = -(v1Y - v2Y);
		return ret;

	}

	public int[] getBounds(){

		int bounds[] = new int[2];
		V xMax = null, yMax = null, xMin = null, yMin = null;

		for (V v : vertexMappings.keySet()){

			if (xMax == null || vertexMappings.get(xMax).getX() + xMax.getSize().getWidth()/2 < vertexMappings.get(v).getX() + v.getSize().getWidth()/2)
				xMax = v;

			if (xMin == null || vertexMappings.get(xMin).getX() -  xMin.getSize().getWidth()/2 > vertexMappings.get(v).getX() - v.getSize().getWidth()/2)
				xMin = v;

			if (yMax == null || vertexMappings.get(yMax).getY() + yMax.getSize().getHeight()/2 < vertexMappings.get(v).getY() + v.getSize().getHeight()/2)
				yMax = v;

			if (yMin == null || vertexMappings.get(yMin).getY() - yMin.getSize().getHeight()/2 > vertexMappings.get(v).getY() - v.getSize().getHeight()/2)
				yMin = v;

		}

		int width = (int) (vertexMappings.get(xMax).getX() + xMax.getSize().getWidth()/2 - vertexMappings.get(xMin).getX() + xMin.getSize().getWidth()/2);
		int height = (int) (vertexMappings.get(yMax).getY() + yMax.getSize().getHeight()/2 - vertexMappings.get(yMin).getY() + yMin.getSize().getHeight()/2);

		bounds[0] = width;
		bounds[1] = height;

		return bounds;
	}

	public void moveBy(int x, int y){
		for (V v : vertexMappings.keySet()){
			Point2D pos = vertexMappings.get(v);
			pos.setLocation(pos.getX() + x, pos.getY() + y);
		}
	}


	public void setVertexPosition(V v, Point2D pos){
		vertexMappings.put(v, pos);
	}

	public void setEdgePosition(E e, List<Point2D> nodes){ 
		edgeMappings.put(e, nodes);
	}

	public Map<V, Point2D> getVertexMappings() {
		return vertexMappings;
	}

	public void setVertexMappings(Map<V, Point2D> vertexMappings) {
		this.vertexMappings = vertexMappings;
	}

	public Map<E, List<Point2D>> getEdgeMappings() {
		return edgeMappings;
	}

	public void setEdgeMappings(Map<E, List<Point2D>> edgeMappings) {
		this.edgeMappings = edgeMappings;
	}

	public void setReursiveLinkDistance(int reursiveLinkDistance) {
		this.reursiveLinkDistance = reursiveLinkDistance;
	}

	public int getReursiveLinkDistance() {
		return reursiveLinkDistance;
	}




}
