package graph.algorithms.drawing;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.properties.splitting.Block;
import graph.properties.splitting.Splitting;
import graph.util.Util;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ConvexDrawing<V extends Vertex, E extends Edge<V>> {

	private Graph<V,E> graph;
	private Class<?> edgeClass;
	private Random rand;
	private Splitting<V, E> splitting;

	public ConvexDrawing(Graph<V,E> graph){
		this.graph = graph;
		edgeClass = graph.getEdges().get(0).getClass();
		rand = new Random();
		splitting = new Splitting<V,E>();
	}

	@SuppressWarnings("unchecked")
	public Map<V, Point2D> execute(List<V> S, List<V> Sstar){
		Map<V, Point2D> ret = new HashMap<V, Point2D>();

		//step one - for each vertex v of degree two not on S
		//replace v together with two edges incident to v with a 
		//single edge joining the vertices adjacent to v

		//leave the original graph intact - make a copy to start with
		Graph<V,E> gPrim = Util.copyGraph(graph);
		//store deleted vertices in order to position them later
		List<V> deleted = new ArrayList<V>();


		Iterator<V> iter = gPrim.getVertices().iterator();
		while (iter.hasNext()){
			V v = iter.next();
			if (!S.contains(v) && graph.vertexDegree(v) == 2){
				iter.remove();
				List<E> edges = graph.adjacentEdges(v);
				E e1 = edges.get(0);
				E e2 = edges.get(1);
				gPrim.getEdges().remove(e1);
				gPrim.getEdges().remove(e2);
				V adjV1 = e1.getOrigin() == v ? e1.getDestination() : e1.getOrigin();
				V adjV2 = e2.getOrigin() == v ? e2.getDestination() : e2.getOrigin();
				E newEdge = Util.createEdge(adjV1, adjV2, edgeClass);
				gPrim.addEdge(newEdge);
				deleted.add(v);
			}
		}

		//step 2 - call Draw on (G', S, S*) to extend S* into a convex drawing of G'



		return ret;
	}

	//when calling draw for the first time, positions should already 
	//contain positions should already contain position of vertices on S
	
	private void draw(Graph<V,E> G, List<V> S, List<V> Sstar, Map<V,Point2D> positions){
		if (G.getVertices().size() <= 3)
			return;

		V v = arbitraryApex(Sstar);

		List<E> vEdges = graph.adjacentEdges(v);
		List<V> connectedVerties = new ArrayList<V>();


		//find vertices v1 and vp+1 as vertices on S adjacent to v
		V v1, vp_1;
		int index = S.indexOf(v);
		int next = (index + 1) % S.size();
		int previous = index - 1;
		if (previous == -1)
			previous = S.size() - 1;

		v1 = S.get(Math.min(next, previous));
		vp_1 = S.get(Math.max(next, previous));

		E currentEdge = G.edgeBetween(v, v1);
		V currentVertex = v1;

		while (connectedVerties.size() < vEdges.size()){
			connectedVerties.add(currentVertex);
			V other = currentEdge.getOrigin() == currentVertex ? currentEdge.getDestination() : currentEdge.getOrigin();

			//find next edge
			List<E> allEdges = G.adjacentEdges(other);
			for (E e : allEdges)
				if (vEdges.contains(e) && e != currentEdge){
					currentEdge = e;
					break;
				}
			currentVertex = other;
		}

		//remove v
		G.removeVertex(v);

		//divide G into blocks and find cut vertices
		List<V> cutVertices = splitting.findAllCutVertices(graph);
		List<Block<V,E>> blocks = splitting.findAllBlocks(G, cutVertices);

		List<V> vis = new ArrayList<V>();
		vis.add(v1);
		vis.addAll(cutVertices);
		vis.add(vp_1);

		List<V> si = new ArrayList<V>();
		for (Block<V,E> bi : blocks){
			
			//TODO find Si
			//find Si
			
			List<V> siApexes = new ArrayList<V>();
			
			//find vertices among v1, v2, .. vp_1 which the block contains
			V vBorder1 = null, vBorder2 = null;
			for (V border : vis){
				if (bi.getVertices().contains(border)){
					if (vBorder1 == null)
						vBorder1 = border;
					else if (vBorder2 == null){
						vBorder2 = border;
						break;
					}
				}
			}
			
			int index1 = S.indexOf(vBorder1);
			int index2 = S.indexOf(vBorder2);
			
			for (int i = index1; i <= index2; i++){
				si.add(S.get(i));
			}
			
			index1 = connectedVerties.indexOf(vBorder1);
			index2 = connectedVerties.indexOf(vBorder2);
			
			siApexes.add(vBorder1);
			for (int i = index2 - 1; i > index1; i--){
				si.add(connectedVerties.get(i));
				siApexes.add(connectedVerties.get(i));
			}
			
			siApexes.add(vBorder2);
		}
		
		//Step 2 - Draw each block bi convex
		
		//Step 2.1 Determine Si*
		//when S* was found, positions of its vertices were found as well
		//so we only need to find position of vertices not on S
		//Locate the vertices in V(Si) - V(S) in the interios of the triangle
		//v*v1*vi+1 (cut vertices + 2 connected to v at the beginning and end)
		//in such way that the vertices adjacent to v are apices of convex polyxon Si*
		//and the others are on thr straight line segments
		
		//Step 2.2
		//recursively call procedure Draw(bi,Si, Si*)

	}

	private V arbitraryApex(List<V> Sstar){
		int i = rand.nextInt(Sstar.size());
		return Sstar.get(i);

	}
	
	private List<List<V>> findExtendableFacialCycles(){
		
	}

}
