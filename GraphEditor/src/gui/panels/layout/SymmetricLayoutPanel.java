package gui.panels.layout;

import graph.elements.Graph;
import graph.layout.PropertyEnums.SymmetricProperties;
import graph.symmetry.Permutation;
import graph.symmetry.nauty.McKayGraphLabelingAlgorithm;
import gui.main.frame.MainFrame;
import gui.model.GraphEdge;
import gui.model.GraphVertex;

import java.awt.Component;
import java.util.List;

import javax.swing.JComboBox;

public class SymmetricLayoutPanel extends LayoutPropertyPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JComboBox<Permutation> cbPermutations;	
	
	public SymmetricLayoutPanel(Class<?> enumClass){
		super(enumClass);
		
		Graph<GraphVertex,GraphEdge> graph = MainFrame.getInstance().getCurrentView().getModel().getGraph();
		McKayGraphLabelingAlgorithm<GraphVertex, GraphEdge> nauty =
				new McKayGraphLabelingAlgorithm<GraphVertex, GraphEdge>(graph);
		
		List<Permutation> permutations = nauty.findAutomorphisms();
		
		Permutation[] permutationArray = new Permutation[permutations.size() +1];
		permutationArray[0] = new Permutation();
		for (int i = 0; i < permutations.size(); i++)
			permutationArray[i + 1] = permutations.get(i);
		
		cbPermutations = new JComboBox<Permutation>(permutationArray);
		
		Component tf = null;
		if (enumClass == SymmetricProperties.class){
			 tf = componentsMap.remove(SymmetricProperties.PERMUTATION);
			 componentsMap.put(SymmetricProperties.PERMUTATION, cbPermutations);
		}
		if (tf != null)
			remove(tf);
		
	
		add(cbPermutations);
		
		
	}
	
}
