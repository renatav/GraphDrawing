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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * Find a cycle basis of an undirected graph using the Paton's
 * algorithm.
 * <p/>
 * See:<br/>
 * K. Paton, An algorithm for finding a fundamental set of cycles
 * for an undirected linear graph, Comm. ACM 12 (1969), pp. 514-518.
 *  Ports the implementation of the following author:
 * @author Nikolay Ognyanov
 *
 * @param <V> The vertex type.
 * @param <E> The edge type.
 */
public class PatonSimpleCycles<V extends Vertex, E extends Edge<V>>{
	
    private Graph<V, E> graph;


    /**
     * Create a cycle basis finder for the specified graph.
     * 
     * @param graph An undirected graph in which to find cycles.
     * @throws IllegalArgumentException if the graph argument is
     *         <code>null</code>.
     */
    public PatonSimpleCycles(Graph<V,E> graph)
    {
        this.graph = graph;
    }


    /**
	 * @return A list simple cycles, where each cycle is represented as
	 * a list of vertices it contains
	 */
    public List<List<V>> findSimpleCycles()
    {
        if (graph == null) {
            throw new IllegalArgumentException("Null graph.");
        }
        Map<V, Set<V>> used = new HashMap<V, Set<V>>();
        Map<V, V> parent = new HashMap<V, V>();
        Stack<V> stack = new Stack<V>();
        List<List<V>> cycles = new ArrayList<List<V>>();

        for (V root : graph.getVertices()) {
            // Loop over the connected
            // components of the graph.
            if (parent.containsKey(root)) {
                continue;
            }
            // Free some memory in case of
            // multiple connected components.
            used.clear();
            // Prepare to walk the spanning tree.
            parent.put(root, root);
            used.put(root, new HashSet<V>());
            stack.push(root);
            // Do the walk. It is a BFS with
            // a FIFO instead of the usual
            // LIFO. Thus it is easier to 
            // find the cycles in the tree.
            while (!stack.isEmpty()) {
                V current = stack.pop();
                Set<V> currentUsed = used.get(current);
                for (E e : graph.allEdges(current)) {
                    V neighbour = e.getDestination();
                    if (neighbour.equals(current)) {
                        neighbour = e.getOrigin();
                    }
                    if (!used.containsKey(neighbour)) {
                        // found a new node
                        parent.put(neighbour, current);
                        Set<V> neighbourUsed = new HashSet<V>();
                        neighbourUsed.add(current);
                        used.put(neighbour, neighbourUsed);
                        stack.push(neighbour);
                    }
                    else if (neighbour.equals(current)) {
                        // found a self loop
                        List<V> cycle = new ArrayList<V>();
                        cycle.add(current);
                        cycles.add(cycle);
                    }
                    else if (!currentUsed.contains(neighbour)) {
                        // found a cycle
                        Set<V> neighbourUsed = used.get(neighbour);
                        List<V> cycle = new ArrayList<V>();
                        cycle.add(neighbour);
                        cycle.add(current);
                        V p = parent.get(current);
                        while (!neighbourUsed.contains(p)) {
                            cycle.add(p);
                            p = parent.get(p);
                        }
                        cycle.add(p);
                        cycles.add(cycle);
                        neighbourUsed.add(current);
                    }
                }
            }
        }
        return cycles;
    }
}
