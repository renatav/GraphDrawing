package gui.actions.analysis;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import graph.properties.Bipartite;
import gui.main.frame.MainFrame;
import gui.model.GraphEdge;
import gui.model.GraphVertex;

public class BipartiteAction extends AnalysisAction{

	private static final long serialVersionUID = 1L;

	public BipartiteAction(){
		putValue(NAME, "Check if bipartite");
		putValue(SHORT_DESCRIPTION, "Check if the graph is bipartite");
		//putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/gui/resources/exit.png")));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Bipartite<GraphVertex, GraphEdge> bipartite = new Bipartite<>(getGraph());
		String answer = bipartite.isBipartite() ? "Yes" : "No";
		JOptionPane.showMessageDialog(MainFrame.getInstance(), prefix + answer, "Graph is a bipartite", JOptionPane.INFORMATION_MESSAGE);
	}

}
