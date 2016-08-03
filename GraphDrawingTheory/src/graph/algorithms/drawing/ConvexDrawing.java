package graph.algorithms.drawing;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Path;
import graph.elements.Vertex;
import graph.exception.CannotBeAppliedException;
import graph.math.Calc;
import graph.math.CircleLayoutCalc;
import graph.math.Line;
import graph.math.Triangle;
import graph.traversal.DijkstraAlgorithm;
import graph.traversal.TraversalUtil;
import graph.util.Util;

public class ConvexDrawing<V extends Vertex, E extends Edge<V>> {

	private Graph<V,E> graph;
	private Class<?> edgeClass;
	private Random rand;
	private Logger log = Logger.getLogger(ConvexDrawing.class);

	/**
	 * An algorithm which will be used to find a path between two vertices, as that is necessary on several occasions
	 */
	private DijkstraAlgorithm<V, E> dijkstra;

	/**
	 * Distance when positioning the face vertices
	 */
	private int treshold = 100;

	public ConvexDrawing(Graph<V,E> graph){
		this.graph = graph;
		edgeClass = graph.getEdges().get(0).getClass();
		rand = new Random();
		dijkstra = new DijkstraAlgorithm<V,E>();
		dijkstra.setDirected(false);
	}


	public Map<V, Point2D> execute(){

		Map<V, Point2D> ret = new HashMap<V, Point2D>();

		try {
			//find the extendable facial cycle (where every vertex of S is an apex of S*)
			//if there are no such cycles, an exception is thrown
			//since it is much more practical, keep edges of a face as well, not just vertices
			PlanarConvexEmbedding<V, E> convexEmbedding = new PlanarConvexEmbedding<V,E>(graph);
			Path<V,E> S = convexEmbedding.convexTesting();
			ret = execute(S);

		} catch (CannotBeAppliedException e) {
			e.printStackTrace();
		}


		return ret;
	}


