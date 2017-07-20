package graph.math;

/**
 * A class which provides an implementation of Cramer's rule for solving
 * a system of equations
 * @author Renata
 */
public class CramersRule {

	private static double determinant(double A[][], int N) {
		
		double[][] m;
		double res;
		if(N == 1)
			res = A[0][0];
		else if(N == 2) {
			res = (A[0][0]*A[1][1]) - (A[1][0]*A[0][1]);
		}
		else{
			res=0; 
			for(int j1=0;j1<N;j1++) { 
				m = new double[N-1][]; 
				for(int k=0;k<(N-1);k++)
					m[k] = new double[N-1];
				for(int i=1;i<N;i++) {
					int j2=0; 
					for(int j=0;j<N;j++) { 
						if(j == j1)
							continue; 
						m[i-1][j2] = A[i][j]; 
						j2++;
					} 
				} 
				res += Math.pow(-1.0,1.0+j1+1.0)* A[0][j1] * determinant(m,N-1);
			}
		}
		return res;
	}

	/**
	 * Given left-hand side equation matrix and right-hand side vector, solves
	 * the system of equations
	 * @param A Right-hand side matrix
	 * @param B Left-hand side vector
	 * @return Array containing the solution of the system of equations
	 */
	public static double[] cramers(double A[][],double B[]) {
		
		int N = A.length;
		double temp[][] = new double[N][N];
		double x[] = new double[N]; 
		for(int i=0;i<N;i++) {
			for(int j=0;j<N;j++){ 
				for(int k=0;k<N;k++){
					if(k == i) 
						temp[j][k] = B[j];
					else temp[j][k] = A[j][k];
				}
			}
			x[i]=determinant(temp,N)/determinant(A,N);
		}
		for(int i=0;i<N;i++){ 
			System.out.println(x[i]);
		} 
		return x;
	}

}