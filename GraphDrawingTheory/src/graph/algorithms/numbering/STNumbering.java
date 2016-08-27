package graph.algorithms.numbering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.log4j.Logger;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.traversal.DFSTreeTraversal;
import graph.trees.DFSTree;

/**
 * Given any edge {s,t} in a biconnected graph G with n vertices, 
 * the vertices can be numbered from 1 to n so that vertex s receives the number 1
 * and vertex t the number n
 * This is called the st-numbering
 * This implementation is based on an algoritm by Even and Tarjan from their paper
 * titled  "Computing an st-numbering"
 * @author xx
 *
 * @param <V>
 * @param <E>
 */
public class STNumbering <V extends Vertex,  E extends Edge<V>> extends Numbering<V,E>{

	private Map<V,Integer> numbering = new HashMap<V,Integer>();
	private Map<Integer, V> inverseNumbering = new HashMap<Integer, V>();
	private boolean debug = false;

	//L(v) = min({v} U {u, there is w such that v*->w and w--u}
	private Map<V,V> LMap = new HashMap<V,V>();
	
	private Logger log = Logger.getLogger(STNumbering.class);
	
	//TODO it seems that the order of edges in new edges can affect the outcome,
	//causing problems in some cases, returning good result in others

	public STNumbering(Graph<V,E> graph, V s, V t){
		order = new ArrayList<V>();
		formOrder(graph, s, t);
	}

	public void formOrder(Graph<V,E> graph, V s, V t){

		//find the edge
		E st = null;
		for (E e : graph.adjacentEdges(s)){
			V v = e.getOrigin() == s ? e.getDestination() : e.getOrigin();
			if (v == t){
				st = e;
				break;
			}
		}

		//the given vertices are not connected
		if (st == null)
			return;


		DFSTreeTraversal<V, E> traversal = new  DFSTreeTraversal<V,E>(graph);
		DFSTree<V,E> dfsTree = traversal.formDFSTree(t);

		//initial numbering can be the dfs numbering
		numbering = dfsTree.getVerticesWithIndexes();

		//initialize L
		//to improve efficiency
		//this step can be couple with the creation of the dfs tree
		for (V v : dfsTree.getVertices()){
			LMap.put(v, L(dfsTree,v));
		}

		if (debug){
			log.info(dfsTree);
			log.info("L map " + LMap);
		}

		stNumber(s, t, st, dfsTree);


		//now order the vertices in the list order so that they are sorded based on
		//the st-number
		for (int i = 0; i < graph.getVertices().size(); i++)
			order.add(inverseNumbering.get(i));
	}


	/**
	 * Finds a simple path of new edges from old vertex v to some
	 * distinct old vertex w, marks edges and vertices on the path old and
	 * return the path
	 * @param v
	 * @param dfsTree
	 * @param newEdges
	 * @param oldEdges
	 * @param newVertices
	 * @param oldVertices
	 * @return The path
	 */
	private List<E> pathfinder(V v, DFSTree<V,E> dfsTree, List<E> newEdges, List<E> oldEdges, List<V> newVertices, List<V> oldVertices){

		if (debug){
			log.info("pathfinder");
			log.info("new Edges " + newEdges);
			log.info("old Edges " + oldEdges);
			log.info("new vertices " + newVertices);
			log.info("old vertices " + oldVertices);
			log.info("current vertex: " + v);
		}

		//if there is a new tree edge v->w
		E newTreeEdge = null;
		//if there is a new cycle edge {v,w} with  w*->v
		E newCycle1 = null;
		//if there is a new cycle edge {v,w} with  v*->w
		E newCycle2 = null;

		V w;
		for (E e : newEdges){

			if (e.getOrigin() == v || e.getDestination() == v){

				w = e.getOrigin() == v ? e.getDestination() : e.getOrigin();

				if (dfsTree.getBackEdges().contains(e)){
					if (dfsTree.getIndex(v) > dfsTree.getIndex(w)){ //w*->v 
						newCycle1 = e;
						break;
					}
					else
						newCycle2 = e;
				}
				else if (dfsTree.getIndex(v) < dfsTree.getIndex(w)){
					//must be a tree edge
					newTreeEdge = e;
				}
			}
		}

		if (debug){
			log.info("new cycle 1 " + newCycle1);
			log.info("new cycle 2 " + newCycle2);
			log.info("new tree edge " + newTreeEdge);
		}


		if (newTreeEdge == null && newCycle1 == null && newCycle2 == null)
			return null;

		List<E> path = new ArrayList<E>();

		if (newCycle1 != null){
			//mark {v,w} old
			//let path be {v,w}
			oldEdges.add(newCycle1);
			newEdges.remove(newCycle1);
			path.add(newCycle1);
		}
		else if (newTreeEdge != null) {
			///mark {v,w} old
			//initialize the path to be {v,w}

			oldEdges.add(newTreeEdge);
			newEdges.remove(newTreeEdge);
			path.add(newTreeEdge);

			//while w is new
			w = newTreeEdge.getOrigin() == v ? newTreeEdge.getDestination() : newTreeEdge.getOrigin();
			if (debug)
				log.info("w: " + w);
			
			while (newVertices.contains(w)){

				//find the new edge {w,x}
				//with x = L(w) or L(x) = L(w)
				V x;
				Iterator<E> iter = newEdges.iterator();
				//if(debug)
				//	System.out.println("new edges " + newEdges);
				
				while (iter.hasNext()){
					E e = iter.next();
					if (e.getOrigin() == w || e.getDestination() == w){

						x = e.getOrigin() == w ? e.getDestination() : e.getOrigin();
						if (LMap.get(w) == x || LMap.get(x) == LMap.get(w)){
							//mark w and {w,x} old
							//add {w,x} to path
							//w = x
							oldEdges.add(e);
							newEdges.remove(e);
							oldVertices.add(w);
							newVertices.remove(w);
							path.add(e);
							w = x;
							if (debug)
								log.info("x " + x);
							break;
						}
					}
				}
			}
		}
		//else if there is a new cycle edge {v[W] with v*->w
		else if (newCycle2 != null){
			//mark {v,w} old
			//initialize path to be {v,w}
			oldEdges.add(newCycle2);
			newEdges.remove(newCycle2);
			path.add(newCycle2);

			//while w is new
			w = newCycle2.getOrigin() == v ? newCycle2.getDestination() : newCycle2.getOrigin();
			V x;
			while (newVertices.contains(w)){
				//find the new edge {w,x} with x->w
				Iterator<E> iter = newEdges.iterator();
				while (iter.hasNext()){
					E e = iter.next();
					if (e.getOrigin() == w || e.getDestination() == w){
						if (!dfsTree.getTreeEdges().contains(e))
							continue;
						x = e.getOrigin() == w ? e.getDestination() : e.getOrigin();
						if (dfsTree.getIndex(x) < dfsTree.getIndex(w)){ //from x to w{
							//mark w and {w,x} old
							//add {w,x} to path
							//w = x
							oldVertices.add(w);
							newVertices.remove(w);
							oldEdges.add(e);
							iter.remove();
							path.add(e);
						}
					}
				}
			}
		}

		//System.out.println(path);
		return path;

	}

