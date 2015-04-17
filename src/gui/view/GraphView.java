package gui.view;

import graph.elements.Graph;
import gui.model.GraphEdge;
import gui.model.GraphVertex;
import gui.state.SelectState;
import gui.state.State;
import gui.view.painters.EdgePainter;
import gui.view.painters.VertexPainter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class GraphView extends JPanel{

	private static final long serialVersionUID = 1L;

	private List<VertexPainter> vertexPainters = new ArrayList<VertexPainter>();
	private List<EdgePainter> edgePainters = new ArrayList<EdgePainter>();
	private Graph<GraphVertex, GraphEdge> graph;
	private State currentState;
	private GraphController controller;
	private List<Point2D> linkPoints;
	private Point2D lastLinkPoint;

	public GraphView(Graph<GraphVertex, GraphEdge> graph){
		this.graph = graph;
		controller = new GraphController();
		addMouseListener(controller);
		addMouseMotionListener(controller);
		currentState = new SelectState(this);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponents(g);
		paintView(g);
	}

	public void paintView(Graphics g){
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.BLUE);
		
		for (EdgePainter edgePainter : edgePainters){
			edgePainter.paint((Graphics2D) g);
		}
		
		for (VertexPainter vertexPainter : vertexPainters)
			vertexPainter.paint(g);

		if (linkPoints != null){
			for (int i = 0; i < linkPoints.size()-1; i++){
				g.drawLine((int) linkPoints.get(i).getX(),(int) linkPoints.get(i).getY(),
						(int) linkPoints.get(i + 1).getX(),(int) linkPoints.get(i+1).getY());
			}
			if (lastLinkPoint != null)
				g.drawLine((int) linkPoints.get(linkPoints.size() - 1).getX(),(int) linkPoints.get(linkPoints.size() - 1).getY(),
						(int) lastLinkPoint.getX(), (int) lastLinkPoint.getY());
		}

	}

	public GraphVertex elementAtPoint(Point2D point){
		for (VertexPainter vp : vertexPainters)
			if (vp.containsPoint(point))
				return vp.getVertex();
		return null;
	}

	public List<VertexPainter> getVertexPainters() {
		return vertexPainters;
	}

	public Graph<GraphVertex, GraphEdge> getGraph() {
		return graph;
	}

	public void addVertexPainter(VertexPainter vertexPainter){
		vertexPainters.add(vertexPainter);
	}


	public class GraphController implements MouseListener, MouseMotionListener{

		public GraphController(){

		}

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mousePressed(MouseEvent e) {
			currentState.mousePressed(e);

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			currentState.mouseReleased(e);

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseDragged(MouseEvent e) {
			currentState.mouseDragged(e);

		}

		@Override
		public void mouseMoved(MouseEvent e) {
			currentState.mouseMoved(e);

		}

	}


	public State getCurrentState() {
		return currentState;
	}

	public void setCurrentState(State currentState) {
		this.currentState = currentState;
	}

	public List<Point2D> getLinkPoints() {
		return linkPoints;
	}

	public void setLinkPoints(List<Point2D> linkPoints) {
		this.linkPoints = linkPoints;
	}

	public void setLastLinkPoint(Point2D lastLinkPoint) {
		this.lastLinkPoint = lastLinkPoint;
	}

	public List<EdgePainter> getEdgePainters() {
		return edgePainters;
	}

}
