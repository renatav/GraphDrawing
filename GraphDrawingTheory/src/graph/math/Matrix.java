package graph.math;

/**
 * @author Renata
 */
public class Matrix {

	/**
	 * Contents of the matrix
	 */
	private double[][] content;
	/**
	 * Number of rows and columns
	 */
	private int rows, columns;
	
	public Matrix(int rows, int columns){
		content = new double[rows][columns];
		this.rows = rows;
		this.columns = columns;
	}

	public void set(int row, int column, double value){
		content[row][column] = value;
	}
	
	public double get(int row, int column){
		return content[row][column];
	}
	
	public double[] get(int row){
		return content[row];
	}
	
	public double[][] values(){
		return content;
	}
	
	public void printMatrix(){
		
		for (int i = 0; i < rows; i++){
			System.out.println("");
			for (int j = 0; j < columns; j++)
				System.out.print(content[i][j] + " ");
		}
		System.out.println("");
	}

	
}