	@SuppressWarnings("unchecked")
	public Map<V, Point2D> execute(Path<V,E> S){

		Map<V, Point2D> ret = new HashMap<V, Point2D>();

		//determine position of S* vertices
		//TODO center as algorithm parameter
		Point2D center = new Point(0,0);

		//convex testing returns S which is equal to S* (there are no vertices 
		//in S which are not in S

		List<V> Svertices = S.pathVertices();
		Svertices.remove(Svertices.size() - 1);
		log.info("Face vertices " + Svertices);
		CircleLayoutCalc<V> circleCalc = new CircleLayoutCalc<V>();
		double radius = circleCalc.calculateRadius(Svertices, treshold);
		Map<V,Point2D> positions = circleCalc.calculatePosition(Svertices, radius, center);
		log.info("Calculating positions of the outer cycle");
		log.info(positions);
		ret.putAll(positions);


		//step one - for each vertex v of degree two not on S
		//replace v together with two edges incident to v with a 
		//single edge joining the vertices adjacent to v

		//leave the original graph intact - make a copy to start with
		Graph<V,E> gPrim = Util.copyGraph(graph);
		//store deleted vertices in order to position them later
		Map<V, E> deletedAdjacentMap = new HashMap<V,E>();


		//once a vertex is deleted and its two edges are deleted
		//and a new one is created
		//there might be a vertex with degree 2 connected to the deleted vertex
		//we don't want to create an edge containing the deleted vertex
		//the newly created edge should be taken into account

		Iterator<V> iter = gPrim.getVertices().iterator();
		while (iter.hasNext()){
			V v = iter.next();
			if (!Svertices.contains(v) && gPrim.vertexDegree(v) == 2){
				log.info("Deleting " + v);
				List<E> edges = gPrim.adjacentEdges(v);
				E e1 = edges.get(0);
				E e2 = edges.get(1);
				log.info("removing " + e1);
				log.info("removing " + e2);
				gPrim.removeEdge(e1);
				gPrim.removeEdge(e2);
				V adjV1 = e1.getOrigin() == v ? e1.getDestination() : e1.getOrigin();
				V adjV2 = e2.getOrigin() == v ? e2.getDestination() : e2.getOrigin();
				E newEdge = Util.createEdge(adjV1, adjV2, edgeClass);
				log.info("Creating " + newEdge);
				gPrim.addEdge(newEdge);
				deletedAdjacentMap.put(v,newEdge);
			}
		}

		for (V v : deletedAdjacentMap.keySet()){
			gPrim.removeVertex(v);
		}


		log.info("G': " + gPrim);
		//step 2 - call Draw on (G', S, S*) to extend S* into a convex drawing of G'
		draw(gPrim, S.getPath(), Svertices, ret);

		//step 3 For each deleted vertex of degree 2 determine its position on the straight 
		//line segment joining the two vertices adjacent to the vertex

		Set<V> deletedVertices = deletedAdjacentMap.keySet();
		List<V> coveredVertices = new ArrayList<V>();

		log.info("deleted vertices: " + deletedVertices);

		for (V v : deletedVertices){

			if (coveredVertices.contains(v))
				continue;

			E addedEdge = deletedAdjacentMap.get(v);
			V firstAdjacent = addedEdge.getOrigin();
			V secondAdjacent = addedEdge.getDestination();
			Point2D pos1 = ret.get(firstAdjacent);
			Point2D pos2 = ret.get(secondAdjacent);
			if (pos1 != null && pos2 != null){
				//find deleted vertices on this line

				List<E> adjacentEdges = graph.adjacentEdges(v);
				E e1 = adjacentEdges.get(0);
				E e2 = adjacentEdges.get(1);
				V e1Next = e1.getOrigin() == v ? e1.getDestination() : e1.getOrigin();
				V e2Next = e2.getOrigin() == v ? e2.getDestination() : e2.getOrigin();

				if ((e1Next == firstAdjacent && e2Next == secondAdjacent) ||
						(e2Next == firstAdjacent && e1Next == secondAdjacent)){

					//just one vertex between two known

					double xPos =  ret.get(e1Next).getX() + ((ret.get(e2Next).getX() - ret.get(e1Next).getX()) / 2);
					double yPos =  ret.get(e1Next).getY() + ((ret.get(e2Next).getY() - ret.get(e1Next).getY()) / 2);
					ret.put(v, new Point2D.Double(xPos, yPos));
				}
				else{

					List<V> verticesOnLine = new ArrayList<V>();
					verticesOnLine.add(v);

					//in all probability, traversing e1 is enough as the
					//vertex won't be somewhere in the middle, but directly 
					//connected to one of the vertices whose positions are known
					//keep both for now
					//traverse e1
					E currentE = e1;
					int numberThroughE1 = 0;
					while (e1Next != firstAdjacent && e1Next != secondAdjacent){
						if (!verticesOnLine.contains(e1Next)){
							verticesOnLine.add(e1Next);
							numberThroughE1++;
						}
						List<E> currentAdjacent = graph.adjacentEdges(e1Next);
						if (currentAdjacent.get(0) == currentE)
							currentE = currentAdjacent.get(1);
						else if (currentAdjacent.get(1) == currentE)
							currentE = currentAdjacent.get(0);

						e1Next = currentE.getOrigin() == e1Next ? currentE.getDestination() : currentE.getOrigin();
					}


					//traverse e2
					currentE = e2;
					while (e2Next != firstAdjacent && e2Next != secondAdjacent){
						if (!verticesOnLine.contains(e2Next))
							verticesOnLine.add(e2Next);

						List<E> currentAdjacent = graph.adjacentEdges(e2Next);
						if (currentAdjacent.get(0) == currentE)
							currentE = currentAdjacent.get(1);
						else if (currentAdjacent.get(1) == currentE)
							currentE = currentAdjacent.get(0);

						e2Next = currentE.getOrigin() == e2Next ? currentE.getDestination() : currentE.getOrigin();
					}

					log.info("vertices on line: " + verticesOnLine);
					coveredVertices.addAll(verticesOnLine);

					int numberOfVerticesOnLine = verticesOnLine.size();
					double incrementX = (ret.get(e2Next).getX() - ret.get(e1Next).getX()) / (numberOfVerticesOnLine + 1);
					double incrementY = (ret.get(e2Next).getY() - ret.get(e1Next).getY()) / (numberOfVerticesOnLine + 1);

					Point2D currentPosition = ret.get(e1Next); //e1Next holds a vertex whose position is known

					for (int i = numberThroughE1; i >= 0; i--){
						V currentV = verticesOnLine.get(i);
						Point2D position = new Point2D.Double(currentPosition.getX() + incrementX, currentPosition.getY() + incrementY);
						ret.put(currentV, position);
						currentPosition = position;
					}


					for (int i = numberThroughE1 + 1; i< numberOfVerticesOnLine; i++){
						V currentV = verticesOnLine.get(i);
						Point2D position = new Point2D.Double(currentPosition.getX() + incrementX, currentPosition.getY() + incrementY);
						ret.put(currentV, position);
						currentPosition = position;
					}
				}


			}

		}


		return ret;
	}


