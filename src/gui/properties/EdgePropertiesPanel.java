package gui.properties;

import gui.main.frame.MainFrame;
import gui.model.GraphEdge;
import gui.model.IGraphElement;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

public class EdgePropertiesPanel extends PropertiesPanel {
	
private static final long serialVersionUID = 1L;
	
	private GraphEdge edge;
	private JTextField tfOriginName, tfDestinationName;

	public EdgePropertiesPanel(){
		
		setLayout(new MigLayout("fillx"));

		JLabel lblOrigin = new JLabel("Origin vertex:");
		JLabel lblDesitnation = new JLabel("Destination vertex:");
		tfOriginName = new JTextField(10);
		tfDestinationName = new JTextField(10);
		tfOriginName.setEditable(false);
		tfDestinationName.setEditable(false);
		add(lblOrigin);
		add(tfOriginName, "wrap");
		add(lblDesitnation);
		add(tfDestinationName, "wrap");
		

		JLabel lblColor = new JLabel("Color:");
		JButton btnColor = new JButton("Choose");
		add(lblColor);
		add(btnColor);
		btnColor.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Color c = JColorChooser.showDialog(null, "Choose a Color", edge.getColor());
				if (c != null){
					edge.setColor(c);
					MainFrame.getInstance().getCurrentView().repaint();
				}

			}
		});

	}
	

	@Override
	public void setElement(IGraphElement element) {
		this.edge = (GraphEdge) element;
		setValues();
	}


	@Override
	public void setValues() {
		tfOriginName.setText((String) edge.getOrigin().getContent());
		tfDestinationName.setText((String) edge.getDestination().getContent());
	}

}
