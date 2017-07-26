package graph.properties.components;

import graph.elements.Vertex;

/**
 * A separation pair, used in Hopcroft-Tarjan splitting
 * @author Renata
 * @param <V> The vertex type
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
	
	
	/**
	 * Creates a separation pair containing two provided vertices of the specified type
	 * @param a The first vertex
	 * @param b The second vertex
	 * @param type Type of the split pair (1 or 2)
	 */
	public SeparationPair(V a, V b, int type){
		this.a = a;
		this.b = b;
		this.type = type;
	}


	/**
	 * @return The first vertex of the pair
	 */
	public V getA() {
		return a;
	}

	/**
	 * @param a The first vertex of the pair to set
	 */
	public void setA(V a) {
		this.a = a;
	}

	/**
	 * @return The second vertex of the pair
	 */
	public V getB() {
		return b;
	}

	/**
	 * @param b The second vertex of the pair to set
	 */
	public void setB(V b) {
		this.b = b;
	}


	/**
	 * @return Type of the pair (1 or 2)
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type Type of the component to set (1 or 2)
	 */
	public void setType(int type) {
		this.type = type;
	}


	@Override
	public String toString() {
		return "SeparationPair [a=" + a + ", b=" + b + ", type=" + type + "]";
	}
	
	

}
