package graph.algorithms.drawing;

import graph.algorithms.numbering.STNumbering;
import graph.algorithms.planarity.BoyerMyrvoldPlanarity;
import graph.algorithms.planarity.PlanarFaces;
import graph.algorithms.planarity.dual.DualGraphVertex;
import graph.algorithms.planarity.dual.STDualGraph;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.exception.CannotBeAppliedException;
import graph.exception.NotPlanarException;
import graph.ordering.TopologicalOrdering;
import jnr.ffi.Struct.ssize_t;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/** Given a graph G = (V, E), a visibility representation Γ, for G maps 
every  vertex  v  in V    to  a  horizontal  vertex  segment  Γ(v)  and  every  edge  (u, v)  ∈ E  to  a 
vertical  edge segment Γ(u, v) such that each vertical edge segment Γ(u, v) has its endpoints 
lying on the horizontal vertex segments Γ(u) and Γ(v) and no other segment intersections 
or overlaps occur. */

public class VisibilityRepresentation<V extends Vertex, E extends Edge<V>> {

	private Map<V, Integer> vYMap;
	private Map<V, Integer> vXMinMap;
	private Map<V, Integer> vXMaxMap;
	private Map<E, Integer> eXMap;
	private Map<E, Integer> eYMaxMap;
	private Map<E, Integer> eYMinMap;
	private Graph<V,E> graph;
	private BoyerMyrvoldPlanarity<V, E> boyerMyrvold;
	private Logger log = Logger.getLogger(VisibilityRepresentation.class);
	private List<V> stOrder;
	
	private List<E> switchedEdges;
	
	public VisibilityRepresentation(Graph<V,E> graph){
		this.graph = graph;
		vYMap = new HashMap<V, Integer>();
		vXMinMap = new HashMap<V, Integer>();
		vXMaxMap = new HashMap<V, Integer>();
		eXMap = new HashMap<E, Integer>();
		eYMaxMap = new HashMap<E, Integer>();
		eYMinMap = new HashMap<E, Integer>();
		boyerMyrvold = new BoyerMyrvoldPlanarity<V,E>();
		switchedEdges = new ArrayList<E>();
		constructVisibilityRepresentation();
		
	}
	
	private void constructVisibilityRepresentation(){
		//make graph biconnected by adding dummy edges - do this later when everything else seems to be fine
		//select edge (s,t) on the external face
		//find external face
		boolean directed = graph.isDirected();
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
			E e = graph.edgeBetween(v1, v2);
			if (e == null)
				e = graph.edgeBetween(v2, v1); 
			externalFace.add(e);
		}
		
		log.info("External face: " + externalFace);
		
		//compute plnar st-graph
		//this is like the original graph, but the edges should be in st-orientation
		//shallow copy doesn't do anything here
		//st graph variable is just to make the code easier to understand
		Graph<V,E> stGraph = graph; 
		constructSTGraph(s, t);
		log.info("ST graph: " + stGraph);
		PlanarFaces<V, E> planarFaces = new PlanarFaces<>(stGraph);
		try {
			planarFaces.formFaces(s, t);
		} catch (NotPlanarException e1) {
			e1.printStackTrace();
		}
		
		//create the dual planar st-graph G*
		STDualGraph<V, E> stDualGraph = new STDualGraph<V,E>(stGraph, externalFace, s, t);
		log.info("Dual graph: " + stDualGraph);
		
		//Compute the optimal topological ordering Tx = T (G∗)
		//Compute the optimal topological ordering Ty = T (G)
		Map<DualGraphVertex<V, E>, Integer> Tx = null;
		Map<V,Integer> Ty = null;
		try {
			Tx = TopologicalOrdering.calculateOrdering(stDualGraph);
			stGraph.setDirected(true);
			Ty = TopologicalOrdering.calculateOrdering(stGraph);
		} catch (CannotBeAppliedException e) {
			e.printStackTrace();
		}
		
		Map<V, List<E>> embedding = planarFaces.getPlanarEmbedding();
		log.info(embedding);
		
