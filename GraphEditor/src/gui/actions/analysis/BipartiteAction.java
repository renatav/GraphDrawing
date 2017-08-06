package gui.actions.analysis;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import gui.main.frame.MainFrame;

public class BipartiteAction extends AnalysisAction{

	private static final long serialVersionUID = 1L;

	public BipartiteAction(){
		putValue(NAME, "Check if bipartite");
		putValue(SHORT_DESCRIPTION, "Check if the graph is bipartite");
		//putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/gui/resources/exit.png")));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String answer = getGraph().isTree() ? "Yes" : "No";
		JOptionPane.showMessageDialog(MainFrame.getInstance(), prefix + answer, "Graph is a tree", JOptionPane.INFORMATION_MESSAGE);
	}

}
