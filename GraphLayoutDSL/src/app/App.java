package app;

import interfaces.ILayout;
import interpreter.java.Interpreter;

public class App {

	public static void main(String[] args) {

		
		//String subgraphsExample = "lay out graph criteria distribute nodes evenly, minimize edge crossings";
		String example = "lay out graph not flow and planarity";
		ILayout result = Interpreter.getInstance().execute(example);
		System.out.println(result);
	}

}
