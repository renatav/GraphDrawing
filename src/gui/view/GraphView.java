package gui.view;

import graph.elements.Graph;
import gui.model.GraphEdge;
import gui.model.GraphElement;
import gui.model.GraphVertex;
import gui.model.SelectionModel;
import gui.state.SelectState;
import gui.state.State;
import gui.view.painters.EdgePainter;
import gui.view.painters.VertexPainter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

public class GraphView extends JPanel implements Observer{

	private static final long serialVersionUID = 1L;

	private List<VertexPainter> vertexPainters = new ArrayList<VertexPainter>();
	private List<EdgePainter> edgePainters = new ArrayList<EdgePainter>();
	private Graph<GraphVertex, GraphEdge> graph;
	private State currentState;
	private GraphController controller;
	private List<Point2D> linkPoints;
	private Point2D lastLinkPoint;
	private SelectionModel selectionModel;
	private final static float dash1[] = {5.0f};
	private static final  BasicStroke dashed =  new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 5.0f,  dash1, 0.0f);
	private Point2D lassoStart, lassoEnd;	
	private Rectangle lassoRectangle;
	
	public GraphView(Graph<GraphVertex, GraphEdge> graph){
		this.graph = graph;
		controller = new GraphController();
		addMouseListener(controller);
		addMouseMotionListener(controller);
		currentState = new SelectState(this);
		selectionModel = new SelectionModel(this);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponents(g);
		paintView((Graphics2D) g);
	}

	public void paintView(Graphics2D g){
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		for (EdgePainter edgePainter : edgePainters){
			edgePainter.paint(g);
		}
		
		for (VertexPainter vertexPainter : vertexPainters){
			vertexPainter.paint(g);
			if (selectionModel.isSelected(vertexPainter.getVertex())){
				g.setStroke(dashed);
				g.draw(vertexPainter.getBounds());
			}
		}
		
		for (EdgePainter edgePainter : edgePainters){
			if (selectionModel.isSelected(edgePainter.getEdge())){
				for (Point2D linkNode : edgePainter.getEdge().getLinkNodes()){
					int dim = 6;
					g.fillRect((int)linkNode.getX() - dim/2, (int)linkNode.getY() - dim/2, dim, dim);
				}
			}
		}

		if (linkPoints != null){
			g.setStroke(dashed);
			g.setColor(Color.BLACK);
			for (int i = 0; i < linkPoints.size()-1; i++){
				g.drawLine((int) linkPoints.get(i).getX(),(int) linkPoints.get(i).getY(),
						(int) linkPoints.get(i + 1).getX(),(int) linkPoints.get(i+1).getY());
			}
			if (lastLinkPoint != null)
				g.drawLine((int) linkPoints.get(linkPoints.size() - 1).getX(),(int) linkPoints.get(linkPoints.size() - 1).getY(),
						(int) lastLinkPoint.getX(), (int) lastLinkPoint.getY());
		}
		
		if (lassoStart != null && lassoEnd != null){
			//draw lasso
			g.setColor(Color.BLACK);
			setLassoRectangle();
			g.draw(lassoRectangle);
		}

	}

	public GraphElement elementAtPoint(Point2D point){
		
		//if both element and link are hit, return element
		
		for (VertexPainter vp : vertexPainters)
			if (vp.containsPoint(point))
				return vp.getVertex();

		for (EdgePainter ep : edgePainters)
			if (ep.containsPoint(point))
				return ep.getEdge();
		
		return null;
	}
	
	public GraphVertex vertexAtPoint(Point2D point){
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
	
	private void setLassoRectangle(){

		int startX = (int) lassoStart.getX();
		int endX = (int) lassoEnd.getX();
		int startY = (int) lassoStart.getY();
		int endY = (int) lassoEnd.getY();
		
		int topLeftX = Math.min(startX, endX);
		int topLeftY = Math.min(startY, endY);
		int width = Math.abs(startX - endX);
		int height = Math.abs(startY - endY);
		
		if (lassoRectangle == null)
			lassoRectangle = new Rectangle(topLeftX, topLeftY, width, height);
		else
			lassoRectangle.setBounds(topLeftX, topLeftY, width, height);
	}
	
	public void selectAllInLassoRectangle(){
		if (lassoRectangle == null)
			return;
		List<GraphVertex> selectedVertices = new ArrayList<GraphVertex>();
		List<GraphEdge> selectedEdges = new ArrayList<GraphEdge>();
		for (VertexPainter vertexPainter : vertexPainters){
			if (lassoRectangle.contains(vertexPainter.getBounds()))
				selectedVertices.add(vertexPainter.getVertex());
		}
		for (EdgePainter edgePainter : edgePainters){
			if (lassoRectangle.contains(edgePainter.getEdgeBounds()))
				selectedEdges.add(edgePainter.getEdge());
		}
		selectionModel.selectVertices(selectedVertices);
		selectionModel.selectEdges(selectedEdges);
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

	@Override
	public void update(Observable o, Object arg) {
		repaint();
	}

	public SelectionModel getSelectionModel() {
		return selectionModel;
	}

	public Point2D getLassoStart() {
		return lassoStart;
	}

	public void setLassoStart(Point2D lassoStart) {
		this.lassoStart = lassoStart;
	}

	public Point2D getLassoEnd() {
		return lassoEnd;
	}

	public void setLassoEnd(Point2D lassoEnd) {
		this.lassoEnd = lassoEnd;
	}

}
