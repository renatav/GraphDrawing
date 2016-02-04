package graph.layout;

import graph.drawing.Drawing;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.layout.util.PositionAction;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.layout.Layout;
import prefuse.data.Node;
import prefuse.data.Table;

public abstract class AbstractPrefuseLayouter<V extends Vertex, E extends Edge<V>> extends AbstractLayouter<V, E> {

	protected Layout layouter;
	protected prefuse.data.Graph prefuseGraph;
	protected Map<V, Node> verticesMap = new HashMap<V, Node>();
	protected Map<Node, Integer> nodeKeyMap = new HashMap<Node, Integer>();
	protected Map<E, prefuse.data.Edge> edgesMap = new HashMap<E, prefuse.data.Edge>();
	protected Visualization vis = new Visualization();
	protected Table nodeData, edgeData;

	protected void createPrefuseGraph(Graph<V,E> graph){
		// Create tables for node and edge data, and configure their columns.
		nodeData = new Table();
		edgeData = new Table(0,1);

		nodeData.addColumn(prefuse.data.Graph.DEFAULT_NODE_KEY, int.class);
		edgeData.addColumn( prefuse.data.Graph.DEFAULT_SOURCE_KEY, int.class);
	    edgeData.addColumn( prefuse.data.Graph.DEFAULT_TARGET_KEY, int.class);

		// Create Graph backed by those tables. 
		prefuseGraph = new prefuse.data.Graph(nodeData, edgeData, true);

		int key = 0;
		
		for (V v : graph.getVertices()){
			Node node = prefuseGraph.addNode();
			node.setInt(prefuse.data.Graph.DEFAULT_NODE_KEY, key);
			verticesMap.put(v, node);
			nodeKeyMap.put(node, key);
			key++;
		}

		for (E e : graph.getEdges()){
			Node n1 = verticesMap.get(e.getOrigin());    	   
			Node n2 = verticesMap.get(e.getDestination());
			
			prefuse.data.Edge edge = prefuseGraph.addEdge(n1, n2);
			edgesMap.put(e, edge);
		}

		// add the graph to the visualization as the data group "graph"
		// nodes and edges are accessible as "graph.nodes" and "graph.edges"
		vis.add("graph", prefuseGraph);
		

	}

	@Override
	public Drawing<V, E> layout(Graph<V, E> graph,
			GraphLayoutProperties layoutProperties) {
		createPrefuseGraph(graph);
		initLayouter(layoutProperties);
		
		Drawing<V,E> drawing = new Drawing<V,E>();
		
		//
		ActionList layout = new ActionList();
		layout.add(layouter);
		vis.putAction("layout", layout);
		PositionAction positionAction = new PositionAction(0);
		positionAction.setVisualization(vis);

		//add the actions to the visualization
		vis.putAction("layout", layout);
		vis.putAction("position", positionAction);

		//needed because the layouter uses it
		Display d = new Display(vis);
		d.setSize(1000,1000); // set display size
		
		//trigger layout
		//schedulers are used
		//therefore, specify that positioning action should be run after layouting is done
		vis.run("layout");
		vis.runAfter("layout", "position");
		
		while (true){
			//wait for positioning to finish
			//again because schedulers are used
			//otherwise, the rest of the code would be executed before positioning action
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (positionAction.isFinished())
				break;
		}
		
		Map<Integer, Point2D> positionsMap = positionAction.getPositionsMap();
		
		for (V v : verticesMap.keySet()){
			Node node = verticesMap.get(v);
			Integer nodeKey = nodeKeyMap.get(node);
			//Integer nodeIndex = prefuseGraph.getNodeIndex(nodeKey);
			Point2D position = positionsMap.get(nodeKey); //node key = node index
			drawing.setVertexPosition(v, position);	
		}
		
		return drawing;
	}

	protected abstract void initLayouter(GraphLayoutProperties layoutProperties);
}
