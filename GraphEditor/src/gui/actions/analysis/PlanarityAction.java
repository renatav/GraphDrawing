package gui.actions.analysis;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import graph.algorithms.planarity.FraysseixMendezPlanarity;
import graph.algorithms.planarity.PlanarityTestingAlgorithm;
import gui.main.frame.MainFrame;
import gui.model.GraphEdge;
import gui.model.GraphVertex;

public class PlanarityAction extends AnalysisAction{

	private static final long serialVersionUID = 1L;
	private PlanarityTestingAlgorithm<GraphVertex, GraphEdge> planarityTest =
			new FraysseixMendezPlanarity<GraphVertex, GraphEdge>();

	public PlanarityAction(){
		putValue(NAME, "Check if planar");
		putValue(SHORT_DESCRIPTION, "Check if the graph is planar");
		//putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/gui/resources/exit.png")));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String answer = planarityTest.isPlannar(getGraph()) ? "Yes" : "No";
		JOptionPane.showMessageDialog(MainFrame.getInstance(), prefix + answer, "Graph is planar", JOptionPane.INFORMATION_MESSAGE);
		
	}

}
