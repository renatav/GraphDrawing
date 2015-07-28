package gui.actions.toolbar;

import gui.commands.CommandExecutor;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

public class RedoAction extends AbstractAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RedoAction() {
		putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/gui/resources/redo.png")));
		putValue(NAME, "Redo");
		putValue(SHORT_DESCRIPTION, "Redo previous action");
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		CommandExecutor.getInstance().redo();
	}
}
	

