package graph.layout;

import graph.drawing.Drawing;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.view.mxGraph;

/**
 * Contains common code used for calling JGraphX layout algorithms
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type 
 */
public abstract class AbstractJGraphXLayouter<V extends Vertex, E extends Edge<V>> extends AbstractLayouter<V, E>{


	/**
	 * JGraphX layout algorithm
	 */
	protected mxGraphLayout layouter;
	/**
	 * JGraphX graph which has to be instantiated
	 */
	protected mxGraph jGraphXGraph;
	/**
	 * Maps vertices of the supplied graph to vertices of the JGraphX graph
	 */
	protected Map<V, Object> verticesMap = new HashMap<V, Object>();
	/**
	 * Maps edges of the supplied graph to edges of the JGraphX graph
	 */
	protected Map<E, Object> edgesMap = new HashMap<E, Object>();

	/**
	 * Converts the given graph into a JGraphX graph
	 * @param graph
	 */
	protected void createJGraphXGraph(Graph<V,E> graph){
		jGraphXGraph = new mxGraph();
		jGraphXGraph.getModel().beginUpdate();
		Object parent = jGraphXGraph.getDefaultParent();
		mxIGraphModel model = jGraphXGraph.getModel();
		try{
			for (V v : graph.getVertices()){
				Dimension size;
				if (v.getSize() == null)
					size = new Dimension(10, 10);
				else
					size = v.getSize();
				
				Object jgraphxVertex = jGraphXGraph.insertVertex(parent, null, v, 0, 0,
						size.getWidth(), size.getHeight());
				model.getGeometry(jgraphxVertex).setHeight(size.getHeight());
				model.getGeometry(jgraphxVertex).setWidth(size.getWidth()); //Doesn't make much difference...
				verticesMap.put(v, jgraphxVertex);
			}
			for (E e : graph.getEdges()){
				Object v1 = verticesMap.get(e.getOrigin());
				Object v2 = verticesMap.get(e.getDestination());
				Object jGraphXEdge = jGraphXGraph.insertEdge(parent, null, null, v1, v2);
				edgesMap.put(e,  jGraphXEdge);
			}
		}
		finally{
			jGraphXGraph.getModel().endUpdate();
		}
	}

	public Drawing<V,E> layout(Graph<V,E> graph, GraphLayoutProperties layoutProperties){
		createJGraphXGraph(graph);
		initLayouter(layoutProperties);
		return createDrawing(graph);
	}

	/**
	 * Executes the layout algorithm and creates the drawing of the graph
	 * (mappings of its vertices and edges to their positions)
	 * @param graph
	 * @return Drawing of the graph
	 */
	protected Drawing<V,E> createDrawing(Graph<V,E> graph){

		Object parent = jGraphXGraph.getDefaultParent();
		layouter.execute(parent);
		Drawing<V, E> drawing = new Drawing<>();

		mxIGraphModel model = jGraphXGraph.getModel();
		for (V v : verticesMap.keySet()){
			mxGeometry geometry = model.getGeometry(verticesMap.get(v));
			drawing.setVertexPosition(v, geometry.getPoint());
		}

		if (positionsEdges){
			for (E e : edgesMap.keySet()){
				mxGeometry geometry = model.getGeometry(edgesMap.get(e));
				if (geometry != null && geometry.getPoints() != null){
					List<Point2D> points = new ArrayList<Point2D>();
					points.add(drawing.getVertexMappings().get(e.getOrigin()));
					for (int i = 1; i < geometry.getPoints().size() - 1; i++)
						points.add(geometry.getPoints().get(i).getPoint());
					points.add(drawing.getVertexMappings().get(e.getDestination()));
					drawing.getEdgeMappings().put(e,points);
				}
			}
			return drawing;
		}


		return drawing;
	}
	
	/**
	 * Initializes the appropriate algorithm (layouter)
	 * @param layoutProperties
	 */
	protected abstract void initLayouter(GraphLayoutProperties layoutProperties);

}
