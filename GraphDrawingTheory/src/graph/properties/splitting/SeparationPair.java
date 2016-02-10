package graph.properties.splitting;

import graph.elements.Vertex;

public class SeparationPair<V extends Vertex> {
	
	private V a;
	private V b;
	private int type;
	
	
	public SeparationPair(V a, V b, int type){
		this.a = a;
		this.b = b;
		this.type = type;
	}


	public V getA() {
		return a;
	}


	public void setA(V a) {
		this.a = a;
	}


	public V getB() {
		return b;
	}


	public void setB(V b) {
		this.b = b;
	}


	public int getType() {
		return type;
	}


	public void setType(int type) {
		this.type = type;
	}


	@Override
	public String toString() {
		return "SeparationPair [a=" + a + ", b=" + b + ", type=" + type + "]";
	}
	
	

}
