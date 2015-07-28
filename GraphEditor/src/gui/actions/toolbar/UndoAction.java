package gui.actions.toolbar;

import gui.commands.CommandExecutor;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

public class UndoAction extends AbstractAction{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UndoAction(){
		putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/gui/resources/undo.png")));
		putValue(NAME, "Undo");
		putValue(SHORT_DESCRIPTION, "Undo previous action");
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		CommandExecutor.getInstance().undo();
	}
	

}