		int yMax = 0;
		// for all v in V do {Assigning positions to the horizontal vertex segments}
		for (V v : embedding.keySet()){
			//Let fl be the face to the left of the leftmost outgoing edge of v
			//Let fr be the face to the right of the rightmost outgoing edge of v
			
			log.info("current v " + v );
			E leftmostOutgoingEdge = null, rightmostOutgoingEdge;
			//the embedding contains a list of edges in the clockwise order for every edge
			//the leftmost would appear first in the embedding, rightmost the last
			//outgoing edges are those whose origin is the vertex in question
			for (E e : embedding.get(v)){
				if (e.getOrigin() == v){
					leftmostOutgoingEdge = e;
					break;
				}
			}
			
			log.info("leftmost outgoing edge: " + leftmostOutgoingEdge);
			if(leftmostOutgoingEdge == null){
				continue; //TODO t vertex has no outgoing edges
			}
			
			rightmostOutgoingEdge = embedding.get(v).get(embedding.get(v).size() - 1);
			
			log.info("rightmost outgoing edge: " + rightmostOutgoingEdge);
			
			List<E> fl = planarFaces.leftFaceOf(leftmostOutgoingEdge);
			List<E> fr = planarFaces.rightFaceOf(rightmostOutgoingEdge);
			log.info(fl);
			log.info(fr);
			
			//TODO further check dividing the external face
			if (isExternalFace(fl, externalFace)){
				if (stDualGraph.getsStar().contains(leftmostOutgoingEdge))
					fl =  stDualGraph.getsStar();
				else
					fl = stDualGraph.gettStar();
			}
			
			if (isExternalFace(fr, externalFace)){
				if (stDualGraph.getsStar().contains(rightmostOutgoingEdge))
					fr =  stDualGraph.getsStar();
				else
					fr = stDualGraph.gettStar();
			}
			
			
			log.info(fl);
			log.info(fr);
			
			DualGraphVertex<V, E> flVertex = stDualGraph.getVertexByContent(fl);
			DualGraphVertex<V, E> frVertex = stDualGraph.getVertexByContent(fr);
			
			
			
			//{fl and fr are vertices in the dual graph G*}
			//Γ(v).y = Ty(v)
			//Γ(v).xmin = Tx(fl)
			//Γ(v).xmax = Tx(fr) − 1
			vYMap.put(v, Ty.get(v));
			if (Ty.get(v) > yMax)
				yMax = Ty.get(v);
			
			log.info(flVertex);
			log.info(Tx);
			
			//TODO not sure if this should be allowed to happen
			//but sometimes fl and fr are the same faces and max if then lower than min
			//if is because of that
			vXMinMap.put(v, Tx.get(flVertex));
			if (fl != fr)
				vXMaxMap.put(v, Tx.get(frVertex) - 1);
			else
				vXMaxMap.put(v, Tx.get(frVertex) + 1);
		}
		
		//the vertex with the highest st-number is not added
		//since it has no outgoing edges
		//based on the example, it will now be added so that
		//it has the same x min and max as the first vertex (with the lowest st-number)
		//and y by one higher than the currently highest
		//TODO check this
		V v = stOrder.get(stOrder.size() - 1);
		vYMap.put(v, yMax + 1);
		vXMinMap.put(v,vXMinMap.get(stOrder.get(0)));
		vXMaxMap.put(v, vXMaxMap.get(stOrder.get(0)));
		
