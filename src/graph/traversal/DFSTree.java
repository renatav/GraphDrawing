package graph.traversal;

import graph.elements.Edge;
import graph.elements.Vertex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DFSTree<V extends Vertex, E extends Edge<V>> {

	private V root;
	private Map<V, Integer> verticesWithIndexes;
	private List<E> treeEdges;
	private List<E> backEdges;
	
	public DFSTree(V root){
		this.root = root;
		this.verticesWithIndexes = new HashMap<V, Integer>();
		this.treeEdges = new ArrayList<E>();
		this.backEdges = new ArrayList<E>();
			
	}
	
	public void formBackEdges(List<E> allEdges){
		for (E e : allEdges)
			if (!treeEdges.contains(e))
				backEdges.add(e);
	}

	public V getRoot() {
		return root;
	}

	public void setRoot(V root) {
		this.root = root;
	}

	public void addVertex(V v, int index){
		verticesWithIndexes.put(v, index);
	}
	
	public void addTreeEdge(E e){
		treeEdges.add(e);
	}
	
	public Set<V> getVertices() {
		return verticesWithIndexes.keySet();
	}


	public Map<V, Integer> getVerticesWithIndexes() {
		return verticesWithIndexes;
	}

	public void setVerticesWithIndexes(Map<V, Integer> verticesWithIndexes) {
		this.verticesWithIndexes = verticesWithIndexes;
	}

	public List<E> getTreeEdges() {
		return treeEdges;
	}

	public void setTreeEdges(List<E> treeEdges) {
		this.treeEdges = treeEdges;
	}

	public List<E> getBackEdges() {
		return backEdges;
	}

	public void setBackEdges(List<E> backEdges) {
		this.backEdges = backEdges;
	}

	@Override
	public String toString() {
		return "DFSTree [root=" + root + ", verticesWithIndexes="
				+ verticesWithIndexes + ", treeEdges=" + treeEdges
				+ ", backEdges=" + backEdges + "]";
	}
	
	
	
}
