/*=============================================================================

    Copyright(©) 2013 Nikolay Ognyanov

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

=============================================================================*/
package graph.algorithm.cycles;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * Find all simple cycles of a directed graph using the Johnson's
 * algorithm.
 * D.B.Johnson, Finding all the elementary circuits of 
 * a directed graph, SIAM J. Comput., 4 (1975), pp. 77-84.
 * Ports the implementation of the following author:
 * @author Nikolay Ognyanov
 *
 * @param <V> The vertex type.
 * @param <E> The edge type.
 */
public class JohnsonSimpleCycles<V extends Vertex, E extends Edge<V>>{

	private Graph<V,E> graph;


	// The main state of the algorithm.
	private List<List<V>>       cycles   = null;
	private Map<V, Integer>     vToI     = null;
	private Set<V>              blocked  = null;
	private Map<V, Set<V>>      bSets    = null;
	private Stack<V>            stack    = null;

	// The state of the embedded Tarjan SCC algorithm.
	private List<Set<V>>        SCCs     = null;
	private int                 index    = 0;
	private Map<V, Integer>     vIndex   = null;
	private Map<V, Integer>     vLowlink = null;
	private Stack<V>            path     = null;
	private Set<V>              pathSet  = null;
	private boolean stopWhenOneFound = false;

	/**
	 * Create a simple cycle finder with an unspecified graph.
	 */
	public JohnsonSimpleCycles()
	{
	}

	/**
	 * Create a simple cycle finder for the specified graph.
	 * 
	 * @param graph A directed graph in which to find cycles.
	 * @throws IllegalArgumentException if the graph argument is
	 *         <code>null</code>.
	 */
	public JohnsonSimpleCycles(Graph<V,E> graph, boolean stopWhenOneFound){
		this.graph = graph;
	}

	public JohnsonSimpleCycles(Graph<V,E> graph){
		this.graph = graph;
	}


	/**
	 * @return A list simple cycles, where each cycle is represented as
	 * a list of vertices it contains
	 */
	public List<List<V>> findSimpleCycles()
	{
		initState();

		int startIndex = 0;
		int size = graph.getVertices().size();
		while (startIndex < size) {

			Object[] minSCCGResult = findMinSCSG(startIndex);
			if (minSCCGResult[0] != null) {
				startIndex = (Integer) minSCCGResult[1];
				@SuppressWarnings("unchecked")


				Graph<V,E> scg = (Graph<V, E>) minSCCGResult[0];
				V startV = toV(startIndex);

				for (E e : scg.allEdges(startV)) {
					V v = e.getDestination();
					blocked.remove(v);
					getBSet(v).clear();
				}
				if (findCyclesInSCG(startIndex, startIndex, scg) && stopWhenOneFound)
					break;
				startIndex++;
			}
			else {
				break;
			}
		}

		List<List<V>> result = cycles;
		clearState();
		return result;
	}

	@SuppressWarnings("unchecked")
	private Object[] findMinSCSG(int startIndex)
	{
		// Per Johnson : "adjacency structure of strong 
		// component K with least vertex in subgraph of 
		// G induced by {s, s+ 1, n}".
		// Or in contemporary terms: the strongly connected
		// component of the subgraph induced by {v1,...,vn}
		// which contains the minimum (among those SCCs)
		// vertex index. We return that index together with
		// the graph.
		initMinSCGState();
		Object[] result = new Object[2];

		List<Set<V>> SCCs = findSCCS(startIndex);
		// find the SCC with the minimum index
		int minIndexFound = Integer.MAX_VALUE;
		Set<V> minSCC = null;
		for (Set<V> scc : SCCs) {
			for (V v : scc) {
				int t = toI(v);
				if (t < minIndexFound) {
					minIndexFound = t;
					minSCC = scc;
				}
			}
		}
		if (minSCC == null) {
			return result;
		}
		// build a graph for the SCC found

		Graph<V,E> resultGraph = new Graph<V,E>();

		for (V v : minSCC) {
			resultGraph.addVertex(v);
		}
		for (V v : minSCC) {
			for (V w : minSCC) {
				if (graph.hasEdge(v, w)) {
					System.out.println("checking for edge between " + v + " and "+ w);
					resultGraph.addEdge(graph.edgeesBetween(v, w).get(0));
				}
			}
		}
		// It is ugly to return results in an array
		// of Object but the idea is to restrict 
		// dependencies to JgraphT only and there is
		// no utility pair container in JgraphT.
		result[0] = resultGraph;
		result[1] = minIndexFound;

		clearMinSCCState();
		return result;
	}

