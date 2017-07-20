package graph.properties.components;

import graph.elements.Vertex;

/**
 * A separation pair, used in Hopcroft-Tarjan splitting
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type 
 */
public class SeparationPair<V extends Vertex> {
	
	/**
	 * The first vertex of the par
	 */
	private V a;
	/**
	 * The second vertex of the pair
	 */
	private V b;
	/**
	 * Separation pair type (1 or 2)
	 */
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
