package graph.exception;

/**
 * Exception which is thrown if the supplied string is not
 * in accordance with the dsl
 * @author Renata
 */
public class DSLException extends Exception{

	private static final long serialVersionUID = 1L;
	private String message;
    
 
    public DSLException(String message) {
        super(message);
        this.message = message;
    }
 
    @Override
    public String toString() {
        return message;
    }
 
    @Override
    public String getMessage() {
        return message;
    }

}
