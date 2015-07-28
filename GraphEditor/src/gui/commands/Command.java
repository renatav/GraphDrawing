package gui.commands;

import gui.view.GraphView;

public abstract class Command {
	
	protected GraphView view;
	
	public Command(GraphView view){
		this.view = view;
	}
	
	public abstract void execute();
	
	public abstract void undo();

}
