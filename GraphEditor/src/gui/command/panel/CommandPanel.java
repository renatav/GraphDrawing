package gui.command.panel;

import graph.algorithm.AlgorithmExecutor;
import graph.algorithm.ExecuteResult;
import graph.algorithm.cycles.SimpleCyclesFinder;
import graph.algorithm.cycles.SimpleUndirectedCyclesFinder;
import graph.algorithms.planarity.BoyerMyrvoldPlanarity;
import graph.algorithms.planarity.FraysseixMendezPlanarity;
import graph.algorithms.planarity.PQTreePlanarity;
import graph.algorithms.planarity.PlanarityTestingAlgorithm;
import graph.drawing.Drawing;
import graph.elements.Graph;
import graph.elements.Path;
import graph.exception.DSLException;
import graph.layout.dsl.DSLLayouter;
import graph.properties.Bipartite;
import graph.properties.components.HopcroftTarjanSplitComponent;
import graph.properties.components.SplitPair;
import graph.properties.splitting.SeparationPairSplitting;
import graph.properties.splitting.TriconnectedSplitting;
import graph.symmetry.Permutation;
import graph.symmetry.PermutationAnalyzator;
import graph.symmetry.PermutationGroup;
import graph.symmetry.nauty.McKayGraphLabelingAlgorithm;
import graph.traversal.DijkstraAlgorithm;
import graph.tree.binary.BinaryTree;
import graph.util.Util;
import gui.main.frame.MainFrame;
import gui.model.GraphEdge;
import gui.model.GraphVertex;
import gui.view.GraphView;
import gui.view.painters.EdgePainter;
import gui.view.painters.VertexPainter;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

public class CommandPanel extends JPanel{

	private static final long serialVersionUID = 1L;

	private static String[] commands;
	private JTextField inputField = new JTextField();
	private JTextArea centralArea = new JTextArea(10, 10);
	private List<String> allCommands = new ArrayList<String>();
	private static Map<String, String> help = new HashMap<String, String>();
	private int currentCommandIndex;
	private static PlanarityTestingAlgorithm<GraphVertex, GraphEdge> planarityTestFM = new FraysseixMendezPlanarity<GraphVertex, GraphEdge>();
	private static PlanarityTestingAlgorithm<GraphVertex, GraphEdge> planarityTestBM = new BoyerMyrvoldPlanarity<GraphVertex, GraphEdge>();
	private static PlanarityTestingAlgorithm<GraphVertex, GraphEdge> planarityTestPQ = new PQTreePlanarity<GraphVertex, GraphEdge>();
	private static McKayGraphLabelingAlgorithm<GraphVertex, GraphEdge> nauty = new McKayGraphLabelingAlgorithm<GraphVertex,GraphEdge>();

	//TODO
	//uputstvo za DSL

