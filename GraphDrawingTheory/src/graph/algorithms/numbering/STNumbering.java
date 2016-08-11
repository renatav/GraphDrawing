package graph.algorithms.numbering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.traversal.DFSTreeTraversal;
import graph.trees.DFSTree;

public class STNumbering <V extends Vertex,  E extends Edge<V>> extends Numbering<V,E>{

	private Map<V,Integer> numbering = new HashMap<V,Integer>();
	private int currentNumber = 1;
	
	//L(v) = min({v} U {u, there is w such that v*->w and w--u}
	private Map<V,V> LMap = new HashMap<V,V>();

	public STNumbering(Graph<V,E> graph){
		order = new ArrayList<V>();
		formOrder(graph);
	}

	public void formOrder(Graph<V,E> graph){
		
		DFSTreeTraversal<V, E> traversal = new  DFSTreeTraversal<V,E>(graph);
		DFSTree<V,E> dfsTree = traversal.formDFSTree(graph.getVertices().get(0));
		preorder(dfsTree);
		
		//initialize L
		//to improve efficiency
		//this step can be couple with the creation of the dfs tree
		for (V v : dfsTree.getVertices()){
			LMap.put(v, L(dfsTree,v));
		}
		
		//st should be an edge between
		//s and t
		//t->s is in the tree
		V s = null, t = null;
		E st = null;
		
		st = dfsTree.getTreeEdges().get(0);
		if (dfsTree.getIndex(st.getOrigin()) < dfsTree.getIndex(st.getDestination())){
			t = st.getOrigin();
			s = st.getDestination();
		}
		else{
			s = st.getOrigin();
			t = st.getDestination();
		}
		

		
		stNumber(s, t, st, dfsTree);
		System.out.println("numbering");
		System.out.println(numbering);
	}

	public void preorder(DFSTree<V,E> dfsTree){
		search(dfsTree, dfsTree.getRoot());
		System.out.println("numbering after preoder " + numbering);
	}

	private void search(DFSTree<V,E> dfsTree, V v){
		//assign v a number higher than all previously assigned numbers
		//for w such that v->w

		numbering.put(v, currentNumber);
		currentNumber++;

		for (E e : dfsTree.allOutgoingTreeEdges(v)){
			V w = e.getOrigin() == v ? e.getDestination() : e.getOrigin();
			search(dfsTree,w);
		}
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


		//if there is a new tree edge v->v
		E newTreeEdge = null;
		//if there is a new cycle edge {v,w} with  w*->v
		E newCycle1 = null;
		//if there is a new cycle edge {v,w} with  v*->w
		E newCycle2 = null;
		
		System.out.println("pathfinder");
		System.out.println("v " + v);
		System.out.println("new edges " + newEdges);

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
				else{
					//must be a tree edge
					newTreeEdge = e;
				}
			}
		}
		
		System.out.println("new cycle 1 " + newCycle1);
		System.out.println("new cycle 2 " + newCycle2);
		System.out.println("tree edge " + newTreeEdge);
		
		

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
			w = newTreeEdge.getOrigin() == v ? newTreeEdge.getDestination() : newTreeEdge.getDestination();
			while (newVertices.contains(w)){
				//find the new edge {w,x} with
				//with x = L(w) or L(x) = L(w)
				V x;
				for (E e : newEdges){
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
							w = x;
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
			w = newCycle2.getOrigin() == v ? newCycle2.getDestination() : newCycle2.getDestination();
			V x;
			while (newVertices.contains(w)){
				//find the new edge {w,x} with x->w
				for (E e : newEdges){
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
							//TODO just the idea, remove so that concurrent modification exception is not thrown
							//and everywhere else where this occurs
							oldEdges.add(e);
							newEdges.remove(e);
							path.add(e);
						}
					}
				}
			}
		}
		
		System.out.println(path);
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
		
		for (E e : dfsTree.getAllEdges())
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
			
			System.out.println("current stack " + stack);
			
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
				for (E e : path){
					pathVertices.add(current);
					V other = e.getOrigin() == current ? e.getDestination() : e.getOrigin();
					current = other;
				}
				System.out.println("path vertices " + pathVertices);
				//the last vertex shouldn't be added
				for (V pathVertex : pathVertices)
					stack.push(pathVertex);
			}
			else{
				numbering.put(v, i);
				i++;
			}
		}
	}

	//L(v) = min({v} U {u, there is w such that v*->w and w--u}
	private V L(DFSTree<V,E> dfsTree, V v){

		V min = v;
		V u;
		for (V w : dfsTree.allDescendantsOf(v, false)){
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
}
