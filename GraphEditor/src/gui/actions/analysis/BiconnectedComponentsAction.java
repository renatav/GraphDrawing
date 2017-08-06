package gui.actions.analysis;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JOptionPane;

import graph.elements.Graph;
import gui.main.frame.MainFrame;
import gui.model.GraphEdge;
import gui.model.GraphVertex;

public class BiconnectedComponentsAction extends AnalysisAction{

	private static final long serialVersionUID = 1L;
	

	public BiconnectedComponentsAction(){
		putValue(NAME, "Biconnected components");
		putValue(SHORT_DESCRIPTION, "List the graph's biconnected components");
		//putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/gui/resources/exit.png")));
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		String ret;
		if (getGraph().isBiconnected()){
			ret = "  Graph is biconnected";
			JOptionPane.showMessageDialog(MainFrame.getInstance(), ret, "Biconnected components", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		else{
			List<Graph<GraphVertex, GraphEdge>> blocks = getGraph().listBiconnectedComponents();
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < blocks.size(); i++){
				Graph<GraphVertex, GraphEdge> block  = blocks.get(i);
				builder.append("Component " + (i+1) + " " + block.printFormat() + "\n");
			}
			ret = builder.toString();
		}
		showScrollableOptionPane("Biconnected components", ret);
	}

}
