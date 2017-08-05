package gui.actions.main.frame;

import graph.elements.Graph;
import gui.main.frame.MainFrame;
import gui.model.GraphEdge;
import gui.model.GraphVertex;

import javax.swing.AbstractAction;

public abstract class AnalysisAction extends AbstractAction{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected String prefix = "";
	
	public Graph<GraphVertex, GraphEdge> getGraph(){
		return MainFrame.getInstance().getCurrentView().getModel().getGraph();
	}

}
