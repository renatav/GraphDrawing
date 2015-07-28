package gui.actions.main.frame;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

public class ExitAction extends AbstractAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public ExitAction(){
		putValue(NAME, "Exit");
		putValue(SHORT_DESCRIPTION, "Close application");
		putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/gui/resources/exit.png")));
		
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		System.exit(0);
		
	}

}
