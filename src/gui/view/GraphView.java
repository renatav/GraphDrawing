package gui.view;

import graph.elements.Graph;
import gui.model.GraphEdge;
import gui.model.GraphVertex;
import gui.state.SelectState;
import gui.state.State;
import gui.view.painters.VertexPainter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JToolBar;

public class GraphView extends JPanel{
	
	private static final long serialVersionUID = 1L;
	
	private List<VertexPainter> vertexPainters = new ArrayList<VertexPainter>();
	private Graph<GraphVertex, GraphEdge> graph;
	private State currentState;
	private GraphController controller;
	
	public GraphView(Graph<GraphVertex, GraphEdge> graph){
		this.graph = graph;
		controller = new GraphController();
		addMouseListener(controller);
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
		for (VertexPainter vertexPainter : vertexPainters)
			vertexPainter.paint(g);
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
	
	
	public class GraphController implements MouseListener{

		public GraphController(){
			currentState = new SelectState();
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
		
	}


	public State getCurrentState() {
		return currentState;
	}

	public void setCurrentState(State currentState) {
		this.currentState = currentState;
	}

}
