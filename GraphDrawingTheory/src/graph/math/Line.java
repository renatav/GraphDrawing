package graph.math;

/**
 * A line defined with k and n parameters, where y=k*x +n
 * @author Renata
 */
public class Line {

	private Double k;
	private Double n;
	
	/**
	 * Constructs a line given k and n parameters
	 * @param k k line parameter
	 * @param n n line parameter
	 */
	public Line(Double k, Double n) {
		super();
		this.k = k;
		this.n = n;
	}
	
	/**
	 * @return k parameter
	 */
	public Double getK() {
		return k;
	}
	
	/**
	 * @param k k value to set
	 */
	public void setK(Double k) {
		this.k = k;
	}
	
	/**
	 * @return n parameter
	 */
	public Double getN() {
		return n;
	}
	
	/**
	 * @param n n value to set
	 */
	public void setN(Double n) {
		this.n = n;
	}

	@Override
	public String toString() {
		return "Line [k=" + k + ", n=" + n + "]";
	}
}
