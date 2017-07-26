package graph.layout;

import java.util.ArrayList;
import java.util.List;

import graph.drawing.Drawing;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.exception.CannotBeAppliedException;

/**
 * Layouter accepts lists of veritces and edges which might in fact form more than one graph
 * It then forms the graphs which can later be layouted using the desired method
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type 
 */
public class Layouter<V extends Vertex, E extends Edge<V>> {


	/**
	 * Edges of the graph (diagram) that is to be laid out
	 */
	private List<E> edges;
	/**
	* Vertices of the graph (diagram) that is to be laid out
	*/
	private List<V> vertices;
	/**
	 * Names the layout algorithm to be applied
	 */
	private LayoutAlgorithms algorithm;
	/**
	 * Properties of the algorithm that should be set
	 */
	private GraphLayoutProperties layoutProperties;
	/**
	 * Factory used to create an instance of the appropriate algorithm
	 */
	private LayouterFactory<V,E> layouterFactory;

	/**
	 * Constructs the layouter without populating list of edges and vertices 
	 */
	public Layouter(){
		layouterFactory = new LayouterFactory<V,E>();
	}

	/**
	 * Constructs the layouter and sets lists of vertices and edges, as well
	 * as the layout algorithm which should be used
	 * @param vertices A list of vertices
	 * @param edges A list of edges
	 * @param algorithm Layout algorithm
	 */
	public Layouter(List<V> vertices, List<E> edges, LayoutAlgorithms algorithm){
		this.edges = edges;
		this.vertices = vertices;
		this.algorithm = algorithm;
		layouterFactory = new LayouterFactory<V,E>();
	}

	/**
	 *  Constructs the layouter and sets lists of vertices and edges, as well
	 * as the layout algorithm which should be used and its properties 
	 * @param vertices A list of vertices
	 * @param edges A list of edges
	 * @param algorithm Layout algorithm
	 * @param layoutProperties Algorithm's proeprties
	 */
	public Layouter(List<V> vertices, List<E> edges, LayoutAlgorithms algorithm, GraphLayoutProperties layoutProperties){
		this(vertices, edges, algorithm);
		this.layoutProperties = layoutProperties;
	}

	@SuppressWarnings("unchecked")
	/**
	 * Creates just one graph from all vertices and edges,
	 * even if it consists of multiple 1-connected components
	 * @param vertices
	 * @param edges
	 * @return Constructed graph
	 */
	private Graph<V,E> formOneGraph(List<V> vertices, List<E> edges){
		Graph<V,E> graph = new Graph<V,E>();

		for (V v : vertices)
			graph.addVertex(v);

		for (E e : edges)
			graph.addEdge(e);

		return graph;
	}

	/**
	 * Forms one graph for each 1-connected component
	 * @param vertices
	 * @param edges
	 * @return A list of formed graphs
	 */
	private List<Graph<V,E>> formGraphs(List<V> vertices, List<E> edges){

		List<Graph<V,E>> graphs = new ArrayList<Graph<V,E>>();
		List<V> coveredVertices = new ArrayList<V>();
		List<E> coveredEdges = new ArrayList<E>();
		Graph<V,E> notConnected = null;
		List<V> verticesWithEdges = null;

		if (algorithm == LayoutAlgorithms.AUTOMATIC){
			verticesWithEdges = new ArrayList<V>();
			for (E e : edges){ //find vertices that don't belong to any of the graphs
				if (!verticesWithEdges.contains(e.getOrigin()))
					verticesWithEdges.add(e.getOrigin());
				if (!verticesWithEdges.contains(e.getDestination()))
					verticesWithEdges.add(e.getDestination());
			}
			if (verticesWithEdges.size() < vertices.size())
				notConnected = new Graph<V,E>();
		}

		for (V v : vertices){
			if (coveredVertices.contains(v))
				continue;
			if (notConnected != null && !verticesWithEdges.contains(v)){
				notConnected.addVertex(v);
				continue;
			}

			Graph<V,E> graph = new Graph<>();
			formGraph(graph, v, coveredVertices, coveredEdges);
			graphs.add(graph);
		}
		
		if (notConnected != null)
			graphs.add(notConnected);

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

	/**
	 * Lays out the graph and returns an instance of the drawing object
	 * @return Laid out drawing (mapping of vertices and edges to their calculated positions)
	 * @throws CannotBeAppliedException If the specified layout algorithm cannot be applied
	 */
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

		if (layouter.isOneGraph()){
			try{
				drawing = layouter.layout(formOneGraph(vertices, edges),layoutProperties);
			}
			catch(Exception ex){
				ex.printStackTrace();
				throw new CannotBeAppliedException("Algorithm cannot be applied. " + ex.getMessage());
			}

			if (!layouter.isPositionsEdges())
				drawing.positionEdges(edges);
			
			return drawing;
		}

		for (Graph<V,E> graph : formGraphs(vertices, edges)){
			try{
				drawing = layouter.layout(graph, layoutProperties);
			}
			catch(Exception ex){
				ex.printStackTrace();
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
			ret.getEdgeMappings().putAll(drawing.getEdgeMappings());

			currentIndex ++;
		}
		
		if (!layouter.isPositionsEdges())
			ret.positionEdges(edges);
		return ret;
	}

	/**
	 * @return the edges
	 */
	public List<E> getEdges() {
		return edges;
	}

	/**
	 * @param edges the edges to set
	 */
	public void setEdges(List<E> edges) {
		this.edges = edges;
	}

	/**
	 * @return the vertices
	 */
	public List<V> getVertices() {
		return vertices;
	}

	/**
	 * @param vertices the vertices to set
	 */
	public void setVertices(List<V> vertices) {
		this.vertices = vertices;
	}

	/**
	 * @return the algorithm
	 */
	public LayoutAlgorithms getAlgorithm() {
		return algorithm;
	}

	/**
	 * @param algorithm the algorithm to set
	 */
	public void setAlgorithm(LayoutAlgorithms algorithm) {
		this.algorithm = algorithm;
	}

	/**
	 * @return properties of the layout algorithm
	 */
	public GraphLayoutProperties getLayoutProperties() {
		return layoutProperties;
	}

	/**
	 * @param layoutProperties properties of the layout algorithm to set
	 */
	public void setLayoutProperties(GraphLayoutProperties layoutProperties) {
		this.layoutProperties = layoutProperties;
	}
}



