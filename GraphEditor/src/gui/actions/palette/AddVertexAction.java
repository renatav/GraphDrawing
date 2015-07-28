package gui.actions.palette;

import gui.main.frame.ElementsEnum;
import gui.main.frame.MainFrame;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class AddVertexAction extends AbstractAction{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AddVertexAction(){
		putValue(NAME, "Vertex");
		putValue(SHORT_DESCRIPTION, "Add vertex");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		MainFrame.getInstance().changeToAdd(ElementsEnum.VERTEX);
		
	}

}
