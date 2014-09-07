package graph.exception;

public class AlgorithmCannotBeAppliedException extends Exception{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String message = "Algorith cannot be applied";
    
    public AlgorithmCannotBeAppliedException() {
        super();
    }
 
    public AlgorithmCannotBeAppliedException(String message) {
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
