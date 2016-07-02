package graph.tree.binary;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.exception.CannotBeAppliedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class BinaryTree<V extends Vertex, E extends Edge<V>> {


	private BinaryTreeNode<V> root;
	private List<BinaryTreeNode<V>> nodes = new ArrayList<BinaryTreeNode<V>>();
	private Map<V, BinaryTreeNode<V>> vertexNodesMap = new HashMap<V, BinaryTreeNode<V>>();
	private Graph<V,E> graph;
	private Logger log = Logger.getLogger(BinaryTree.class);

	public BinaryTree(Graph<V,E> graph) throws CannotBeAppliedException{
		this.graph = graph;
		formBinaryTree(graph);
	}

	private void formBinaryTree(Graph<V, E> graph) throws CannotBeAppliedException{
		//start with the leaves and go upwards
		//or just traverse the vertices, form a map of 
		//created nodes - vertices
		//if the vertex is encountered again, grab the node from the map
		//avoid two traversal that way

		//notes of the binary tree should have between 1 and 3 links
		//leaves have 1
		//root has 1 or 2
		//ordinary nodes have 2 or 3 (parent and 1 or 2 children)

		//find leaves and go from the bottom
		List<BinaryTreeNode<V>> leaves = new ArrayList<BinaryTreeNode<V>>();
		for (V v : graph.getVertices())
			if (graph.adjacentVertices(v).size() == 1){
				BinaryTreeNode<V> leaf = new BinaryTreeNode<V>(v);
				vertexNodesMap.put(v, leaf);
				leaves.add(leaf);
			}

		if (leaves.size() == 0)
			throw new CannotBeAppliedException("Not aabinary tree");

		formTree(leaves);

	}


	private void formTree(List<BinaryTreeNode<V>> currentLevel){
		log.info("form for: " + currentLevel);
		
		List<BinaryTreeNode<V>> nextLevel = new ArrayList<BinaryTreeNode<V>>();
		BinaryTreeNode<V> lChild, rChild, parent, notSet;
		int notSetNum;
		V notSetAdjacent;

		for (BinaryTreeNode<V> currentNode : currentLevel){
			log.info("current node: " + currentNode);
			//add to nodes
			if (!nodes.contains(currentNode))
				nodes.add(currentNode);
			V current = currentNode.getVertex();
			
			if (currentNode.getLeft() != null && currentNode.getRight() != null
					&& graph.adjacentVertices(current).size() == 2){
				root = currentNode;
				continue;
			}
			
			
			List<V> adjacent = graph.adjacentVertices(current);
			if (adjacent.size() == 1){ //leaf node
				BinaryTreeNode<V> adjNode = vertexNodesMap.get(adjacent.get(0));
				if (adjNode == null){
					adjNode = new BinaryTreeNode<V>(adjacent.get(0));
					nextLevel.add(adjNode);
					vertexNodesMap.put(adjacent.get(0),adjNode);
				}
				currentNode.setParent(adjNode);
				log.info("parent: " + adjNode);

				if (adjNode.getLeft() == null)
					adjNode.setLeft(currentNode);
				else
					adjNode.setRight(currentNode);

			}
			else{

				lChild = null;
				rChild = null;
				parent = null;
				notSet = null;
				notSetNum = 0;
				notSetAdjacent = null;

				//if both children were set, and there are three adjacent
				//the third one is the parent
				//if one child is set and there are two adjacent, it is
				//either the root node or the second node is the 

				for (V adj : adjacent){
					BinaryTreeNode<V> adjNode = vertexNodesMap.get(adj);
					log.info("Current adjacent: " + adj);
					System.out.println(currentNode.getLeft());
					if (adjNode != null){ //already processed
						if (currentNode.getLeft() == adjNode)
							lChild = adjNode;
						else if (currentNode.getRight() == adjNode)
							rChild = adjNode;
						else if (currentNode.getParent() == adjNode)
							parent = adjNode;
						else{
							notSet = adjNode;
							notSetNum++;
						}
					}
					else {
						notSetAdjacent = adj;
						notSetNum++;
					}
				}

				System.out.println(notSetNum);
				System.out.println(lChild);
				System.out.println(rChild);
				System.out.println(parent);
				System.out.println(notSet);
				System.out.println(notSetAdjacent);

				if (notSetNum == 1){
					if (notSet == null){
						notSet = new BinaryTreeNode<V>(notSetAdjacent);
						vertexNodesMap.put(notSetAdjacent, notSet);
					}
					if (rChild != null || (adjacent.size() == 2 && lChild != null)){ //set parent
						//current node's parent is the not set node
						log.info("Setting the parent of " + currentNode + " to " + notSet );

						nextLevel.add(notSet);
						log.info("adding " + notSet + " to next level");

						currentNode.setParent(notSet);
						if (notSet.getLeft() == null)
							notSet.setLeft(currentNode);
						else
							notSet.setRight(currentNode);
					}
					else{
						//not both children are set
						log.info("Setting the child of " + currentNode + " to " + notSet );
						if (lChild == null)
							currentNode.setLeft(notSet);
						else if (rChild == null)
							currentNode.setRight(notSet);
						notSet.setParent(currentNode);
					}
				}

				//mora se ovo jos prosiriti tako da se
				//i tu nesto postavlja
				//kada je root?
				//kako da znamo ko je parent, ko child
				//imamo v1, v3, v3 je kandidat za child
				//nekako videti da se to poveze


				if (notSetNum == 2){
					//if parent is set, we know we need to set the two children
					//if a child and the parent are missing, we don't know
					//what to set to what
					if (parent != null){
						//both that are not set are children
						for (V adj : adjacent){
							if (adj == parent)
								continue;

							BinaryTreeNode<V> adjNode = vertexNodesMap.get(adj);
							if (adjNode == null){
								adjNode = new BinaryTreeNode<V>(adj);
								vertexNodesMap.put(adj, adjNode);
							}
							if (currentNode.getLeft() == null){
								log.info("Setting the left child of " + currentNode + " to " + adjNode );
								currentNode.setLeft(adjNode);
								adjNode.setParent(currentNode);
							}
							else{
								log.info("Setting the right child of " + currentNode + " to " + adjNode );
								currentNode.setRight(adjNode);
								adjNode.setParent(currentNode);
							}
						}
					}
				}
			}
		}

		if (nextLevel.size() > 0)
			formTree(nextLevel);
	}

	public BinaryTreeNode<V> getRoot() {
		return root;
	}

	public void setRoot(BinaryTreeNode<V> root) {
		this.root = root;
	}

	public List<BinaryTreeNode<V>> getNodes() {
		return nodes;
	}

	public void setNodes(List<BinaryTreeNode<V>> nodes) {
		this.nodes = nodes;
	}

	@Override
	public String toString() {
		return "BinaryTree [root=" + root + ", nodes=" + nodes + "]";
	}

}




