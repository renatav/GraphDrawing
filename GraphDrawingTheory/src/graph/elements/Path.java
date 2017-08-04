package graph.elements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a path (list of edges)
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type 
 */
public class Path<V extends Vertex, E extends Edge<V>> {
	
	/**
	 * Path, consisting of a list of edges
	 */
	private List<E> path = new ArrayList<E>();
	/**
	 * A list of edge directions. i-th member of this list determines the direction
	 * of the i-th edge of the path
	 */
	private List<EdgeDirection> directions = new ArrayList<EdgeDirection>();
	/**
	 * Unique vertices on the path
	 */
	private List<V> uniqueVertices = new ArrayList<V>();

	/**
	 * Creates an empty path
	 */
	public Path(){
		
	}
	
	/**
	 * Creates a path given edges and the direction in which they are traversed
	 * @param path Edges forming the paths
	 * @param directions Directions in which the edges should be traversed
	 */
	public Path(List<E> path, List<EdgeDirection> directions) {
		super();
		for (int i = 0; i < path.size(); i++)
			addEdge(path.get(i), directions.get(i));
		this.directions = directions;
	}
	
	/**
	 * Creates a path given the edges it contains. Directions are left
	 * empty.
	 * @param path Edges forming the path
	 */
	public Path(List<E> path) {
		super();
		this.path = path;
	}
	
	
	/**
	 * Adds an edge to the path
	 * @param e Edge
	 * @param direction Direction in which the edge should be traversed
	 */
	public void addEdge(E e, EdgeDirection direction){
		path.add(e);
		directions.add(direction);
		V v1 = e.getDestination();
		V v2 = e.getOrigin();
		if (!uniqueVertices.contains(v1))
			uniqueVertices.add(v1);
		if (!uniqueVertices.contains(v2))
			uniqueVertices.add(v2);
	}
	
	/**
	 * Checks if the path contains a certain edge
	 * @param e Edge
	 * @return {@code true} if the path contains {@code e}, {@code false} otherwise
	 */
	public boolean containsEdge(E e){
		return path.contains(e);
	}
	
	/**
	 * @return Edges belonging to the path
	 */
	public List<E> getPath() {
		return path;
	}

	/**
	 * @param path Edges of the path to set
	 */
	public void setPath(List<E> path) {
		this.path = path;
	}
	
	/**
	 * Size of the path - number of edges belonging to it
	 * @return Size of the path
	 */
	public int size(){
		return path.size();
	}
	
	/**
	 * 
	 * @return {@code true} if the path is cyclic, {@code false} otherwise
	 */
	public boolean isCyclic(){
		V first;
		V last;
		if (directions.get(0) == EdgeDirection.TO_DESTINATION)
			first = path.get(0).getOrigin();
		else
			first = path.get(0).getDestination();
		
		if (directions.get(path.size() - 1) == EdgeDirection.TO_DESTINATION)
			last = path.get(path.size() - 1).getOrigin();
		else
			last = path.get(path.size() - 1).getDestination();
		
		return first == last;
	}

	/**
	 * @return Directions in which the edges should be traverse
	 */
	public List<EdgeDirection> getDirections() {
		return directions;
	}

	/**
	 * @param directions Directions to set
	 */
	public void setDirections(List<EdgeDirection> directions) {
		this.directions = directions;
	}
	
	/**
	 * Checks if there is at least one edge which occurs multiple times in
	 * the path's list of edges 
	 * @return {@code true} if the path contains duplicates, {@code false} otherwise
	 */
	public boolean containsDuplicates(){
		Set<E> set = new HashSet<E>(path);
		return set.size() == path.size();
	}
	
	/**
	 * Finds all vertices belonging to the path
	 * @return All vertices on the path
	 */
	public List<V> pathVertices(){
		List<V> ret = new ArrayList<V>();
		
		for (int i = 0; i < path.size(); i++){
			if (directions.get(i) == EdgeDirection.TO_DESTINATION){
				if (i == 0)
					ret.add(path.get(i).getOrigin());
				ret.add(path.get(i).getDestination());
				
			}
			else{
				if (i == 0)
					ret.add(path.get(i).getDestination());
				ret.add(path.get(i).getOrigin());
			}
		}
		return ret;
	}
	
	/**
	 * List all vertices on the path, only including one vertex once, even if
	 * it is contained multiple times
	 * @return A list of unique vertices
	 */
	public List<V> pathVertivesWithoutDuplicates(){
		List<V> ret = new ArrayList<V>();
		
		for (int i = 0; i < path.size(); i++){
			if (directions.get(i) == EdgeDirection.TO_DESTINATION){
				if (i == 0){
					if (!ret.contains(path.get(i).getOrigin()))
						ret.add(path.get(i).getOrigin());
				}
				if (!ret.contains(path.get(i).getDestination()))
						ret.add(path.get(i).getDestination());
				
			}
			else{
				if (i == 0){
					if (!ret.contains(path.get(i).getDestination()))
						ret.add(path.get(i).getDestination());
				}
				if (!ret.contains(path.get(i).getOrigin()))
					ret.add(path.get(i).getOrigin());
			}
		}
		return ret;
	}
	
	@Override
	public String toString() {
		String ret = "(";
		for (int i = 0; i < path.size(); i++){
			if (directions.size() <= i || directions.get(i) == EdgeDirection.TO_DESTINATION){
				if (i == 0)
					ret += " " + path.get(i).getOrigin();
				ret += "-" + path.get(i).getDestination();
				
			}
			else{
				if (i == 0)
					ret += " " + path.get(i).getDestination();
				ret += "-" + path.get(i).getOrigin();
			}
		}
		ret += ")";
		return ret;
	}

	/**
	 * @return A list of unique vertices (no duplicates)
	 */
	public List<V> getUniqueVertices() {
		return uniqueVertices;
	}


}
