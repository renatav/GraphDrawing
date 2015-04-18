package gui.command.panel;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

public class CommandPanel extends JPanel{
	
	private static final long serialVersionUID = 1L;

	public CommandPanel(){
		setLayout(new MigLayout("fill"));
		
		JTextField inputField = new JTextField();
		add(inputField, "dock south, growx");
		
		JTextArea centralArea = new JTextArea(10, 10);
		centralArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(centralArea);
		add(scrollPane, "grow");
		
	}

}
