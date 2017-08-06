package gui.actions.palette;

import graph.drawing.Drawing;
import graph.elements.Graph;
import graph.exception.CannotBeAppliedException;
import graph.layout.LayoutAlgorithms;
import graph.layout.GraphLayoutProperties;
import graph.layout.Layouter;
import gui.dialogs.LayoutDialog;
import gui.main.frame.MainFrame;
import gui.model.GraphEdge;
import gui.model.GraphVertex;
import gui.view.GraphView;

import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

public class LayoutAction extends AbstractAction{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LayoutAction(){
		putValue(NAME, "Layout");
		putValue(SHORT_DESCRIPTION, "Lay out graph");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		GraphView view = MainFrame.getInstance().getCurrentView();
		if (view == null)
			return;
		LayoutDialog d = new LayoutDialog();
		d.setVisible(true);
		if (d.isOk()){
			LayoutAlgorithms algorithm = d.getAlogithm();
			GraphLayoutProperties layoutProeprties = d.getLayoutProperties();
			Graph<GraphVertex, GraphEdge> graph = view.getModel().getGraph();
			Layouter<GraphVertex, GraphEdge> layouter = new Layouter<>(graph.getVertices(), graph.getEdges(), 
					algorithm, layoutProeprties);
			try{
				Drawing<GraphVertex, GraphEdge> drawing = layouter.layout();
				for (GraphVertex vert : graph.getVertices()){
					vert.setPosition(drawing.getVertexMappings().get(vert));
				}
				for (GraphEdge edge : graph.getEdges()){
					List<Point2D> points = drawing.getEdgeMappings().get(edge);
					edge.setLinkNodesFromPositions(points);
				}
				view.repaint();
			}
			catch (CannotBeAppliedException ex){
				JOptionPane.showMessageDialog(MainFrame.getInstance(), ex.getMessage());
			}
		}
		
		
		
	}
}

