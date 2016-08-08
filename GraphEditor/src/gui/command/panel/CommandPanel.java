package gui.command.panel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import graph.algorithm.AlgorithmExecutor;
import graph.algorithm.ExecuteResult;
import graph.algorithm.cycles.SimpleCyclesFinder;
import graph.algorithm.cycles.SimpleUndirectedCyclesFinder;
import graph.algorithms.connectivity.PlanarAugmentation;
import graph.algorithms.drawing.ConvexDrawing;
import graph.algorithms.planarity.BoyerMyrvoldPlanarity;
import graph.algorithms.planarity.PlanarityTestingAlgorithm;
import graph.drawing.Drawing;
import graph.elements.Graph;
import graph.exception.CannotBeAppliedException;
import graph.exception.DSLException;
import graph.layout.dsl.DSLLayouter;
import graph.ordering.TopologicalOrdering;
import graph.properties.components.SplitPair;
import graph.properties.splitting.AlgorithmErrorException;
import graph.properties.splitting.HopcroftTarjanSplitting;
import graph.properties.splitting.SeparationPairSplitting;
import graph.properties.splitting.Splitting;
import graph.symmetry.Permutation;
import graph.symmetry.PermutationAnalyzator;
import graph.symmetry.nauty.McKayGraphLabelingAlgorithm;
import graph.tree.bc.BCTree;
import graph.tree.binary.BinaryTree;
import graph.tree.spqr.SPQRTree;
import gui.main.frame.MainFrame;
import gui.model.GraphEdge;
import gui.model.GraphVertex;
import gui.view.GraphView;
import gui.view.painters.EdgePainter;
import gui.view.painters.VertexPainter;
import net.miginfocom.swing.MigLayout;

public class CommandPanel extends JPanel{

	private static final long serialVersionUID = 1L;

	private static String[] commands;
	private JTextField inputField = new JTextField();
	private JTextArea centralArea = new JTextArea(10, 10);
	private List<String> allCommands = new ArrayList<String>();
	private int currentCommandIndex;
	//private static PlanarityTestingAlgorithm<GraphVertex, GraphEdge> planarityTest = new FraysseixMendezPlanarity<GraphVertex, GraphEdge>();
	private static PlanarityTestingAlgorithm<GraphVertex, GraphEdge> planarityTest = new BoyerMyrvoldPlanarity<GraphVertex, GraphEdge>();
	private static Splitting<GraphVertex, GraphEdge> splitting = new Splitting<>();

