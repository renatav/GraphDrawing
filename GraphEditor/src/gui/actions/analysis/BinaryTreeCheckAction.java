package gui.actions.analysis;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import graph.properties.Bipartite;
import graph.tree.binary.BinaryTree;
import gui.main.frame.MainFrame;
import gui.model.GraphEdge;
import gui.model.GraphVertex;

public class BinaryTreeCheckAction extends AnalysisAction{

	private static final long serialVersionUID = 1L;

	public BinaryTreeCheckAction(){
		putValue(NAME, "Check if binary tree");
		putValue(SHORT_DESCRIPTION, "Check if the graph is a binary tree");
		//putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/gui/resources/exit.png")));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		BinaryTree<GraphVertex, GraphEdge> binaryTree = new BinaryTree<>(getGraph());
		String answer = binaryTree.isCanBeConstructed() ? "Yes" : "No";
		JOptionPane.showMessageDialog(MainFrame.getInstance(), prefix + answer, "Graph is a binary tree", JOptionPane.INFORMATION_MESSAGE);
	}
}
