package gui.actions.analysis;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import graph.elements.Graph;
import graph.elements.Path;
import graph.traversal.DijkstraAlgorithm;
import graph.util.Util;
import gui.components.PathPanel;
import gui.main.frame.MainFrame;
import gui.model.GraphEdge;
import gui.model.GraphVertex;

public class PathAction extends AnalysisAction{

	private static final long serialVersionUID = 1L;
	
	private PathPanel pathPanel;

	public PathAction(){
		putValue(NAME, "Path");
		putValue(SHORT_DESCRIPTION, "Find a path between two vertices");
		pathPanel = new PathPanel();
		//putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/gui/resources/exit.png")));
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		pathPanel.clearFields();
		JOptionPane.showMessageDialog(MainFrame.getInstance(), pathPanel, "Enter vertices", JOptionPane.PLAIN_MESSAGE);
		String message = "";
		String v1Str = pathPanel.getV1();
		String v2Str = pathPanel.getV2();
		Graph<GraphVertex, GraphEdge> graph = getGraph();

		GraphVertex v1 = graph.getVertexByContent(v1Str);

		if (v1 == null)
			message = "Entered origin does not exist\n";

		GraphVertex v2 = graph.getVertexByContent(v2Str);
		if (v2 == null)
			message += "Entered destination does not exist";

		if (!message.equals("")){
			JOptionPane.showMessageDialog(MainFrame.getInstance(), message, "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		DijkstraAlgorithm<GraphVertex, GraphEdge> dijsktra = new DijkstraAlgorithm<>(graph);
		Path<GraphVertex, GraphEdge> path = dijsktra.getPath(v1, v2);
		String answer;
		if (path == null)
			answer = "Vertices are not connected";
		else
			answer = Util.addNewLines(path.toString(), ",", 30);
		JOptionPane.showMessageDialog(MainFrame.getInstance(), prefix + answer, "Path between " + v1Str + " and " + v2Str, JOptionPane.INFORMATION_MESSAGE);

	}

}
