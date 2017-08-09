package gui.model;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import graph.elements.Edge;

public class GraphEdge extends GraphElement implements Edge<GraphVertex>{

	private GraphVertex origin, destination;
	private List<LinkNode> linkNodes = new ArrayList<LinkNode>();
	private int weight;
	
	public GraphEdge(GraphVertex origin, GraphVertex destination){
		this.origin = origin;
		this.destination = destination;
		this.color = Color.BLUE;
	}
	
	public GraphEdge(){
		
	}
	
	public void setNodesBasedOnVertices(){
		linkNodes.add(new LinkNode(this, origin.getPosition()));
		linkNodes.add(new LinkNode(this, destination.getPosition()));
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
		return weight;
	}


	public List<LinkNode> getLinkNodes() {
		return linkNodes;
	}

	public void setLinkNodesFromPositions(List<Point2D> positions){
		linkNodes.clear();
		for (Point2D point : positions)
			linkNodes.add(new LinkNode(this, point));
	}
	public void setLinkNodes(List<LinkNode> linkNodes) {
		this.linkNodes = linkNodes;
	}

	@Override
	public String toString() {
		return "[" +origin + ", " + destination + "]";
	}

	@Override
	public void setWeight(int weight) {
		this.weight = weight;
		
	}
	
	
	
	

}
