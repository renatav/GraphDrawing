package gui.actions.analysis;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import graph.properties.splitting.AlgorithmErrorException;
import graph.properties.splitting.SeparationPairSplitting;
import gui.main.frame.MainFrame;
import gui.model.GraphEdge;
import gui.model.GraphVertex;

public class TriconnectedAction extends AnalysisAction{

	private static final long serialVersionUID = 1L;

	public TriconnectedAction(){
		putValue(NAME, "Check triconnectivity");
		putValue(SHORT_DESCRIPTION, "Check if the graph is triconnected");
		//putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/gui/resources/exit.png")));
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		SeparationPairSplitting<GraphVertex, GraphEdge> separationPairsSplitting =
				new SeparationPairSplitting<GraphVertex, GraphEdge>();

		String answer = "No";
		try {
			answer = separationPairsSplitting.findSeaparationPairs(getGraph()).size() == 0 ? "Yes" : "No";
		} catch (AlgorithmErrorException e) {
			e.printStackTrace();
		}
		JOptionPane.showMessageDialog(MainFrame.getInstance(), prefix + answer, "Graph is triconnected", JOptionPane.INFORMATION_MESSAGE);
	}
}
