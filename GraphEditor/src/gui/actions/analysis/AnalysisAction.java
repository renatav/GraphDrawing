package gui.actions.analysis;

import javax.swing.AbstractAction;

import graph.elements.Graph;
import gui.main.frame.MainFrame;
import gui.model.GraphEdge;
import gui.model.GraphVertex;

public abstract class AnalysisAction extends AbstractAction{
	
	private static final long serialVersionUID = 1L;

	protected String prefix = "";
	
	protected Graph<GraphVertex, GraphEdge> getGraph(){
		return MainFrame.getInstance().getCurrentView().getModel().getGraph();
	}
	
	protected void showScrollableOptionPane(String title, String text){
		MainFrame.getInstance().showScrollableOptionPane(title, text);
	}

}
