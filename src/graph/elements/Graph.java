package graph.elements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Graph<V extends Vertex,E extends Edge>{
	
	private Collection<V> vertices;
	private Collection<E> edges;
	
	
	public List<E> getAllSelfLoopEdges(){
		List<E> ret = new ArrayList<E>();
		for (E e : edges){
			if (e.getSource() == e.getDestination())
				ret.add(e);
		}
		return ret;
	}
	
	public boolean hasSelfLoopEdges(){
		for (E e : edges){
			if (e.getSource() == e.getDestination())
				return true;
		}
		return false;
	}
	
	public boolean isSimple(){
		if (hasSelfLoopEdges())
			return false;
		Set<V> covered = new HashSet<V>(); 
		for (V v : vertices){
			if (covered.contains(v))
				return false;
			covered.add(v);
		}
		return false;
	}

}
