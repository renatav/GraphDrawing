package graph.symmetry.nauty;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.symmetry.Permutation;
import graph.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of McKay's canonical graph labeling algorithm
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type 
 */
public class McKayGraphLabelingAlgorithm<V extends Vertex, E extends Edge<V>> {


	private Graph<V,E> graph;
	private BinaryRepresentation<V, E> binaryRepresenatation;


	public McKayGraphLabelingAlgorithm(Graph<V,E> graph){
		this.graph = graph;
	}

	public void execute(){

		OrderedPartition<V> pi = new OrderedPartition<V>(graph.getVertices());
		binaryRepresenatation = new BinaryRepresentation<V,E>(graph);
		OrderedPartition<V> refined = refinementProcedure(pi);
		SearchTree<V> tree = createSearchTree(refined);
		List<SearchTreeNode<V>> terminalNodes = tree.getTerminalNodes();

		//canonicalIsomorphism(terminalNodes);
		findAutomorphisms(terminalNodes);

	}
	
	/**
	 * Finds automorphisms of the graph
	 * @return A list of graph's automorphisms
	 */
	public List<Permutation> findAutomorphisms(){

		OrderedPartition<V> pi = new OrderedPartition<V>(graph.getVertices());
		binaryRepresenatation = new BinaryRepresentation<V,E>(graph);
		OrderedPartition<V> refined = refinementProcedure(pi);
		SearchTree<V> tree = createSearchTree(refined);
		List<SearchTreeNode<V>> terminalNodes = tree.getTerminalNodes();

		//canonicalIsomorphism(terminalNodes);
		return findAutomorphisms(terminalNodes);
		
	}

	private OrderedPartition<V> refinementProcedure(OrderedPartition<V> pi){

		OrderedPartition<V> tau = new OrderedPartition<V>(pi.getPartition());
		List<Pair<List<V>,List<V>>> B = new ArrayList<Pair<List<V>, List<V>>>();
		Map<Integer, List<V>> degreesMap = new HashMap<Integer,List<V>>();

		while (true){
			B.clear();
			for (List<V> Vi : tau.getPartition()){
				for (List<V> Vj : tau.getPartition()){
					//check if Vj shatters Vi
					int deg = deg(Vi.get(0), Vj);
					for (int i = 1; i < Vi.size(); i++){
						if (deg(Vi.get(i), Vj) != deg){
							B.add(new Pair<List<V>,List<V>>(Vi, Vj));
							break;
						}
					}
				}
			}

			if (B.size() == 0)
				break;
			
			//System.out.println("B: " + B);

			//now find the minimum element
			Pair<List<V>, List<V>> minimalPair = findMinimal(B);

			//System.out.println("Minimal pair is " + minimalPair );

			//now replace Vi with X1,X2,...Xt
			degreesMap.clear();
			List<V> Vi = minimalPair.getKey();
			List<V> Vj = minimalPair.getValue();

			List<Integer> degrees = new ArrayList<Integer>();
			for (V v : Vi){
				Integer deg = deg(v, Vj);
				List<V> verticesWithDegree = degreesMap.get(deg);
				if (verticesWithDegree == null){
					verticesWithDegree = new ArrayList<V>();
					degreesMap.put(deg, verticesWithDegree);
				}
				verticesWithDegree.add(v);
				if (!degrees.contains(deg))
					degrees.add(deg);
			}

			//sort, to insert those with lower degrees first
			Collections.sort(degrees);

			List<List<V>> replacements = new ArrayList<List<V>>();
			for (Integer degree : degrees){
				replacements.add(degreesMap.get(degree));
			}

			tau.replace(Vi, replacements);

		}
		return tau;

	}


	/*
	 * For lexicographic total order
	 * (a,b) <= (c,d) if a<c or a=c and b<=d
	 */
	private Pair<List<V>, List<V>> findMinimal(List<Pair<List<V>,List<V>>> B){
		Pair<List<V>, List<V>> minimalPair = null;
		String minimalBinary1 = null;
		String minimalBinary2 = null;

		for (Pair<List<V>, List<V>> separationPair : B){

			String binary1 = binaryRepresenatation.binaryRepresenatation(separationPair.getKey());

			if (minimalPair == null){
				minimalPair = separationPair;
				minimalBinary1 = binary1;
			}
			else{

				//compare current to minimal

				if (binary1.compareTo(minimalBinary1) < 0){
					minimalPair = separationPair;
					minimalBinary1 = binary1;
					minimalBinary2 = null;
				}

				else if (binary1.compareTo(minimalBinary1) == 0){
					if (minimalBinary2 == null)
						minimalBinary2 =  binaryRepresenatation.binaryRepresenatation(minimalPair.getValue());

					String binary2 =  binaryRepresenatation.binaryRepresenatation(separationPair.getValue());
					if (binary2.compareTo(minimalBinary2) <= 0){
						minimalPair = separationPair;
						minimalBinary1 = binary1;
						minimalBinary2 = binary2;
					}

				}
			}

		}

		//System.out.println("Minimap pair is: " + minimalPair.getKey() + " " + minimalPair.getValue());
		return minimalPair;

	}

