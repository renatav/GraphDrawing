package graph.math;

/**
 * A line defined with k and n parameters, where y=k*x +n
 * @author Renata
 */
public class Line {

	private Double k;
	private Double n;
	
	public Line(Double k, Double n) {
		super();
		this.k = k;
		this.n = n;
	}
	
	public Double getK() {
		return k;
	}
	public void setK(Double k) {
		this.k = k;
	}
	public Double getN() {
		return n;
	}
	public void setN(Double n) {
		this.n = n;
	}

	@Override
	public String toString() {
		return "Line [k=" + k + ", n=" + n + "]";
	}
}
