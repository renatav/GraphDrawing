package gui.commands;

import gui.model.LinkNode;
import gui.view.GraphView;

import java.awt.geom.Point2D;

public class MoveNodeCommand extends Command {

	private int moveX, moveY;
	private LinkNode node;
	
	public MoveNodeCommand(GraphView view, LinkNode node, int moveX, int moveY) {
		super(view);
		this.moveX = moveX;
		this.moveY = moveY;
		this.node = node;
	}

	@Override
	public void execute() {
		Point2D pos = node.getPosition();
		pos.setLocation(pos.getX() + moveX, pos.getY() + moveY);
		view.repaint();
	}

	@Override
	public void undo() {
		Point2D pos = node.getPosition();
		pos.setLocation(pos.getX() - moveX, pos.getY() - moveY);
		view.repaint();
	}

}
