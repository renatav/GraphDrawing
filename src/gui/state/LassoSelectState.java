package gui.state;

import gui.main.frame.MainFrame;
import gui.view.GraphView;

import java.awt.event.MouseEvent;

public class LassoSelectState extends State{
	

	public LassoSelectState(GraphView view) {
		this.view = view;
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		view.setLassoEnd(null);
		view.setLassoStart(null);
		view.repaint();
		
		//select elements
		view.selectAllInLassoRectangle();
		
		MainFrame.getInstance().changeToSelect();
		
	}
	
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if (view.getLassoStart() == null)
			view.setLassoStart(e.getPoint());
		view.setLassoEnd(e.getPoint());
		view.repaint();
	}
}
