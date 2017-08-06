package gui.components;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

public class PathPanel extends JPanel{

	private static final long serialVersionUID = 1L;
	private JTextField tfV1 = new JTextField(10);
	private JTextField tfV2 = new JTextField(10);

	public PathPanel(){
		setLayout(new MigLayout());
		setSize(200,200);
		add(new JLabel("Origin"));
		add(tfV1, "wrap");
		add(new JLabel("Destination"));
		add(tfV2, "wrap");
	}

	public void clearFields(){
		tfV1.setText("");
		tfV2.setText("");
	}

	public String getV1(){
		return tfV1.getText().trim();
	}

	public String getV2(){
		return tfV2.getText().trim();
	}
}