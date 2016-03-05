package graph.properties.components;

import graph.elements.Edge;
import graph.elements.Vertex;

import java.util.ArrayList;
import java.util.List;

public class BiconnectedComponent<V extends Vertex, E extends Edge<V>> extends Component<V,E> {

	private List<V> vertices;
	
	public BiconnectedComponent(List<E> edges) {
		super(edges);
		vertices = new ArrayList<V>();
		for (E e : edges){
			if (!vertices.contains(e.getOrigin()))
				vertices.add(e.getOrigin());
			if (!vertices.contains(e.getDestination()))
				vertices.add(e.getDestination());
		}
	}

	public List<V> getVertices() {
		return vertices;
	}

	public void setVertices(List<V> vertices) {
		this.vertices = vertices;
	}


	@Override
	public String toString() {
		return "BiconnectedComponent [vertices=" + vertices + ", edges="
				+ edges + "]";
	}

}
