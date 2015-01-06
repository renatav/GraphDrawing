package graph.layout;

import graph.drawing.Drawing;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.layout.box.BoxLayouter;
import graph.layout.circle.CircleLayouter;
import graph.layout.force.directed.FruchtermanReingoldLayouter;
import graph.layout.force.directed.KamadaKawaiLayouter;
import graph.layout.force.directed.SpringLayouter;

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



	private List<E> edges;
	private List<V> vertices;
	private Algorithms algorithm;


	public Layouter(List<V> vertices, List<E> edges, Algorithms algorithm){
		this.edges = edges;
		this.vertices = vertices;
		this.algorithm = algorithm;
	}

	@SuppressWarnings("unchecked")
	private Graph<V,E> formOneGraph(List<V> vertices, List<E> edges){
		Graph<V,E> graph = new Graph<V,E>();
		
		for (V v : vertices)
			graph.addVertex(v);
		
		for (E e : edges)
			graph.addEdge(e);
		
		return graph;
	}

	private List<Graph<V,E>> formGraphs(List<V> vertices, List<E> edges){
		
		List<Graph<V,E>> graphs = new ArrayList<Graph<V,E>>();
		List<V> coveredVertices = new ArrayList<V>();
		List<E> coveredEdges = new ArrayList<E>();
		
		for (V v : vertices){
			if (coveredVertices.contains(v))
				continue;

			Graph<V,E> graph = new Graph<>();
			formGraph(graph, v, coveredVertices, coveredEdges);
			graphs.add(graph);
		}
		
		return graphs;
	}
	
	@SuppressWarnings("unchecked")
	private void formGraph(Graph<V,E> graph, V v, List<V> coveredVertices, List<E> coveredEdges){
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
				formGraph(graph, origin, coveredVertices, coveredEdges);
			else if (desitnation != v)
				formGraph(graph, desitnation, coveredVertices, coveredEdges);
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
		
		AbstractLayouter<V, E> layouter;
		
		if (algorithm == Algorithms.BOX){
			layouter = new BoxLayouter<>(formOneGraph(vertices, edges));
			drawing = layouter.layout();
			return drawing.getVertexMappings();
		}
		
		else{
		for (Graph<V,E> graph : formGraphs(vertices, edges)){

			
			
			if (algorithm == Algorithms.KAMADA_KAWAI)
				layouter = new KamadaKawaiLayouter<>(graph);
			else if (algorithm == Algorithms.FRUCHTERMAN_REINGOLD)
				layouter= new FruchtermanReingoldLayouter<>(graph);
			else if (algorithm == Algorithms.CIRCLE)
				layouter = new CircleLayouter<>(graph);
			else
				layouter = new SpringLayouter<>(graph);


			drawing = layouter.layout();
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

			if (currentIndex % numInRow == 0){
				currentStartPositionY += maxYInRow + spaceY;
				maxYInRow = 0;
				currentStartPositionX = startX;
			}

			ret.putAll(drawing.getVertexMappings());

			currentIndex ++;
		}

		return ret;
		}

	}



}
