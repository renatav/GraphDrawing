package gui.view;

import graph.elements.Graph;
import gui.main.frame.MainFrame;
import gui.model.GraphEdge;
import gui.model.GraphModel;
import gui.model.GraphVertex;
import gui.model.IGraphElement;
import gui.model.SelectionModel;
import gui.state.SelectState;
import gui.state.State;
import gui.view.GraphView.GraphController.CancelAction;
import gui.view.painters.EdgePainter;
import gui.view.painters.VertexPainter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class GraphView extends JPanel implements Observer{

	private static final long serialVersionUID = 1L;

	private List<VertexPainter> vertexPainters = new ArrayList<VertexPainter>();
	private List<EdgePainter> edgePainters = new ArrayList<EdgePainter>();
	private GraphModel model;
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
	private AffineTransform transformation = new AffineTransform();
	private static final double TRANSLATION_STEP = 12;
	private boolean disablCenterZoom;
	private double scale = 1;
	private static final double SCALE_STEP = 0.1;

	public GraphView(Graph<GraphVertex, GraphEdge> graph){
		model = new GraphModel(graph, this);
		setFocusable(true);
		requestFocus();
		controller = new GraphController();
		CancelAction cancelAction = controller.new CancelAction();
		addMouseListener(controller);
		addMouseMotionListener(controller);
		addMouseWheelListener(controller);
		currentState = new SelectState(this);
		selectionModel = new SelectionModel(this);
		getActionMap().put("cancelAction", cancelAction);
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancelAction");
		
	}

	public GraphView(GraphModel model){
		this.model = model;
		setFocusable(true);
		controller = new GraphController();
		CancelAction cancelAction = controller.new CancelAction();
		addMouseListener(controller);
		addMouseMotionListener(controller);
		addMouseWheelListener(controller);
		currentState = new SelectState(this);
		selectionModel = new SelectionModel(this);
		getActionMap().put("cancelAction", cancelAction);
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancelAction");
		

		//initialize painters
		for (GraphVertex vertex : model.getGraph().getVertices())
			vertexPainters.add(new VertexPainter(vertex));
		for (GraphEdge edge : model.getGraph().getEdges())
			edgePainters.add(new EdgePainter(edge));
		
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponents(g);
		paintView((Graphics2D) g);
	}

	public void paintView(Graphics2D g){

		g.setColor(model.getColor());
		g.fillRect(0, 0, getWidth(), getHeight());
		g.transform(transformation);


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

	public IGraphElement elementAtPoint(Point2D point){

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

	public GraphModel getModel(){
		return model;
	}

	public void addVertexPainter(VertexPainter vertexPainter){
		vertexPainters.add(vertexPainter);
	}

	public void addEdgePainter(EdgePainter edgePainter){
		edgePainters.add(edgePainter);
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

	private void transformFromUserSpace(Point2D userSpace) {
		transformation.transform(userSpace, userSpace);
	}


	private void transformToUserSpace(Point2D deviceSpace) {
		try {
			transformation.inverseTransform(deviceSpace, deviceSpace);
		} catch (NoninvertibleTransformException e) {
			e.printStackTrace();
		}
	}

	private MouseEvent makeUserSpaceMouseEvent(MouseEvent e) {
		Point2D point = e.getPoint();
		transformToUserSpace(point);
		return new MouseEvent(
				e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(), 
				(int)point.getX(), (int)point.getY(), 
				e.getXOnScreen(), e.getYOnScreen(), 
				e.getClickCount(), e.isPopupTrigger(), e.getButton()
				);
	}


	//***************************
	//		Scrolling
	//***************************	
	public void scrollToPoint(Point target){
		Point current = new Point(0,0);
		transformToUserSpace(current);
		double scrollX = current.getX() - target.getX();
		double scrollY = current.getY() - target.getY();
		transformation.translate(scrollX, scrollY);
		repaint();
	}

	public void scrollBy(int deltaX, int deltaY){
		transformation.translate(deltaX, deltaY);
		repaint();
	}


	public void scrollRight(){
		transformation.translate(-TRANSLATION_STEP / transformation.getScaleX(), 0);
		repaint();
	}


	public void scrollLeft(){
		transformation.translate(TRANSLATION_STEP / transformation.getScaleX(), 0);
		repaint();
	}

	public void scrollUp(){
		transformation.translate(0, TRANSLATION_STEP / transformation.getScaleY());
		repaint();
	}

	public void scrollDown(){
		transformation.translate(0, -TRANSLATION_STEP / transformation.getScaleY());
		repaint();
	}

	//*****************************************
	//				ZOOM
	//*****************************************
	public void zoom(double scale) {
		if (!disablCenterZoom)
			zoomAtPoint(scale, getCenterPoint());
		else
			disablCenterZoom = false;
	}

	public void zoomAtPoint(double scale, Point2D position) {


		scale = limitScaleFactor(scale);


		Point2D oldPosition = new Point2D.Double(position.getX(), position.getY());

		transformToUserSpace(oldPosition);

		transformation.setToScale(scale, scale);

		Point2D newPosition = new Point2D.Double(position.getX(), position.getY());
		transformToUserSpace(newPosition);


		double tx = newPosition.getX() - oldPosition.getX();
		double ty = newPosition.getY() - oldPosition.getY();

		transformation.translate(tx, ty);

		disablCenterZoom = true;
		repaint();

	}

	public void zoomToPoint(Point2D position){
		transformFromUserSpace(position);
		zoomToPoint(1, position, false);
	}


	protected Point2D getCenterPoint() {
		return new Point2D.Double(getWidth() / 2, getHeight() / 2);
	}


	protected void zoomToPoint(double scale, Point2D position, boolean limitScaleFactor) {
		if (limitScaleFactor)
			scale = limitScaleFactor(scale);

		transformToUserSpace(position);

		transformation.setToScale(scale, scale);

		Point2D center = getCenterPoint();
		transformToUserSpace(center);

		transformation.translate(center.getX() - position.getX(),
				center.getY() - position.getY());

		repaint();
	}

	private double limitScaleFactor(double scale) {
		final double scaleMax = 5;
		final double scaleMin = 0.1; 

		if (scale > scaleMax) {
			return scaleMax;
		}
		return (scale < scaleMin) ? scaleMin : scale;
	}

	public class GraphController implements MouseListener, MouseMotionListener, MouseWheelListener{

		public GraphController(){

		}

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mousePressed(MouseEvent e) {
			currentState.mousePressed(makeUserSpaceMouseEvent(e));

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			currentState.mouseReleased(makeUserSpaceMouseEvent(e));

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
			currentState.mouseDragged(makeUserSpaceMouseEvent(e));

		}

		@Override
		public void mouseMoved(MouseEvent e) {
			currentState.mouseMoved(makeUserSpaceMouseEvent(e));
			Point2D point = e.getPoint();
			transformToUserSpace(point);
			MainFrame.getInstance().updateStatusBarPosition(point);



		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			int notches = e.getWheelRotation();
			if (e.isControlDown()){
				if (notches < 0)
					scale += SCALE_STEP;
				else
					scale -= SCALE_STEP;
				zoomAtPoint(scale, e.getPoint());
			}
			else{
				if (notches < 0){ //mouse moved up
					if (e.isShiftDown())
						scrollRight();
					else
						scrollUp();
				}
				else if (notches > 0){
					if (e.isShiftDown())
						scrollLeft();
					else
						scrollDown();
				}
				Point2D point = e.getPoint();
				transformToUserSpace(point);
				MainFrame.getInstance().updateStatusBarPosition(point);
			}

		}

		
		public class CancelAction extends AbstractAction{

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				currentState.cancel();
				
			}
			
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
