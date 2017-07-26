package graph.algorithms.planarity;

import graph.algorithms.lrpartition.LRPartition;
import graph.elements.Edge;
import graph.elements.EdgeDirection;
import graph.elements.Graph;
import graph.elements.Path;
import graph.elements.Vertex;
import graph.traversal.GraphTraversal;
import graph.trees.DFSTree;

import java.util.List;

import org.apache.log4j.Logger;

/**
 * An algorithm for checking the planarity of a graph based on Fraysseix and Mendez's algorithm
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type 
 */
public class FraysseixMendezPlanarity<V extends Vertex, E extends Edge<V>> extends PlanarityTestingAlgorithm<V,E> {

	private Logger log = Logger.getLogger(FraysseixMendezPlanarity.class);
	
	private boolean debug = false;
	
	@Override
	public boolean isPlannar(Graph<V,E> graph) {

		if (debug)
			log.info("checking cyclic");
		if (!graph.isCyclic())
			return true;
		if (debug)
			log.info("finished checking cyclic");
		
		LRPartition<V, E> partition = new LRPartition<V,E>(graph);
		
		return partition.createLRPartition();
	}

	/**
	 * Implementation of the procedure for finding a fundamental cycle
	 * @param tree DFS tree
	 * @param edge Edge
	 * @return Fundamental cycle if it exists, null otherwise
	 */
	public Path<V,E> findFundamentalCycle(DFSTree<V, E> tree, E edge){

		if (!tree.getBackEdges().contains(edge))
			return null;

		Path<V,E> cycle = null;
		V start = edge.getOrigin();
		V end = edge.getDestination();
		List<Path<V,E>> paths = GraphTraversal.findAllPathsDFS(tree, start, end);
		cycle = paths.get(0);
		cycle.getPath().add(edge);
		E last = cycle.getPath().get(cycle.getPath().size() - 1);
		EdgeDirection lastDirection = cycle.getDirections().get(cycle.getDirections().size() - 1);
		V lastVertex = lastDirection == EdgeDirection.TO_DESTINATION ? last.getDestination() : last.getOrigin();
		cycle.getDirections().add(edge.getOrigin() == lastVertex ? EdgeDirection.TO_DESTINATION : EdgeDirection.TO_ORIGIN);
		if (debug)
			log.info("Fundamental cycle " + cycle);
		return cycle;
	}





}
