package graph.properties.splitting;

import graph.elements.Edge;
import graph.elements.Vertex;

public class SplitPair<V extends Vertex, E extends Edge<V>> {
	
	private V v, u;
	

	public SplitPair(V v, V u) {
		super();
		this.v = v;
		this.u = u;
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
	
	

}
