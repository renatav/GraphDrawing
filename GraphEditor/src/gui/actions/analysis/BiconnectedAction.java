package gui.actions.analysis;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import gui.main.frame.MainFrame;

public class BiconnectedAction extends AnalysisAction{

	private static final long serialVersionUID = 1L;
	

	public BiconnectedAction(){
		putValue(NAME, "Check biconnectivity");
		putValue(SHORT_DESCRIPTION, "Check if the graph is biconnected");
		//putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/gui/resources/exit.png")));
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String answer = getGraph().isBiconnected() ? "Yes" : "No";
		JOptionPane.showMessageDialog(MainFrame.getInstance(), 	prefix + answer, "Graph is biconnected", JOptionPane.INFORMATION_MESSAGE);
	}

}
