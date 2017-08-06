package gui.actions.analysis;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JOptionPane;

import graph.util.Util;
import gui.main.frame.MainFrame;
import gui.model.GraphVertex;

public class CutVerticesAction extends AnalysisAction{

	private static final long serialVersionUID = 1L;
	

	public CutVerticesAction(){
		putValue(NAME, "Cut vertices");
		putValue(SHORT_DESCRIPTION, "List the graph's cut vertices");
		//putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/gui/resources/exit.png")));
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		List<GraphVertex> cutVertices = getGraph().listCutVertices();
		String ret ="";
		if (cutVertices.size() == 0)
			ret = " Graph is biconnected";
		else{
			ret = Util.replaceSquareBrackets(Util.addNewLines(cutVertices.toString(), ",", 30));
		}
		JOptionPane.showMessageDialog(MainFrame.getInstance(), prefix + ret, "Cut vertices", JOptionPane.INFORMATION_MESSAGE);
	}

}