	public CommandPanel(){
		setLayout(new MigLayout("fill"));

		add(inputField, "dock south, growx");

		centralArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(centralArea);
		add(scrollPane, "grow");

		initCommands();

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



	private String processCommand(String command){
		command = command.trim();
		allCommands.add(command);
		currentCommandIndex = allCommands.size();

		Graph<GraphVertex, GraphEdge> graph = MainFrame.getInstance().getCurrentView().getModel().getGraph();

		if (command.equals(commands[0]))
			System.exit(0);
		if (command.startsWith(commands[1])){ //create graph
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

			return directed ? "Directed graph \"" + name + "\" created" : "Undirected graph \"" + name + "\" created";

		}
		if (command.startsWith(commands[2])){ //add vertex
			command = command.substring(commands[2].length()).trim();
			String[] split = command.split(" ");
			if (split.length < 2)
				return "Please enter vertex name and position as (x, y)";
			if (split[0].contains("(") && !split[1].contains("("))
				return "Please enter vertex name and position as (x, y)";
			String content = split[0];
			int positionStart = command.indexOf("(");
			if (positionStart == -1)
				return "Please enter vertex name and position as (x, y)";
			String position = command.substring(positionStart + 1).trim();
			if (!position.endsWith(")"))
				return "Please enter vertex name and position as (x, y)";
			position = position.substring(0, position.length() - 1);
			String[] nums = position.split(",");
			int x = Integer.parseInt(nums[0].trim());
			int y = Integer.parseInt(nums[1].trim());
			Point2D point = new Point2D.Double(x, y);
			if (content.equals(""))
				return "Please enter vertex name";
			GraphVertex vert = new GraphVertex(point, content);
			MainFrame.getInstance().getCurrentView().getModel().addVertex(vert);
			MainFrame.getInstance().getCurrentView().addVertexPainter(new VertexPainter(vert));
			return "Vertex " + content + " added at position " + "(" + x + ", " + y +")";
		}

		if (command.startsWith(commands[3])){
			command = command.substring(commands[3].length()).trim();
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
			GraphEdge edge = new GraphEdge(vert1, vert2);
			edge.setNodesBasedOnVertices();
			MainFrame.getInstance().getCurrentView().getModel().addEdge(edge);
			MainFrame.getInstance().getCurrentView().addEdgePainter(new EdgePainter(edge, MainFrame.getInstance().getCurrentView().getModel().getGraph()));
			return "Edge " + v1 + ", " + v2 + " added";
		}



		if (command.startsWith(commands[4])){
			return MainFrame.getInstance().getCurrentView().getModel().getGraph().isConnected() ? "yes" : "no";
		}

		if (command.startsWith(commands[5])){
			return MainFrame.getInstance().getCurrentView().getModel().getGraph().isBiconnected() ? "yes" : "no";

		}

		if (command.startsWith(commands[6])){
			return MainFrame.getInstance().getCurrentView().getModel().getGraph().isCyclic() ? "yes" : "no";

		}

		if (command.startsWith(commands[7])){
			ExecuteResult result = AlgorithmExecutor.execute(planarityTest, "isPlannar", MainFrame.getInstance().getCurrentView().getModel().getGraph());
			return ((Boolean) result.getValue() ? "yes" : "no" )+ " [in " + result.getDuration() + " ms]";
		}

		if (command.startsWith(commands[8])){
			//	if (MainFrame.getInstance().getCurrentView().getModel().getGraph().isBiconnected())
			//	return "Graph is biconnected.";
			//return splitting.findAllCutVertices(MainFrame.getInstance().getCurrentView().getModel().getGraph()).toString();
			return graph.listCutVertices().toString();
		}		

		if (command.startsWith(commands[9])){
			if (MainFrame.getInstance().getCurrentView().getModel().getGraph().isBiconnected())
				return "Graph is biconnected.";
			return graph.listBiconnectedComponents() + ""; 
		}	

		if (command.startsWith(commands[10])){
			if (!MainFrame.getInstance().getCurrentView().getModel().getGraph().isBiconnected())
				return "Graph is not biconnected.";
			return splitting.findAllSplitPairs(MainFrame.getInstance().getCurrentView().getModel().getGraph()).toString(); 
		}	

		if (command.startsWith(commands[11])){
			if (!graph.isBiconnected())
				return "Graph is not biconnected.";
			String[] split = command.split(",");
			if (split.length < 2)
				return "Please enter a split pair consisting of two vertices";
			String v1 = split[0].substring(commands[11].length()).trim();
			String v2 = split[1];

			GraphVertex vert1 = graph.getVertexByContent(v1);
			if (vert1 == null)
				return "Unknown vertex \"" + v1 + "\"";
			GraphVertex vert2 = graph.getVertexByContent(v2);
			if (vert2 == null)
				return "Unknown vertex \"" + v2 + "\"";

			SplitPair<GraphVertex, GraphEdge> pair = new SplitPair<GraphVertex, GraphEdge>(vert1, vert2);

			return splitting.findAllSplitComponents(graph, pair).toString(); 
		}

		if (command.startsWith(commands[12])){
			command = command.substring(commands[12].length()).trim();
			String[] split = command.split(" ");
			if (split.length < 2)
				return "Please enter split pair and edge";

			if (!graph.isBiconnected())
				return "Graph is not biconnected.";

			String sp = split[0].substring(1, split[0].length()-1);
			String[] split2 = sp.split(",");
			if (split2.length < 2)
				return "Please enter a split pair consisting of two vertices";
			String v1 = split2[0];
			String v2 = split2[1];

			GraphVertex vert1 = graph.getVertexByContent(v1);
			if (vert1 == null)
				return "Unknown vertex \"" + v1 + "\"";
			GraphVertex vert2 = graph.getVertexByContent(v2);
			if (vert2 == null)
				return "Unknown vertex \"" + v2 + "\"";

			sp = split[1].substring(1, split[1].length()-1);
			split2 = sp.split(",");
			if (split2.length < 2)
				return "Please enter an edge consisting of two vertices";
			v1 = split2[0];
			v2 = split2[1];

			GraphVertex vert3 = graph.getVertexByContent(v1);
			if (vert3 == null)
				return "Unknown vertex \"" + v1 + "\"";
			GraphVertex vert4 = graph.getVertexByContent(v2);
			if (vert4 == null)
				return "Unknown vertex \"" + v2 + "\"";
			if (!graph.hasEdge(vert3, vert4))
				return "Edge doesn't exist";
			GraphEdge edge = graph.edgeesBetween(vert3, vert4).get(0);

			SplitPair<GraphVertex, GraphEdge> pair = new SplitPair<GraphVertex, GraphEdge>(vert1, vert2);

			return splitting.splitGraph(pair, edge, graph).toString(); 
		}


		if (command.startsWith(commands[13])){
			command = command.substring(commands[13].length()).trim();
			String[] split = command.split(" ");
			if (split.length < 1)
				return "Please enter one edge";

			if (!graph.isBiconnected())
				return "Graph is not biconnected.";


			String sp = split[0].substring(1, split[0].length()-1);
			String[] split2 = sp.split(",");
			if (split2.length < 2)
				return "Please enter an edge consisting of two vertices";
			String v1 = split2[0];
			String v2 = split2[1];

			GraphVertex vert3 = graph.getVertexByContent(v1);
			if (vert3 == null)
				return "Unknown vertex \"" + v1 + "\"";
			GraphVertex vert4 = graph.getVertexByContent(v2);
			if (vert4 == null)
				return "Unknown vertex \"" + v2 + "\"";
			if (!graph.hasEdge(vert3, vert4))
				return "Edge doesn't exist";
			GraphEdge edge = graph.edgeesBetween(vert3, vert4).get(0);

			return splitting.maximalSplitPairs(graph, edge).toString(); 
		}

		if (command.startsWith(commands[14])){
			command = command.substring(commands[14].length()).trim();
			String[] split = command.split(" ");
			if (split.length < 1)
				return "Please enter one edge";

			if (!graph.isBiconnected())
				return "Graph is not biconnected.";


			String sp = split[0].substring(1, split[0].length()-1);
			String[] split2 = sp.split(",");
			if (split2.length < 2)
				return "Please enter an edge consisting of two vertices";
			String v1 = split2[0];
			String v2 = split2[1];

			GraphVertex vert3 = graph.getVertexByContent(v1);
			if (vert3 == null)
				return "Unknown vertex \"" + v1 + "\"";
			GraphVertex vert4 = graph.getVertexByContent(v2);
			if (vert4 == null)
				return "Unknown vertex \"" + v2 + "\"";
			if (!graph.hasEdge(vert3, vert4))
				return "Edge doesn't exist";
			GraphEdge edge = graph.edgeesBetween(vert3, vert4).get(0);

			try {
				new SPQRTree<GraphVertex, GraphEdge>(edge, graph).printTree();
				return "";
			} catch (CannotBeAppliedException e) {
				return "Couldn't construct spqr tree: " + e.getMessage();
			}
		}

		if (command.equals(commands[16])){
			McKayGraphLabelingAlgorithm<GraphVertex, GraphEdge> nauty = new McKayGraphLabelingAlgorithm<GraphVertex,GraphEdge>(graph);
			List<Permutation> automorphisms = nauty.findAutomorphisms();
			String ret = "\n";
			for (Permutation p : automorphisms){
				ret += p.cyclicRepresenatation() + "\n";
			}

			return ret;
		}

		if (command.equals(commands[17])){
			SimpleCyclesFinder<GraphVertex, GraphEdge> jcycles = new SimpleCyclesFinder<GraphVertex,GraphEdge>();
			System.out.println(jcycles.findCycles(graph));
			return (jcycles.toString());
		}
		if (command.equals(commands[18])){
			SimpleUndirectedCyclesFinder<GraphVertex, GraphEdge> cycles = 
					new SimpleUndirectedCyclesFinder<GraphVertex, GraphEdge>(graph);
			String ret = "\n";
			for (List<GraphVertex> cycle : cycles.findAllCycles())
				ret += cycle + "\n";
			return ret;
		}

		if (command.equals(commands[19])){
			String ret = "";
			PermutationAnalyzator<GraphVertex, GraphEdge> analyzator =new PermutationAnalyzator<GraphVertex,GraphEdge>(graph);
			ret += analyzator.findReflectionGroups() + "\n";
			ret += analyzator.findRotationGroups() + "\n";
			ret += analyzator.findDihedralGroups();
			return ret;

		}

		if (command.equals(commands[20])){
			System.out.println("convex");
			ConvexDrawing<GraphVertex, GraphEdge> drawing = new ConvexDrawing<GraphVertex,GraphEdge>(graph);
			drawing.execute();
		}

		if (command.equals(commands[21])){
			return graph.listBiconnectedComponents().toString();
		}

		if (command.equals(commands[22])){
			SeparationPairSplitting<GraphVertex, GraphEdge> separationPairsSplitting = new SeparationPairSplitting<GraphVertex, GraphEdge>();
			try {
				List<SplitPair<GraphVertex, GraphEdge>> separationPairs = separationPairsSplitting.findSeaparationPairs(graph);
				return separationPairs.toString();
			} catch (AlgorithmErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return e.getMessage();
			}

		}


		else if (command.equals(commands[24])){
			HopcroftTarjanSplitting<GraphVertex, GraphEdge> hopcroftTarjan = new HopcroftTarjanSplitting<GraphVertex, GraphEdge>(graph);
			try {
				hopcroftTarjan.execute();
			} catch (AlgorithmErrorException e) {
				return e.getMessage();
			}
			return "Done";

		}

		else if (command.equals(commands[23])){
			centralArea.setText("");
			return "";
		}

		else if (command.startsWith(commands[25])){
			//Layout DSL input
			DSLLayouter<GraphVertex, GraphEdge> dslLayout = new DSLLayouter<GraphVertex, GraphEdge>(graph.getVertices(), 
					graph.getEdges(), command);
			try{
				Drawing<GraphVertex, GraphEdge> drawing = dslLayout.layout();
				GraphView view = MainFrame.getInstance().getCurrentView();
				for (GraphVertex vert : drawing.getVertexMappings().keySet()){
					vert.setPosition(drawing.getVertexMappings().get(vert));
				}
				for (GraphEdge edge : drawing.getEdgeMappings().keySet()){
					List<Point2D> points = drawing.getEdgeMappings().get(edge);
					edge.setLinkNodes(points);
				}
				view.repaint();
				return "Done";
			}
			catch(DSLException ex){
				return ex.getMessage();
			}
		}

		else if (command.trim().equals("Binary tree")){
			BinaryTree<GraphVertex, GraphEdge> binaryTree = new BinaryTree<GraphVertex,GraphEdge>(graph);
			boolean balanced = binaryTree.isBalanced();
			return binaryTree.toString() + "\n Balanced: " + balanced;
		}
		else if (command.trim().equals("is ring")){
			return graph.isRing() + "";
		}
		else if (command.trim().equals("test")){
			//execute whatever that is being tested
			try {
				//Map<GraphVertex,Integer> ordering = TopologicalOrdering.calculateOrdering(graph);
				//System.out.println(ordering);
				//BCTree<GraphVertex, GraphEdge> bcTree = new BCTree<GraphVertex, GraphEdge>(graph);
				//System.out.println(bcTree);
			PlanarAugmentation<GraphVertex, GraphEdge> planarAugmentation = new PlanarAugmentation<GraphVertex, GraphEdge>();
			Graph<GraphVertex,GraphEdge> biconnected = planarAugmentation.planarBiconnected(graph);
			return biconnected.toString();
			} catch (CannotBeAppliedException e) {
				e.printStackTrace();
			}
		}

		if (command.equals(commands[15])){
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
			return builder.toString();
		}

		return "Unknown command";
	}


	private static  void initCommands(){
		commands = new String[29];

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
		commands[15] = "help";
		commands[16] = "automorphisms";
		commands[17] = "list base cycles";
		commands[18] = "list all cycles";
		commands[19] = "groups";
		commands[20] = "convex";
		commands[21] = "list biconnected components";
		commands[22] = "separation pairs";
		commands[23] = "clear";
		commands[24] = "splitting";
		commands[25] = "layout";
		commands[26] = "binary tree";
		commands[27] = "is ring";
		commands[28] = "test";
	}


}
