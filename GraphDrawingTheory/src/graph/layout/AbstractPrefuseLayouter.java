package graph.layout;

import graph.drawing.Drawing;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.layout.Layout;
import prefuse.data.Node;
import prefuse.data.Table;
import prefuse.data.Tuple;
import prefuse.data.tuple.TupleSet;
import prefuse.visual.VisualItem;

public abstract class AbstractPrefuseLayouter<V extends Vertex, E extends Edge<V>> extends AbstractLayouter<V, E> {



	protected Layout layouter;
	protected prefuse.data.Graph prefuseGraph;
	protected Map<V, Node> verticesMap = new HashMap<V, Node>();
	protected Map<E, prefuse.data.Edge> edgesMap = new HashMap<E, prefuse.data.Edge>();
	protected Visualization vis = new Visualization();

	protected void createPrefuseGraph(Graph<V,E> graph){
		// Create tables for node and edge data, and configure their columns.
		Table nodeData = new Table();
		Table edgeData = new Table(0,1);

		//nodeData.addColumn("node", Integer.class);
		//edgeData.addColumn("edge", Integer.class);
		edgeData.addColumn( prefuse.data.Graph.DEFAULT_SOURCE_KEY, int.class);
	    edgeData.addColumn( prefuse.data.Graph.DEFAULT_TARGET_KEY, int.class);

		// Create Graph backed by those tables. 
		prefuseGraph = new prefuse.data.Graph(nodeData, edgeData, true);


		for (V v : graph.getVertices()){
			Node node = prefuseGraph.addNode();
			verticesMap.put(v, node);
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
		
		ActionList layout = new ActionList();
		layout.add(layouter);
		vis.putAction("layout", layout);
		
		TupleSet visGroup = vis.getVisualGroup("graph.nodes");
		Iterator<Tuple> iter = visGroup.tuples();
		while (iter.hasNext()){
			Tuple t = iter.next();
			VisualItem item = vis.getVisualItem("graph.nodes", t);
			System.out.println(item.getX());
			System.out.println(item);
		}
		
		
		return new Drawing<V,E>();
		//return createDrawing(graph);
	}

	protected abstract void initLayouter(GraphLayoutProperties layoutProperties);
}