	//when calling draw for the first time, positions should already 
	//contain position of vertices on S
	/**
	 * Extends a convex polygon S* of the outer facial cycle of a plane
	 *  graph G into a convex drawing of G where G
	 * has no vertex of degree 2 not on S
	 * @param G 2-connected plane graph
	 * @param S Outer facial cycle
	 * @param Sstar Extendable convex polygon of S
	 * @param positions
	 */
	private void draw(Graph<V,E> G, List<E> S, List<V> Svertices, Map<V,Point2D> positions){

		log.info("Calling draw for " + G);

		//if G has at most 3 vertices
		//a convex drawing has been obtained - return
		if (G.getVertices().size() <= 3)
			return;

		//select and arbitrary vertex of S* and let G' = G-v (remove v from G)
		V v = arbitraryApex(Svertices);

		log.info("Selected arbitrary vertex " + v);

		//find vertices v1 and vp+1 as vertices on S adjacent to v
		V v1, vp_1;
		int index = Svertices.indexOf(v);
		int next = (index + 1) % Svertices.size();
		int previous = index - 1;
		if (previous == -1)
			previous = Svertices.size() - 1;

		v1 = Svertices.get(Math.min(next, previous));
		vp_1 = Svertices.get(Math.max(next, previous));

		log.info("Previous " + v1);
		log.info("Next " + vp_1);

		//remove v, determine which vertices are adjacent to it

		List<V> adjacentVertices = new ArrayList<V>();
		adjacentVertices.addAll(G.adjacentVertices(v));
		log.info("Adjacent vertices: " + adjacentVertices);
		G.removeVertex(v);
		log.info("Removed vertex " + v);
		log.info(G);


		//divide the G into blocks and find cut vertices

		List<V> cutVertices = G.listCutVertices();
		List<Graph<V, E>> blocks = G.listBiconnectedComponents();
		log.info("Cut vertices: " + cutVertices);
		log.info("Blocks: " + blocks);

		List<V> vis = new ArrayList<V>();
		//vis.add(v1);
		vis.addAll(cutVertices);
		vis.add(vp_1);

		//v1 is in B1
		//Vp+1 is in Bp
		//Vi is in Bi-1 and Bi
		//vi , 2<=i<=p is a cut vertex if G'
		V currentV = v1;

		//Step 2 - Draw each block bi convex

		//Step 2.1 Determine Si*
		//when S* was found, positions of its vertices were found as well
		//so we only need to find position of vertices not on S
		//Locate the vertices in V(Si) - V(S) in the interiors of the triangle
		//v*v1*vi+1 (cut vertices + 2 connected to v at the beginning and end)
		//in such way that the vertices adjacent to v are apices of convex polygon Si*
		//and the others are on the straight line segments

		//Step 2.2
		//recursively call procedure Draw(bi,Si, Si*)

		List<E> blockEdgesOnS = new ArrayList<E>();
		List<E> otherBlockEdges = new ArrayList<E>();
		List<V> verticesOnS = new ArrayList<V>();
		List<V> SiVerticesNotOnSAdjToV = new ArrayList<V>();
		List<V> SiVerticesNotOnSNotAdjToV = new ArrayList<V>();

		while (blocks.size() > 0){
			//find the block which contains the current vertex
			Graph<V, E> foundBlock = null;
			for (Graph<V, E> block :  blocks)
				if (block.getVertices().contains(currentV)){
					foundBlock = block;
					break;
				}
			blocks.remove(foundBlock);
			log.info("Block " + foundBlock);

			//find the next vertex (the one both on S and in the block)
			V otherVertex = null;
			for (V sVertex : vis)
				if (foundBlock.getVertices().contains(sVertex)){
					otherVertex = sVertex;
					break;
				}


			//now that the current block was determined
			//find its extendable facial cycle
			//for each Bi, Si is the union of Vi-Vi+1 path on S and on the block not counting edges of S
			blockEdgesOnS.clear();
			otherBlockEdges.clear();
			//also form a list of all vertices which are on S
			//it will later be used to calculate the positions of other vertices
			//on Si and not on S
			verticesOnS.clear();

			for (E e : foundBlock.getEdges()){
				if (S.contains(e))
					blockEdgesOnS.add(e);
				else
					otherBlockEdges.add(e);
				if (S.contains(e.getDestination()))
					verticesOnS.add(e.getDestination());
				if (S.contains(e.getOrigin()))
					verticesOnS.add(e.getOrigin());

			}

			verticesOnS.remove(currentV);
			verticesOnS.remove(otherVertex);

			log.info("S: " + S);
			log.info("Block edges on S: " + blockEdgesOnS);
			log.info("Other block edges: " + otherBlockEdges);


			if (otherBlockEdges.size() > 0){
				dijkstra.setEdges(otherBlockEdges);
				//exclude vertices on S in the block 
				//List<E> otherPath = dijkstra.getPath(currentV, otherVertex, verticesOnS).getPath();
				//we need to find a path from the two vertices on the facial cycle
				//but no edges from the cycle should be on it
				//and there should be no edges between a vertex on the path
				//and a vertex on the facial cycle
				//not counting the two cut vertices

				//set adjacent in such way that those edges connecting a vertex to a vertex on S are given priority
				//as long as they are not in the block
				//skip edges on S that are in the block and those which connect a vertex to an edge
				//in the block and on S

				if (currentV != otherVertex){
					prepareAdjacencyLists(currentV, otherVertex, foundBlock, graph, Svertices, blockEdgesOnS);

					List<E> otherPath = TraversalUtil.circularNoCrossingsPath(currentV, otherVertex, foundBlock.getAdjacentLists(), true, Svertices, blockEdgesOnS);
					log.info("Other path: (from " + currentV + " to " + otherVertex + ": " + otherPath);
					blockEdgesOnS.addAll(otherPath);
				}
			}

			//form Si and Si vertices, which will be used during the recursive call

			List<E> Si;
			List<V> SiVertices;

			if (blockEdgesOnS.size() > 1){

				dijkstra.setEdges(blockEdgesOnS);
				E first = blockEdgesOnS.get(0);
				blockEdgesOnS.remove(0);
				Path<V,E> path = dijkstra.getPath(first.getDestination(), first.getOrigin());
				Si = path.getPath();
				SiVertices = path.pathVertices();
				Si.add(first);
				log.info("Resulting path " + Si);
			}
			else{
				Si = blockEdgesOnS;
				SiVertices = new ArrayList<V>();
				for (E e : blockEdgesOnS){
					if (!SiVertices.contains(e.getOrigin()))
						SiVertices.add(e.getOrigin());
					if (!SiVertices.contains(e.getDestination()))
						SiVertices.add(e.getDestination());
				}
			}


			//we know positions of Si vertices which are on S
			//it's now necessary to determine positions of those which are not
			//for each such vertex, do the following:
			//if it is adjacent to v (previously selected arbitrary apex)
			//make it an apex of the polygon (for the block) and inside triangle v-vi-vi+1
			//else, place it on a straight line segment

			SiVerticesNotOnSAdjToV.clear();
			SiVerticesNotOnSNotAdjToV.clear();

			log.info("S vertices: " + Svertices);

			//exclude already positioned vertices, they don't need to be moved
			//happens if a vertex is not on S of the current block, but is of some previous ones
			//don't more it in that case


			V previousOrigin = null;
			V previousDestination = null;

			for (E e : otherBlockEdges){
				V origin = e.getOrigin();
				V destination = e.getDestination();


				if (!positions.containsKey(origin) && origin != previousOrigin && origin != previousDestination && !Svertices.contains(origin)){
					if (adjacentVertices.contains(origin)){
						if (!SiVerticesNotOnSAdjToV.contains(origin))
							SiVerticesNotOnSAdjToV.add(origin);
					}
					else{
						if (!SiVerticesNotOnSNotAdjToV.contains(origin))
							SiVerticesNotOnSNotAdjToV.add(origin);
					}
				}

				if (!positions.containsKey(destination) && destination != previousOrigin && origin != previousDestination && !Svertices.contains(destination)){
					if (adjacentVertices.contains(destination)){
						if (!SiVerticesNotOnSAdjToV.contains(destination))
							SiVerticesNotOnSAdjToV.add(destination);
					}
					else{
						if (!SiVerticesNotOnSNotAdjToV.contains(destination))
							SiVerticesNotOnSNotAdjToV.add(destination);
					}
				}
				previousOrigin = origin;
				previousDestination = destination;

			}

			log.info("Vertices not on S adjacent to v " + SiVerticesNotOnSAdjToV);
			log.info("Vertices not on S not adjacent to v " + SiVerticesNotOnSNotAdjToV);

			boolean inBlock = foundBlock.getVertices().contains(v);
			
			if (SiVerticesNotOnSAdjToV.size() > 0)
				positionVerticesAsApices(currentV, otherVertex,v, positions, SiVerticesNotOnSAdjToV, inBlock);

			List<V> apices = new ArrayList<V>();
			for (V vert : SiVertices)
				if (!SiVerticesNotOnSNotAdjToV.contains(vert))
					apices.add(vert);	

			log.info("Si " + SiVertices);

			if (SiVerticesNotOnSNotAdjToV.size() > 0)
				positionVerticesOnStraightLineSegments(Si, positions);

			//don't call draw if all vertices have been positioned
			if (SiVertices.size() != foundBlock.getVertices().size())
				draw(foundBlock, Si, SiVertices, positions);

			vis.remove(otherVertex);
			currentV = otherVertex;

		}
	}

