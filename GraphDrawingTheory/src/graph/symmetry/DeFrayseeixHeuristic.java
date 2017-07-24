package graph.symmetry;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.math.CzekanovskiDiceDistance;
import graph.math.GramSchmidt;
import graph.math.MatrixUtil;
import graph.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

/**
 * A class for calculating De Frayseeix's heuristic
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type 
 */
public class DeFrayseeixHeuristic<V extends Vertex, E extends Edge<V>> {

	/**
	 * Calculates the heuristic
	 * @param graph Graph
	 * @return A list of pairs consisting of the eugenvalue and a column of the orthonormal basis
	 */
	public List<Pair<Double, double[]>>  calculate(Graph<V,E> graph){
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
					totalSum += res;
				}
			Double dpp = totalSum / (double) n;
				
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
				
			EigenvalueDecomposition eigenValueDecomposition = W.eig();
			Matrix eigenVectors = eigenValueDecomposition.getV();
			
			//isolate eigenvectors associated with strictly positive eigenvalues
			List<Integer> positiveIndexes = new ArrayList<Integer>();
			for (int i = 0; i < eigenValueDecomposition.getRealEigenvalues().length; i++){
				double eigenValue = eigenValueDecomposition.getRealEigenvalues()[i];
				if (eigenValue > 0)
					positiveIndexes.add(i);
				}
				
			//find indexes of strictly positive eigenvalues
			Matrix orthonormalBasis = GramSchmidt.execute(eigenVectors);
			List<Pair<Double, double[]>> eigenValuesVectors = new ArrayList<Pair<Double, double[]>>();
			for (Integer index : positiveIndexes){
				Double eigenValue = eigenValueDecomposition.getRealEigenvalues()[index];
				double[] column = MatrixUtil.getColumn(orthonormalBasis.getArray(), index);
				eigenValuesVectors.add(new Pair<Double, double[]>(eigenValue, column));
			}
			return eigenValuesVectors;
	}
	
}
