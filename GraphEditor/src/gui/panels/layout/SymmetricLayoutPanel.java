package gui.panels.layout;

import graph.elements.Graph;
import graph.layout.GraphLayoutProperties;
import graph.layout.PropertyEnums.SymmetricCircleProperties;
import graph.symmetry.Permutation;
import graph.symmetry.nauty.McKayGraphLabelingAlgorithm;
import gui.main.frame.MainFrame;
import gui.model.GraphEdge;
import gui.model.GraphVertex;

import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JTextField;

public class SymmetricLayoutPanel extends LayoutPropertyPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JComboBox<Permutation> cbPermutations;	
	
	public SymmetricLayoutPanel(Class<?> enumClass){
		super(enumClass);
		
		JTextField tf = textFieldsMap.remove(SymmetricCircleProperties.PERMUTATION);
		remove(tf);
		
		Graph<GraphVertex,GraphEdge> graph = MainFrame.getInstance().getCurrentView().getModel().getGraph();
		McKayGraphLabelingAlgorithm<GraphVertex, GraphEdge> nauty =
				new McKayGraphLabelingAlgorithm<GraphVertex, GraphEdge>(graph);
		
		List<Permutation> permutations = nauty.findAutomorphisms();
		
		Permutation[] permutationArray = new Permutation[permutations.size() +1];
		permutationArray[0] = new Permutation();
		for (int i = 0; i < permutations.size(); i++)
			permutationArray[i + 1] = permutations.get(i);
		
		cbPermutations = new JComboBox<Permutation>(permutationArray);
		add(cbPermutations);
		
	}
	
	public GraphLayoutProperties getEnteredLayoutProperties(){
		
		GraphLayoutProperties layoutProperties = super.getEnteredLayoutProperties();
		
		layoutProperties.setProperty(SymmetricCircleProperties.PERMUTATION, cbPermutations.getSelectedItem());
		
		return layoutProperties;
	}
	
	
	
	


}
