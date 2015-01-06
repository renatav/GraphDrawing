package graph.layout.force.directed;

import java.awt.geom.Point2D;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import graph.drawing.Drawing;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.layout.AbstractLayouter;

public abstract class AbstractForceDirectedLayouter<V extends Vertex, E extends Edge<V>> extends AbstractLayouter<V,E>{
	
	
	protected AbstractLayout<V,E> layouter;
	protected UndirectedSparseGraph<V,E> jungGraph;
	
	public AbstractForceDirectedLayouter(Graph<V, E> graph) {
		super(graph);
	}

	@Override
	public Drawing<V,E> layout(){
		createJungGraph();
		initLayouter();
		return createDrawing();
		
	}

	protected Drawing<V,E> createDrawing(){
		
		//triggers layouting
		new DefaultVisualizationModel<V, E>(layouter);

		Drawing<V, E> drawing = new Drawing<>();


		for (V v : graph.getVertices()){
			Point2D p = layouter.transform(v);
			drawing.setVertexPosition(v, p);
		}
		drawing.separate(100, 100);
		return drawing;
	}

	protected void createJungGraph(){

		jungGraph =  new UndirectedSparseGraph<V, E>();

		for (V v : graph.getVertices())
			jungGraph.addVertex(v);

		for (E e : graph.getEdges())
			jungGraph.addEdge(e, e.getOrigin(), e.getDestination());
	}

	protected abstract void initLayouter();





}
