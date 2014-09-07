package graph.algorithms.planarity;

import graph.algorithms.bipartite.Bipartite;
import graph.elements.Edge;
import graph.elements.EdgeDirection;
import graph.elements.Graph;
import graph.elements.Path;
import graph.elements.Vertex;
import graph.traversal.GraphTraversal;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Implementation of the Auslander-Parter Algorithm for checking planarity of a given graph
 * @author xxx
 *
 * @param <V>
 * @param <E>
 */
public class AuslanderParterPlanarity<V extends Vertex,E extends Edge<V>> extends PlanarityTestingAlgorithm<V, E>{


	public AuslanderParterPlanarity() {
	}


	@Override
	public boolean isPlannar(Graph<V,E> graph){

		//System.out.println(graph);
		traversal = new GraphTraversal<>(graph);

		if (!graph.isCyclic()){
			return true;
		}

		if (graphIsASinngleCycle(graph)){
			return true;
		}

		Path<V,E> cycle = chooseCycle(graph);

		if (cycle == null)
			return true; //no separating cycles

		Graph<InterlacementGraphVertex<V, E>, InterlacementGraphEdge<V, E>> interlacementGraph = 
				constructInterlacementGraph(cycle, graph);

		Bipartite<InterlacementGraphVertex<V, E>, InterlacementGraphEdge<V, E>> bipartite = 
				new Bipartite<InterlacementGraphVertex<V, E>, InterlacementGraphEdge<V, E>>(interlacementGraph);

		if (!bipartite.isBipartite())
			return false;

		//
		for (Path<V,E>  segment : segmentsYieldedByCycle(cycle, graph)){
						System.out.println("Segment: " + segment);
						System.out.println("Cycle: " + cycle);
						System.out.println("Test graph: " + constructSegmentCycleGraph(segment, cycle) );
//						if (!isPlannar(constructSegmentCycleGraph(segment, cycle)))
//							return false;
			
		}

		return true;
	}


	@SuppressWarnings("unchecked")
	private Graph<V,E> constructSegmentCycleGraph(Path<V,E> segment, Path<V,E> cycle){
		Graph<V,E> testGraph = new Graph<V,E>();
		cycle.getPath().addAll(segment.getPath());
		for (E e : cycle.getPath()){
			if (!testGraph.getVertices().contains(e.getDestination()))
				testGraph.addVertex(e.getDestination());
			if (!testGraph.getVertices().contains(e.getOrigin()))
				testGraph.addVertex(e.getOrigin());
			if (!testGraph.getEdges().contains(e))
				testGraph.addEdge(e);
		}

		return testGraph;
	}


	private boolean graphIsASinngleCycle(Graph<V,E> currentGraph){
		List<Path<V,E>> cycles = traversal.findAllCycles();
		for (Path<V,E> cycle : cycles){
			if (cycle.size() != currentGraph.getEdges().size())
				return false;
		}
		return true;

	}

	@SuppressWarnings("unchecked")
	private Graph<InterlacementGraphVertex<V, E>, InterlacementGraphEdge<V, E>> constructInterlacementGraph(Path<V,E> cycle, Graph<V,E> currentGraph) {


		List<Path<V,E>> segments = segmentsYieldedByCycle(cycle, currentGraph);
		Graph<InterlacementGraphVertex<V, E>, InterlacementGraphEdge<V, E>> interlacementGraph = 
				new Graph<InterlacementGraphVertex<V, E>, InterlacementGraphEdge<V, E>>();


		for (Path<V,E> segment : segments)
			interlacementGraph.addVertex(new InterlacementGraphVertex<>(segment));

		for (int i = 0; i < segments.size(); i++){
			for (int j = i + 1; j < segments.size(); j++){
				Path<V,E> segment1 = segments.get(i);
				Path<V,E> segment2 = segments.get(j);

				if (!segmentsCompatible(cycle, segment1, segment2))
					interlacementGraph.addEdge(new InterlacementGraphEdge<V,E>((InterlacementGraphVertex<V, E>)interlacementGraph.getVertexByContent(segment1),
							(InterlacementGraphVertex<V, E>) interlacementGraph.getVertexByContent(segment2)));


			}
		}
		return interlacementGraph;

	}




