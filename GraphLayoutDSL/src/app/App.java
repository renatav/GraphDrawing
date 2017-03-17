package app;

import interfaces.ILayout;
import interpreter.java.Interpreter;

public class App {

	public static void main(String[] args) {

		
		String subgraphsExample = "lay out subgraph v 1, v 2 criteria  minimize bands, planar; subgraph v 3, v 4 style symmetric; others algorithm Kamada Kawai";
		ILayout result = Interpreter.getInstance().execute(subgraphsExample);
		System.out.println(result);
	}

}
