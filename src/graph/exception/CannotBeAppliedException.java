package graph.exception;

public class CannotBeAppliedException extends Exception{
	  /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private String message = "Algorith cannot be applied";
	    
	    public CannotBeAppliedException() {
	        super();
	    }
	 
	    public CannotBeAppliedException(String message) {
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
