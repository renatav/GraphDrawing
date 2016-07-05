package app;

import interfaces.ILayout;
import interpreter.java.Interpreter;

public class App {

	public static void main(String[] args) {

		
		String subgraphsExample = "layout graph style automatic";
		ILayout result = Interpreter.getInstance().execute(subgraphsExample);
		System.out.println(result);
	}

}
