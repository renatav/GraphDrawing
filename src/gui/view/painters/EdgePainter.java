package gui.view.painters;

import gui.model.GraphEdge;
import gui.model.GraphVertex;

import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

public class EdgePainter {

	private GraphEdge edge;
	
	public EdgePainter(GraphEdge edge){
		this.edge = edge;
	}
	public void paint(Graphics2D g){

		GraphVertex destination = edge.getDestination();
		
		GeneralPath path = new GeneralPath();
		
		path.moveTo(edge.getLinkNodes().get(0).getX(), edge.getLinkNodes().get(0).getY());
		for (int i = 1; i < edge.getLinkNodes().size(); i++){
			path.lineTo(edge.getLinkNodes().get(i).getX(), edge.getLinkNodes().get(i).getY());
		}
		

		//last segment
		Point2D p1 = edge.getLinkNodes().get(edge.getLinkNodes().size() - 2);
		Point2D p2 = edge.getLinkNodes().get(edge.getLinkNodes().size() - 1);
		
		Point2D intersection = PaintingUtil.getCircleLineIntersectionPoint(p1, p2, destination.getPosition(),
				(double) destination.getSize().getWidth()/2).get(0);
		
		System.out.println(intersection);
		System.out.println(p1);
		System.out.println(p2);
		
		g.draw(path);
		
	}
}
