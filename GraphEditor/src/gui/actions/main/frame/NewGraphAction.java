package gui.actions.main.frame;

import gui.main.frame.MainFrame;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class NewGraphAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	public NewGraphAction(){
		putValue(NAME, "New");
		putValue(SHORT_DESCRIPTION, "Create new graph");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		MainFrame.getInstance().addNewDiagram();
		
	}
	
}
