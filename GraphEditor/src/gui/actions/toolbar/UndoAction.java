package gui.actions.toolbar;

import gui.commands.CommandExecutor;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

public class UndoAction extends AbstractAction{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UndoAction(){
		putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/gui/resources/undo.png")));
		putValue(NAME, "Undo");
		putValue(SHORT_DESCRIPTION, "Undo previous action");
		KeyStroke ctrlZ = KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		putValue(ACCELERATOR_KEY,ctrlZ);
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		CommandExecutor.getInstance().undo();
	}
	

}
