package gui.actions.main.frame;

import gui.main.frame.MainFrame;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

public class NewGraphAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	public NewGraphAction(){
		putValue(NAME, "New");
		putValue(SHORT_DESCRIPTION, "Create new graph");
		putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/gui/resources/new.png")));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		MainFrame.getInstance().addNewDiagram();
		
	}
	
}
