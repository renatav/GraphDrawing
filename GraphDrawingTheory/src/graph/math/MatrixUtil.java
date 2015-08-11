package graph.math;

import Jama.Matrix;

public class MatrixUtil {
	
	
	public static double[] getColumn(double[][] A, int column){
		int n = A.length;
		double[] ret = new double[n]; 
		for(int row = 0; row < n; row++)
		    ret[row] = A[row][column];
		return ret;
	}
	
	public static void printMatrix(Matrix m){
		double[][] values = m.getArray();
		for (int i = 0; i < m.getRowDimension(); i++){
			System.out.println("");
			for (int j = 0; j < m.getColumnDimension(); j++)
				System.out.print(Math.round(values[i][j] * 1000)/(double)1000 + " ");
		}
		System.out.println("");
	}
}
