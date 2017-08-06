package gui.actions.analysis;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JOptionPane;

import graph.properties.components.HopcroftTarjanSplitComponent;
import graph.properties.splitting.TriconnectedSplitting;
import gui.main.frame.MainFrame;
import gui.model.GraphEdge;
import gui.model.GraphVertex;

public class TriconnectedComponentsAction extends AnalysisAction{

	private static final long serialVersionUID = 1L;
	

	public TriconnectedComponentsAction(){
		putValue(NAME, "Triconnected components");
		putValue(SHORT_DESCRIPTION, "List the graph's triconnected components");
		//putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/gui/resources/exit.png")));
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		TriconnectedSplitting<GraphVertex, GraphEdge> splitting = new TriconnectedSplitting<GraphVertex, GraphEdge>(getGraph());
		List<HopcroftTarjanSplitComponent<GraphVertex, GraphEdge>>  components = splitting.formTriconnectedComponents();
		String ret = "";
		if (components.size() == 0){
			ret = "Graph is triconnected";
			JOptionPane.showMessageDialog(MainFrame.getInstance(), ret, "Triconnected components", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < components.size(); i++){
			HopcroftTarjanSplitComponent<GraphVertex, GraphEdge> component  = components.get(i);
			builder.append("Component " + (i+1) + " " + component.printFormat() + "\n");
		}
		ret = builder.toString();
		showScrollableOptionPane("Triconnected components", ret);
	}
}
