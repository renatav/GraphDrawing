package graph.math;

/**
 * Class represents a mathematical matrix
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
	
	/**
	 * Constructs an empty matrix with the given number of row and columns
	 * @param rows Number of rows
	 * @param columns Number of columns
	 */
	public Matrix(int rows, int columns){
		content = new double[rows][columns];
		this.rows = rows;
		this.columns = columns;
	}

	/**
	 * Sets value of a particular cell of the matrix
	 * @param rows Number of rows
	 * @param columns Number of columns
	 * @param value Value to set
	 */
	public void set(int row, int column, double value){
		content[row][column] = value;
	}
	
	/**
	 * Returns the value of a particular cell of the matrix
	 * @param rows Number of rows
	 * @param columns Number of columns
	 * @return Value of the cell at [{@code row}][@{code column}]
	 */
	public double get(int row, int column){
		return content[row][column];
	}
	
	/**
	 * Returns contents of a row of the matrix
	 * @param row Row
	 * @return Content of row with index {@code row}
	 */
	public double[] get(int row){
		return content[row];
	}
	
	/**
	 * @return Content of the matrix
	 */
	public double[][] values(){
		return content;
	}
	
	/**
	 * Prints the matrix
	 */
	public void printMatrix(){
		
		for (int i = 0; i < rows; i++){
			System.out.println("");
			for (int j = 0; j < columns; j++)
				System.out.print(content[i][j] + " ");
		}
		System.out.println("");
	}

	
}
