package graph.math;

import Jama.Matrix;

/**
 * A class containing certain methods for working with matrices
 * @author Renata
 *
 */
public class MatrixUtil {
	
	
	/**
	 * Extracts a column from a matrix
	 * @param A Matrix
	 * @param column Column index
	 * @return An array represented the extracted column
	 */
	public static double[] getColumn(double[][] A, int column){
		int n = A.length;
		double[] ret = new double[n]; 
		for(int row = 0; row < n; row++)
		    ret[row] = A[row][column];
		return ret;
	}
	
	/**
	 * Prints the given matrix
	 * @param m Matrix
	 */
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
