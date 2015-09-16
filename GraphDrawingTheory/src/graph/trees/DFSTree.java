package graph.trees;

import graph.elements.Edge;
import graph.elements.EdgeDirection;
import graph.elements.Graph;
import graph.elements.Path;
import graph.elements.Vertex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DFSTree<V extends Vertex, E extends Edge<V>> extends Graph<V, E>{

	private V root;
	private Map<V, Integer> verticesWithIndexes;
	private List<E> treeEdges;
	private List<E> backEdges;

	public DFSTree(V root){
		super();
		this.root = root;
		this.verticesWithIndexes = new HashMap<V, Integer>();
		this.treeEdges = new ArrayList<E>();
		this.backEdges = new ArrayList<E>();
		this.directed = false;

	}
	
	public DFSTree(V root, int[] numbering, List<E> treeEdges, List<E> backEdges, List<V> vertices){
		this.root = root;
		this.verticesWithIndexes = new HashMap<V, Integer>();
		for (V v : vertices)
			verticesWithIndexes.put(v, numbering[vertices.indexOf(v)]);
		this.treeEdges = treeEdges;
		this.backEdges = backEdges;
		
	}



	public void formBackEdges(List<E> allEdges){
		for (E e : allEdges)
			if (!treeEdges.contains(e))
				backEdges.add(e);
	}

	public int getIndex(V  v){
		return verticesWithIndexes.get(v);
	}


	public void addVertex(V v, int index){
		super.addVertex(v);
		verticesWithIndexes.put(v, index);
	}

	@SuppressWarnings("unchecked")
	public void addTreeEdge(E e){
		treeEdges.add(e);
		super.addEdge(e);
	}


	@Override	
	public LinkedList<E> allEdges(V v){
		LinkedList<E> ret = new LinkedList<E>();
		List<E> all = new ArrayList<>(treeEdges);
		all.addAll(backEdges);
		for (E e : all)
			if (e.getDestination() == v || e.getOrigin() == v)
				ret.add(e);
		return ret;
	}
	/**
	 * Finds the incoming edge (starting from an ancestor of v (edge with lower index) and ending in v)
	 * @param v
	 * @return
	 */
	public E incomingEdge(V v){

		V other;
		for (E e : allEdges(v)){
			if (treeEdges.contains(e)){
				other = e.getOrigin() == v ? e.getDestination() : e.getOrigin();
				if (compareVertices(v, other) == 1) //index of v is higher
					return e;
			}
		}
		return null;
	}

	public List<E> allIncomingBackEdges(V v){

		List<E> ret = new ArrayList<E>();

		V other;
		for (E e : backEdges){
			if (e.getOrigin() == v || e.getDestination() == v){
				other = e.getOrigin() == v ? e.getDestination() : e.getOrigin();
				if (compareVertices(v, other) == -1) //index of v is lower
					ret.add(e);
			}
		}
		return ret;
	}

	/**
	 * Finds all edges starting from v and ending in a descendant of v (index of v is lower)
	 * @param v
	 * @return
	 */
	public List<E> allOutgoingTreeEdges(V v){

		List<E> ret = new ArrayList<E>();
		V other;
		for (E e : allEdges(v)){
			if (treeEdges.contains(e)){
				other = e.getOrigin() == v ? e.getDestination() : e.getOrigin();
				if (compareVertices(v, other) == -1) //index of v is lower
					if (!ret.contains(e))
						ret.add(e);
			}
		}
		return ret;
	}

	public List<E> allOutgoingEdges(V v){
		List<E> ret = new ArrayList<E>();
		V other;
		for (E e : allEdges(v)){
			other = e.getOrigin() == v ? e.getDestination() : e.getOrigin();
			if (treeEdges.contains(e)){
				if (compareVertices(v, other) == -1){ //index of v is lower
					if (!ret.contains(e))
						ret.add(e);
				}
			}
			else{
				if (compareVertices(v, other) == 1){
					if (!ret.contains(e)){
						ret.add(e);
					}
				}
			}
		}
		return ret;
	}

	/**
	 * Returns all vertexes that are descendants of vertex v in the tree
	 * @param v
	 * @return
	 */
	public List<V> allDescendantsOf(V v, boolean includeVertex){


		List<V> ret = new ArrayList<V>();
		allDescendantsOf(v, ret);
		if (includeVertex)
			ret.add(0, v);

		return ret;

	}


	private void allDescendantsOf(V current, List<V> descendants){

		V other;
		for (E e : allOutgoingTreeEdges(current)){
			other = e.getOrigin() == current ? e.getDestination() : e.getOrigin();
			if (verticesWithIndexes.get(current) < verticesWithIndexes.get(other) && !descendants.contains(other) ){
				descendants.add(other);
				allDescendantsOf(other, descendants);
			}
		}
	}

	public List<V> directDescendantsOf(V v){
		List<V> ret = new ArrayList<V>();
		V other;
		for (E e : allOutgoingTreeEdges(v)){
			other = e.getOrigin() == v ? e.getDestination() : e.getOrigin();
			if (verticesWithIndexes.get(v) < verticesWithIndexes.get(other) && !ret.contains(other) ){
				ret.add(other);
			}
		}
		return ret;
	}

	/**
	 * Compares indexes of vertices v1 and v2
	 * @param v1
	 * @param v2
	 * @return 1 if v1 has higher index
	 * 		    0 if indexes are the same
	 * 			-1 if v2 has higher index
	 */
	private int compareVertices(V v1, V v2){
		int index1 = verticesWithIndexes.get(v1);
		int index2 = verticesWithIndexes.get(v2);

		if (index1 > index2)
			return 1;
		else if (index1 == index2)
			return 0;
		else
			return -1;
	}
	
	
	/**The lowpoint of a vertex v, denoted by lowpt(v), is the lowest DFS index of
	 *an ancestor of v reachable through a back edge from a descendant of v
	 * @param v
	 * @return lowpt of v if there are back edges leading form descendant to ancestor of v, -1 otherwise
	 */
	public int lowpt(V v){
		List<V> descendants = allDescendantsOf(v, true); 
		int currentIndex = getIndex(v);
		Integer lowest = null;
		for (E back : getBackEdges()){

			if (descendants.contains(back.getDestination()) || descendants.contains(back.getOrigin())){
				int index;
				if (descendants.contains(back.getDestination()))
					index = getIndex(back.getOrigin());
				else
					index = getIndex(back.getDestination());
				if (index < currentIndex)
					if (lowest == null || lowest > index)
						lowest = index;
			}
		}
		if (lowest == null)
			return -1;
		return lowest;

	}
	
	/**
	 * The lowpoint of a vertex v, denoted by lowpt(v), is the lowest DFS index of
	 * an ancestor of v reachable through a back edge from a descendant of v
	 * lowpt1 is the lowest index, lowpt2 is the second lowest 
	 * @param v
	 * @return
	 */
	public int[] lowpts(V v){
		Integer lowpt1 = null;
		Integer lowpt2 = null; 
		
		List<V> descendants = allDescendantsOf(v, true); 
		int currentIndex = getIndex(v);
		for (E back : getBackEdges()){

			if (descendants.contains(back.getDestination()) || descendants.contains(back.getOrigin())){
				
				int index;
				if (descendants.contains(back.getDestination()))
					index = getIndex(back.getOrigin());
				else
					index = getIndex(back.getDestination());
				
				if (index < currentIndex){
					if (lowpt1 == null || lowpt1 > index){
						if (lowpt1 != null)
							lowpt2 = lowpt1;
						lowpt1 = index;
					}
					else if (lowpt1 != null && index > lowpt1){
						//set lowpt2
						if (lowpt2 == null || lowpt2 > index){
							lowpt2 = index;
						}
					}
				}
			}
		}
		
		if (lowpt1 == null){
			lowpt1 = currentIndex;
			lowpt2 = currentIndex;
		}
		else if (lowpt2 == null){
			lowpt2 = currentIndex;
		}
			
		return new int[]{lowpt1, lowpt2};
		
	}


	/**Hhighpoint of a vertex is the highest DFS index of an ancestor of v
	 * reachable through a back edge from a descendant of v
	 * @param v
	 * @return highpt of v if there are back edges leading form descendant to ancestor of v, -1 otherwise
	 */
	public int highpt(V v){
		List<V> descendants = allDescendantsOf(v, false);
		int currentIndex = getIndex(v);
		Integer highest = null;
		for (E back : getBackEdges()){

			if (descendants.contains(back.getDestination()) || descendants.contains(back.getOrigin())){
				int index;
				if (descendants.contains(back.getDestination()))
					index = getIndex(back.getOrigin());
				else
					index = getIndex(back.getDestination());
				if (index < currentIndex)
					if (highest == null || highest < index)
						highest = index;
			}
		}
		if (highest == null)
			return -1;
		return highest;

	}
	
	
	/**
	 * The lowpt of an edge (v, w) is its lowest
	 * return point (or w if none exists).
	 * @param v
	 * @return
	 */
	public int lowpt(E e){

		V origin, destination;

		if (getIndex(e.getOrigin()) < getIndex(e.getDestination())){
			origin = e.getOrigin();
			destination = e.getDestination();
		}
		else {
			destination = e.getOrigin();
			origin = e.getDestination();
		}

		if (backEdges.contains(e)){
			return getIndex(origin);
		}

		//else tree edge

		int lowpt = lowpt(destination);
		if (lowpt != -1)
			return lowpt;
		return getIndex(destination);

	}


	public int highpt(E e){

		V origin, destination;

		if (getIndex(e.getOrigin()) < getIndex(e.getDestination())){
			origin = e.getOrigin();
			destination = e.getDestination();
		}
		else {
			destination = e.getOrigin();
			origin = e.getDestination();
		}

		if (backEdges.contains(e)){
			return getIndex(destination);
		}

		//else tree edge

		int highpt = highpt(destination);
		if (highpt != -1)
			return highpt;
		return getIndex(origin);

	}

	/**
	 * Given a tree edge e = (u, v), its returning edges are those back
	 * edges that from a descendant of v (included v itself ) go to an ancestor of u different from u
	 * itself.
	 * @param e
	 * @return
	 */
	public List<E> returningEdges(E e){


		List<E> ret = new ArrayList<E>();

		if (backEdges.contains(e))
			ret.add(e);
		else{

			V origin, destination;

			if (getIndex(e.getOrigin()) < getIndex(e.getDestination())){
				origin = e.getOrigin();
				destination = e.getDestination();
			}
			else {

				destination = e.getOrigin();
				origin = e.getDestination();
			}


			int originIndex = getIndex(origin);

			List<V> descendants = allDescendantsOf(destination, true); 

			for (E back : backEdges){
				if (descendants.contains(back.getDestination()) || descendants.contains(back.getOrigin())){
					int index;
					if (descendants.contains(back.getDestination()))
						index = getIndex(back.getOrigin());
					else
						index = getIndex(back.getDestination());


					//ancestor of origin
					if (index < originIndex)
						ret.add(back);
				}
			}
		}
		return ret;

	}

	public E getHighestReturningEdge(E e){
		E highestEdge = null;
		int highestPoint = -1;

		for (E returningEdge : returningEdges(e)){

			int currentPoint = Math.min(getIndex(returningEdge.getOrigin()), getIndex(returningEdge.getDestination()));
			if (currentPoint > highestPoint){
				highestPoint = currentPoint;
				highestEdge = returningEdge;
			}
		}

		return highestEdge;

	}

	/**
	 * Vertex directly adjacent to v by a back edge that has the lowest index of all
	 * such vertices
	 * @param v Vertex
	 * @return least ancestor if one exists, null otherwise
	 */
	public V leastAncestor(V v){
		
		V leastAncestor = null;

		for (E backEdge : backEdges){

			V other = null;

			if (backEdge.getOrigin() == v)
				other = backEdge.getDestination();
			else if (backEdge.getDestination() == v)
				other = backEdge.getOrigin();

			//if back edges goes from v to other
			if (other != null && verticesWithIndexes.get(v) > verticesWithIndexes.get(other)){
				if (leastAncestor == null || getIndex(other) < getIndex(leastAncestor))
					leastAncestor = other;
			}
				
		}

		return leastAncestor;
	}

	
	public List<E> treeEdgesBetween(V first, V target){
		System.out.println("path between: " + first  + ", " + target);
		List<Path<V,E>> allPaths = findAllPathsDFS(first, target);
		if (allPaths.size() == 0)
			return null;
		return allPaths.get(0).getPath();
	}

	public List<V> treePathBetween(V first, V target){
		Path<V,E> path = findAllPathsDFS(first, target).get(0);
		List<V> ret = path.pathVertices();

		Collections.sort(ret, new Comparator<V>(){

			@Override
			public int compare(V o1, V o2) {

				if (verticesWithIndexes.get(o1) > verticesWithIndexes.get(o2))
					return 1;
				else if  (verticesWithIndexes.get(o1) < verticesWithIndexes.get(o2))
					return -1;
				else
					return 0;
			}
			
		});
		
		return ret;

	}

	public List<Path<V, E>> findAllPathsDFS(V first, V target){
		List<Path<V,E>> paths = new ArrayList<Path<V,E>>();
		findAllPathsDFS(new ArrayList<E>(), new ArrayList<EdgeDirection>(),  paths, first, first, target);
		return paths;
	}

	private void findAllPathsDFS(List<E> visited, List<EdgeDirection> directions, List<Path<V, E>> paths, 
			V currentVertex, V start, V end) {        

		if (currentVertex.equals(end)) { 
			if (!(currentVertex.equals(start) && visited.size() == 0)){
				paths.add(new Path<V, E>(visited, directions));
				return;
			}
		}
		List<E> edges = allOutgoingTreeEdges(currentVertex);

		for (E e : edges) {
			if (visited.contains(e)) {
				continue;
			}

			List<E> temp = new ArrayList<E>();
			List<EdgeDirection> directionsTemp = new ArrayList<EdgeDirection>();
			temp.addAll(visited);
			temp.add(e);
			directionsTemp.addAll(directions);
			V nextVert;
			if (currentVertex == e.getOrigin()){
				nextVert = e.getDestination();
				directionsTemp.add(EdgeDirection.TO_DESTINATION);
			}
			else{
				nextVert = e.getOrigin();
				directionsTemp.add(EdgeDirection.TO_ORIGIN);
			}

			findAllPathsDFS(temp, directionsTemp, paths, nextVert, start, end);
		}
	}

	public V getRoot() {
		return root;
	}

	public void setRoot(V root) {
		this.root = root;
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

	public List<E> getAllEdges(){
		List<E> ret = new ArrayList<E>();
		ret.addAll(backEdges);
		ret.addAll(treeEdges);
		return ret;
	}

	@Override
	public String toString() {
		String ret = "DFS Tree: \n";
		for (V v : verticesWithIndexes.keySet())
			ret += " Vertex " + v +" with index = " + verticesWithIndexes.get(v);

		ret += "\nTree edges: " + treeEdges;

		ret += "\nBack edges: " + backEdges;

		return ret;


	}
	

}
