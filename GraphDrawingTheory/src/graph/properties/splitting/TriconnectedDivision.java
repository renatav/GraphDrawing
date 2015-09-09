package graph.properties.splitting;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.properties.components.BiconnectedComponent;
import graph.properties.components.HopcroftSplitComponent;
import graph.properties.components.SplitComponentType;
import graph.trees.DFSTree;
import graph.util.Pair;
import graph.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Implementation of Hopcroft's and Tarjan's algorithm which divides a graph
 * into triconnected components and finds separation pairs
 * @author xx
 *
 */

public class TriconnectedDivision<V extends Vertex, E extends Edge<V>> {



	private Graph<V,E> graph;

	public TriconnectedDivision(Graph<V,E> graph){
		this.graph = graph;
	}
	
	
	public void execute(Graph<V,E> graph){
		SeparationPairSplitting<V, E> splitting = new SeparationPairSplitting<V,E>();
		Class edgeClass = graph.getEdges().get(0).getClass();
		splitting.findSeaparationPairs(graph, edgeClass);
	}
	

	private void triconnected(Graph<V,E> graph){

		List<HopcroftSplitComponent> splitComponents = new ArrayList<HopcroftSplitComponent>();

		//remove all multiedges
		//and create triple bonds

		Graph<V,E> gPrim= graph;

		List<List<E>> multiedges = graph.listMultiEdges();
		if (multiedges.size() > 0){

			gPrim = Util.copyGraph(graph);
			for (List<E> multi : multiedges){

				HopcroftSplitComponent<V, E> tripleBond = new HopcroftSplitComponent<V,E>(SplitComponentType.TRIPLE_BOND, multi, null);
				splitComponents.add(tripleBond);

				//remove all but one edge (which represents all three)
				for (int i = 1; i < multi.size(); i++){
					gPrim.removeEdge(multi.get(i));
				}
			}

		}

		//find biconnected components of G'
		List<BiconnectedComponent<V, E>> biconnectedComponents = gPrim.listBiconnectedComponents();



	}




}
