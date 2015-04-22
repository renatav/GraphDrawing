package gui.properties;

import gui.main.frame.MainFrame;
import gui.model.GraphModel;
import gui.model.IGraphElement;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

public class GraphModelPropertiesPanel extends PropertiesPanel{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private GraphModel model;
	private JTextField tfName;
	
	
	public GraphModelPropertiesPanel(){
		
		setLayout(new MigLayout("fillx"));
		JLabel lblName = new JLabel("Name:");
		tfName = new JTextField(10);
		add(lblName);
		add(tfName, "wrap");

		tfName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				model.setName(tfName.getText());
				MainFrame.getInstance().getCurrentView().repaint();
			}

		});

		JLabel lblColor = new JLabel("Color:");
		JButton btnColor = new JButton("Choose");
		add(lblColor);
		add(btnColor);
		btnColor.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Color c = JColorChooser.showDialog(null, "Choose a Color", model.getColor());
				if (c != null){
					model.setColor(c);
					MainFrame.getInstance().getCurrentView().repaint();
				}

			}
		});

	}

		
	@Override
	public void setValues() {
		tfName.setText(model.getName());
		
	}

	@Override
	public void setElement(IGraphElement element) {
		model = (GraphModel) element;  
		setValues();
	}

}
