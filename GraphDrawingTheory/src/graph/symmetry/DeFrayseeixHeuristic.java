package graph.symmetry;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

public class DeFrayseeixHeuristic<V extends Vertex, E extends Edge<V>> {
	
//http://math.nist.gov/javanumerics/jama/doc/
//http://math.nist.gov/javanumerics/jama/
//https://code.google.com/p/evolutionary-algorithm/source/browse/trunk/%20evolutionary-algorithm/jaea/src/optimization/operator/individual/gramschmidt.java?r=3
	
	public void calculate(Graph<V,E> graph){
		CzekanovskiDiceDistance<V, E> distance = new CzekanovskiDiceDistance<>(graph);
		List<V> vertices = graph.getVertices();
		int n = vertices.size();
		double[][] data = new double[n][n];
		
		Map<V,Double> dviMap = new HashMap<V, Double>();
		
		//calculate d^2(vi,.) for all vertices
		//and calculate d^2(.,.)
		Double totalSum = (double) 0;
		for (V vi : vertices){
			Double sum = (double) 0;
			for (V vj : vertices)
				sum += distance.getDistance(vi, vj);
			Double res = sum / (double) n;
			dviMap.put(vi, res);
			System.out.println(res);
			totalSum += res;
		}
		Double dpp = totalSum / (double) n;
		System.out.println(dpp);
		
		
		//now fill matrix
		for (int i = 0; i < n; i++){
			V vi = vertices.get(i);
			for (int j = 0; j < n; j++){
				V vj = vertices.get(j);
				//Wi,j = 1/2(d^2(vi,.) + d^2(vj,.) - d^2(.,.))
				data[i][j] = (dviMap.get(vi) + dviMap.get(vj) - dpp) / (double)2;
			}
		}
		
		Matrix W = new Matrix(data);
		EigenvalueDecomposition eigenValue = W.eig();
		Matrix diag = eigenValue.getD();
		System.out.println("ff");
		for (int i = 0; i < eigenValue.getImagEigenvalues().length; i++)
			System.out.println(eigenValue.getImagEigenvalues()[i]);
		System.out.println("ff");
		for (int i = 0; i < eigenValue.getImagEigenvalues().length; i++)
			System.out.println(eigenValue.getRealEigenvalues()[i]);
	}
}