	/**
	 * Prepares the adjacency lists map to be used to determine the face cycle of a block
	 * The path should connect v1 and v2
	 * @param v1
	 * @param v2
	 * @param foundBlock
	 * @param svertices
	 * @param blockEdgesOnS
	 * @return
	 */
	private void prepareAdjacencyLists(V v1, V v2, Graph<V, E> block, Graph<V,E> graph, List<V> sVertices,
			List<E> blockEdgesOnS) {

		//organize adjacency list so that edges between a vertex and vertices on S (not on block) 
		//are given priority

		//log.info("ordering adjacency lists");
		List<V> blockVertices = block.getVertices();

		List<E> vertexAdj = new ArrayList<E>();
		for (V v : block.getVertices()){
			//log.info("V: " + v);
			vertexAdj.clear();
			for (E e : block.adjacentEdges(v)){
				if (blockEdgesOnS.contains(e))
					vertexAdj.add(e);
				else{
					V w = e.getOrigin() == v ? e.getDestination() : e.getOrigin();
					//see if this vertex is connected to some vertex of the facial cycle
					//which isn't in the block
					boolean connectedToFacialvertex = false;
					for (E e1 : graph.adjacentEdges(w)){
						V other = e1.getOrigin() == w ? e1.getDestination() : e1.getOrigin();
						if (sVertices.contains(other) && !blockVertices.contains(other)){
							connectedToFacialvertex = true;
							break;
						}
					}
					if (connectedToFacialvertex)
						vertexAdj.add(0, e);
					else
						vertexAdj.add(e);
				}
			}

			//log.info(vertexAdj);
			block.adjacentEdges(v).clear();
			block.adjacentEdges(v).addAll(vertexAdj);
		}

	}


