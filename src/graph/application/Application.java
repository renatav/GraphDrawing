package graph.application;

import graph.application.elements.TestEdge;
import graph.application.elements.TestVertex;
import graph.elements.Graph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Application {

	private static Map<String, Graph<TestVertex, TestEdge>> graphs;
	private static String[] commands;


	public static void main(String[] args) {

		initCommands();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		graphs = new HashMap<String, Graph<TestVertex,TestEdge>>();
		Graph<TestVertex, TestEdge> graph = new Graph<TestVertex, TestEdge>();
		graphs.put("test", graph);
		graph.addVertex(new TestVertex("1"));
		graph.addVertex(new TestVertex("2"));
		
		System.out.println("Create graph and test its properties. Type quit to quit.");

		String command = "";
		while (!command.equals("quit")){
			try{
				command = br.readLine().trim().toLowerCase();
				System.out.println(processCommand(command));
			} catch (IOException ioe) {
				System.exit(1);
			}
		}
	}

	private static String processCommand(String command){
		if (command.equals(commands[0]))
			System.exit(0);
		if (command.startsWith(commands[1])){
			command = command.substring(commands[1].length()).trim();
			String[] split = command.split(" ");
			if (split.length == 0)
				return "Plese enter graph's name";
			String name = split[0];
			String directedS = "";
			if (split.length > 1)
				directedS = split[1];
			boolean directed = false;
			if (!directedS.equals("")){
				try{
					directed = Boolean.parseBoolean(directedS);
				}
				catch(Exception ex){

				}
			}

				graphs.put(name, new Graph<TestVertex, TestEdge>(directed));
				
			return directed ? "Directed graph \"" + name + "\" created" : "Undirected graph \"" + name + "\" created";
			
		}
		if (command.startsWith(commands[2])){
			command = command.substring(commands[2].length()).trim();
			String[] split = command.split(" ");
			if (split.length < 2)
				return "Please enter vertex content and graph's name";
			String content = split[0];
			String graph = split[1];
			if (content.equals("") || graph.equals(""))
				return "Please enter vertex content and graph's name";
			if (!graphs.containsKey(graph))
				return "Unknown graph";
			TestVertex vert = new TestVertex(content);
			if (graphs.get(graph).hasVertex(vert))
				return "Graph already contains vertex";
			graphs.get(graph).addVertex(vert);
			return "Vertex added";
		}

		if (command.startsWith(commands[3])){
			command = command.substring(commands[3].length()).trim();
			String[] split = command.split(" ");
			if (split.length < 3)
				return "Please enter vertices and graph's name";
			String vert1C = split[0];
			String vert2C = split[1];
			String name = split[2];
			
			if (!graphs.containsKey(name))
				return "Unknown graph";
			Graph<TestVertex, TestEdge> graph = graphs.get(name);
			TestVertex vert1 = graph.getVertexByContent(vert1C);
			if (vert1 == null)
				return "Unknown vertex \"" + vert1 + "\"";
			TestVertex vert2 = graph.getVertexByContent(vert2C);
			if (vert2 == null)
				return "Unknown vertex \"" + vert2 + "\"";
			TestEdge edge = new TestEdge(vert1, vert2);
			graphs.get(name).addEdge(edge);
			return "Edge added";
		}
		
		if (command.startsWith(commands[4])){
			String name = command.substring(commands[4].length()).trim();
			if (!graphs.containsKey(name))
				return "Unknown graph";
			
			return graphs.get(name).isBiconnected() ? "yes" : "no";
			
		}
		
		if (command.equals(commands[5])){
			StringBuilder builder = new StringBuilder("Commands:\n");
			builder.append("quit\n");
			builder.append("create graph name [true/false] \n");
			builder.append("add vertex content graph\n");
			builder.append("add edge vertex1 vertex2\n");
			builder.append("is connected graph\n");
			return builder.toString();
		}


		return "Unknown command";
	}

	
	private static  void initCommands(){
		commands = new String[6];
		commands[0] = "quit";
		commands[1] = "create graph";
		commands[2] = "add vertex";
		commands[3] = "add edge";
		commands[4] = "is connected";
		commands[5] = "help";
 		
	}




}
