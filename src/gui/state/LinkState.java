package gui.state;

import graph.elements.Graph;
import gui.main.frame.MainFrame;
import gui.model.GraphEdge;
import gui.model.GraphVertex;
import gui.view.GraphView;
import gui.view.painters.EdgePainter;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

public class LinkState extends State{


	private List<Point2D> linkPoints = new ArrayList<Point2D>();
	private GraphVertex startVertex, endVertex;


	public LinkState(GraphView view){
		this.view = view;
	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)){
			Point2D p = e.getPoint();
			GraphVertex vertex = view.vertexAtPoint(p);
			if (startVertex == null && vertex != null)
				startVertex = vertex;
			else if (startVertex != null && vertex != null){
				endVertex = vertex;
				linkPoints.add(p);
				view.setLastLinkPoint(null);
			}
			if (startVertex != null && endVertex == null){
				linkPoints.add(p);
				view.setLinkPoints(linkPoints);
				view.repaint();
			}
			if (endVertex != null){
				link();
			}
		}
		else if (SwingUtilities.isRightMouseButton(e)){
			clearAndRepaint();
			MainFrame.getInstance().changeToSelect();
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (startVertex != null && endVertex == null){
			view.setLastLinkPoint(e.getPoint());
			view.repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (startVertex != null && endVertex == null){
			view.setLastLinkPoint(e.getPoint());
			view.repaint();
		}

	}

	private void link(){
		GraphEdge edge = new GraphEdge(startVertex, endVertex);
		List<Point2D> edgePoints = new ArrayList<Point2D>(linkPoints);
		edge.setLinkNodes(edgePoints);
		Graph<GraphVertex, GraphEdge> graph = view.getGraph();
		graph.addEdge(edge);
		EdgePainter painter = new EdgePainter(edge);
		view.getEdgePainters().add(painter);
		clearAndRepaint();
	}
	
	private void clearAndRepaint(){
		view.getLinkPoints().clear();
		view.setLastLinkPoint(null);
		startVertex = null;
		endVertex = null;
		linkPoints.clear();
		view.repaint();
	}
}
