package graph.algorithms.drawing;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.math.CircleLayoutCalc;
import graph.math.CramersRule;
import graph.math.Matrix;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TutteEmbedding<V extends Vertex, E extends Edge<V>> {


	private Graph<V,E> graph;

	public TutteEmbedding(Graph<V,E> graph){
		this.graph = graph;
	}

	public Map<V, Point2D> execute (List<V> J, Point2D center, double treshold) {

		Map<V, Point2D> ret = new HashMap<V, Point2D>();

		//		BoyerMyrvoldPlanarity<V, E> planarity = new BoyerMyrvoldPlanarity<V,E>();
		//		if (!planarity.isPlannar(graph))
		//			throw new NotPlanarException();
		//		
		//		List<V> J = planarity.getOutsideFace();

		//calculate positions of the outside face

		CircleLayoutCalc<V> circleCalc = new CircleLayoutCalc<V>();
		double radius = circleCalc.calculateRadius(J, treshold);
		Map<V,Point2D> positions = circleCalc.calculatePosition(J, radius, center);
		ret.putAll(positions);

		//now calculate barycentric coordinates of the other vertices
		//order vertices in such way that those on the orside face - J are placed first

		List<V> orderedVertices = new ArrayList<V>();
		for (V v : J)
			orderedVertices.add(v);

		//now add the inner vertices
		for (V v : graph.getVertices())
			if (!J.contains(v))
				orderedVertices.add(v);

		
		//form matrix K

		int m = orderedVertices.size();
		int n = J.size();
		if (n == m)
			return ret;

		Matrix K = new Matrix(m, m);

		for (int i = 0; i < m; i++)
			for (int j = 0; j < m; j++){
				V vi = orderedVertices.get(i);
				V vj = orderedVertices.get(j);
				if (vi == vj){
					K.set(i, j, (double) graph.vertexDegree(vi));
				}
				else{
					int numOfEdges = graph.edgeesBetween(vi,vj).size();
					K.set(i,j, (double) -numOfEdges);
				}
			}
		
	//	System.out.println("K:");
		//K.printMatrix();

		//form equesions
		//we know Vix and Viy for i <=n

		
		int numOfInner = m - n;
		
		Matrix aX = new Matrix(numOfInner, numOfInner);
		Matrix aY = new Matrix(numOfInner, numOfInner);
		Matrix bX = new Matrix(1, numOfInner);
		Matrix bY = new Matrix(1, numOfInner);
		
		int eqesionIndex = 0;
		
		for (int p = n; p < m; p++){
			
			double kx = 0;
			double ky = 0;
			
			for (int i = 0; i < n; i++){
				Point2D vPosition = positions.get(orderedVertices.get(i));
				double vx = vPosition.getX();
				double vy = vPosition.getY();
				kx += K.get(p, i) * vx;
				ky += K.get(p, i) * vy;
				
			}
			
		//	System.out.println("kx " + kx);
		//	System.out.println("ky " + ky);
			
			bX.set(0, eqesionIndex, -kx);
			bY.set(0, eqesionIndex, -ky);
			
			int index = 0;
			for (int i = n; i < m; i++){
				double c = K.get(p, i);
				aX.set(eqesionIndex, index, c);
				aY.set(eqesionIndex, index, c);
				index ++;
			}
			
			eqesionIndex ++;
		}
		
		
//		System.out.println("Coefficient x");
//		aX.printMatrix();
//		bX.printMatrix();
//		System.out.println("Coefficient y");
//		aY.printMatrix();
//		bY.printMatrix();
		
		//now solve using Cramer's rule
		double[] xCoords = CramersRule.cramers(aX.values(), bX.values()[0]);
		double[] yCoords = CramersRule.cramers(aY.values(), bY.values()[0]);
		
		
		int index = 0;
		for (int i = n; i < m; i++){
			V v = orderedVertices.get(i);
			Point2D position = new Point2D.Double(xCoords[index], yCoords[index]);
			ret.put(v, position);
			index++;
			
		}

		return ret;
	}

}
