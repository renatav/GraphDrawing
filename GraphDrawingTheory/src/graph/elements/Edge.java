package graph.elements;


public interface Edge<V extends Vertex> {
	
	V getOrigin();
	V getDestination();
	void setOrigin(V origin);
	void setDestination(V destination);
	int getWeight();

}
