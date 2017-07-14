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

	public Path(){
		
	}
	
	public Path(List<E> path, List<EdgeDirection> directions) {
		super();
		for (int i = 0; i < path.size(); i++)
			addEdge(path.get(i), directions.get(i));
		this.directions = directions;
	}
	
	public Path(List<E> path) {
		super();
		this.path = path;
	}
	
	
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
	
	public boolean containsEdge(E e){
		return path.contains(e);
	}
	
	public List<E> getPath() {
		return path;
	}

	public void setPath(List<E> path) {
		this.path = path;
	}
	
	public int size(){
		return path.size();
	}
	
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

	public List<EdgeDirection> getDirections() {
		return directions;
	}

	public void setDirections(List<EdgeDirection> directions) {
		this.directions = directions;
	}
	
	public boolean containsDuplicates(){
		Set<E> set = new HashSet<E>(path);
		return set.size() == path.size();
	}
	
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
//		String ret = "Path";
//		for (int i = 0; i < path.size(); i++){
//			if (directions.size() <= i || directions.get(i) == EdgeDirection.TO_DESTINATION){
//				if (i == 0)
//					ret += " " + path.get(i).getOrigin();
//				ret += " " + path.get(i).getDestination();
//				
//			}
//			else{
//				if (i == 0)
//					ret += " " + path.get(i).getDestination();
//				ret += " " + path.get(i).getOrigin();
//			}
//		}
//		return ret;
		return path.toString();
	}

	public List<V> getUniqueVertices() {
		return uniqueVertices;
	}


}
