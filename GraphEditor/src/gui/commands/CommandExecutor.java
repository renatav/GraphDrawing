package gui.commands;

import java.util.ArrayList;
import java.util.List;

public class CommandExecutor {
	
	private List<Command> commands = new ArrayList<Command>();
	private List<Command> redoList = new ArrayList<Command>();
	private static CommandExecutor instance;
	
	
	public void execute(Command command){
		command.execute();
		commands.add(0,command);
	}
	
	public static CommandExecutor getInstance(){
		if (instance == null)
			instance = new CommandExecutor();
		return instance;
	}
	
	public void undo(){
		if (commands.size() == 0)
			return;
		Command c = commands.get(0);
		c.undo();
		redoList.add(0,c);
		commands.remove(0);
	}
	
	public void redo(){
		if (redoList.size() == 0)
			return;
		Command c = redoList.get(0);
		c.execute();
		commands.add(0,c);
		redoList.remove(0);
	}

}
