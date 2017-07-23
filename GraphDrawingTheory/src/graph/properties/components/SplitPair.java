package graph.properties.components;

import graph.elements.Edge;
import graph.elements.Vertex;

/**
 * Class represent a split pair of a graph.
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type 
 */
public class SplitPair<V extends Vertex, E extends Edge<V>> {
	
	/**
	 * Split pair vertices
	 */
	private V v, u;
	/**
	 * Split pair type (1 or 2)
	 */
	private int type;
	

	/**
	 * Construct a split pair consisting of the two given vertices
	 * @param v The first split pair vertex
	 * @param u The second split pair vertex
	 */
	public SplitPair(V v, V u) {
		super();
		this.v = v;
		this.u = u;
	}
	
	/**
	 * Construct a split pair consisting of the two given vertices and of the
	 * specified type
	 * @param v The first split pair vertex
	 * @param u The second split pair vertex
	 * @param type The pair's type (1 or 2)
	 */
	public SplitPair(V v, V u, int type) {
		super();
		this.v = v;
		this.u = u;
		this.type = type;
	}

	/**
	 * @return The first vertex of the pair
	 */
	public V getV() {
		return v;
	}

	/**
	 * @param v The first vertex of the pair to set
	 */
	public void setV(V v) {
		this.v = v;
	}

	/**
	 * @return The second vertex of the pair
	 */
	public V getU() {
		return u;
	}

	/**
	 * @param u The second vertex of he pair to set
	 */
	public void setU(V u) {
		this.u = u;
	}
	
	/**
	 * @return Split pair's type (1 or 2)
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type Type of the pair to set (1 or 2)
	 */
	public void setType(int type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "( " + v + ", " + u + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((u == null) ? 0 : u.hashCode());
		result = prime * result + ((v == null) ? 0 : v.hashCode());
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		SplitPair<V,E> other = (SplitPair<V,E>) obj;
		if (u.equals(other.getU()))
			return v.equals(other.getV());
		else if (u.equals(other.getV()))
			return v.equals(other.getV());
		return false;
	}

}
