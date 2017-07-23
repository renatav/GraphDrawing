package graph.math;

import Jama.Matrix;


/**
 * Implementation of the GramSchmidt process
 * Given a matrix A, representing a set of base vectors
 * calculates the orthonormal basis
 * @author Renata
 *
 */
public class GramSchmidt {

	/**
	 * Calculates orthonormal basis gibev a matrix
	 * @param A Matrix
	 * @return Orthonormal basis
	 */
	public static Matrix execute(Matrix A){
		int n = A.getRowDimension();
		int m = A.getColumnDimension();
		double[][] Q = zeros(n,m);
		double[][] R = zeros(n,m);
		
		double[][] aVal = A.getArray();
		double[][]  V = new double[1][n];
		
		for (int j = 0; j < m; j++){
			double[] v = MatrixUtil.getColumn(aVal, j);
			
			
			for (int i = 0; i <= j -1; i++){
				
				R[i][j] = mulVectors(
						MatrixUtil.getColumn(Q,i), MatrixUtil.getColumn(aVal,j));
				
				v = substractVectors(v, mulVectorScalar(R[i][j], MatrixUtil.getColumn(Q,i)));
			}
			
			//create a matrix in order to get its norm
			V[0] = v;
			Matrix Vmat = new Matrix(V);
			R[j][j] = Vmat.norm2();
			
			for (int i = 0; i < n; i++){
				Q[i][j] = v[i]/R[j][j];
			}
		}
		
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
	

}
