package gui.model;

import graph.elements.Graph;
import gui.view.GraphView;

import java.util.Observable;

public class GraphModel extends Observable{
	
	private Graph<GraphVertex, GraphEdge> graph;
	
	
	public GraphModel(Graph<GraphVertex, GraphEdge> graph, GraphView view){
		this.graph = graph;
		addObserver(view);
	}
	
	public void addVertex(GraphVertex v){
		graph.addVertex(v);
		observerChanged();
	}
	
	public void removeVertex(GraphVertex v){
		graph.removeVertex(v);
		observerChanged();
	}
	
	public void addEdge(GraphEdge e){
		graph.addEdge(e);
		observerChanged();
	}
	
	public void removeEdge(GraphEdge e){
		graph.removeEdge(e);
		observerChanged();
	}
	
	public int getVerticeCount(){
		return graph.getVertices().size();
	}
	
	public int getEdgesCount(){
		return graph.getEdges().size();
	}
	
	public GraphVertex getVertexByContent(String content){
		return graph.getVertexByContent(content);
	}

	
	private void observerChanged(){
		setChanged();
		notifyObservers();
	}

	public Graph<GraphVertex, GraphEdge> getGraph() {
		return graph;
	}

	
	

}
