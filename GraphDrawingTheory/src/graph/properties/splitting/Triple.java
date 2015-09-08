package graph.properties.splitting;

public class Triple {
	
	
	private int h;
	private int a;
	private int b;
	
	
	public Triple(int h, int a, int b) {
		super();
		this.h = h;
		this.a = a;
		this.b = b;
	}
	
	public int getH() {
		return h;
	}
	public void setH(int h) {
		this.h = h;
	}
	public int getA() {
		return a;
	}
	public void setA(int a) {
		this.a = a;
	}
	public int getB() {
		return b;
	}
	public void setB(int b) {
		this.b = b;
	}

	@Override
	public String toString() {
		return "Triple [h=" + h + ", a=" + a + ", b=" + b + "]";
	}
	
	
	

}