	/**
	 * Computes the st-numbering of biconnected graph 
	 * @param s
	 * @param t
	 * @param st
	 */
	private void stNumber(V s, V t, E st, DFSTree<V,E> dfsTree){
		//mark s,t and {s,t} old and other vertices and edges new
		List<V> newVertices = new ArrayList<V>();
		List<E> newEdges = new ArrayList<E>();
		List<V> oldVertices = new ArrayList<V>();
		List<E> oldEdges = new ArrayList<E>();

		oldVertices.add(s);
		oldVertices.add(t);
		oldEdges.add(st);

		for (E e : dfsTree.getTreeEdges())
			if (e != st)
				newEdges.add(e);
		for (E e : dfsTree.getBackEdges())
			if (e != st)
				newEdges.add(e);

		for (V v : dfsTree.getVertices())
			if (v != s && v != t)
				newVertices.add(v);


		//initialize stack to contain s on top of t
		Stack<V> stack = new Stack<V>();
		stack.push(t);
		stack.push(s);
		List<V> pathVertices = new ArrayList<V>();
		//i = 0
		int i = 0;
		//while stack is not empty do
		V v;
		while (!stack.isEmpty()){

			if (debug)
				log.info("current stack " + stack);

			//let v be the top vertex on the stack
			//delete v from stack
			v = stack.pop();
			List<E> path = pathfinder(v, dfsTree, newEdges, oldEdges, newVertices, oldVertices);
			//if path {v1,v2},....,{vk-1,vk}  is not null (v1 = v)
			//add vk-1...v1 to stack
			//else numner(v) = i; i++
			if (path != null){
				pathVertices.clear();
				V current = v;
				if (debug)
					log.info("PATH " + path);
				for (E e : path){
					pathVertices.add(current);
					if (debug)
						log.info("current " + current);
					V other = e.getOrigin() == current ? e.getDestination() : e.getOrigin();
					current = other;
				}
				//add vk-1...v1 to stack
				//the last vertex shouldn't be added to stack and its not in the list for that reason
				for (int j = pathVertices.size() -1; j >=0; j--)
					stack.push(pathVertices.get(j));
			}
			else{
				if (debug)
					System.out.println(i + ": " + v);
				numbering.put(v, i);
				inverseNumbering.put(i, v);
				i++;
			}
		}
	}

	//L(v) = min({v} U {u, if there is w such that v*->w and w--u}
	private V L(DFSTree<V,E> dfsTree, V v){

		V min = v;
		V u;
		for (V w : dfsTree.allDescendantsOf(v, true)){
			for (E backEdge : dfsTree.getBackEdges()){
				if (backEdge.getOrigin() == w || backEdge.getDestination() == w){
					u = backEdge.getOrigin() == w ? backEdge.getDestination() : backEdge.getOrigin();
					if (dfsTree.getIndex(w) > dfsTree.getIndex(u)) //back edge from w to u
						if (numbering.get(u) < numbering.get(min))
							min = u;
				}
			}
		}
		return min;
	}

	/**
	 * @return the numbering
	 */
	public Map<V, Integer> getNumbering() {
		return numbering;
	}
}
