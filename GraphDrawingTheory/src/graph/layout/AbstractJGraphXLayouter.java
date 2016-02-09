package graph.layout;

import graph.drawing.Drawing;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.view.mxGraph;

public abstract class AbstractJGraphXLayouter<V extends Vertex, E extends Edge<V>> extends AbstractLayouter<V, E>{


	protected mxGraphLayout layouter;
	protected mxGraph jGraphXGraph;
	protected Map<V, Object> verticesMap = new HashMap<V, Object>();
	protected Map<E, Object> edgesMap = new HashMap<E, Object>();

	protected void createJGraphXGraph(Graph<V,E> graph){
		jGraphXGraph = new mxGraph();
		jGraphXGraph.getModel().beginUpdate();
		Object parent = jGraphXGraph.getDefaultParent();
		mxIGraphModel model = jGraphXGraph.getModel();
		try{
			for (V v : graph.getVertices()){
				Object jgraphxVertex = jGraphXGraph.insertVertex(parent, null, v, 0, 0,
						v.getSize().getWidth(), v.getSize().getHeight());
				model.getGeometry(jgraphxVertex).setHeight(v.getSize().getHeight());
				model.getGeometry(jgraphxVertex).setWidth(v.getSize().getWidth()); //Doesn't make much difference...
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
	protected abstract void initLayouter(GraphLayoutProperties layoutProperties);

}
