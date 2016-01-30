package graph.layout.organic;

import graph.drawing.Drawing;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.layout.AbstractJGraphXLayouter;
import graph.layout.GraphLayoutProperties;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.util.mxPoint;

public class JGraphHierarchicalLayouter<V extends Vertex, E extends Edge<V>> extends AbstractJGraphXLayouter<V, E> {

	
	
	public JGraphHierarchicalLayouter(){
		positionsEdges = true;
	}
	
	@Override
	protected void initLayouter(GraphLayoutProperties layoutProperties) {
		mxHierarchicalLayout hierarchicalLayouter = new mxHierarchicalLayout(jGraphXGraph);

		layouter = hierarchicalLayouter;

	}

	@Override
	protected Drawing<V,E> createDrawing(Graph<V,E> graph){
		Drawing<V,E> drawing = super.createDrawing(graph);

		for (E e : edgesMap.keySet()){
			mxIGraphModel model = jGraphXGraph.getModel();
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
}
