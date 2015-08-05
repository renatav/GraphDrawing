package gui.command.panel;

import graph.algorithm.AlgorithmExecutor;
import graph.algorithm.ExecuteResult;
import graph.algorithms.planarity.BoyerMyrvoldPlanarity;
import graph.algorithms.planarity.PlanarityTestingAlgorithm;
import graph.elements.Graph;
import graph.exception.CannotBeAppliedException;
import graph.nauty.McKayGraphLabelingAlgorithm;
import graph.properties.splitting.SplitPair;
import graph.properties.splitting.Splitting;
import graph.tree.spqr.SPQRTree;
import gui.main.frame.MainFrame;
import gui.model.GraphEdge;
import gui.model.GraphVertex;
import gui.view.painters.EdgePainter;
import gui.view.painters.VertexPainter;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

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
				// TODO Auto-generated method stub
				
			}
		});
	}

	
	
	private String processCommand(String command){
		command = command.trim();
		allCommands.add(command);
		currentCommandIndex = allCommands.size();
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
			MainFrame.getInstance().getCurrentView().addEdgePainter(new EdgePainter(edge));
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
			if (MainFrame.getInstance().getCurrentView().getModel().getGraph().isBiconnected())
				return "Graph is biconnected.";
			return splitting.findAllCutVertices(MainFrame.getInstance().getCurrentView().getModel().getGraph()).toString(); 
		}		

		if (command.startsWith(commands[9])){
			if (MainFrame.getInstance().getCurrentView().getModel().getGraph().isBiconnected())
				return "Graph is biconnected.";
			return splitting.findAllBlocks(MainFrame.getInstance().getCurrentView().getModel().getGraph()).toString(); 
		}	

		if (command.startsWith(commands[10])){
			if (!MainFrame.getInstance().getCurrentView().getModel().getGraph().isBiconnected())
				return "Graph is not biconnected.";
			return splitting.findAllSplitPairs(MainFrame.getInstance().getCurrentView().getModel().getGraph()).toString(); 
		}	

		if (command.startsWith(commands[11])){
			Graph<GraphVertex, GraphEdge> graph = MainFrame.getInstance().getCurrentView().getModel().getGraph();
			if (!graph.isBiconnected())
				return "Graph is not biconnected.";
			String[] split = command.split(",");
			if (split.length < 2)
				return "Please enter a split pair consisting of two vertices";
			String v1 = split[0];
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

			Graph<GraphVertex, GraphEdge> graph = MainFrame.getInstance().getCurrentView().getModel().getGraph();
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


			Graph<GraphVertex, GraphEdge> graph = MainFrame.getInstance().getCurrentView().getModel().getGraph();
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


			Graph<GraphVertex, GraphEdge> graph = MainFrame.getInstance().getCurrentView().getModel().getGraph();
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
			Graph<GraphVertex, GraphEdge> graph = MainFrame.getInstance().getCurrentView().getModel().getGraph();
			McKayGraphLabelingAlgorithm<GraphVertex, GraphEdge> nauty = new McKayGraphLabelingAlgorithm<GraphVertex,GraphEdge>(graph);
			nauty.execute();
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
		commands[15] = "help";
		commands[16] = "nauty";

	}
		

}
