package graph.elements;

public interface Edge<V extends Vertex> {
	
	V getOrigin();
	V getDestination();
	int getWeight();

}
