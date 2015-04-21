package gui.model;

import graph.elements.Edge;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class GraphEdge extends GraphElement implements Edge<GraphVertex>{

	private GraphVertex origin, destination;
	private List<Point2D> linkNodes = new ArrayList<Point2D>();
	
	public GraphEdge(GraphVertex origin, GraphVertex destination){
		this.origin = origin;
		this.destination = destination;
		this.color = Color.BLUE;
		
	}
	
	public void setNodesBasedOnVertices(){
		linkNodes.add(origin.getPosition());
		linkNodes.add(destination.getPosition());
	}

	@Override
	public GraphVertex getOrigin() {
		return origin;
	}

	@Override
	public GraphVertex getDestination() {
		return destination;
	}

	@Override
	public void setOrigin(GraphVertex origin) {
		this.origin = origin;
		
	}

	@Override
	public void setDestination(GraphVertex destination) {
		this.destination = destination;
		
	}

	@Override
	public int getWeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	public List<Point2D> getLinkNodes() {
		return linkNodes;
	}

	public void setLinkNodes(List<Point2D> linkNodes) {
		this.linkNodes = linkNodes;
	}
	
	

}
