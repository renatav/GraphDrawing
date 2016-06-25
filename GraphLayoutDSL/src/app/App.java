package app;

import interfaces.ILayout;
import interpreter.java.Interpreter;

public class App {

	public static void main(String[] args) {

		Interpreter interpreter = Interpreter.getInstance();
		ILayout result;
		//result = interpreter.execute("Layout graph algorithm radial tree  horizontal = 5 vertical = 10");
		result = interpreter.execute("Layout graph algorithm compact tree  horizontal resize parents");
		//result = interpreter.execute("Layout graph algorithm node link tree  orientation = left");
		//result = interpreter.execute("Layout graph style circular");
		//result = interpreter.execute("Layout graph criteria minimize edge crossings, minimize bands");
		System.out.println(result);
		
		String subgraphsExample = "Layout subgraph v1, v2, v3 algorithm radial tree  horizontal = 5 vertical = 10;"
				+ "subgraph v4, v5, v6 style circular;"
				+ "others criteria minimize edge crossings, minimize bands";
		result = interpreter.execute(subgraphsExample);
		System.out.println(result);
	}

}
