package graph.layout;

import graph.drawing.Drawing;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.exception.CannotBeAppliedException;

import java.util.ArrayList;
import java.util.List;

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
	private LayoutAlgorithms algorithm;
	private GraphLayoutProperties layoutProperties;
	private LayouterFactory<V,E> layouterFactory;


	public Layouter(List<V> vertices, List<E> edges, LayoutAlgorithms algorithm){
		this.edges = edges;
		this.vertices = vertices;
		this.algorithm = algorithm;
		layouterFactory = new LayouterFactory<V,E>();
	}

	public Layouter(List<V> vertices, List<E> edges, LayoutAlgorithms algorithm, GraphLayoutProperties layoutProperties){
		this(vertices, edges, algorithm);
		this.layoutProperties = layoutProperties;
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

	public Drawing<V,E> layout() throws CannotBeAppliedException{

		int startX = 200;
		int startY = 200;

		int spaceX = 200;
		int spaceY = 200;
		int numInRow = 4;
		int currentIndex = 1;

		int currentStartPositionX = startX;
		int currentStartPositionY = startY;

		int maxYInRow = 0;

		Drawing<V,E> ret =  new Drawing<V,E>();

		Drawing<V,E> drawing = null;

		AbstractLayouter<V, E> layouter = layouterFactory.createLayouter(algorithm);

		if (AlgorithmProperties.isOneGraph(algorithm)){
			try{
				drawing = layouter.layout(formOneGraph(vertices, edges),layoutProperties);
			}
			catch(Exception ex){
				throw new CannotBeAppliedException("Algorithm cannot be applied. " + ex.getMessage());
			}
			drawing.positionEdges(edges);
			return drawing;
		}


		for (Graph<V,E> graph : formGraphs(vertices, edges)){
			try{
				drawing = layouter.layout(graph, layoutProperties);
			}
			catch(Exception ex){
				throw new CannotBeAppliedException("Algorithm cannot be applied. " + ex.getMessage());
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

			if (currentIndex % numInRow == 0){
				currentStartPositionY += maxYInRow + spaceY;
				maxYInRow = 0;
				currentStartPositionX = startX;
			}

			ret.getVertexMappings().putAll(drawing.getVertexMappings());

			currentIndex ++;
		}
		drawing.positionEdges(edges);
		return drawing;
	}
}



