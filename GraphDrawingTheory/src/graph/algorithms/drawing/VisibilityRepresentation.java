package graph.algorithms.drawing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import graph.algorithms.planarity.BoyerMyrvoldPlanarity;
import graph.algorithms.planarity.dual.STDualGraph;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

public class VisibilityRepresentation<V extends Vertex, E extends Edge<V>> {

	private Map<V, Integer> xMinMap;
	private Map<V, Integer> yMinMap;
	private Map<V, Integer> xMaxMap;
	private Map<V, Integer> yMaxMap;
	private Graph<V,E> graph;
	private BoyerMyrvoldPlanarity<V, E> boyerMyrvold;
	private Logger log = Logger.getLogger(VisibilityRepresentation.class);
	
	public VisibilityRepresentation(Graph<V,E> graph){
		this.graph = graph;
		xMinMap = new HashMap<V, Integer>();
		yMinMap = new HashMap<V, Integer>();
		xMaxMap = new HashMap<V, Integer>();
		yMaxMap = new HashMap<V, Integer>();
		constructVisibilityRepresentation();
		boyerMyrvold = new BoyerMyrvoldPlanarity<V,E>();
	}
	
	private void constructVisibilityRepresentation(){
		//make graph biconnected by adding dummy edges - do this later when everything else seems to be fine
		//select edge (s,t) on the external face
		//find external face
		boyerMyrvold.isPlannar(graph);
		List<V> externalFaceVertices = boyerMyrvold.getOutsideFace();
		log.info("External face vertices: " + externalFaceVertices);
		V s = externalFaceVertices.get(0);
		V t = externalFaceVertices.get(1);
		log.info("s: " + s);
		log.info("t: " + t);
		
		List<E> externalFace = new ArrayList<E>();
		for (int i = 0; i < externalFaceVertices.size(); i++){
			V v1 = externalFaceVertices.get(i);
			V v2;
			if (i == externalFaceVertices.size() - 1)
				v2 = externalFaceVertices.get(0);
			else
				v2 = externalFaceVertices.get(i + 1);
			externalFace.add(graph.edgeBetween(v1, v2));
		}
		
		log.info("External face: " + externalFace);
		
		//compute plnar st-graph
		//this is like the original graph, but the edges should be in st-orientation
		Graph<V,E> stGraph = constructSTGraph();
		//create the dual planar st-graph G*
		STDualGraph<V, E> stDualGraph = new STDualGraph<V,E>(stGraph, externalFace, s, t);
		//Compute the optimal topological ordering Tx = T (G∗) 
		//Compute the optimal topological ordering Ty = T (G)
		/*
		 * for all v ∈ V do {Assigning positions to the horizontal vertex segments}
			Let fl be the face to the left of the leftmost outgoing edge of v
			Let fr be the face to the right of the rightmost outgoing edge of v
			10: {fl and fr are vertices in the dual graph G∗}
			Γ(v).y ← Ty(v)
			Γ(v).xmin ← Tx(fl)
			Γ(v).xmax ← Tx(fr) − 1
			end for
			15: for all e = (u, v) ∈ E do {Assigning positions to the vertical edge segments}
			Let fl be the face to the left of e {fl is a vertex in G∗}
			Γ(e).x ← Tx(fl)
			Γ(e).ymin ← Ty(u)
			Γ(e).ymax ← Ty(v)
			20: end for
			Remove any added “dummy” edges
		 */
		
		
	}
	
	private Graph<V,E> constructSTGraph(){
		//TODO
		return graph;
	}
}
