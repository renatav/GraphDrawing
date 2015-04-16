package gui.actions.main.frame;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class ExitAction extends AbstractAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public ExitAction(){
		putValue(NAME, "Exit");
		putValue(SHORT_DESCRIPTION, "Close application");
		
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		System.exit(0);
		
	}

}
