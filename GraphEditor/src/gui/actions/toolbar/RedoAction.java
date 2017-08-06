package gui.actions.toolbar;

import gui.commands.CommandExecutor;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

public class RedoAction extends AbstractAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RedoAction() {
		putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/gui/resources/redo.png")));
		putValue(NAME, "Redo");
		putValue(SHORT_DESCRIPTION, "Redo previous action");
		KeyStroke ctrlY = KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		putValue(ACCELERATOR_KEY,ctrlY);
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		CommandExecutor.getInstance().redo();
	}
}
	

