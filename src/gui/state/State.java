package gui.state;

import gui.main.frame.MainFrame;
import gui.view.GraphView;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public abstract class State implements MouseListener, MouseMotionListener, KeyListener {
	

	protected GraphView view;
	
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void keyTyped(KeyEvent e){
		// TODO Auto-generated method stub
	}
	
	@Override
	public void keyReleased(KeyEvent e){
		// TODO Auto-generated method stub
	}
	
	@Override
	public void keyPressed(KeyEvent e){
		// TODO Auto-generated method stub
	}
	
	public void cancel(){
		MainFrame.getInstance().changeToSelect();
	}
	

	public GraphView getView() {
		return view;
	}

	public void setView(GraphView view) {
		this.view = view;
	}


}
