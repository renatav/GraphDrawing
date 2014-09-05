package graph.drawing;

import graph.elements.Edge;
import graph.elements.Vertex;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A drawing Γ of a graph G = (V, E) is  a mapping of each vertex v in V to a
distinct point Γ(v) and of each edge e = (u, v) in E to a simple open Jordan curve Γ(e),
represented here with a list of its nodes' positions,
which has Γ(u) and Γ(v) as its endpoints. 
 * @author xxx
 *
 * @param <V>
 * @param <E>
 */
public class Drawing<V extends Vertex, E extends Edge<V>> {

	private Map<V, Point2D> vertexMappings;
	private Map<E, List<Point2D>> edgeMappings;
	
	
	public Drawing(Map<V, Point2D> vertexMappings,
			Map<E, List<Point2D>> edgeMappings) {
		super();
		this.vertexMappings = vertexMappings;
		this.edgeMappings = edgeMappings;
	}
	
	public Drawing(){
		vertexMappings = new HashMap<V, Point2D>();
		edgeMappings = new HashMap<E, List<Point2D>>();
	}

	public void setVertexPosition(V v, Point2D pos){
		vertexMappings.put(v, pos);
	}
	
	public void setEdgePosition(E e, List<Point2D> nodes){
		edgeMappings.put(e, nodes);
	}
	
	public Map<V, Point2D> getVertexMappings() {
		return vertexMappings;
	}

	public void setVertexMappings(Map<V, Point2D> vertexMappings) {
		this.vertexMappings = vertexMappings;
	}

	public Map<E, List<Point2D>> getEdgeMappings() {
		return edgeMappings;
	}

	public void setEdgeMappings(Map<E, List<Point2D>> edgeMappings) {
		this.edgeMappings = edgeMappings;
	}
	
	
	
	
}