	private  Path<V,E>  chooseCycle(Graph<V,E> currentGraph){

		return traversal.findAllCycles().get(1);
		//		for (Path<V,E> cycle : traversal.findAllCycles()){
		//			List<Path<V,E>> segments = segmentsYieldedByCycle(cycle, currentGraph);
		//			if (segments.size() >= 2){ //cycle is separating
		//				return cycle;
		//			}
		//		}
		//		return null;


	}


	/**
	 * A cycle C of  G is said to be  separating  if it has at least two segments, while it is called 
			non-separating  otherwise
	 * @return
	 */
	private boolean cycleIsSeparating(Path<V,E> cycle, Graph<V,E> currentGraph){
		return segmentsYieldedByCycle(cycle, currentGraph).size() >= 2;
	}

	/**
	 *  Each such cycle C yields a collection of connected, edge-induced subgraphs S , i = 1,...,k as follows. 
	 *  Either S is an edge that connects two vertices of C that are not consecutive (i.e., a chord), or S is induced 
	 * by  the edges of  a connected  component  of  G \ C together  with  the edges connecting  that 
	 * component  to  C. Each S is  called  a  segment.
	 * @param cycle
	 * @return
	 */
	public List<Path<V,E>> segmentsYieldedByCycle(Path<V,E> cycle, Graph<V,E> currentGraph){
		List<Path<V,E>> segments = new ArrayList<Path<V,E>>();
		List<V> verticesOnCycle = cycle.pathVertices();
		List<E> coveredEdges = new ArrayList<E>();
		List<E> currentEdges;
		for (V v : verticesOnCycle){
			currentEdges = currentGraph.allEdges(v);

			for (E e : currentEdges){
				if (coveredEdges.contains(e) || cycle.containsEdge(e))
					continue;
				coveredEdges.add(e);
				EdgeDirection direction = e.getOrigin() == v ? EdgeDirection.TO_DESTINATION : EdgeDirection.TO_ORIGIN;
				Path<V, E> path = new Path<V,E>();
				if (verticesOnCycle.contains(e.getOrigin()) &&
						verticesOnCycle.contains(e.getDestination())){
					path.addEdge(e, direction);
				}
				else{
					V innerVertex =  e.getDestination() == v ? e.getOrigin() : e.getDestination();
					path.addEdge(e, direction);
					allPathsIncludingVertexNonrecursive(innerVertex, verticesOnCycle, path, coveredEdges, currentGraph);
				}
				segments.add(path);

			}
		}

		return segments;

	}

	private void allPathsIncludingVertex(V v, List<V> excluding, Path<V,E> path, List<E> covered, Graph<V,E> currentGraph){
		V other;
		EdgeDirection direction;
		for (E e : currentGraph.allEdges(v)){
			if (covered.contains(e))
				continue;
			direction = e.getOrigin() == v ? EdgeDirection.TO_DESTINATION : EdgeDirection.TO_ORIGIN;
			other = e.getOrigin() == v ? e.getDestination() : e.getOrigin();
			path.addEdge(e, direction);
			covered.add(e);
			if (!excluding.contains(other))
				allPathsIncludingVertexNonrecursive(other, excluding, path, covered, currentGraph);
		}
	}

