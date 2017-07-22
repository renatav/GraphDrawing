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
	/**
	 * @param origin Origin vertex to set
	 */
	void setOrigin(V origin);
	/**
	 * @param destination Destination vertex to set
	 */
	void setDestination(V destination);
	/**
	 * @return Weight of the edge
	 */
	int getWeight();
	/**
	 * @param weight Weight to set
	 */
	void setWeight(int weight);

}
