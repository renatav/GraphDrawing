package graph.algorithms.crossing;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

public class CrossingNumbers<V extends Vertex, E extends Edge<V>> {
	
	
	private Graph<V,E> graph;
	
	/**
	 * The crossing number cr(G) is defined to be
		the minimum number of crossings in any drawing of G
	 */
	private int cr;
	
	/**
	 *  The pairwise crossing number of G, denoted with pcr(G), is the minimum number
	 *	of pairs of edges (e1, e2) ∈ E × E, e1 not equal e2, such that e1 and e2 determine at least
  	 * determine at least one crossing over all drawings of G
  	 */
	private int pcr;
	
	/**
	* The odd-crossing number of G, denoted with ocr(G),, is the minimum number of
	* pairs of edges (e1, e2) ∈ E × E, e1 not equal e2, such that e1 and e2 cross an odd number of times 
	* over all drawings of G
	*/

	private int ocr;
	
	public CrossingNumbers(Graph<V,E> graph){
		this.graph = graph;
	}
	
	
	public void calculateCr(){
		
	}
	
	public void calculatePcr(){
		
	}
	
	public void calculateOcr(){
		
	}

	public int getCr() {
		return cr;
	}

	public int getPcr() {
		return pcr;
	}


	public int getOcr() {
		return ocr;
	}


	public Graph<V, E> getGraph() {
		return graph;
	}


	public void setGraph(Graph<V, E> graph) {
		this.graph = graph;
	}



}