	private void allPathsIncludingVertexNonrecursive(V v, List<V> excluding, Path<V,E> path, List<E> covered, Graph<V,E> currentGraph){
		EdgeDirection direction;
		Stack<E> stack = new Stack<E>();
		Stack<EdgeDirection> directionStack = new Stack<EdgeDirection>();

		for (E e : currentGraph.allEdges(v)){
			if (covered.contains(e))
				continue;
			direction = e.getOrigin() == v ? EdgeDirection.TO_DESTINATION : EdgeDirection.TO_ORIGIN;
			stack.add(0, e);
			directionStack.add(0, direction);
		}

		E current;
		EdgeDirection currentDirection;
		while (!stack.empty()){

			current = stack.pop();
			currentDirection = directionStack.pop();
			if (covered.contains(current))
				continue;
			path.addEdge(current, currentDirection);


			V nextVertex = currentDirection == EdgeDirection.TO_DESTINATION ? current.getDestination() : current.getOrigin();
			if (!excluding.contains(nextVertex))
				for (E e : currentGraph.allEdges(nextVertex)){
					if (covered.contains(e))
						continue;
					direction = e.getOrigin() == v ? EdgeDirection.TO_DESTINATION : EdgeDirection.TO_ORIGIN;
					stack.add(0, e);
					directionStack.add(0, direction);
				}
		}

	}

	public List<V> segmentAttachments(Path<V,E> cycle, Path<V,E> segment){
		List<V> ret = new ArrayList<V>();

		List<V> cycleVertices = cycle.pathVertices();
		for (V v : segment.pathVertices())
			if (cycleVertices.contains(v))
				ret.add(v);

		return ret;
	}

	/**
	 * Two  segments  are  compatible,  if  and  only  if  their  attachments  do  not  interleave. 
	 * @param cycle
	 * @param segment1
	 * @param segment2
	 * @return
	 */
	public boolean segmentsCompatible(Path<V,E> cycle, Path<V,E> segment1, Path<V,E> segment2){


		List<V> segment1CycleVertices = cycleVerticesOnSegment(cycle, segment1);
		List<V> segment2CycleVertices = cycleVerticesOnSegment(cycle, segment2);
		List<V> cycleVertices = cycle.pathVertices();


		V current, next;
		int index = 0;
		while (index < segment1CycleVertices.size() - 1){
			current = segment1CycleVertices.get(index);

			if (index == segment1CycleVertices.size() - 1)
				next = segment1CycleVertices.get(0);
			else
				next = segment1CycleVertices.get(index + 1);

			int nextOnCycle = cycleVertices.indexOf(current);


			V cycleVert;
			while (true){

				nextOnCycle = nextOnCycle < cycleVertices.size() - 1 ? nextOnCycle + 1 : 0;
				cycleVert = cycleVertices.get(nextOnCycle);
				//ok
				if (segment1CycleVertices.contains(cycleVert))
					break;

				//
				if (segment2CycleVertices.contains(cycleVert)){


					int segment2Index = segment2CycleVertices.indexOf(cycleVert);
					if (segment2Index == segment2CycleVertices.size() - 1)
						break;
					int nextOnSecond = segment2Index + 1; 

					V nextVertOnSecond = segment2CycleVertices.get(nextOnSecond);


					if (beforeOnCycle(cycleVertices, cycleVert, nextVertOnSecond, next) == 2){
						return false;

					}

				}

			}

			index ++;
		}

		return true;
	}


	/**
	 * 
	 * @param cycleVertices
	 * @param testStart
	 * @param test1
	 * @param test2
	 * @return 1 if test1 is before, 2 if test2 is before, 0 if they are the same, -1 error
	 */
	private int beforeOnCycle(List<V> cycleVertices, V testStart, V test1, V test2){
		if (!cycleVertices.contains(test1) || !cycleVertices.contains(test2))
			return -1;

		if (test1 == test2)
			return 0;

		int index = cycleVertices.indexOf(testStart);


		while (true){
			index = index < cycleVertices.size() - 1 ? index + 1 : 0;
			if (cycleVertices.get(index) == test1)
				return 1;
			if (cycleVertices.get(index) == test2)
				return 2;
		}



	}

	private List<V> cycleVerticesOnSegment(Path<V,E> cycle, Path<V,E> segment){
		List<V> ret = new ArrayList<V>();
		List<V> cycleVertices = cycle.pathVertices();
		List<V> segmentVertices = segment.pathVertices();  

		for (V v : segmentVertices)
			if (cycleVertices.contains(v))
				ret.add(v);

		return ret;


	}



}
