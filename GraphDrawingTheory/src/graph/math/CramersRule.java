package graph.math;

public class CramersRule {
	

	public static int getDet(int[][] a) {
		int n = a.length - 1;
		if (n < 0)
			return 0;
		int M[][][] = new int[n + 1][][];
		M[n] = a; // init first, largest, M to a
		// create working arrays
		for (int i = 0; i < n; i++)
			M[i] = new int[i + 1][i + 1];
		return getDet(M, n);
	} // end method getDecDet double [][] parameter

	public static int getDet(int[][][] M, int m) {
		if (m == 0)
			return M[0][0][0];
		int e = 1;
		// init subarray to upper left mxm submatrix
		for (int i = 0; i < m; i++)
			for (int j = 0; j < m; j++)
				M[m - 1][i][j] = M[m][i][j];
		int sum = M[m][m][m] * getDet(M, m - 1);
		// walk through rest of rows of M
		for (int i = m - 1; i >= 0; i--) {
			for (int j = 0; j < m; j++)
				M[m - 1][i][j] = M[m][i + 1][j];
			e = -e;
			sum += e * M[m][i][m] * getDet(M, m - 1);
		} // end for each row of matrix
		return sum;
	} // end getDecDet double [][][], int

	public static int[] crame(int[][] m) {
		int[] result;
		if (m.length == 2) {
			result = new int[m.length];
			int D = getDet(m);
			for (int i = 0; i < m.length; i++) {
				result[i] = getDet(slide(m, i, m.length)) / D;
			}
		} else if (m.length == 3) {
			result = new int[m.length];
			int D = getDet(m);
			for (int i = 0; i < m.length; i++) {
				result[i] = (getDet(slide(m, i, m.length)) / D);
			}
		} else {
			return new int[] {};
		}
		return result;
	}

	public static int[][] slide(int[][] base, int col, int fin) {
		int[][] copy = new int[base.length][];
		for (int i = 0; i < base.length; i++) {
			int[] aMatrix = base[i];
			int aLength = aMatrix.length;
			copy[i] = new int[aLength];
			System.arraycopy(aMatrix, 0, copy[i], 0, aLength);
		}
		for (int i = 0; i < base.length; i++) {
			copy[i][col] = base[i][fin];
		}
		return copy;
	}

	public static int product(int[] a, int[] b) {
		int p = 0;
		int[] fin = new int[(a.length - 1)];
		for (int x = 0; x < fin.length; x++) {
			fin[x] = a[x] * b[x];
		}
		for (int f : fin) {
			p += f;
		}
		return p;
	}

	public static boolean check(int[] a, int[] b, int z) {
		return product(a, b) == z;
	}

	public static void info(String log) {
		System.out.println(log);
	}
	
	public static void main(String[] args) {
		int[][] matrix = new int[3][3];
		matrix[0] = new int[] { 3, 5, -1, -2 };
		matrix[1] = new int[] { 1, -4, 2, 13 };
		matrix[2] = new int[] { 2, 4, 3, 1 };
		int[] r = crame(matrix);
		info("x: " + r[0] + ", y: " + r[1] + ", z: " + r[2]);
		for (int i = 0; i < matrix.length; i++) {
			int[] base = matrix[i];
			if (check(base, r, base[3])) {
				info("System " + (i + 1) + " checks!");
			} else {
				info("System " + (i + 1) + " fails check!");
			}
		}
	}
}