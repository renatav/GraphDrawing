package graph.elements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Path<V extends Vertex, E extends Edge<V>> {
	
	private List<E> path = new ArrayList<E>();
	private List<EdgeDirection> directions = new ArrayList<EdgeDirection>();

	public Path(){
		
	}
	
	public Path(List<E> path, List<EdgeDirection> directions) {
		super();
		this.path = path;
		this.directions = directions;
	}
	
	public Path(List<E> path) {
		super();
		this.path = path;
	}
	
	
	
	public void addEdge(E e, EdgeDirection direction){
		path.add(e);
		directions.add(direction);
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
	
	@Override
	public String toString() {
		String ret = "Path";
		for (int i = 0; i < path.size(); i++){
			if (directions.size() <= i || directions.get(i) == EdgeDirection.TO_DESTINATION){
				if (i == 0)
					ret += " " + path.get(i).getOrigin();
				ret += " " + path.get(i).getDestination();
				
			}
			else{
				if (i == 0)
					ret += " " + path.get(i).getDestination();
				ret += " " + path.get(i).getOrigin();
			}
		}
		return ret;
	}


}
