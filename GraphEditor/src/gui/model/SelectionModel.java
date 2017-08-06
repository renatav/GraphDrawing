package gui.model;

import gui.view.GraphView;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class SelectionModel extends Observable{
	
	private List<GraphVertex> selectedVertices = new ArrayList<GraphVertex>();
	private List<GraphEdge> selectedEdges = new ArrayList<GraphEdge>();
	private LinkNode selectedNode;
	
	public SelectionModel(GraphView view){
		addObserver(view);
	}
	
	public void selecteVertex(GraphVertex vertex){
		selectedVertices.clear();
		selectedVertices.add(vertex);
		selectedEdges.clear();
		observerChanged();
	}
	
	public void selecteEdge(GraphEdge edge){
		selectedEdges.clear();
		selectedEdges.add(edge);
		selectedVertices.clear();
		observerChanged();
	}
	
	public void selectVertices(List<GraphVertex> vertices){
		selectedVertices.clear();
		selectedVertices.addAll(vertices);
		observerChanged();
	}
	
	public void selectEdges(List<GraphEdge> edges){
		selectedEdges.clear();
		selectedEdges.addAll(edges);
		observerChanged();
	}
	
	public void clearSelection(){
		selectedEdges.clear();
		selectedVertices.clear();
		observerChanged();
	}
	
	public void addVertexToSelection(GraphVertex vertex){
		selectedVertices.add(vertex);
		observerChanged();
	}
	
	public void addEdgeToSelection(GraphEdge edge){
		selectedEdges.add(edge);
		observerChanged();
	}
	
	public void removeVertexFromSelection(GraphVertex vertex){
		if (selectedVertices.contains(vertex))
			selectedVertices.remove(vertex);
		observerChanged();
	}
	
	public void removeEdgeFromSelection(GraphEdge edge){
		if (selectedEdges.contains(edge))
			selectedEdges.remove(edge);
		observerChanged();
	}
	
	public void addVerticesToSelection(List<GraphVertex> vertices){
		selectedVertices.addAll(vertices);
		observerChanged();
	}
	
	public void addEdgesToSelection(List<GraphEdge> edges){
		selectedEdges.addAll(edges);
		observerChanged();
	}
	
	public boolean isSelected(GraphVertex vertex){
		return selectedVertices.contains(vertex);
	}
	
	public boolean isSelected(GraphEdge edge){
		return selectedEdges.contains(edge);
	}
	
	public List<GraphVertex> getSelectedVertices() {
		return selectedVertices;
	}
	
	private void observerChanged(){
		setChanged();
		notifyObservers();
	}

	public void setSelectedVertices(List<GraphVertex> selectedVertices) {
		this.selectedVertices = selectedVertices;
	}

	public List<GraphEdge> getSelectedEdges() {
		return selectedEdges;
	}

	public void setSelectedEdges(List<GraphEdge> selectedEdges) {
		this.selectedEdges = selectedEdges;
	}

	public LinkNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(LinkNode selectedNode) {
		this.selectedNode = selectedNode;
	}

}
