package graph.properties.splitting;

/**
 * An exception thrown if an inconsistency is detected while performing an algorithm
 * Signalizes an implementation error. Used for debugging.
 * @author Renata
 *
 */
public class AlgorithmErrorException extends Exception{
	
	private static final long serialVersionUID = 1L;

	public AlgorithmErrorException(String message){
		super(message);
	}
	

}
