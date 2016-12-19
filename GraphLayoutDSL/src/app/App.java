package app;

import interfaces.ILayout;
import interpreter.java.Interpreter;

public class App {

	public static void main(String[] args) {

		
		String subgraphsExample = "lay out subgraph v1, v2 algorithm Kamada Kawai length factor = 5;subgraph v3, v4 style symmetric; others criteria  minimize bands, planar";
		ILayout result = Interpreter.getInstance().execute(subgraphsExample);
		System.out.println(result);
	}

}
