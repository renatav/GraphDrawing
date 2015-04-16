package gui.view.painters;

import gui.model.GraphVertex;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;

public class VertexPainter {

	private GraphVertex vertex;
	
	public VertexPainter(GraphVertex vertex){
		this.vertex = vertex;
	}
	
	public void paint(Graphics g){
		
	
		System.out.println(vertex.getPosition().getX());
		
		//vertex.setContent("CVOR A");
		calculateVertexRadius(g.getFontMetrics());
		int x = (int) vertex.getPosition().getX();
		int y = (int) vertex.getPosition().getY();
		int r = vertex.getSize().width;
		x -= r/2;
		y -= r/2;
		
		
		g.setColor(Color.GRAY);
		g.fillOval(x, y, r, r);
		g.setColor(Color.BLACK);
		
		
		
		Dimension textDim = PaintingUtil.calculateStringDimension(g.getFontMetrics(), 
				(String)vertex.getContent());
		
		
		int startX = x + r/2  - (int)textDim.getWidth()/2;
		int startY = y + r/2 + (int)textDim.getHeight()/2;
		
		g.drawString((String) vertex.getContent(),startX, startY);
	}
	
	private void calculateVertexRadius(FontMetrics fontMetrics){
		
		Dimension textDim = PaintingUtil.calculateStringDimension(fontMetrics, 
				(String)vertex.getContent());

		System.out.println(textDim);
		int width = (int) textDim.getWidth();
		int height = (int) textDim.getHeight();
		int radius = Math.max(width, height);
		//padding
		radius += 10;
		vertex.setSize(new Dimension(radius, radius));
		
	}
}
