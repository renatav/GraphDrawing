package gui.model;

import graph.elements.Graph;
import gui.view.GraphView;

import java.awt.Color;
import java.util.Observable;

public class GraphModel extends Observable implements IGraphElement{
	
	private Graph<GraphVertex, GraphEdge> graph;
	private Color color;
	private String name;
	
	
	public GraphModel(Graph<GraphVertex, GraphEdge> graph, GraphView view){
		this.graph = graph;
		addObserver(view);
		this.name = "Graph";
		//this.color = new Color(228,228,255);
		this.color = Color.white;
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

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
	}

	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	

}
