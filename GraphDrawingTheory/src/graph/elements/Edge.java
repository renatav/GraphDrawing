package graph.elements;


/**
 * Edge of the graph
 * @author Renata
 * @param <V> The vertex type
 */
public interface Edge<V extends Vertex> {
	
	/**
	 * @return Origin of the edge
	 */
	V getOrigin();
	/**
	 * @return Destination of the edge
	 */
	V getDestination();
	void setOrigin(V origin);
	void setDestination(V destination);
	/**
	 * @return Weight of the edge
	 */
	int getWeight();
	void setWeight(int weight);

}