	private List<Set<V>> findSCCS(int startIndex)
	{
		// Find SCCs in the subgraph induced
		// by vertices startIndex and beyond.
		// A call to StrongConnectivityInspector
		// would be too expensive because of the
		// need to materialize the subgraph.
		// So - do a local search by the Tarjan's
		// algorithm and pretend that vertices
		// with an index smaller than startIndex
		// do not exist.
		for (V v : graph.getVertices()) {
			int vI = toI(v);
			if (vI < startIndex) {
				continue;
			}
			if (!vIndex.containsKey(v)) {
				getSCCs(startIndex, vI);
			}
		}
		List<Set<V>> result = SCCs;
		SCCs = null;
		return result;
	}

	private void getSCCs(int startIndex, int vertexIndex)
	{
		V vertex = toV(vertexIndex);
		vIndex.put(vertex, index);
		vLowlink.put(vertex, index);
		index++;
		path.push(vertex);
		pathSet.add(vertex);

		List<E> edges = graph.outEdges(vertex);
		if (edges != null){
			for (E e : edges) {
				V successor = e.getDestination();
				int successorIndex = toI(successor);
				if (successorIndex < startIndex) {
					continue;
				}
				if (!vIndex.containsKey(successor)) {
					getSCCs(startIndex, successorIndex);
					vLowlink.put(
							vertex,
							Math.min(vLowlink.get(vertex),
									vLowlink.get(successor)));
				}
				else if (pathSet.contains(successor)) {
					vLowlink.put(
							vertex,
							Math.min(vLowlink.get(vertex),
									vIndex.get(successor)));
				}
			}
		}
		if (vLowlink.get(vertex).
				equals(vIndex.get(vertex)))
		{
			Set<V> result = new HashSet<V>();
			V temp = null;
			do {
				temp = path.pop();
				pathSet.remove(temp);
				result.add(temp);
			} while (!vertex.equals(temp));
			if (result.size() == 1) {
				V v = result.iterator().next();
				if (graph.hasEdge(vertex, v)) {
					SCCs.add(result);
				}
			}
			else {
				SCCs.add(result);
			}
		}
	}

	private boolean findCyclesInSCG(int startIndex,
			int vertexIndex,
			Graph<V, E> scg)
	{
		// Find cycles in a strongly connected graph
		// per Johnson.
		boolean foundCycle = false;
		V vertex = toV(vertexIndex);
		stack.push(vertex);
		blocked.add(vertex);

		for (E e : scg.outEdges(vertex)) {
			V successor = e.getDestination();
			int successorIndex = toI(successor);
			if (successorIndex == startIndex) {
				List<V> cycle = new ArrayList<V>();
				cycle.addAll(stack);
				cycles.add(cycle);
				foundCycle = true;
				if (stopWhenOneFound)
					return true;
			}
			else if (!blocked.contains(successor)) {
				boolean gotCycle =
						findCyclesInSCG(startIndex, successorIndex, scg);
				foundCycle = foundCycle || gotCycle;
			}
		}
		if (foundCycle) {
			unblock(vertex);
		}
		else {
			for (E ew : scg.outEdges(vertex)) {
				V w = ew.getDestination();
				Set<V> bSet = getBSet(w);
				bSet.add(vertex);
			}
		}
		stack.pop();
		return foundCycle;
	}

	private void unblock(V vertex)
	{
		blocked.remove(vertex);
		Set<V> bSet = getBSet(vertex);
		while (bSet.size() > 0) {
			V w = bSet.iterator().next();
			bSet.remove(w);
			if (blocked.contains(w)) {
				unblock(w);
			}
		}
	}

	private void initState()
	{
		cycles = new LinkedList<List<V>>();


		vToI = new HashMap<V, Integer>();
		blocked = new HashSet<V>();
		bSets = new HashMap<V, Set<V>>();
		stack = new Stack<V>();

		for (int i = 0; i < graph.getVertices().size(); i++) {
			vToI.put(graph.getVertices().get(i), i);
		}
	}

	private void clearState()
	{
		cycles = null;
		vToI = null;
		blocked = null;
		bSets = null;
		stack = null;
	}

	private void initMinSCGState()
	{
		index = 0;
		SCCs = new ArrayList<Set<V>>();
		vIndex = new HashMap<V, Integer>();
		vLowlink = new HashMap<V, Integer>();
		path = new Stack<V>();
		pathSet = new HashSet<V>();
	}

	private void clearMinSCCState()
	{
		index = 0;
		SCCs = null;
		vIndex = null;
		vLowlink = null;
		path = null;
		pathSet = null;
	}

	private Integer toI(V vertex)
	{
		return vToI.get(vertex);
	}

	private V toV(Integer i)
	{
		return graph.getVertices().get(i);
	}

	private Set<V> getBSet(V v)
	{
		// B sets typically not all needed,
		// so instantiate lazily.
		Set<V> result = bSets.get(v);
		if (result == null) {
			result = new HashSet<V>();
			bSets.put(v, result);
		}
		return result;
	}
}
