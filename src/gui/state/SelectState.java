package gui.state;

import gui.view.GraphView;

import java.awt.event.MouseEvent;

public class SelectState extends State{

	
	public SelectState(GraphView view) {
		this.view = view;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		System.out.println(view.elementAtPoint(e.getPoint()));
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
