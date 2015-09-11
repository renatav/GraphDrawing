package graph.properties.components;

import graph.elements.Edge;
import graph.elements.Vertex;

public class SplitPair<V extends Vertex, E extends Edge<V>> {
	
	private V v, u;
	private int type;
	

	public SplitPair(V v, V u) {
		super();
		this.v = v;
		this.u = u;
	}
	
	public SplitPair(V v, V u, int type) {
		super();
		this.v = v;
		this.u = u;
		this.type = type;
	}

	public V getV() {
		return v;
	}

	public void setV(V v) {
		this.v = v;
	}

	public V getU() {
		return u;
	}

	public void setU(V u) {
		this.u = u;
	}

	@Override
	public String toString() {
		return "SplitPair [v=" + v + ", u=" + u + "]";
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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
		
	

}
