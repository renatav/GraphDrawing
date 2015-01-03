package graph.drawing;

import graph.elements.Edge;
import graph.elements.Vertex;

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


	public void separate(int minXDistance, int minYDistance){
		List<V> covered = new ArrayList<V>();

		//separate on y axis
		while (covered.size() < vertexMappings.size()){
			V top = findTopExcluding(covered);
			covered.add(top);
			for (V other : vertexMappings.keySet()){

				if (covered.contains(other))
					continue;

				int yDist = calcDistances(top, other)[1];
				int minOverallDistanceY = (int) (top.getSize().getHeight()/2 + other.getSize().getHeight()/2 + minYDistance); 
				if (yDist < minOverallDistanceY){
					int toBeMoved = minOverallDistanceY - yDist;
					Point2D position = vertexMappings.get(other);
					position.setLocation(position.getX(), position.getY() + toBeMoved);
				}

			}
		}

		//separate on x axis
		covered.clear();
		while (covered.size() < vertexMappings.size()){
			V leftmost = findLeftmostExcluding(covered);
			covered.add(leftmost);
			for (V other : vertexMappings.keySet()){

				if (covered.contains(other))
					continue;

				int xDist = calcDistances(leftmost, other)[0];
				int minOverallDistanceX = (int) (leftmost.getSize().getWidth()/2 + other.getSize().getWidth()/2 + minXDistance); 
				if (xDist < minOverallDistanceX){
					int toBeMoved = minOverallDistanceX - xDist;
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
		int v1X = (int) vertexMappings.get(v1).getX();
		int v2X = (int) vertexMappings.get(v2).getX();
		int v1Y = (int) vertexMappings.get(v1).getY();
		int v2Y = (int) vertexMappings.get(v2).getY();

		int[] ret = new int[2];
		ret[0] = Math.abs(v1X - v2X);
		ret[1] = Math.abs(v1Y - v2Y);
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

		System.out.println("width: " + width);
		System.out.println("height: " + height);

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




}
