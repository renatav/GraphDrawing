package graph.properties.components;

/**
 * A triple as defined by Hopcroft and Tarjan and used in their triconnected splitting algorithms
 * @author Renata
 */
public class Triple {
	
	private int h;
	private int a;
	private int b;
	
	
	/**
	 * Constructs a triple with the provided h,a and b values.
	 * @param h H value
	 * @param a A value
	 * @param b B value
	 */
	public Triple(int h, int a, int b) {
		super();
		this.h = h;
		this.a = a;
		this.b = b;
	}

	/**
	 * @return H value
	 */
	public int getH() {
		return h;
	}
	
	/**
	 * @param h H value to set
	 */
	public void setH(int h) {
		this.h = h;
	}
	
	/**
	 * @return A value
	 */
	public int getA() {
		return a;
	}
	
	/**
	 * @param a A value to set
	 */
	public void setA(int a) {
		this.a = a;
	}
	
	/**
	 * @return B value
	 */
	public int getB() {
		return b;
	}
	
	/**
	 * @param b B value to set
	 */
	public void setB(int b) {
		this.b = b;
	}

	@Override
	public String toString() {
		return "Triple [h=" + h + ", a=" + a + ", b=" + b + "]";
	}
	
	
	

}
