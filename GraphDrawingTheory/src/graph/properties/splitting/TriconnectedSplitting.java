package graph.properties.splitting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.properties.components.HopcroftTarjanSplitComponent;
import graph.properties.components.SplitTriconnectedComponentType;

/**
 * Forms triconnected components of a graph using Hopcroft-Tarjan splitting 
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type
 */
public class TriconnectedSplitting<V extends Vertex, E extends Edge<V>>  {
	
	/**A map with virtual edges (separation pairs) as  keys and a list of its
	 * split components as values
	 */
	private Map<E, List<HopcroftTarjanSplitComponent<V, E>>> virtualEdgesSplitComponentsMap;
	
	/**
	 * Hopcroft-Tarjan's algorithm for dividing a graph into split components and detecting separation pairs
	 */
	private HopcroftTarjanSplitting<V, E> hopcroftTarjanSplitting;
	
	
	public TriconnectedSplitting(Graph<V,E> graph){
		hopcroftTarjanSplitting = new HopcroftTarjanSplitting<V,E>(graph, false);
		virtualEdgesSplitComponentsMap = new HashMap<E, List<HopcroftTarjanSplitComponent<V, E>>>();
	}
	
	
	public List<HopcroftTarjanSplitComponent<V, E>> formTriconnectedComponents(List<HopcroftTarjanSplitComponent<V, E>> splitComponents){
		
		initVirtualEdgesComponentsMap(splitComponents);
		
		List<HopcroftTarjanSplitComponent<V,E>> triconnectedComponents = new ArrayList<HopcroftTarjanSplitComponent<V,E>>();
		
		List<HopcroftTarjanSplitComponent<V, E>> processedComponents = new ArrayList<HopcroftTarjanSplitComponent<V,E>>();

		for (HopcroftTarjanSplitComponent<V, E> splitComponent : splitComponents){
			if (processedComponents.contains(splitComponent))
				continue;
			if (splitComponent.getType() == SplitTriconnectedComponentType.TRICONNECTED_GRAPH)
				triconnectedComponents.add(splitComponent);
			else {
				//if the component is a ring or a bond, merge it with other components to get a ring or a bond
				//depending on the type of the component
				if (splitComponent.getType() == SplitTriconnectedComponentType.TRIPLE_BOND){
					HopcroftTarjanSplitComponent<V, E> bond = new HopcroftTarjanSplitComponent<V,E>();
					bond.getEdges().addAll(splitComponent.getEdges());
					bond.getVirtualEdges().addAll(splitComponent.getVirtualEdges());
					bond.setType(SplitTriconnectedComponentType.BOND);

					//find other triple bonds and merge them
					int numOfMeges = 0;
					//all virtual edges should be the same, get one
					E virtualEdge = splitComponent.getVirtualEdges().get(0);
					for (HopcroftTarjanSplitComponent<V, E> component : virtualEdgesSplitComponentsMap.get(virtualEdge))
						if (component.getType() == SplitTriconnectedComponentType.TRIPLE_BOND && component != splitComponent){
							numOfMeges ++;
							bond.getEdges().addAll(component.getEdges());
							bond.getVirtualEdges().addAll(component.getVirtualEdges());
							processedComponents.add(component);
						}

					//remove virtual edges where the merge took place
					//one merge = remove two edges
					if (numOfMeges > 0)
						for (int i = 0; i < 2*numOfMeges; i++){
							bond.getEdges().remove(virtualEdge);
							bond.getVirtualEdges().remove(virtualEdge);
						}
					triconnectedComponents.add(bond);							
				}
				//the component is a triangle, merge it with other triangles as far as possible
				else{
					//TODO proveriti ovo spajanje... kako uopste treba da se radi
					HopcroftTarjanSplitComponent<V, E> ring = new HopcroftTarjanSplitComponent<V,E>();
					ring.getEdges().addAll(splitComponent.getEdges());
					ring.getVirtualEdges().addAll(splitComponent.getVirtualEdges());
					ring.setType(SplitTriconnectedComponentType.RING);
					List<E> edgesToBeMerged = new ArrayList<E>(ring.getVirtualEdges());
					List<E> newVirtualEdges = new ArrayList<E>();
					boolean merged;
					while (edgesToBeMerged.size() > 0){
						newVirtualEdges.clear();

						for (E e : edgesToBeMerged){
							merged = false;
								System.out.println("virtual edge to merge on " + e);
							System.out.println(virtualEdgesSplitComponentsMap.get(e));
							for (HopcroftTarjanSplitComponent<V, E> component : virtualEdgesSplitComponentsMap.get(e)){
								if (processedComponents.contains(component)|| component == splitComponent || 
										component.getType() != SplitTriconnectedComponentType.TRIANGLE || 
										component.getVirtualEdges().size() == component.getEdges().size()) // from looking at the example in chiba's paper (TODO - see if this is correct)
									continue;
								merged = true;
								//	System.out.println("merging with component " + component);
								for (E componentEdge : component.getEdges())
									if (component.getVirtualEdges().contains(componentEdge)){
										if (componentEdge != e){
											newVirtualEdges.add(componentEdge);
											ring.addVirtualEdge(componentEdge);
										}
									}
									else
										ring.addEdge(componentEdge);
								processedComponents.add(component);
							}
							if (merged){
								ring.getEdges().remove(e);
								ring.getVirtualEdges().remove(e);
							}
						}
						edgesToBeMerged.clear();
						edgesToBeMerged.addAll(newVirtualEdges);
					}
					triconnectedComponents.add(ring);
				}
			}
			processedComponents.add(splitComponent);
		}
		return triconnectedComponents;
	}

	public List<HopcroftTarjanSplitComponent<V, E>> formTriconnectedComponents(){
		
		try {
			hopcroftTarjanSplitting.execute();
		} catch (AlgorithmErrorException e1) {
		}
		List<HopcroftTarjanSplitComponent<V, E>> splitComponenets = hopcroftTarjanSplitting.getSplitComponents();
		return formTriconnectedComponents(splitComponenets);
	}
	
	private void initVirtualEdgesComponentsMap(List<HopcroftTarjanSplitComponent<V, E>>  components){
		virtualEdgesSplitComponentsMap.clear();
		for (HopcroftTarjanSplitComponent<V, E> splitComponent : components){
			for (E e : splitComponent.getVirtualEdges()){
				if (!virtualEdgesSplitComponentsMap.containsKey(e))
					virtualEdgesSplitComponentsMap.put(e, new ArrayList<HopcroftTarjanSplitComponent<V, E>>());
				if (!virtualEdgesSplitComponentsMap.get(e).contains(splitComponent))
					virtualEdgesSplitComponentsMap.get(e).add(splitComponent);
			}
		}
	}


}
