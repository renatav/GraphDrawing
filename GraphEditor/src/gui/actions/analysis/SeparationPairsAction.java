package gui.actions.analysis;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JOptionPane;

import graph.properties.components.SplitPair;
import graph.properties.splitting.AlgorithmErrorException;
import graph.properties.splitting.SeparationPairSplitting;
import graph.util.Util;
import gui.main.frame.MainFrame;
import gui.model.GraphEdge;
import gui.model.GraphVertex;

public class SeparationPairsAction  extends AnalysisAction{

	private static final long serialVersionUID = 1L;
	

	public SeparationPairsAction(){
		putValue(NAME, "Separation pairs");
		putValue(SHORT_DESCRIPTION, "List the graph's separation pairs");
		//putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/gui/resources/exit.png")));
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String ret = "";
		try {
			SeparationPairSplitting<GraphVertex, GraphEdge> separationPairsSplitting =
					new SeparationPairSplitting<GraphVertex, GraphEdge>();
			List<SplitPair<GraphVertex, GraphEdge>> separationPairs = separationPairsSplitting.findSeaparationPairs(getGraph());
			if (separationPairs.size() == 0){
				JOptionPane.showMessageDialog(MainFrame.getInstance(), "Graph is triconnected", "Separation pairs", JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			ret = separationPairs.toString();
			ret = Util.removeSquareBrackets(Util.addNewLines(ret, "),", 40));

		} catch (AlgorithmErrorException e) {
			e.printStackTrace();
		}
		showScrollableOptionPane("Separation pairs", ret);
	}
}
