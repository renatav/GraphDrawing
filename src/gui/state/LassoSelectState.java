package gui.state;

import gui.main.frame.MainFrame;
import gui.properties.PropertiesFactory;
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
		
		boolean clearPanel = true;
		if (view.getSelectionModel().getSelectedVertices().size() == 1){
			clearPanel = false;
			MainFrame.getInstance().setPropertiesPanel(PropertiesFactory.getPropertiesPanel(view.getSelectionModel().getSelectedVertices().get(0)));
		}
		else if (view.getSelectionModel().getSelectedEdges().size() == 1){
			clearPanel = false;
			MainFrame.getInstance().setPropertiesPanel(PropertiesFactory.getPropertiesPanel(view.getSelectionModel().getSelectedEdges().get(0)));
		}
		if (clearPanel)
			MainFrame.getInstance().setPropertiesPanel(null);
		
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