		//for all e = (u, v) ∈ E do {Assigning positions to the vertical edge segments}
		for (E e : stGraph.getEdges()){
			//Let fl be the face to the left of e {fl is a vertex in G∗}
			List<E> fl = planarFaces.leftFaceOf(e);
			if (fl.equals(stDualGraph.getExternalFace())){
				if (stDualGraph.getsStar().contains(e))
					fl =  stDualGraph.getsStar();
				else
					fl = stDualGraph.gettStar();
			}
			
			//check if vertex is external face
			
			DualGraphVertex<V, E> flVertex;
			if (isExternalFace(fl, externalFace)){
				//check if this edge belongs to sStar or tStar
				if (stDualGraph.getsStar().contains(e))
					flVertex = stDualGraph.getVertexByContent(stDualGraph.getsStar());
				else
					flVertex = stDualGraph.getVertexByContent(stDualGraph.gettStar());
			}
			else
				flVertex = stDualGraph.getVertexByContent(fl);
			
			
			//Γ(e).x = Tx(fl)
			//Γ(e).ymin = Ty(u)
			//Γ(e).ymax = Ty(v)
			eXMap.put(e, Tx.get(flVertex));
			eYMinMap.put(e, Ty.get(e.getOrigin()));
			eYMaxMap.put(e, Ty.get(e.getDestination()));
			
		}
		
		//Remove any added "dummy" edges
		//also get switched edges to previous state
		for (E e : switchedEdges){
			V origin = e.getOrigin();
			V destination = e.getDestination();
			e.setOrigin(destination);
			e.setDestination(origin);
		}
		graph.setDirected(directed);
		
		log.info("E x map " + eXMap);
		log.info("E y min map " + eYMinMap);
		log.info("E y max map " + eYMaxMap);
		log.info("V y map " + vYMap);
		log.info("V x min map " + vXMinMap);
		log.info("V x max map " + vXMaxMap);
	}
	
	private void constructSTGraph(V s, V t){
		STNumbering<V, E> stNumbering = new STNumbering<V,E>(graph, s, t);
		stOrder = stNumbering.getOrder();
		log.info("St order " + stNumbering.getOrder());
		for (E e : graph.getEdges()){
			V origin = e.getOrigin();
			V destination = e.getDestination();
			if (stNumbering.getNumbering().get(origin) > stNumbering.getNumbering().get(destination)){
				e.setOrigin(destination);
				e.setDestination(origin);
				switchedEdges.add(e);
			}
		}
	}
	
	public boolean isExternalFace(List<E> test, List<E> externalFace){
		if (test.size() != externalFace.size())
			return false;
		for (E e : test)
			if (!externalFace.contains(e))
				return false;
		return true;
	}

	/**
	 * @return the vYMap
	 */
	public Map<V, Integer> getvYMap() {
		return vYMap;
	}

	/**
	 * @param vYMap the vYMap to set
	 */
	public void setvYMap(Map<V, Integer> vYMap) {
		this.vYMap = vYMap;
	}

	/**
	 * @return the vXMinMap
	 */
	public Map<V, Integer> getvXMinMap() {
		return vXMinMap;
	}

	/**
	 * @param vXMinMap the vXMinMap to set
	 */
	public void setvXMinMap(Map<V, Integer> vXMinMap) {
		this.vXMinMap = vXMinMap;
	}

	/**
	 * @return the vXMaxMap
	 */
	public Map<V, Integer> getvXMaxMap() {
		return vXMaxMap;
	}

	/**
	 * @param vXMaxMap the vXMaxMap to set
	 */
	public void setvXMaxMap(Map<V, Integer> vXMaxMap) {
		this.vXMaxMap = vXMaxMap;
	}

	/**
	 * @return the eXMap
	 */
	public Map<E, Integer> geteXMap() {
		return eXMap;
	}

	/**
	 * @param eXMap the eXMap to set
	 */
	public void seteXMap(Map<E, Integer> eXMap) {
		this.eXMap = eXMap;
	}

	/**
	 * @return the eYMaxMap
	 */
	public Map<E, Integer> geteYMaxMap() {
		return eYMaxMap;
	}

	/**
	 * @param eYMaxMap the eYMaxMap to set
	 */
	public void seteYMaxMap(Map<E, Integer> eYMaxMap) {
		this.eYMaxMap = eYMaxMap;
	}

	/**
	 * @return the eYMinMap
	 */
	public Map<E, Integer> geteYMinMap() {
		return eYMinMap;
	}

	/**
	 * @param eYMinMap the eYMinMap to set
	 */
	public void seteYMinMap(Map<E, Integer> eYMinMap) {
		this.eYMinMap = eYMinMap;
	}
	
}
