package gui.view.painters;

import graph.elements.Graph;
import gui.model.GraphEdge;
import gui.model.GraphVertex;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class EdgePainter implements IElementPainter{

	private GraphEdge edge;
	private Graph<GraphVertex, GraphEdge> graph;
	private GeneralPath path = new GeneralPath();
	private double selectionMaxDistance=8;
	private BasicStroke stroke = new BasicStroke(2);

	public EdgePainter(GraphEdge edge, Graph<GraphVertex, GraphEdge> graph){
		this.edge = edge;
		this.graph = graph;
	}
	
	public void paint(Graphics2D g){

		GraphVertex destination = edge.getDestination();
		g.setStroke(stroke);

		path.reset();
		path.moveTo(edge.getLinkNodes().get(0).getX(), edge.getLinkNodes().get(0).getY());
		for (int i = 1; i < edge.getLinkNodes().size() - 1; i++){
			path.lineTo(edge.getLinkNodes().get(i).getX(), edge.getLinkNodes().get(i).getY());
		}


		//last segment
		Point2D p1 = edge.getLinkNodes().get(edge.getLinkNodes().size() - 2);
		Point2D p2 = edge.getLinkNodes().get(edge.getLinkNodes().size() - 1);

		Point2D intersection = PaintingUtil.getCircleLineIntersectionPoint(p1, p2, destination.getPosition(),
				(double) destination.getSize().getWidth()/2).get(0);
		path.lineTo(intersection.getX(), intersection.getY());

		g.setColor(edge.getColor());
		g.draw(path);
		
		if (graph.isDirected())
			drawArrow(g, (int) p1.getX(), (int) p1.getY(), (int) intersection.getX(), (int) intersection.getY());

	}

	private void drawArrow( Graphics2D g, int x, int y, int xx, int yy )
	  {
	    float arrowWidth = 10.0f ;
	    float theta = 0.423f ;
	    int[] xPoints = new int[ 3 ] ;
	    int[] yPoints = new int[ 3 ] ;
	    float[] vecLine = new float[ 2 ] ;
	    float[] vecLeft = new float[ 2 ] ;
	    float fLength;
	    float th;
	    float ta;
	    float baseX, baseY ;

	    xPoints[ 0 ] = xx ;
	    yPoints[ 0 ] = yy ;

	    // build the line vector
	    vecLine[ 0 ] = (float)xPoints[ 0 ] - x ;
	    vecLine[ 1 ] = (float)yPoints[ 0 ] - y ;

	    // build the arrow base vector - normal to the line
	    vecLeft[ 0 ] = -vecLine[ 1 ] ;
	    vecLeft[ 1 ] = vecLine[ 0 ] ;

	    // setup length parameters
	    fLength = (float)Math.sqrt( vecLine[0] * vecLine[0] + vecLine[1] * vecLine[1] ) ;
	    th = arrowWidth / ( 2.0f * fLength ) ;
	    ta = arrowWidth / ( 2.0f * ( (float)Math.tan( theta ) / 2.0f ) * fLength ) ;

	    // find the base of the arrow
	    baseX = ( (float)xPoints[ 0 ] - ta * vecLine[0]);
	    baseY = ( (float)yPoints[ 0 ] - ta * vecLine[1]);

	    // build the points on the sides of the arrow
	    xPoints[ 1 ] = (int)( baseX + th * vecLeft[0] );
	    yPoints[ 1 ] = (int)( baseY + th * vecLeft[1] );
	    xPoints[ 2 ] = (int)( baseX - th * vecLeft[0] );
	    yPoints[ 2 ] = (int)( baseY - th * vecLeft[1] );

	    g.drawLine( x, y, (int)baseX, (int)baseY ) ;
	    g.fillPolygon( xPoints, yPoints, 3 ) ;
	  }
	
	public boolean containsPoint(Point2D point) {
		Point2D currentPoint = edge.getLinkNodes().get(0);
		Point2D previousPoint;
		Line2D.Double currentSegment=new Line2D.Double();
		for (int i = 1; i < edge.getLinkNodes().size(); i++){
			previousPoint=currentPoint;
			currentPoint =  edge.getLinkNodes().get(i);
			currentSegment.setLine(previousPoint,currentPoint);
			if (currentSegment.ptLineDist(point) <= selectionMaxDistance && inRange(previousPoint,currentPoint,point))
				return true;
		}
		return false;
	}
	
	/**
	 * @param lineStart - the first vertex
	 * @param lineEnd - the second vertex
	 * @param p 
	 * @return indications whether point p is within selectionMaxDistance from the line
	 * @author tim1
	 */
	public boolean inRange(Point2D lineStart, Point2D lineEnd, Point2D p){
		double xMin,xMax,yMin,yMax;
		if(lineStart.getX()>=lineEnd.getX()){
			xMax=lineStart.getX()+ selectionMaxDistance;
			xMin=lineEnd.getX()- selectionMaxDistance;
		}
		else{
			xMax=lineEnd.getX() +  selectionMaxDistance;
			xMin=lineStart.getX() -  selectionMaxDistance;
		}
		if(lineStart.getY()>=lineEnd.getY()){
			yMax=lineStart.getY() +  selectionMaxDistance;
			yMin=lineEnd.getY() -  selectionMaxDistance;
		}
		else{
			yMax=lineEnd.getY() +  selectionMaxDistance;
			yMin=lineStart.getY() -  selectionMaxDistance;
		}
		if (p.getX()<=xMax && p.getX()>=xMin && p.getY()<=yMax && p.getY()>=yMin)
			return true;
		return false;
	}
	
	
	public Rectangle getEdgeBounds(){
		return path.getBounds();
	}
	
	public GraphEdge getEdge() {
		return edge;
	}
}
