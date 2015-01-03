package graph.layout;

import java.awt.geom.Point2D;

import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import graph.drawing.Drawing;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

public class KamadaKawai<V extends Vertex, E extends Edge<V>> {

	private Graph<V,E> graph;

	public KamadaKawai(Graph<V,E> graph){
		this.graph = graph;
	}
	
	public Drawing<V,E> layout(){

		UndirectedSparseGraph<V,E> jungGraph =  new UndirectedSparseGraph<V, E>();

		for (V v : graph.getVertices())
			jungGraph.addVertex(v);


		for (E e : graph.getEdges())
			jungGraph.addEdge(e, e.getOrigin(), e.getDestination());


		KKLayout<V, E> kkLayout = new KKLayout<>(jungGraph);
		kkLayout.setAdjustForGravity(true);

		if (jungGraph.getVertexCount() < 4){
			kkLayout.setLengthFactor(0.9);
			kkLayout.setDisconnectedDistanceMultiplier(0.8);
		}
		else if (jungGraph.getVertexCount() < 10){
			kkLayout.setLengthFactor(1.5);
			kkLayout.setDisconnectedDistanceMultiplier(3);
		}
		else if (jungGraph.getVertexCount() < 20){
			kkLayout.setLengthFactor(2);
			kkLayout.setDisconnectedDistanceMultiplier(5);
		}
		else {
			kkLayout.setLengthFactor(3);
			kkLayout.setDisconnectedDistanceMultiplier(10);
		}


		//triggers layouting
		new DefaultVisualizationModel<V, E>(kkLayout);

		Drawing<V, E> drawing = new Drawing<>();


		for (V v : graph.getVertices()){
			Point2D p = kkLayout.transform(v);
			System.out.println("VErtex: " + p);
			drawing.setVertexPosition(v, p);
		}
		//drawing.separate(100, 100);
		return drawing;
	}
}