	//TODO check this positioning
	//in some cases, an apex get positioned on the wrong side of the facial cycle

	private void positionVerticesAsApices(V vi, V vi_1, V v, Map<V,Point2D> positions, List<V> vertices, boolean inBlock){
		//vertices should be apices of a polygon and should be placed inside the triangle whose
		//apices are vi, vi_1 and v
		Point2D viPoint = positions.get(vi);
		Point2D vi_1Point = positions.get(vi_1);
		Point2D vPoint = positions.get(v);
		

		log.info("positioning apices " + vertices);
		log.info("viPoint " + viPoint);
		log.info("vi_1Point " + vi_1Point);
		log.info("vPoint " + vPoint);

		//second idea
		//find centroid of the triangle
		////place one vertex there
		//draw a line parallel to vi,vi+1 through the controid
		//find the intersection with v,vi and v, vi+1
		//get two new triangles
		//vi, intersection, centroid
		//vi+1, other intersection centroid
		//then to the same for those two triangles
		//find their centroids
		//then form two new triangles
		//do so in a way that will guarantee that their centroid
		//is placed so that the line constantly rises or drops
		//and moves from left to right - no zig zags
		//that's the purpose of the parallel line - prevent
		//such oscillations
		//if one of the apices of a new triangle is the old centroid
		//the other apex should be on the parallel line drawn to contain it
		//intersection of the median containing the new centroid and that parallel line

		int positionedVertices = 0;

		int currentIndex = 0;
		V current;
		Map<Integer, List<Triangle>> trianglesLevelsMap = new HashMap<Integer, List<Triangle>>();
		int level = 1;
		Triangle t = new Triangle(viPoint, vi_1Point, vPoint);
		List<Triangle> levelOne = new ArrayList<Triangle>();
		levelOne.add(t);
		trianglesLevelsMap.put(1, levelOne);

		while (positionedVertices < vertices.size()){
			current = vertices.get(currentIndex);
			List<Triangle> triangles = trianglesLevelsMap.get(level);
			if (triangles.size() == 0){
				trianglesLevelsMap.remove(level);
				level ++;
				triangles = trianglesLevelsMap.get(level);
			}
			//TODO
			//this shouldn't be random and neither should the first one
			//always be selcted
			//it should be noted to which vertices the one to be positioned is connected
			//and position it so that there are no intersections
			t = triangles.get(0); 
			
			triangles.remove(t);
			//position current vertex
			Point2D centroid = Calc.triangleCentroid(t);
			positions.put(current, centroid);
			log.info("Setting position of " + current + ": " + centroid);
			positionedVertices++;
			currentIndex++;
			//divide the triangle,form new ones
			//if current triangle is at level one (vi, vi+1, v)
			//draw a line through centroid parallel to vi,vi+1
			//new triangles are vi, intersection 1, centroid
			//and vi+i, intersection 2, centroid
			//try to establish some convention regarding which point will be a,b and c
			//use that to generalize division and creation of new triangles

			Line parallelTo = Calc.lineThroughTwoPoints(t.getA(), t.getB());
			System.out.println("parallel to: " + parallelTo);
			Line parallelLine = Calc.parallelLineThroughPoint(parallelTo, centroid);
			System.out.println("parallel line: " + parallelLine);
			List<Triangle> nextLevelTriangls = trianglesLevelsMap.get(level + 1);
			Triangle t1, t2;

			if (nextLevelTriangls == null){
				nextLevelTriangls = new ArrayList<Triangle>();
				trianglesLevelsMap.put(level + 1, nextLevelTriangls);
			}

			if (level == 1){
				Line l1 = Calc.lineThroughTwoPoints(t.getA(), t.getC()); //vi and v
				Point2D intersection1 = Calc.intersectionOfLines(l1, parallelLine);
				Line l2 = Calc.lineThroughTwoPoints(t.getB(), t.getC()); //vi+1 and v
				Point2D intersection2 = Calc.intersectionOfLines(l2, parallelLine);
				t1 = new Triangle(t.getA(), intersection1, centroid);
				t2 = new Triangle(t.getB(), intersection2, centroid);
				System.out.println("triangle 1" + t1);
				System.out.println("triangle 2" + t2);
			}
			else{

				//TODO proveriti izbor temena

				//for the side which has two points on the parallel line
				//take the new point as the intersection with the median
				//with that line, new centroid, old centroid
				//for the other one, draw new parallel line
				//form the triangle taking intersection with the appropriate triangle side
				//one old vertex and new centroid

				//each triangle should be formed in the way such that
				//b and c are on the same parallel line
				//a is the remaining apex
				Line parallelSide = Calc.lineThroughTwoPoints(t.getB(), t.getC());
				Line median = Calc.lineThroughTwoPoints(t.getA(), centroid);
				Point2D intersection1 = Calc.intersectionOfLines(parallelSide, median);
				t1 = new Triangle(centroid, intersection1, t.getC());

				//the side that doesn't have C 
				Line intersectionSide = Calc.lineThroughTwoPoints(t.getA(), t.getB());
				Point2D intersection = Calc.intersectionOfLines(parallelLine, intersectionSide);
				t2 = new Triangle(t.getA(), intersection, centroid);
			}

			nextLevelTriangls.add(t1);
			nextLevelTriangls.add(t2);
		}
}

private void positionVerticesOnStraightLineSegments(List<E> Si, Map<V,Point2D> positions){
	//position vertices on straight line segments
	log.info("Positioning vertices on straight line segments");


	//the first one should be the vertex that the first two edges do not have in common
	E e1 = Si.get(0);
	E e2 = Si.get(1);
	V current = e1.getOrigin();
	if (e2.getOrigin() == current || e2.getDestination() == current)
		current = e1.getDestination();

	V firstPositioned = null;
	V secondPositioned = null;
	List<V> toPosition = new ArrayList<V>();
	V firstPositionedEncountered = null;

	if (positions.containsKey(current)){
		firstPositioned = current;
		firstPositionedEncountered = firstPositioned;
	}

	boolean allPostioned = false;
	int currentEdgeIndex = 0;
	while (!allPostioned){
		E e = Si.get(currentEdgeIndex);
		log.info(Si);
		log.info("current edge:" + e);
		V other = e.getOrigin() == current ? e.getDestination() : e.getOrigin();
		log.info("Other: " + other);
		log.info(firstPositionedEncountered);
		log.info(firstPositioned);

		if (positions.containsKey(other)){
			if (firstPositioned == null){
				firstPositioned = other;
				firstPositionedEncountered = firstPositioned;
			}
			else
				secondPositioned = other;
		}
		else if (firstPositioned != null)
			toPosition.add(other);

		if (secondPositioned == firstPositionedEncountered)
			allPostioned = true;

		if (secondPositioned != null){
			if (toPosition.size() > 0){//not to adjacent positioned vertices
				//place vertices of to position between first and second

				log.info("Positioning " + toPosition + " between " + firstPositioned + " and " + secondPositioned);
				Point2D p1 = positions.get(firstPositioned);
				Point2D p2 = positions.get(secondPositioned);
				double xDiff = (p2.getX() - p1.getX()) / (toPosition.size() + 1);
				double yDiff = (p2.getY() - p1.getY()) / (toPosition.size() + 1);
				int increment = 1;
				for (V v : toPosition){
					positions.put(v, new Point2D.Double(p1.getX() + increment * xDiff, p1.getY() + increment * yDiff));
					increment++;
				}
				toPosition.clear();
			}

			firstPositioned = secondPositioned;
			secondPositioned = null;
		}
		current = other;
		if (currentEdgeIndex == Si.size() - 1)
			currentEdgeIndex = 0;
		else
			currentEdgeIndex++;
	}
}


private V arbitraryApex(List<V> Sstar){
	int i = rand.nextInt(Sstar.size());
	return Sstar.get(i);

}


}
