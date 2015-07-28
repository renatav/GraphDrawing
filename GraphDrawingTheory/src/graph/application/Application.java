package graph.application;

import graph.algorithms.planarity.AuslanderParterPlanarity;
import graph.application.elements.TestEdge;
import graph.application.elements.TestVertex;
import graph.elements.Graph;
import graph.exception.CannotBeAppliedException;
import graph.properties.splitting.SplitPair;
import graph.properties.splitting.Splitting;
import graph.tree.spqr.SPQRTree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Application {

	private static Map<String, Graph<TestVertex, TestEdge>> graphs;
	private static String[] commands;
	private static AuslanderParterPlanarity<TestVertex, TestEdge> planarityTest = new AuslanderParterPlanarity<>();
	private static Splitting<TestVertex, TestEdge> splitting = new Splitting<>();


	public static void main(String[] args) {

		initCommands();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		graphs = new HashMap<String, Graph<TestVertex,TestEdge>>();
		Graph<TestVertex, TestEdge> graph = new Graph<TestVertex, TestEdge>();
		graphs.put("test", graph);
		TestVertex vert1 = new TestVertex("1");
		TestVertex vert2 = new TestVertex("2");
		graph.addVertex(vert1, vert2);
		graph.addEdge(new TestEdge(vert1, vert2));

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
			if (split.length < 2)
				return "Please enter vertices and graph's name";
			String name = split[1];

			String sp = split[0].substring(1, split[0].length()-1);
			String[] split2 = sp.split(",");
			if (split2.length < 2)
				return "Please enter an edge consisting of two vertices";
			String v1 = split2[0].trim();
			String v2 = split2[1].trim();

			if (!graphs.containsKey(name))
				return "Unknown graph";
			Graph<TestVertex, TestEdge> graph = graphs.get(name);
			TestVertex vert1 = graph.getVertexByContent(v1);
			if (vert1 == null)
				return "Unknown vertex \"" + v1 + "\"";
			TestVertex vert2 = graph.getVertexByContent(v2);
			if (vert2 == null)
				return "Unknown vertex \"" + v2 + "\"";
			TestEdge edge = new TestEdge(vert1, vert2);
			graphs.get(name).addEdge(edge);
			return "Edge added";
		}

		if (command.startsWith(commands[4])){
			String name = command.substring(commands[4].length()).trim();
			if (!graphs.containsKey(name))
				return "Unknown graph";

			return graphs.get(name).isConnected() ? "yes" : "no";

		}

		if (command.startsWith(commands[5])){
			String name = command.substring(commands[5].length()).trim();
			if (!graphs.containsKey(name))
				return "Unknown graph";

			return graphs.get(name).isBiconnected() ? "yes" : "no";

		}

		if (command.startsWith(commands[6])){
			String name = command.substring(commands[6].length()).trim();
			if (!graphs.containsKey(name))
				return "Unknown graph";

			return graphs.get(name).isCyclic() ? "yes" : "no";

		}

		if (command.startsWith(commands[7])){
			String name = command.substring(commands[7].length()).trim();
			if (!graphs.containsKey(name))
				return "Unknown graph";
			return planarityTest.isPlannar(graphs.get(name)) ? "yes" : "no";
		}

		if (command.startsWith(commands[8])){
			String name = command.substring(commands[8].length()).trim();
			if (!graphs.containsKey(name))
				return "Unknown graph";
			Graph<TestVertex, TestEdge> graph = graphs.get(name);
			if (graph.isBiconnected())
				return "Graph is biconnected.";
			return splitting.findAllCutVertices(graph).toString(); 
		}		

		if (command.startsWith(commands[9])){
			String name = command.substring(commands[9].length()).trim();
			if (!graphs.containsKey(name))
				return "Unknown graph";
			Graph<TestVertex, TestEdge> graph = graphs.get(name);
			if (graph.isBiconnected())
				return "Graph is biconnected.";
			return splitting.findAllBlocks(graph).toString(); 
		}	

		if (command.startsWith(commands[10])){
			String name = command.substring(commands[10].length()).trim();
			if (!graphs.containsKey(name))
				return "Unknown graph";
			Graph<TestVertex, TestEdge> graph = graphs.get(name);
			if (!graph.isBiconnected())
				return "Graph is not biconnected.";
			return splitting.findAllSplitPairs(graph).toString(); 
		}	



		if (command.startsWith(commands[11])){
			command = command.substring(commands[11].length()).trim();
			String[] split = command.split(" ");
			if (split.length < 2)
				return "Please enter graph's name and split pair";


			String name = split[1];
			if (!graphs.containsKey(name))
				return "Unknown graph";
			Graph<TestVertex, TestEdge> graph = graphs.get(name);
			if (!graph.isBiconnected())
				return "Graph is not biconnected.";
			String sp = split[0].substring(1, split[0].length()-1);
			String[] split2 = sp.split(",");
			if (split2.length < 2)
				return "Please enter a split pair consisting of two vertices";
			String v1 = split2[0];
			String v2 = split2[1];

			TestVertex vert1 = graph.getVertexByContent(v1);
			if (vert1 == null)
				return "Unknown vertex \"" + v1 + "\"";
			TestVertex vert2 = graph.getVertexByContent(v2);
			if (vert2 == null)
				return "Unknown vertex \"" + v2 + "\"";

			SplitPair<TestVertex, TestEdge> pair = new SplitPair<TestVertex, TestEdge>(vert1, vert2);

			return splitting.findAllSplitComponents(graph, pair).toString(); 
		}


		if (command.startsWith(commands[12])){
			command = command.substring(commands[12].length()).trim();
			String[] split = command.split(" ");
			if (split.length < 3)
				return "Please enter split pair, edge and graph's name";


			String name = split[2];
			if (!graphs.containsKey(name))
				return "Unknown graph";
			Graph<TestVertex, TestEdge> graph = graphs.get(name);
			if (!graph.isBiconnected())
				return "Graph is not biconnected.";

			String sp = split[0].substring(1, split[0].length()-1);
			String[] split2 = sp.split(",");
			if (split2.length < 2)
				return "Please enter a split pair consisting of two vertices";
			String v1 = split2[0];
			String v2 = split2[1];

			TestVertex vert1 = graph.getVertexByContent(v1);
			if (vert1 == null)
				return "Unknown vertex \"" + v1 + "\"";
			TestVertex vert2 = graph.getVertexByContent(v2);
			if (vert2 == null)
				return "Unknown vertex \"" + v2 + "\"";

			sp = split[1].substring(1, split[1].length()-1);
			split2 = sp.split(",");
			if (split2.length < 2)
				return "Please enter an edge consisting of two vertices";
			v1 = split2[0];
			v2 = split2[1];

			TestVertex vert3 = graph.getVertexByContent(v1);
			if (vert3 == null)
				return "Unknown vertex \"" + v1 + "\"";
			TestVertex vert4 = graph.getVertexByContent(v2);
			if (vert4 == null)
				return "Unknown vertex \"" + v2 + "\"";
			if (!graph.hasEdge(vert3, vert4))
				return "Edge doesn't exist";
			TestEdge edge = graph.edgeesBetween(vert3, vert4).get(0);

			SplitPair<TestVertex, TestEdge> pair = new SplitPair<TestVertex, TestEdge>(vert1, vert2);

			return splitting.splitGraph(pair, edge, graph).toString(); 
		}
		
		if (command.startsWith(commands[13])){
			command = command.substring(commands[13].length()).trim();
			String[] split = command.split(" ");
			if (split.length < 2)
				return "Please enter edge and graph's name";


			String name = split[1];
			if (!graphs.containsKey(name))
				return "Unknown graph";
			Graph<TestVertex, TestEdge> graph = graphs.get(name);
			if (!graph.isBiconnected())
				return "Graph is not biconnected.";


			String sp = split[0].substring(1, split[0].length()-1);
			String[] split2 = sp.split(",");
			if (split2.length < 2)
				return "Please enter an edge consisting of two vertices";
			String v1 = split2[0];
			String v2 = split2[1];

			TestVertex vert3 = graph.getVertexByContent(v1);
			if (vert3 == null)
				return "Unknown vertex \"" + v1 + "\"";
			TestVertex vert4 = graph.getVertexByContent(v2);
			if (vert4 == null)
				return "Unknown vertex \"" + v2 + "\"";
			if (!graph.hasEdge(vert3, vert4))
				return "Edge doesn't exist";
			TestEdge edge = graph.edgeesBetween(vert3, vert4).get(0);

			return splitting.maximalSplitPairs(graph, edge).toString(); 
		}
		
		if (command.startsWith(commands[14])){
			command = command.substring(commands[14].length()).trim();
			String[] split = command.split(" ");
			if (split.length < 2)
				return "Please enter edge and graph's name";


			String name = split[1];
			if (!graphs.containsKey(name))
				return "Unknown graph";
			Graph<TestVertex, TestEdge> graph = graphs.get(name);
			if (!graph.isBiconnected())
				return "Graph is not biconnected.";


			String sp = split[0].substring(1, split[0].length()-1);
			String[] split2 = sp.split(",");
			if (split2.length < 2)
				return "Please enter an edge consisting of two vertices";
			String v1 = split2[0];
			String v2 = split2[1];

			TestVertex vert3 = graph.getVertexByContent(v1);
			if (vert3 == null)
				return "Unknown vertex \"" + v1 + "\"";
			TestVertex vert4 = graph.getVertexByContent(v2);
			if (vert4 == null)
				return "Unknown vertex \"" + v2 + "\"";
			if (!graph.hasEdge(vert3, vert4))
				return "Edge doesn't exist";
			TestEdge edge = graph.edgeesBetween(vert3, vert4).get(0);

			try {
				new SPQRTree<TestVertex, TestEdge>(edge, graph).printTree();
				return "";
			} catch (CannotBeAppliedException e) {
				return "Couldn't construct spqr tree: " + e.getMessage();
			}
		}


		if (command.startsWith(commands[15])){
			String name = command.substring(commands[15].length()).trim();
			if (!graphs.containsKey(name))
				return "Unknown graph";

			return graphs.toString();
		}


		if (command.equals(commands[16])){
			StringBuilder builder = new StringBuilder("Commands:\n");
			builder.append("quit\n");
			builder.append("create graph name [true/false] \n");
			builder.append("add vertex content graph\n");
			builder.append("add edge {vertex1, vertex2}\n");
			builder.append("is connected graph\n");
			builder.append("is biconnected graph\n");
			builder.append("is cyclic graph\n");
			builder.append("is planar graph\n");
			builder.append("list cut vertices graph\n");
			builder.append("list blocks graph\n");
			builder.append("list split pairs graph\n");
			builder.append("list split components {u,v} graph\n");
			builder.append("split graph {u,v} {e1, e2} graph\n");
			builder.append("maximal split pairs {e1, e2} graph\n");
			builder.append("construct spqr tree {e1, e2} graph\n");
			builder.append("print\n");
			return builder.toString();
		}


		return "Unknown command";
	}


	private static  void initCommands(){
		commands = new String[17];
		commands[0] = "quit";
		commands[1] = "create graph";
		commands[2] = "add vertex";
		commands[3] = "add edge";
		commands[4] = "is connected";
		commands[5] = "is biconnected";
		commands[6] = "is cyclic";
		commands[7] = "is planar";
		commands[8] = "list cut vertices";
		commands[9] = "list blocks";
		commands[10] = "list split pairs";
		commands[11] = "list split components";
		commands[12] = "split graph";
		commands[13] = "maximal split pairs";
		commands[14] = "construct spqr";
		commands[15] = "print";
		commands[16] = "help";

	}




}
