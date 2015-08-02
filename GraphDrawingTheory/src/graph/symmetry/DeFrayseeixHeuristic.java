package graph.symmetry;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTable.PrintMode;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

public class DeFrayseeixHeuristic<V extends Vertex, E extends Edge<V>> {

	//http://math.nist.gov/javanumerics/jama/doc/
	//http://math.nist.gov/javanumerics/jama/
	//https://code.google.com/p/evolutionary-algorithm/source/browse/trunk/%20evolutionary-algorithm/jaea/src/optimization/operator/individual/gramschmidt.java?r=3

	public void calculate(Graph<V,E> graph){
		//		CzekanovskiDiceDistance<V, E> distance = new CzekanovskiDiceDistance<>(graph);
		//		List<V> vertices = graph.getVertices();
		//		int n = vertices.size();
		//		double[][] data = new double[n][n];
		//		
		//		Map<V,Double> dviMap = new HashMap<V, Double>();
		//		
		//		//calculate d^2(vi,.) for all vertices
		//		//and calculate d^2(.,.)
		//		Double totalSum = (double) 0;
		//		for (V vi : vertices){
		//			Double sum = (double) 0;
		//			for (V vj : vertices)
		//				sum += distance.getDistance(vi, vj);
		//			Double res = sum / (double) n;
		//			dviMap.put(vi, res);
		//			System.out.println(res);
		//			totalSum += res;
		//		}
		//		Double dpp = totalSum / (double) n;
		//		System.out.println(dpp);
		//		
		//		
		//		//now fill matrix
		//		for (int i = 0; i < n; i++){
		//			V vi = vertices.get(i);
		//			for (int j = 0; j < n; j++){
		//				V vj = vertices.get(j);
		//				//Wi,j = 1/2(d^2(vi,.) + d^2(vj,.) - d^2(.,.))
		//				data[i][j] = (dviMap.get(vi) + dviMap.get(vj) - dpp) / (double)2;
		//			}
		//		}
		//		
		//		Matrix W = new Matrix(data);
		//		printMatric(W);
		//		EigenvalueDecomposition eigenValue = W.eig();
		//		Matrix diag = eigenValue.getD();
		//		printMatric(diag);
		//		System.out.println("ff");
		//		for (int i = 0; i < eigenValue.getImagEigenvalues().length; i++)
		//			System.out.println(eigenValue.getImagEigenvalues()[i]);
		//		System.out.println("ff");
		//		for (int i = 0; i < eigenValue.getImagEigenvalues().length; i++)
		//			System.out.println(eigenValue.getRealEigenvalues()[i]);



	}


	private static void printMatrix(Matrix m){
		double[][] values = m.getArray();
		for (int i = 0; i < values.length; i++){
			System.out.println("");
			for (int j = 0; j < values.length; j++)
				System.out.print(Math.round(values[i][j] * 1000)/(double)1000 + " ");
		}
		System.out.println("");
	}

	public static void main(String[] args){
		//Tesitram da li  su to prave funkcije

		double[][] data = new double[3][3];
		data[0] = new double[] {1,3,3};
		data[1] = new double[] {-3,-5,-3};
		data[2] = new double[] {3,3,1};
		Matrix A = new Matrix(data);
		
		EigenvalueDecomposition eigenValue = A.eig();
		
	//	for (int i = 0; i < eigenValue.getImagEigenvalues().length; i++)
		//		System.out.println(eigenValue.getRealEigenvalues()[i]); //1, -2, -2 T
		
		Matrix V = eigenValue.getV(); //matrica eigenvectora (P)
		//printMatrix(V);
		//System.out.println(V.getArray());
		Matrix D = eigenValue.getD();
		//printMatrix(D);
		
		double[][] testGS = new double[2][2];
		testGS[0] = new double[]{4,-2};
		testGS[1] = new double[]{3,1};
		printMatrix(new Matrix(testGS));
		gramSchmidt(new Matrix(testGS));
		
		
	}	
	/*
	 * 
	 */
	
	private static Matrix gramSchmidt(Matrix A){
		int n = A.getRowDimension();
		double[][] Q = zeros(n,n);
		double[][] R = zeros(n,n);
		
		double[][] aVal = A.getArray();
		double[][]  V = new double[n][n];
		
		for (int j = 0; j < n; j++){
			double[] v = getColumn(aVal, j);
			
			//V[j] = aVal[j];
			
			for (int i = 0; i <= j -1; i++){
				//ri,j = qi*aj;
				R[i][j] = mulVectors(Q[i], aVal[j]);
				v = substractVectors(v, mulVectorScalar(R[i][j], Q[i]));
			}
			
			//pretvori v u matricu zbog norme
			
			Matrix Vmat = new Matrix(V);
			R[j][j] = Vmat.norm2();
			Q[j] = mulVectorScalar((double)1/R[j][j], V[j]);  //da li i ovo treba da je kolona, pazi...
		}
		
		printMatrix(new Matrix(Q));
		printMatrix(new Matrix(R));
		
		return new Matrix(Q);
		
	}
	
	private static double mulVectors(double[] a, double[]b){
		int size = a.length;
		double res = 0;
		for (int i = 0; i < size; i++)
			res += a[i] * b[i];
		return res;
	}
	
	private static double[][] zeros(int n, int m){
		double[][] ret = new double[n][m];
		for (int i = 0; i < n; i++)
			for (int j = 0; j < m; j++)
				ret[i][j] = 0;
		return ret;
	}	
	
	private static double[] mulVectorScalar(double s, double[] v){
		double[] ret = new double[v.length];
		for (int i = 0; i < v.length; i++)
			ret[i] = s * v[i];
		return ret;
	}
	
	private static double[] substractVectors(double[] v1, double[] v2){
		double[] ret = new double[v1.length];
		for (int i = 0; i < v1.length; i++)
			ret[i] = v1[i] - v2[i];
		return ret;
	}
	
	private static double[] getColumn(double[][] A, int column){
		double[] ret = new double[A.length]; 
		for(int row = 0; row < A.length; row++)
		    ret[row] = A[row][column];
		return ret;
	}
	
	
	
}
