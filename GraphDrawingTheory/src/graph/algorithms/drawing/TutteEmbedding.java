package graph.algorithms.drawing;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.layout.circle.CircleLayoutCalc;
import graph.util.Matrix;

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

	public Map<V, Point2D> execute (List<V> J, Point2D center, double treshold) throws NotPlanarException{


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

		Matrix<Integer> K = new Matrix<Integer>(m, m);

		for (int i = 0; i < m; i++)
			for (int j = 0; j < m; j++){

				V vi = orderedVertices.get(i);
				V vj = orderedVertices.get(j);
				if (vi == vj){
					K.set(i, j, graph.vertexDegree(vi));
				}
				else{
					int numOfEdges = graph.edgeesBetween(vi,vj).size();
					K.set(i,j,-numOfEdges);
				}
			}

		//form equesions
		//we know Vix and Viy for i <=n

		
		int numOfInner = m - n;
		
		double[][] coefficientsX = new double[numOfInner][numOfInner + 1];
		double[][] coefficientsY = new double[numOfInner][numOfInner + 1];
		
		int eqesionIndex = 0;
		
		for (int p = n; p < m; p++){
			
			double[] coefficientX = new double[numOfInner + 1]; //how number of inside vertices
			double[] coefficientY = new double[numOfInner + 1]; //how number of inside vertices
			
			double kx = 0;
			double ky = 0;
			
			for (int i = 0; i < n; i++){
				Point2D vPosition = positions.get(orderedVertices.get(i));
				double vx = vPosition.getX();
				double vy = vPosition.getY();
				kx += K.get(p, i) * vx;
				ky += K.get(p, i) * vy;
			}
			
			coefficientX[numOfInner - 1] = kx;
			coefficientY[numOfInner - 1] = ky;
			
			int index = 0;
			for (int i = n; i < m; i++){
				double c = K.get(p, i);
				coefficientX[index] = c;
				coefficientY[index] = c;
				index ++;
			}
			
			coefficientsX[eqesionIndex] = coefficientX;
			coefficientsY[eqesionIndex] = coefficientY;
				
			eqesionIndex ++;
		}
		
		//now solve using Cramer's rule
		

		return ret;



	}



}
