package gui.actions.analysis;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JOptionPane;

import graph.algorithm.cycles.SimpleCyclesFinder;
import graph.util.Util;
import gui.main.frame.MainFrame;
import gui.model.GraphEdge;
import gui.model.GraphVertex;

public class CycleBasisAction extends AnalysisAction{

	private static final long serialVersionUID = 1L;
	
	private SimpleCyclesFinder<GraphVertex, GraphEdge> cyclesFinder = new SimpleCyclesFinder<GraphVertex,GraphEdge>();

	public CycleBasisAction(){
		putValue(NAME, "Cycle basis");
		putValue(SHORT_DESCRIPTION, "Find the grap's cycle basis");
		//putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/gui/resources/exit.png")));
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		List<List<GraphVertex>> cycles = cyclesFinder.findCycles(getGraph());
		String cyclesStr = "";
		if (cycles.size() == 0){
			cyclesStr = "Graph is not cyclic";
			JOptionPane.showMessageDialog(MainFrame.getInstance(), cyclesStr, "Cycles basis", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		else{
			for (int i = 0; i < cycles.size(); i++){
				cyclesStr += Util.replaceSquareBrackets(cycles.get(i).toString());
				if (i < cycles.size() - 1)
					cyclesStr += ", ";
			}
			cyclesStr = Util.addNewLines(cyclesStr, "),", 30);
		}
		showScrollableOptionPane("Cycles basis", cyclesStr);
	}

}
