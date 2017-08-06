package gui.commands;

import gui.model.GraphEdge;
import gui.model.GraphElement;
import gui.model.GraphVertex;
import gui.model.LinkNode;
import gui.view.GraphView;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class MoveCommand extends Command {


	private List<GraphElement> elements;
	private int moveX, moveY;

	public MoveCommand(GraphView view, List<GraphElement> elements,
			int moveX, int moveY) {

		super(view);
		this.elements = elements;
		this.moveX = moveX;
		this.moveY = moveY;
	}

	@Override
	public void execute() {
		move(moveX, moveY);
		view.repaint();
	}
	

	@Override
	public void undo() {
		move(-moveX, -moveY);
		view.repaint();
	}
	
	
	private void move(int moveX, int moveY){
		List<GraphEdge> processedEdges = new ArrayList<GraphEdge>();
		for (GraphElement element : elements){
			if (element instanceof GraphVertex){
				GraphVertex v = (GraphVertex) element;

				int currentX = (int) v.getPosition().getX();
				int currentY = (int) v.getPosition().getY();
				v.getPosition().setLocation(currentX + moveX, currentY + moveY);
				//move the edges
				for (GraphEdge edge : view.getModel().getGraph().allEdges(v)){
					if (processedEdges.contains(edge))
						continue;
					processedEdges.add(edge);
					if (elements.contains(edge.getOrigin()) && elements.contains(edge.getDestination())){
						//move all edges
						for (LinkNode node : edge.getLinkNodes()){
							int currentNodeX = (int) node.getPosition().getX();
							int currentNodeY = (int) node.getPosition().getY();
							node.getPosition().setLocation(currentNodeX + moveX, currentNodeY + moveY);
						}
					}
					else{

						Point2D currentNodePoint;
						if (edge.getOrigin() == v)
							currentNodePoint = edge.getLinkNodes().get(0).getPosition();
						else
							currentNodePoint = edge.getLinkNodes().get(edge.getLinkNodes().size() - 1).getPosition();
						int currentNodeX = (int) currentNodePoint.getX();
						int currentNodeY = (int) currentNodePoint.getY();
						currentNodePoint.setLocation(currentNodeX + moveX, currentNodeY + moveY);
					}
				}
			}
		}
	}

}
