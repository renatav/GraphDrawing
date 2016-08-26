package graph.algorithms.planarity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import graph.algorithms.drawing.NotPlanarException;
import graph.elements.Edge;
import graph.elements.EdgeDirection;
import graph.elements.Graph;
import graph.elements.Vertex;

/**
 * For each edge, there should be a right and a left face
 * If (v,w) is an edge, the left face is the one that contains (v,w) and
 * the right face is the one that contains (w,v)
 * @author xx
 *
 * @param <V>
 * @param <E>
 */
public class PlanarFaces<V extends Vertex, E extends Edge<V>> {
	
	/**
	 * Map contains an edge and a list of faces
	 * The list will only contain two faces
	 * The first one being the left one
	 * and the second being the right one
	 */
	private Map<E, List<List<E>>> edgeFacesMap;
	/**
	 * List of all faces. Nothing new compared
	 * to the map above, but this makes it more practical
	 * when, for example, a face containing certain vertex needs to be found
	 */
	private List<List<E>> allFaces;
	private Graph<V,E> graph;
	
	private Logger log = Logger.getLogger(PlanarFaces.class);
	
	public PlanarFaces(Graph<V,E> graph){
		this.graph = graph;
	}
	
	public void formFaces() throws NotPlanarException{
		//Select some edge (v,w)
		//go from v to w
		//find the closest edge in A(w) to (v,w) in the clockwise direction
		//and keep going until we return to v
		//Do this until all edges are traversed twice, once in every direction
		
		log.info("Form faces");
		edgeFacesMap = new HashMap<E, List<List<E>>>();
		allFaces = new ArrayList<List<E>>();
		
		Embedding<V,E> embedding = PlanarEmbedding.emedGraph(graph);
		
		for (E e : graph.getEdges())
			edgeFacesMap.put(e, new ArrayList<List<E>>());
		
		
		List<E> toDestinationTraversed = new ArrayList<E>();
		List<E> toOriginTraversed = new ArrayList<E>();
		
		List<E> allEdges = new ArrayList<E>();
		//initially add all edges, and as an edge is traversed
		//from origin to destination, remove it
		allEdges.addAll(graph.getEdges());
		
		//to destination is from a vertex with lower st-number to 
		//a vertex with higher st-number
		//to origin is the other way around
		
		
		Map<V,Integer> stNumbering = embedding.getStNumbering();
		Map<V,List<E>> planarEmbedding = embedding.getEmbedding();
		
		log.info("st numbering " + stNumbering);
		log.info("Embedding: " + embedding);
		
		int numOfEdges = allEdges.size();
		
		
		List<EdgeDirection> edgeDirections = new ArrayList<EdgeDirection>();
		List<V> orderedVertices = new ArrayList<V>(2);
		
		while (toDestinationTraversed.size() < numOfEdges && toOriginTraversed.size() < numOfEdges){
			
			//creating a new list every time because
			//different references are needed
			List<E> faceEdges = new ArrayList<E>();
			edgeDirections.clear();
			
			E edge = allEdges.get(0);
			toDestinationTraversed.add(edge);
			allEdges.remove(0);
			
			
			getOrderedEdgeVertices(stNumbering, edge, orderedVertices);
			V v = orderedVertices.get(0);
			V w = orderedVertices.get(1);
			V first = v;
			
			log.info("staring edge " + edge);
			log.info("v " + v);
			log.info("w " + w);
			
			faceEdges.add(edge);
			edgeDirections.add(EdgeDirection.TO_DESTINATION);
			
			while (w != first){
				List<E> wEdges = planarEmbedding.get(w);
				//find the next edge to take in vertex w
				int edgeIndex = wEdges.indexOf(edge);
				edgeIndex = wEdges.size() - 1 == edgeIndex ? 0 : edgeIndex + 1;
				edge = wEdges.get(edgeIndex);
				faceEdges.add(edge);
				
				log.info("Edge " + edge);
				
				getOrderedEdgeVertices(stNumbering, edge, orderedVertices);
				V v1 = orderedVertices.get(0);
				V v2 = orderedVertices.get(1);
				if (w == v1){
					toDestinationTraversed.add(edge);
					edgeDirections.add(EdgeDirection.TO_DESTINATION);
					w = v2;
					allEdges.remove(edge);
				}
				else{
					toOriginTraversed.add(edge);
					edgeDirections.add(EdgeDirection.TO_ORIGIN);
					w = v1;
				}
				log.info("next w " + w);
			}
			
			log.info("face edges " + faceEdges);
			log.info("directions " + edgeDirections);
			
			allFaces.add(faceEdges);
			
			for (int i = 0; i < faceEdges.size(); i++){
				E currentEdge = faceEdges.get(i);
				EdgeDirection direction = edgeDirections.get(i);
				if (direction == EdgeDirection.TO_ORIGIN)
					edgeFacesMap.get(currentEdge).add(faceEdges);
				else
					edgeFacesMap.get(currentEdge).add(0, faceEdges);
			}
			
		}
		
		log.info(edgeFacesMap);
	}
	
	private void getOrderedEdgeVertices(Map<V,Integer> stNumbering, E edge, List<V> orderedVetices){
		V v1 = edge.getOrigin();
		V v2 = edge.getDestination();
		orderedVetices.clear();
		if (stNumbering.get(v1) < stNumbering.get(v2)){
			orderedVetices.add(0, v1);
			orderedVetices.add(1, v2);
		}
		else{
			orderedVetices.add(0, v2);
			orderedVetices.add(1, v1);
		}
	}
	
	/**
	 * Left face of the given edge
	 * @param edge
	 * @return left face
	 */
	public List<E> leftFaceOf(E edge){
		return edgeFacesMap.get(edge).get(0);
	}

	/**
	 * Right face of the given edge
	 * @param edge
	 * @return right face
	 */
	public List<E> rightFaceOf(E edge){
		return edgeFacesMap.get(edge).get(1);
	}

	/**
	 * @return the allFaces
	 */
	public List<List<E>> getAllFaces() {
		return allFaces;
	}

	/**
	 * @param allFaces the allFaces to set
	 */
	public void setAllFaces(List<List<E>> allFaces) {
		this.allFaces = allFaces;
	}
	
	

}
