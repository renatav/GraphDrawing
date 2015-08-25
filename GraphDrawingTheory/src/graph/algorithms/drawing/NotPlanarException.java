package graph.algorithms.drawing;

public class NotPlanarException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotPlanarException(){
		super("Graph is not planar");
	}

}
