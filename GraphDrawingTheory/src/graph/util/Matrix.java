package graph.util;


public class Matrix<T> {

	private T[][] content;
	
	@SuppressWarnings("unchecked")
	public Matrix(int rows, int columns){
		content = (T[][])new Object[rows][columns];
	}

	public void set(int row, int column, T value){
		content[row][column] = value;
	}
	
	public T get(int row, int column){
		return content[row][column];
	}
	
}
