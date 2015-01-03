package graph.layout;

import graph.drawing.Drawing;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Layouter accepts lists of veritces and edges which might in fact form more than one graph
 * It then forms the graphs which can later be layouted using the desired method
 * @author xxx
 *
 * @param <V>
 * @param <E>
 */
public class Layouter<V extends Vertex, E extends Edge<V>> {
	
	
	/*
	 * Layout algorithms
	 */
	public static int BOX_LAYOUT = 1;
	public static int KAMADA_KAWAI = 2;
	
	private List<Graph<V,E>> graphs;
	private List<V> coveredVertices;
	private List<E> coveredEdges;
	private List<E> edges;
	private int algorithm;
	
	
	
	public Layouter(List<V> vertices, List<E> edges, int algorithm){
		graphs = new ArrayList<Graph<V,E>>();
		coveredVertices = new ArrayList<V>();
		coveredEdges = new ArrayList<E>();
		this.edges = edges;
		this.algorithm = algorithm;
		
		for (V v : vertices){
			if (coveredVertices.contains(v))
				continue;
			
			Graph<V,E> graph = new Graph<>();
			formGraph(graph, v);
			graphs.add(graph);
 		}
		
	}
	
	
	@SuppressWarnings("unchecked")
	private void formGraph(Graph<V,E> graph, V v){
		coveredVertices.add(v);
		graph.addVertex(v);
		
		for (E e : findAllEdgesContainigVertex(v)){
			
			//avoid infinite recursion
			if (coveredEdges.contains(e))
				continue;
			
			coveredEdges.add(e);
			
			V origin = e.getOrigin();
			V desitnation = e.getDestination();
			
			if (!graph.hasVertex(origin))
				graph.addVertex(origin);
			if (!graph.hasVertex(desitnation))
				graph.addVertex(desitnation);
			
			graph.addEdge(e);
			
			//call formGraph with the other vertex as argument
			
			if (origin != v)
				formGraph(graph, origin);
			else if (desitnation != v)
				formGraph(graph, desitnation);
		}
	}
			
	
	private List<E> findAllEdgesContainigVertex(V v){
		List<E> ret = new ArrayList<E>();
		for (E e : edges)
			if (e.getOrigin() == v || e.getDestination() == v)
				ret.add(e);
		
		return ret;
	}
	
	public Map<V, Point2D> layout(){
		
		
		int startX = 200;
		int startY = 200;
		
		int spaceX = 200;
		int spaceY = 200;
		int numInRow = 4;
		int currentIndex = 1;
		
		int currentStartPositionX = startX;
		int currentStartPositionY = startY;
		
		int maxYInRow = 0;
		
		Map<V, Point2D> ret = new HashMap<V, Point2D>();
		
		Drawing<V,E> drawing = null;
		for (Graph<V,E> graph : graphs){
			if (graph.getVertices().size() == 1){
				Map<V, Point2D> mapping = new HashMap<V, Point2D>();
				mapping.put(graph.getVertices().get(0), new Point(startX, startY));
				drawing = new Drawing<V,E>(mapping, null);
			}
			else if (algorithm == KAMADA_KAWAI){
				KamadaKawai<V, E> kk = new KamadaKawai<>(graph);
				drawing = kk.layout();
			}
			
				int currentLeftmost = drawing.findLeftmostPosition();
				int currentTop = drawing.findTop();
				
				
				//leftmost should start at point currentStartPositionX
				int moveByX = currentStartPositionX - currentLeftmost;
				
				//top should start at point currentStartPositionY
				int moveByY = currentStartPositionY - currentTop;
				
				drawing.moveBy(moveByX, moveByY);
				
				int[] bounds = drawing.getBounds();
				if (bounds[1] > maxYInRow)
					maxYInRow = bounds[1];
				
				currentStartPositionX += bounds[0] + spaceX;
				
				if (currentIndex % numInRow == 0)
					currentStartPositionY += maxYInRow + spaceY;
				
				ret.putAll(drawing.getVertexMappings());
				
				currentIndex ++;
			}
			
		return ret;
		
	}
	
	

}
