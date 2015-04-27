package gui.dialogs;

import graph.elements.Graph;
import gui.model.GraphEdge;
import gui.model.GraphVertex;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import net.miginfocom.swing.MigLayout;

public class KamadaKawaiPanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JTextField tfLengthFactor = new JTextField(5);
	private JTextField tfDisconnectedDistanceMultiplier = new JTextField(5);
	
	public KamadaKawaiPanel(Graph<GraphVertex, GraphEdge> graph){
		setLayout(new MigLayout());
		add(new JLabel("Length factor:"));
		add(tfLengthFactor, "wrap");
		add(new JLabel("Disconnected distance multiplier:"));
		add(tfDisconnectedDistanceMultiplier, "wrap");
		setBorder(BorderFactory.createTitledBorder("Kamada-Kawai properties"));
		
		//set initial values
		
		if (graph.getVertices().size() < 4){
			tfLengthFactor.setText("0.9");
			tfDisconnectedDistanceMultiplier.setText("0.8");
		}
		else if (graph.getVertices().size() < 10){
			tfLengthFactor.setText("1.5");
			tfDisconnectedDistanceMultiplier.setText("3");
		}
		else if (graph.getVertices().size() < 20){
			tfLengthFactor.setText("2");
			tfDisconnectedDistanceMultiplier.setText("5");
		}
		else {
			tfLengthFactor.setText("3");
			tfDisconnectedDistanceMultiplier.setText("10");
	}
	}
	
	public Double getLengthFactor(){
		return Double.parseDouble(tfLengthFactor.getText());
	}
	
	public Double getDisconnectedDistanceMultiplier(){
		return Double.parseDouble(tfDisconnectedDistanceMultiplier.getText());
	}
	
	
}
