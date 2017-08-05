package gui.actions.main.frame;

import gui.main.frame.MainFrame;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

public class ConnectedAction extends AnalysisAction{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConnectedAction(){
		putValue(NAME, "Check connectivity");
		putValue(SHORT_DESCRIPTION, "Check if the graph is connected");
		//putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/gui/resources/exit.png")));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String answer = getGraph().isConnected() ? "Yes" : "No";
		JOptionPane.showMessageDialog(MainFrame.getInstance(), prefix + answer, "Graph is connected", JOptionPane.INFORMATION_MESSAGE);
		
	}

}
