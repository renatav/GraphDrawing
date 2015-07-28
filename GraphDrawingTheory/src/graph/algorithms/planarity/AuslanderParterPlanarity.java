package graph.algorithms.planarity;

import graph.algorithms.interlacement.InterlacementGraphEdge;
import graph.algorithms.interlacement.InterlacementGraphVertex;
import graph.elements.Edge;
import graph.elements.EdgeDirection;
import graph.elements.Graph;
import graph.elements.Path;
import graph.elements.Vertex;
import graph.properties.bipartite.Bipartite;
import graph.traversal.GraphTraversal;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Implementation of the Auslander-Parter Algorithm for checking planarity of a given graph
 * @author xxx
 * @param <V>
 * @param <E>
 */
public class AuslanderParterPlanarity<V extends Vertex,E extends Edge<V>> extends PlanarityTestingAlgorithm<V, E>{


	public AuslanderParterPlanarity() {
	}


	@Override
	public boolean isPlannar(Graph<V,E> graph){

		if (!graph.isCyclic())
			return true;

		List<Graph<V,E>> tested = new ArrayList<Graph<V,E>>();
		Stack<Graph<V,E>> graphStack = new Stack<Graph<V,E>>();

		graphStack.push(graph);

		int ciklus = 0;
		Graph<V,E> current;
		while (!graphStack.isEmpty()){

			if (ciklus > 0)
				break;
			ciklus ++;
			current = graphStack.pop();
			tested.add(current);

			traversal = new GraphTraversal<>(current);

			if (graphIsASinngleCycle(current)){
				continue;
			}

			Path<V,E> cycle = chooseCycle(current);

			if (cycle == null){
				continue; //no separating cycles
			}

			Graph<InterlacementGraphVertex<V, E>, InterlacementGraphEdge<V, E>> interlacementGraph = 
					constructInterlacementGraph(cycle, current);


			Bipartite<InterlacementGraphVertex<V, E>, InterlacementGraphEdge<V, E>> bipartite = 
					new Bipartite<InterlacementGraphVertex<V, E>, InterlacementGraphEdge<V, E>>(interlacementGraph);

			if (!bipartite.isBipartite())
				return false;

			for (Path<V,E>  segment : segmentsYieldedByCycle(cycle, current)){
				Graph<V,E> next = constructSegmentCycleGraph(segment, cycle);
				next.setDirected(false);
				if (!tested.contains(next)){
					graphStack.push(next);
				}

			}

		}
		return true;
	}

	/**
	 * Constructs a subgraphs composed by a cycle and a segment
	 * @param segment
	 * @param cycle
	 * @return Constructed subgraph
	 */
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


	/**
	 * Check if graph is a single cycle
	 * @param currentGraph
	 * @return true if graph is a singe cycle, false otherwise
	 */
	private boolean graphIsASinngleCycle(Graph<V,E> currentGraph){
		List<Path<V,E>> cycles = traversal.findAllCycles();
		if (cycles.size() == 0)
			return false;

		for (Path<V,E> cycle : cycles)
			if (cycle.size() != currentGraph.getEdges().size())
				return false;

		return true;

	}

	/**
	 * Construct interlacement graph 
	 * The interlacement graph of the segments of G with respect to C is the graph whose vertices 
	    are the segments of G and whose edges are the pairs of interlacing segments. 
	 * @param cycle
	 * @param currentGraph
	 * @return Interlacement graph
	 */
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



	/**
	 * Finds and return a separating cycle if one exists
	 * @param currentGraph
	 * @return A separating cycle if it exists, null otherwise
	 */
	private  Path<V,E>  chooseCycle(Graph<V,E> currentGraph){

		for (Path<V,E> cycle : traversal.findAllCycles()){
			List<Path<V,E>> segments = segmentsYieldedByCycle(cycle, currentGraph);
			if (segments.size() >= 2){ //cycle is separating
				return cycle;
			}
		}
		return null;

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

	@SuppressWarnings("unused")
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

			covered.add(current);
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
	
	/**
	 * Segment ttachments are vertices which are contained by both the segment and the cycle
	 * @param cycle
	 * @param segment
	 * @return Segment attachments fot the given segmetn and path
	 */
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



		//iterate through first segment's vertices
		//check if there is a vertex on second segment that comes before and a vertex that comes after the current one
		for (V segment1Vertex : segment1CycleVertices)
			for (int i = 0; i < segment2CycleVertices.size(); i++)
				for (int j = i + 1; j < segment2CycleVertices.size(); j++)
					if (isInBetwweenOnCycle(cycleVertices, segment2CycleVertices.get(i), segment2CycleVertices.get(j), segment1Vertex))
						return false;

		return true;
	}


	/**
	 * Check if a vertex is between two vertices on a cycle
	 * @param cycleVertices
	 * @param start
	 * @param end
	 * @param test
	 * @return
	 */
	private boolean isInBetwweenOnCycle(List<V> cycleVertices, V start, V end, V test){
		if (start == test || start == end || test == end)
			return false;

		if (!cycleVertices.contains(start) || !cycleVertices.contains(end) || !cycleVertices.contains(test))
			return false;

		int startIndex = cycleVertices.indexOf(start);
		int cycleSize = cycleVertices.size();
		//see which vertex is found first - end or test
		//simply checking the indexes is not sufficient 
		//due to the cyclicar nature (of the cycle :)) 
		int next = startIndex;

		while (true){
			next = startIndex < cycleSize - 1 ? next + 1 : 0;
			if (cycleVertices.get(next) == test)
				return true;
			else if (cycleVertices.get(next) == end)
				return false;
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
