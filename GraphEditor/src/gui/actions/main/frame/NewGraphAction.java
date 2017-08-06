package gui.actions.main.frame;

import gui.main.frame.MainFrame;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

public class NewGraphAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	public NewGraphAction(){
		putValue(NAME, "New");
		putValue(SHORT_DESCRIPTION, "Create new graph");
		putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/gui/resources/new.png")));
		KeyStroke ctrlN = KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		putValue(ACCELERATOR_KEY,ctrlN);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		MainFrame.getInstance().addNewDiagram();
		
	}
	
}
