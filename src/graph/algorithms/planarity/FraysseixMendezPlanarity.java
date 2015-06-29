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

public class FraysseixMendezPlanarity<V extends Vertex, E extends Edge<V>> extends PlanarityTestingAlgorithm<V,E> {

	private Logger log = Logger.getLogger(FraysseixMendezPlanarity.class);
	
	@Override
	public boolean isPlannar(Graph<V,E> graph) {

		log.info("checking cyclic");
		if (!graph.isCyclic())
			return true;
		log.info("finished checking cyclic");
		
		LRPartition<V, E> partition = new LRPartition<V,E>(graph);
		
		return partition.createLRPartition();
	}


	public Path<V,E> findFundamentalCycle(DFSTree<V, E> tree, E edge){

		if (!tree.getBackEdges().contains(edge))
			return null;

		Path<V,E> cycle = null;
		GraphTraversal<V, E> treeTraversal = new GraphTraversal<V,E>(tree);
		V start = edge.getOrigin();
		V end = edge.getDestination();
		List<Path<V,E>> paths = treeTraversal.findAllPathsDFS(start, end);
		cycle = paths.get(0);
		cycle.getPath().add(edge);
		E last = cycle.getPath().get(cycle.getPath().size() - 1);
		EdgeDirection lastDirection = cycle.getDirections().get(cycle.getDirections().size() - 1);
		V lastVertex = lastDirection == EdgeDirection.TO_DESTINATION ? last.getDestination() : last.getOrigin();
		cycle.getDirections().add(edge.getOrigin() == lastVertex ? EdgeDirection.TO_DESTINATION : EdgeDirection.TO_ORIGIN);

		return cycle;
	}





}