	public CommandPanel(){
		setLayout(new MigLayout("fill"));

		add(inputField, "dock south, growx");
		inputField.setText("Enter command");

		centralArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(centralArea);
		add(scrollPane, "grow");

		initCommands();
		initHelp();

		inputField.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				if (inputField.getText().equals("Enter command"))
					inputField.setText("");
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (inputField.getText().equals(""))
					inputField.setText("Enter command");

			}
		});

		inputField.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER){
					String command = inputField.getText();
					String reply = processCommand(command);
					inputField.setText("");
					if (!command.equals("clear"))
						centralArea.setText(centralArea.getText() + "\n" + command + "> " + reply);
				}
				else if (e.getKeyCode() == KeyEvent.VK_UP){
					if (currentCommandIndex > 0)
						currentCommandIndex --;
					inputField.setText(allCommands.get(currentCommandIndex));
				}
				else if (e.getKeyCode() == KeyEvent.VK_DOWN){
					if (currentCommandIndex < allCommands.size() - 1)
						currentCommandIndex ++;
					if (currentCommandIndex < allCommands.size())
						inputField.setText(allCommands.get(currentCommandIndex));
				}

			}

			@Override
			public void keyPressed(KeyEvent e) {

			}
		});

	}



	@SuppressWarnings("unchecked")
	private String processCommand(String command){
		command = command.trim();
		allCommands.add(command);
		currentCommandIndex = allCommands.size();

		if (command.trim().equals(commands[0])){ //create graph
			command = command.substring(commands[0].length()).trim();
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

			String ret = directed ? "Directed graph \"" + name + "\" created" : "Undirected graph \"" + name + "\" created";
			Graph<GraphVertex, GraphEdge> newGraph = new Graph<GraphVertex, GraphEdge>(directed);
			GraphView view = new GraphView(newGraph);
			MainFrame.getInstance().addDiagram(view, name);
			return ret;
		}

		if (MainFrame.getInstance().getCurrentView() == null)
			return "Create or open a graph";

		Graph<GraphVertex, GraphEdge> graph = MainFrame.getInstance().getCurrentView().getModel().getGraph();

		if (command.startsWith(commands[1])){ //add vertex
			command = command.substring(commands[1].length()).trim();
			String[] split = command.split(" ");
			if (split.length == 0)
				return "Please enter vertex name and position as (x, y). Type \"help add vertex\" for details";
			if (split[0].contains("(") && !split[1].contains("("))
				return "Please enter vertex name and position as (x, y). Type \"help add vertex\" for details";
			String content = split[0];
			int positionStart = command.indexOf("(");
			if (positionStart == -1)
				return "Please enter vertex name and position as (x, y). Type \"help add vertex\" for details";
			String position = command.substring(positionStart + 1).trim();
			if (!position.endsWith(")"))
				return "Please enter vertex name and position as (x, y). Type \"help add vertex\" for details";
			position = position.substring(0, position.length() - 1);
			String[] nums = position.split(",");
			int x = Integer.parseInt(nums[0].trim());
			int y = Integer.parseInt(nums[1].trim());
			Point2D point = new Point2D.Double(x, y);
			if (content.equals(""))
				return "Please enter vertex name. Type \"help add vertex\" for details";
			GraphVertex vert = new GraphVertex(point, content);
			MainFrame.getInstance().getCurrentView().getModel().addVertex(vert);
			MainFrame.getInstance().getCurrentView().addVertexPainter(new VertexPainter(vert));
			return "Vertex " + content + " added at position " + "(" + x + ", " + y +")";
		}

		if (command.startsWith(commands[2])){
			command = command.substring(commands[2].length()).trim();
			String[] split = command.split(" ");
			if (split.length < 2)
				return "Please enter two vertices. Type \"help add edge\" for details";

			String v1 = split[0].trim();
			String v2 = split[1].trim();

			GraphVertex vert1 = MainFrame.getInstance().getCurrentView().getModel().getVertexByContent(v1);
			if (vert1 == null)
				return "Unknown vertex \"" + v1 + "\"";
			GraphVertex vert2 = MainFrame.getInstance().getCurrentView().getModel().getVertexByContent(v2);
			if (vert2 == null)
				return "Unknown vertex \"" + v2 + "\"";
			GraphEdge edge = new GraphEdge(vert1, vert2);
			edge.setNodesBasedOnVertices();
			MainFrame.getInstance().getCurrentView().getModel().addEdge(edge);
			MainFrame.getInstance().getCurrentView().addEdgePainter(new EdgePainter(edge, MainFrame.getInstance().getCurrentView().getModel().getGraph()));
			return "Edge " + v1 + ", " + v2 + " added";
		}


		if (command.trim().equals(commands[3])){
			ExecuteResult result = AlgorithmExecutor.execute(graph, "isConnected");
			return ((Boolean) result.getValue() ? "yes" : "no" )+ " [in " + result.getDuration() + " ms]"; 
		}

		if (command.trim().equals(commands[4])){
			ExecuteResult result = AlgorithmExecutor.execute(graph, "isBiconnected");
			return ((Boolean) result.getValue() ? "yes" : "no" )+ " [in " + result.getDuration() + " ms]";
		}

		if (command.trim().equals(commands[5])){
			SeparationPairSplitting<GraphVertex, GraphEdge> separationPairsSplitting =
					new SeparationPairSplitting<GraphVertex, GraphEdge>();

			String answer = "no";
			ExecuteResult result = AlgorithmExecutor.execute(separationPairsSplitting, "findSeaparationPairs", graph);
			String time =  " [in " + result.getDuration() + " ms]";
			List<SplitPair<GraphVertex, GraphEdge>> separationPairs = (List<SplitPair<GraphVertex, GraphEdge>>) result.getValue();
			answer = separationPairs.size() == 0 ? "yes" : "no";
			return answer + time;
		}

		if (command.trim().equals(commands[6])){
			ExecuteResult result = AlgorithmExecutor.execute(graph, "isCyclix");
			return ((Boolean) result.getValue() ? "yes" : "no" )+ " [in " + result.getDuration() + " ms]";

		}

		if (command.trim().startsWith(commands[7])){
			command = command.substring(commands[7].length()).trim();
			PlanarityTestingAlgorithm<GraphVertex, GraphEdge> planarityTest;
			if (command.length() == 0)
				 planarityTest = planarityTestFM;
			else{
				if (command.equals("Fraysseix-Mendez"))
					planarityTest = planarityTestFM;
				else if (command.equals("Boyer-Myrvold"))
					planarityTest = planarityTestBM;
				else if (command.equals("PQ"))
					planarityTest = planarityTestPQ;
				else
					return "Unknown algorithm specified. Type \"help is planar\" for more details";
			}
			ExecuteResult result = AlgorithmExecutor.execute(planarityTest, "isPlannar", graph);
			return ((Boolean) result.getValue() ? "yes" : "no" )+ " [in " + result.getDuration() + " ms]";
		}

		if (command.trim().equals(commands[8])){
			ExecuteResult result = AlgorithmExecutor.execute(graph, "listCutVertices");
			String time =  " [in " + result.getDuration() + " ms]";
			List<GraphVertex> cutVertices = (List<GraphVertex>) result.getValue();
			String ret;
			if (cutVertices.size() == 0)
				ret = "Graph is biconnected";
			else{
				ret = Util.replaceSquareBrackets(Util.addNewLines(cutVertices.toString(), ",", 30));
			}
			return ret + time;
		}		

		if (command.trim().equals(commands[9])){
			String ret;
			if (graph.isBiconnected()){
				ret = "Graph is biconnected";
				return ret;
			}
			else{
				ExecuteResult result = AlgorithmExecutor.execute(graph, "listBiconnectedComponents");
				String time =  " [in " + result.getDuration() + " ms]";
				List<Graph<GraphVertex, GraphEdge>> blocks = (List<Graph<GraphVertex, GraphEdge>>) result.getValue();
				StringBuilder builder = new StringBuilder();
				for (int i = 0; i < blocks.size(); i++){
					Graph<GraphVertex, GraphEdge> block  = blocks.get(i);
					builder.append("Component " + (i+1) + " " + block.printFormat() + "\n");
				}
				ret = builder.toString();
				return ret + time;
			}
		}	

		if (command.trim().equals(commands[10])){
			String ret = "";
			SeparationPairSplitting<GraphVertex, GraphEdge> separationPairsSplitting =
					new SeparationPairSplitting<GraphVertex, GraphEdge>();
			List<SplitPair<GraphVertex, GraphEdge>> separationPairs;

			ExecuteResult result = AlgorithmExecutor.execute(separationPairsSplitting, "findSeaparationPairs", graph);
			String time =  " [in " + result.getDuration() + " ms]";
			separationPairs = (List<SplitPair<GraphVertex, GraphEdge>>) result.getValue();

			if (separationPairs.size() == 0){
				ret = "Graph is triconnected";
			}
			else{
				ret = separationPairs.toString();
			}
			return ret + time;
		}	


		if (command.trim().equals(commands[11])){
			TriconnectedSplitting<GraphVertex, GraphEdge> splitting = new TriconnectedSplitting<GraphVertex, GraphEdge>(graph);
			ExecuteResult result = AlgorithmExecutor.execute(splitting, "formTriconnectedComponents");
			List<HopcroftTarjanSplitComponent<GraphVertex, GraphEdge>>  components = (List<HopcroftTarjanSplitComponent<GraphVertex, GraphEdge>>) result.getValue();
			String time =  " [in " + result.getDuration() + " ms]";
			String ret = "";
			if (components.size() == 0){
				ret = "Graph is triconnected";
			}
			else{
				StringBuilder builder = new StringBuilder();
				for (int i = 0; i < components.size(); i++){
					HopcroftTarjanSplitComponent<GraphVertex, GraphEdge> component  = components.get(i);
					builder.append("Component " + (i+1) + " " + component.printFormat() + "\n");
				}
				ret = builder.toString();
			}
			return ret + time;
		}


		if (command.trim().equals(commands[12])){
			String ret = "";
			
			ExecuteResult result = AlgorithmExecutor.execute(nauty, "findAutomorphisms", graph);
			String time =  " [in " + result.getDuration() + " ms]";
			List<Permutation> automorphisms = (List<Permutation>) result.getValue();
			for (Permutation p : automorphisms){
				ret += p.cyclicRepresenatation() + "\n";
			}
			return ret + time;
		}

		if (command.trim().equals(commands[13])){
			SimpleCyclesFinder<GraphVertex, GraphEdge> cyclesFinder = new SimpleCyclesFinder<GraphVertex,GraphEdge>();
			ExecuteResult result = AlgorithmExecutor.execute(cyclesFinder, "findCycles", graph);
			String time =  " [in " + result.getDuration() + " ms]";
			List<List<GraphVertex>> cycles = (List<List<GraphVertex>>) result.getValue();
			String cyclesStr = "";
			if (cycles.size() == 0)
				cyclesStr = "Graph is not cyclic";
			else{
				for (int i = 0; i < cycles.size(); i++){
					cyclesStr += Util.replaceSquareBrackets(cycles.get(i).toString());
					if (i < cycles.size() - 1)
						cyclesStr += ", ";
				}
				cyclesStr = Util.addNewLines(cyclesStr, "),", 30);
			}
			return cyclesStr + time;
		}

		if (command.trim().equals(commands[14])){
			SimpleUndirectedCyclesFinder<GraphVertex, GraphEdge> cycles = 
					new SimpleUndirectedCyclesFinder<GraphVertex, GraphEdge>(graph);
			ExecuteResult result = AlgorithmExecutor.execute(cycles, "findAllCycles");
			List<List<GraphVertex>> allCycles = (List<List<GraphVertex>>) result.getValue();
			String time =  " [in " + result.getDuration() + " ms]";
			String ret = "\n";
			for (List<GraphVertex> cycle : allCycles)
				ret += cycle + "\n";
			return Util.replaceSquareBrackets(ret) + time;
		}

		if (command.trim().equals(commands[15])){
			String ret = "";
			long totalTime = 0;
			PermutationAnalyzator<GraphVertex, GraphEdge> analyzator = new PermutationAnalyzator<GraphVertex,GraphEdge>(graph);
			ExecuteResult result = AlgorithmExecutor.execute(analyzator, "findReflectionGroups");
			totalTime += result.getDuration();
			List<PermutationGroup> groups = (List<PermutationGroup>) result.getValue();
			result = AlgorithmExecutor.execute(analyzator, "findRotationGroups");
			totalTime += result.getDuration();
			groups.addAll((List<PermutationGroup>) result.getValue());
			result = AlgorithmExecutor.execute(analyzator, "findDihedralGroups");
			totalTime += result.getDuration();
			groups.addAll((List<PermutationGroup>) result.getValue());
			for (PermutationGroup gr : groups)
				ret += gr + "\n";

			String time =  " [in " + totalTime + " ms]";
			return ret + time;

		}

		if (command.trim().equals(commands[16])){
			ExecuteResult result = AlgorithmExecutor.execute(graph, "isTree");
			return ((Boolean) result.getValue() ? "yes" : "no" )+ " [in " + result.getDuration() + " ms]";
		}

		if (command.trim().equals(commands[17])){
			BinaryTree<GraphVertex, GraphEdge> binaryTree = new BinaryTree<GraphVertex,GraphEdge>(graph);
			ExecuteResult result = AlgorithmExecutor.execute(binaryTree, "execute");
			return binaryTree.isCanBeConstructed() ? "yes" : "no" + " [in " + result.getDuration() + " ms]";
		}

		if (command.trim().equals(commands[18])){
			BinaryTree<GraphVertex, GraphEdge> binaryTree = new BinaryTree<GraphVertex,GraphEdge>(graph);
			ExecuteResult result = AlgorithmExecutor.execute(binaryTree, "execute");
			long totalTime = result.getDuration();
			if (!binaryTree.isCanBeConstructed())
				return "Not a binary tree" + " [in " + totalTime + " ms]";
			result = AlgorithmExecutor.execute(binaryTree, "isBalanced");
			totalTime += result.getDuration();
			String answer = binaryTree.isBalanced() ? "yes" : "no";
			return answer + " [in " + totalTime + " ms]";
		}

		if (command.trim().equals(commands[19])){
			ExecuteResult result = AlgorithmExecutor.execute(graph, "isRing");
			return ((Boolean) result.getValue() ? "yes" : "no" )+ " [in " + result.getDuration() + " ms]";
		}

		if (command.trim().equals(commands[20])){
			Bipartite<GraphVertex, GraphEdge> bipartite = new Bipartite<>(graph);
			ExecuteResult result = AlgorithmExecutor.execute(bipartite, "isBipartite");
			return ((Boolean) result.getValue() ? "yes" : "no" )+ " [in " + result.getDuration() + " ms]";
		}

		if (command.startsWith(commands[21])){
			command = command.substring(commands[21].length()).trim();
			String[] split = command.split(" ");
			if (split.length < 2)
				return "Please enter two vertices";

			String v1 = split[0].trim();
			String v2 = split[1].trim();

			GraphVertex vert1 = MainFrame.getInstance().getCurrentView().getModel().getVertexByContent(v1);
			if (vert1 == null)
				return "Unknown vertex \"" + v1 + "\"";
			GraphVertex vert2 = MainFrame.getInstance().getCurrentView().getModel().getVertexByContent(v2);
			if (vert2 == null)
				return "Unknown vertex \"" + v2 + "\"";

			DijkstraAlgorithm<GraphVertex, GraphEdge> dijsktra = new DijkstraAlgorithm<>(graph);

			String answer;
			long start = System.currentTimeMillis();
			Path<GraphVertex, GraphEdge> path = dijsktra.getPath(vert1, vert2);
			long end = System.currentTimeMillis();
			long duration = end - start;
			String time = " [in " + duration + " ms]";

			if (path == null)
				answer = "Vertices are not connected";
			else
				answer = path.toString();
			return answer + time;
		}

		if (command.trim().equals(commands[22])){
			centralArea.setText("");
			return "";
		}

		if (command.startsWith(commands[23])){
			//Layout DSL input
			List<GraphVertex> vertices = graph.getVertices();
			List<GraphEdge> edges = graph.getEdges();

			DSLLayouter<GraphVertex, GraphEdge> dslLayout =
					new DSLLayouter<GraphVertex, GraphEdge>(vertices, edges, command);	

			try{
				Drawing<GraphVertex, GraphEdge> drawing = dslLayout.layout();
				GraphView view = MainFrame.getInstance().getCurrentView();
				for (GraphVertex vert : drawing.getVertexMappings().keySet()){
					vert.setPosition(drawing.getVertexMappings().get(vert));
				}
				for (GraphEdge edge : drawing.getEdgeMappings().keySet()){
					List<Point2D> points = drawing.getEdgeMappings().get(edge);
					edge.setLinkNodesFromPositions(points);
				}
				view.repaint();
				return "Done";
			}
			catch(DSLException ex){
				return ex.getMessage();
			}
		}

		if (command.startsWith(commands[24])){
			command = command.substring(commands[24].length()).trim();
			if (command.length() == 0){
				StringBuilder builder = new StringBuilder("Commands:\n");
				for (int i = 0; i < commands.length - 1; i++){
					builder.append(help.get(commands[i]) + "\n");
				}
				return builder.toString();
			}
			if (help.containsKey(command)){
				return help.get(command);
			}
		}

		return "Unknown command. Type help to view a list of available commands";
	}

	private static void initHelp(){
		help.put("create graph", "create graph {name} [true/false] - Creates a new directed or undirected graph."
				+ " True stands for directed, false for undirected. If nothing is provided, a new undirected graph is created");
		help.put("add vertex", "add vertex {name} ({x},{y}) - Creates a new vertex and adds it to the current graph at position x,y");
		help.put("add edge", "add edge {vertex1} {vertex2} - Creates a new edge connecting two specified vertices of the current graph");
		help.put("is connected", "is connected - Checks if the current graph is connected");
		help.put("is biconnected", "is biconnected - Checks if the current graph is biconnected");
		help.put("is cyclic", "is cyclic - Checks if the current graph is cyclic");
		help.put("is planar", "is planar [Fraysseix-Mendez|Boyer-Myrvold|PQ] - Checks if the current graph is planar. An algorithm can optionally be specified.");
		help.put("is triconnected", "is triconnected - Checks if the current graph is triconnected");
		help.put("list cut vertices", "list cut vertices - Lists cut vertices of the current graph");
		help.put("list blocks", "list blocks - Lists blocks of the current graph");
		help.put("list separation pairs", "list separation pairs - Lists separation pairs of the current graph");
		help.put("list triconnected components", "list triconnected components - Lists triconnected components of the current graph");
		help.put("list automorphisms", "list automorphisms - Lists automorphisms of the current graph");
		help.put("cycles basis", "cycles basis - Finds the cycles basis of the current graph");
		help.put("list separation pairs", "list separation pairs - Lists separation pairs of the current graph");
		help.put("list all cycles", "list all cycles - Lists all cycles of the current graph. Not recommended to be used if the graph is relatively big. Due to the usually very high number of cycles, the calculation could be time consuming.");
		help.put("automorphism groups", "automorphism groups - Analyzes the automorphisms of the current graph and finds automorphism groups");
		help.put("is tree", "is tree - Checks if the current graph is a tree");
		help.put("is binary tree", "is binary tree - Checks if the current graph is a binary tree");
		help.put("is balanced binary tree", "is balanced binary tree - Checks if the current graph is a balanced binary tree");
		help.put("is ring", "is ring - Checks if the current graph is a ring");
		help.put("is bipartite", "is bipartite - Checks if the current graph is a bipartite");
		help.put("clear", "clear - Clears the content of the command panel");
		help.put("path between", "path between {vertex1} {vertex2} - Finds the shortest path between the two vertices of the current graph");
		help.put("lay out", "lay out {description} - Lays out the current graph. Provided descirption should conform to the language rules");
	}

	private static  void initCommands(){
		commands = new String[25];

		commands[0] = "create graph";
		commands[1] = "add vertex";
		commands[2] = "add edge";
		commands[3] = "is connected";
		commands[4] = "is biconnected";
		commands[5] = "is triconnected";
		commands[6] = "is cyclic";
		commands[7] = "is planar";
		commands[8] = "list cut vertices";
		commands[9] = "list blocks";
		commands[10] = "list separation pairs";
		commands[11] = "list triconnected components";
		commands[12] = "list automorphisms";
		commands[13] = "cycles basis";
		commands[14] = "list all cycles";
		commands[15] = "automorphism groups";
		commands[16] = "is tree";
		commands[17] = "is binary tree";
		commands[18] = "is balanced binary tree";
		commands[19] = "is ring";
		commands[20] = "is bipartite";
		commands[21] = "path between";
		commands[22] = "clear";
		commands[23] = "lay out";
		commands[24] = "help";
	}


}
