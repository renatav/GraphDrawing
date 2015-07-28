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
	private JLabel positionLabel;
	
	public StatusBar(){
		setLayout(new MigLayout("insets 0 10 0 10, fillx"));
		add(new JLabel("Currect state: "), "split 2");
		statusLabel = new JLabel();
		positionLabel = new JLabel();
		add(statusLabel);
		add(positionLabel, "align right");
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
	}

	public void setLabelText(String text){
		statusLabel.setText(text);
		statusLabel.repaint();
	}
	
	public void setPositionText(String position){
		positionLabel.setText(position);
		positionLabel.repaint();
	}

	
}
