package gui.state;

import gui.commands.Command;
import gui.commands.CommandExecutor;
import gui.commands.MoveNodeCommand;
import gui.main.frame.MainFrame;
import gui.model.GraphVertex;
import gui.model.LinkNode;
import gui.view.GraphView;
import gui.view.painters.VertexPainter;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.SwingUtilities;

public class MoveNodeState extends State{

	private LinkNode node;
	int moveX = 0, moveY = 0;
	int startX, startY;
	int prevLocationX, prevLocationY;
	private Point2D testPoint;
	private int lastX, lastY;

	public MoveNodeState(LinkNode node, GraphView view, Point2D startPosition){

		this.node = node;
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
			
			boolean firstOrLast = (node.getEdge().getLinkNodes().get(0) == node || 
					node.getEdge().getLinkNodes().get(node.getEdge().getLinkNodes().size() - 1) == node	) ? true : false;
			
			int moveX;
			int moveY;
			
			if (firstOrLast){
				moveX = lastX - startX;
				moveY = lastY - startY;
			}
			else{
				moveX =  prevLocationX - startX;
				moveY = prevLocationY - startY;
			}
			
			move(-moveX, -moveY);
			
			Command command = new MoveNodeCommand(view, node, moveX, moveY);
			CommandExecutor.getInstance().execute(command);
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
		if (node.getEdge().getLinkNodes().get(0) == node || 
				node.getEdge().getLinkNodes().get(node.getEdge().getLinkNodes().size() - 1) == node){

			GraphVertex vertex = node.getEdge().getDestination();
			if (node.getEdge().getLinkNodes().get(0) == node)
				vertex = node.getEdge().getOrigin();
			
			VertexPainter painter = view.findVertexPainter(vertex);
			if (testPoint == null)
				testPoint = new Point2D.Double(node.getPosition().getX() + moveX, node.getPosition().getY() + moveY);
			else
				testPoint.setLocation(node.getPosition().getX() + moveX, node.getPosition().getY() + moveY);
			if (painter.containsPoint(testPoint)){
				lastX = (int) node.getPosition().getX() + moveX;
				lastY = (int) node.getPosition().getY() + moveY;
				node.getPosition().setLocation(lastX, lastY);
			}
			
		}
		else{
			Point2D currentPosition = node.getPosition();
			node.getPosition().setLocation(currentPosition.getX() + moveX, currentPosition.getY() + moveY);
		}

	}
}
