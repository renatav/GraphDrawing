package gui.state;

import gui.main.frame.MainFrame;
import gui.model.GraphEdge;
import gui.model.GraphVertex;
import gui.view.GraphView;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

public class MoveState extends State{

	private List<GraphVertex> vertices;
	int moveX = 0, moveY = 0;
	int startX, startY;
	int prevLocationX, prevLocationY;

	public MoveState(List<GraphVertex> vertices, GraphView view, Point2D startPosition){

		this.vertices = vertices;
		this.view = view;
		startX = (int) startPosition.getX();
		startY = (int) startPosition.getY();
		prevLocationX = startX;
		prevLocationY = startY;

	}

	@Override
	public void mouseDragged(MouseEvent e) {

		Point2D currentPoint = e.getPoint();
		int x = (int) currentPoint.getX();
		int y = (int) currentPoint.getY();
		int moveX = x - prevLocationX;
		int moveY = y - prevLocationY;

		move(moveX, moveY);


		view.repaint();
		prevLocationX = x;
		prevLocationY = y;
	}


	@Override
	public void mouseReleased(MouseEvent e) {

		if (SwingUtilities.isLeftMouseButton(e)){
			MainFrame.getInstance().changeToSelect();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {

		Point2D currentPoint = e.getPoint();
		int x = (int) currentPoint.getX();
		int y = (int) currentPoint.getY();
		int moveX = startX - x;
		int moveY = startY - y;

		move(moveX, moveY);
		
		view.repaint();
		MainFrame.getInstance().changeToSelect();
	}

	@Override
	public void cancel(){

		int moveX = startX - prevLocationX;
		int moveY = startY - prevLocationY;

		move(moveX, moveY);
		
		view.repaint();
		MainFrame.getInstance().changeToSelect();
	}

	private void move(int moveX, int moveY){

		List<GraphEdge> processedEdges = new ArrayList<GraphEdge>();
		for (GraphVertex v : vertices){
			int currentX = (int) v.getPosition().getX();
			int currentY = (int) v.getPosition().getY();
			v.getPosition().setLocation(currentX + moveX, currentY + moveY);
			//move the edges
			for (GraphEdge edge : view.getModel().getGraph().allEdges(v)){
				if (processedEdges.contains(edge))
					continue;
				processedEdges.add(edge);
				if (vertices.contains(edge.getOrigin()) && vertices.contains(edge.getDestination())){
					//move all edges
					for (Point2D p : edge.getLinkNodes()){
						int currentNodeX = (int) p.getX();
						int currentNodeY = (int) p.getY();
						p.setLocation(currentNodeX + moveX, currentNodeY + moveY);
					}
				}
				else{

					Point2D currentNodePoint;
					if (edge.getOrigin() == v)
						currentNodePoint = edge.getLinkNodes().get(0);
					else
						currentNodePoint = edge.getLinkNodes().get(edge.getLinkNodes().size() - 1);
					int currentNodeX = (int) currentNodePoint.getX();
					int currentNodeY = (int) currentNodePoint.getY();
					currentNodePoint.setLocation(currentNodeX + moveX, currentNodeY + moveY);
				}
			}
		}

	}
}