	private SearchTree<V> createSearchTree(OrderedPartition<V> rootPartition){
		//System.out.println("Root " + rootPartition);
		SearchTree<V> tree = new SearchTree<V>(rootPartition);
		SearchTreeNode<V> root = tree.getRoot();
		createSearchTree(root);
		return tree;

	}

	private void createSearchTree(SearchTreeNode<V> currentNode){
		//split tree note, create children, process children
		OrderedPartition<V> currentPartition = currentNode.getNodePartition();
		List<V> firstNontrivialrPart = currentPartition.getFirstNontrivialPart();
		if (firstNontrivialrPart == null)
			return;
		//System.out.println("Current partition: " + currentPartition);
		for (V u : firstNontrivialrPart){
			//System.out.println("Splitting by " + u);
			OrderedPartition<V> partition = splitPartition(u, currentPartition);
			partition = refinementProcedure(partition);
			//System.out.println(partition);
			new SearchTreeNode<V>(partition, u, currentNode);
		}
		for (SearchTreeNode<V> node : currentNode.getChildren())
			createSearchTree(node);

	}

	private OrderedPartition<V> splitPartition(V u, OrderedPartition<V> pi){

		//find part which contains u
		List<V> Vi = pi.partContainingVertex(u);
		OrderedPartition<V> piPrim = new OrderedPartition<V>();
		int i = pi.getPartition().indexOf(Vi);
		for (int j = 0; j < pi.getPartition().size(); j++){
			List<V> currentPart = pi.getPartition().get(j);
			if (j != i){
				piPrim.addPart(currentPart);
				continue;
			}
			//add trivial partition {u}
			//add Vi/u
			List<V> uPart = new ArrayList<V>();
			uPart.add(u);
			piPrim.addPart(uPart);
			List<V> ViPart = new ArrayList<V>();
			ViPart.addAll(Vi);
			ViPart.remove(u);
			piPrim.addPart(ViPart);

		}

		return piPrim;
	}


	@SuppressWarnings("unused")
	private OrderedPartition<V> canonicalIsomorphism(List<SearchTreeNode<V>> terminalNodes){
		OrderedPartition<V> maxPartition = null;
		String maxBinary = null;
		for (SearchTreeNode<V> node : terminalNodes){
			OrderedPartition<V> partition = node.getNodePartition();
			String binary = binaryRepresenatation.binaryRepresenatation(partition.getVerticesInOrder());
			if (maxBinary != null)
				System.out.println(binary.compareTo(maxBinary));

			if (maxBinary == null || binary.compareTo(maxBinary) == 1){
				maxPartition = partition;
				maxBinary = binary;
			}

		}
		return maxPartition;
	}

	private List<Permutation> findAutomorphisms(List<SearchTreeNode<V>> terminalNodes){
		List<Permutation> ret = new ArrayList<Permutation>();

		//calculate permutations
		List<Permutation> allPermutations = new ArrayList<Permutation>();
		for (SearchTreeNode<V> node : terminalNodes){
			Permutation p = permutation(node.getNodePartition());
			allPermutations.add(p);
		}

		for (int i = 0; i < allPermutations.size(); i++)
			for (int j = i; j < allPermutations.size(); j++){
				Permutation p1 = allPermutations.get(i);
				Permutation p2 = allPermutations.get(j);
				
				//calculate p1 * (p2)^-1
				Permutation inverse = p2.inverse();
				Permutation p = p1.mul(inverse);
				
				if (!ret.contains(p))
					if (checkAutomorphism(p))
						ret.add(p);
			}
		return ret;
	}

	private boolean checkAutomorphism(Permutation permutation){
		for (E e : graph.getEdges()){
			V v1 = e.getOrigin();
			V v2 = e.getDestination();
			Integer v1Index = graph.getVertices().indexOf(v1);
			Integer v2Index = graph.getVertices().indexOf(v2);
			Integer mappedV1Index = permutation.getPermutation().get(v1Index);
			Integer mappedV2Index = permutation.getPermutation().get(v2Index);
			V v1Mapped = graph.getVertices().get(mappedV1Index);
			V v2Mapped = graph.getVertices().get(mappedV2Index);
			if (graph.edgeBetween(v1Mapped, v2Mapped) == null)
				return false;
		}
		return true;
	}

	private Permutation permutation(OrderedPartition<V> discretePartition){
		Map<Integer, Integer> permutation = new HashMap<Integer, Integer>();
		for (int i = 0; i < discretePartition.getPartition().size(); i++){
			List<V> part = discretePartition.getPartition().get(i);
			V v = part.get(0); //the only one
			Integer vertexIndex = graph.getVertices().indexOf(v);
			permutation.put(vertexIndex, i);
		}
		return new Permutation(permutation);
	}


	private int deg(V v, List<V> Vj){
		int deg = 0;
		for (V test : graph.adjacentVertices(v)){
			if (Vj.contains(test))
				deg ++;
		}
		return deg;
	}

}
