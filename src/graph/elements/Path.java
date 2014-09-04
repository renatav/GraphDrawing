package graph.elements;

import java.util.ArrayList;
import java.util.List;

public class Path<V extends Vertex, E extends Edge<V>> {
	
	private List<E> path = new ArrayList<E>();

	public Path(List<E> path) {
		super();
		this.path = path;
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

	@Override
	public String toString() {
		return "Path [" + path + "]";
	}
	
	

}
