package gui.actions.analysis;

import java.awt.event.ActionEvent;
import java.util.List;

import graph.symmetry.Permutation;
import graph.symmetry.nauty.McKayGraphLabelingAlgorithm;
import gui.model.GraphEdge;
import gui.model.GraphVertex;

public class AutomorphismsAction extends AnalysisAction{

	private static final long serialVersionUID = 1L;
	
	McKayGraphLabelingAlgorithm<GraphVertex, GraphEdge> nauty = new McKayGraphLabelingAlgorithm<GraphVertex,GraphEdge>();

	public AutomorphismsAction(){
		putValue(NAME, "Automorphisms");
		putValue(SHORT_DESCRIPTION, "List the graph's automorphisms");
		//putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/gui/resources/exit.png")));
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String ret = "";
		
		List<Permutation> automorphisms = nauty.findAutomorphisms(getGraph());
		for (Permutation p : automorphisms){
			ret += p.cyclicRepresenatation() + "\n";
		}
		showScrollableOptionPane("Automorphisms", ret);	
	}
}
