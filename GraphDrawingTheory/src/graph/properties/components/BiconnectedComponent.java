package graph.properties.components;

import java.util.List;

import graph.elements.Edge;
import graph.elements.Vertex;

public class BiconnectedComponent<V extends Vertex, E extends Edge<V>> extends Component<V,E> {

	public BiconnectedComponent(List<E> edges) {
		super(edges);
	}

	@Override
	public String toString() {
		return "BiconnectedComponent [edges=" + edges + "]";
	}

}
