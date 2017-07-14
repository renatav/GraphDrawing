package app;

import interfaces.ILayout;
import interpreter.java.Interpreter;

public class App {

	public static void main(String[] args) {

		
		String subgraphsExample = "lay out graph criteria distribute nodes evenly, minimize edge crossings";
		ILayout result = Interpreter.getInstance().execute(subgraphsExample);
		System.out.println(result);
	}

}
