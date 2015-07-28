package gui.commands;

import java.util.ArrayList;
import java.util.List;

public class CommandExecutor {
	
	private List<Command> commands = new ArrayList<Command>();
	private static CommandExecutor instance;
	
	
	public void execute(Command command){
		command.execute();
		commands.add(command);
	}
	
	public static CommandExecutor getInstance(){
		if (instance == null)
			instance = new CommandExecutor();
		return instance;
	}
	
	
	
	

}
