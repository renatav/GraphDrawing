package gui.actions.analysis;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import gui.main.frame.MainFrame;

public class CyclicAction extends AnalysisAction{

	private static final long serialVersionUID = 1L;
	

	public CyclicAction(){
		putValue(NAME, "Check if cyclic");
		putValue(SHORT_DESCRIPTION, "Check if the graph is cyclic");
		//putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/gui/resources/exit.png")));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String answer = getGraph().isCyclic() ? "Yes" : "No";
		JOptionPane.showMessageDialog(MainFrame.getInstance(), prefix + answer, "Graph is cyclic", JOptionPane.INFORMATION_MESSAGE);
		
	}

}
