package gui.util;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import net.miginfocom.swing.MigLayout;

public class StatusBar extends JPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel statusLabel;
	
	public StatusBar(){
		setLayout(new MigLayout("insets 0 10 0 0"));
		
		add(new JLabel("Currect state: "));
		statusLabel = new JLabel();
		add(statusLabel);
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
	}

	public void setLabelText(String text){
		statusLabel.setText(text);
		statusLabel.repaint();
	}

	
}
