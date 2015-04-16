package gui.state;

import gui.view.GraphView;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public abstract class State implements MouseListener {
	

	protected GraphView view;
	
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public GraphView getView() {
		return view;
	}

	public void setView(GraphView view) {
		this.view = view;
	}

}
