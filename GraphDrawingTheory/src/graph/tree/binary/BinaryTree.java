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

/**
 * A binary tree and methods for its construction
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type 
 */
public class BinaryTree<V extends Vertex, E extends Edge<V>> {

	/**
	 * Root of the binary tree
	 */
	private BinaryTreeNode<V> root;
	/**
	 * Nodes of the tree
	 */
	private List<BinaryTreeNode<V>> nodes = new ArrayList<BinaryTreeNode<V>>();
	/**
	 * A mapping of vertices of the original graph and corresponding binary tree nodes 
	 */
	private Map<V, BinaryTreeNode<V>> vertexNodesMap = new HashMap<V, BinaryTreeNode<V>>();
	/**
	 * Original graph, based on which the tree is constructed
	 */
	private Graph<V,E> graph;
	
	private Logger log = Logger.getLogger(BinaryTree.class);
	/**
	 * Indicates if binary tree can be constructed
	 */
	private boolean canBeConstructed;

	/**
	 * Tried to construct the binary tree given a graph
	 * @param graph Graph
	 */
	public BinaryTree(Graph<V,E> graph){
		this.graph = graph;
		try {
			formBinaryTree(graph);
			canBeConstructed = true;
		} catch (CannotBeAppliedException e) {
			canBeConstructed = false;
		}
	}

	private void formBinaryTree(Graph<V, E> graph) throws CannotBeAppliedException{
		//start with the leaves and go upwards
		//nodes of the binary tree should have between 1 and 3 links
		//leaves have 1
		//root has 1 or 2
		//ordinary nodes have 2 or 3 (parent and 1 or 2 children)

		//trivial cases
		if (graph.getVertices().size() == 1){
			root = new BinaryTreeNode<V>(graph.getVertices().get(0));
			nodes.add(root);
		}
		else if (graph.getVertices().size() == 2){
			if (graph.getEdges().size() != 1)
				throw new CannotBeAppliedException("Not a binary tree");

			BinaryTreeNode<V> node1 = new BinaryTreeNode<V>(graph.getVertices().get(0));
			BinaryTreeNode<V> node2 = new BinaryTreeNode<V>(graph.getVertices().get(1));
			node1.setLeft(node2);
			node2.setParent(node1);
			node1.setHeight(2);
			nodes.add(node1);
			nodes.add(node2);
			root = node1;
		}
		else{
			List<BinaryTreeNode<V>> leaves = new ArrayList<BinaryTreeNode<V>>();
			for (V v : graph.getVertices())
				if (graph.adjacentVertices(v).size() == 1){
					BinaryTreeNode<V> leaf = new BinaryTreeNode<V>(v);
					vertexNodesMap.put(v, leaf);
					leaves.add(leaf);
				}

			if (leaves.size() == 0)
				throw new CannotBeAppliedException("Not a binary tree");


			formTree(leaves, false);
			if (root == null)
				throw new CannotBeAppliedException("Not a binary tree");
		}

	}

	private void formTree(List<BinaryTreeNode<V>> currentLevel, boolean debug) throws CannotBeAppliedException{
		if (debug)
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

			//			if (currentNode.getLeft() != null && currentNode.getRight() != null
			//					&& graph.adjacentVertices(current).size() == 2){
			//				root = currentNode;
			//				continue;
			//			}


			List<V> adjacent = graph.adjacentVertices(current);
			if (adjacent.size() > 3)
				throw new CannotBeAppliedException("Not a binary tree");

			if (adjacent.size() == 1){ //leaf node
				BinaryTreeNode<V> adjNode = vertexNodesMap.get(adjacent.get(0));
				if (adjNode == null){
					adjNode = new BinaryTreeNode<V>(adjacent.get(0));
					nextLevel.add(adjNode);
					vertexNodesMap.put(adjacent.get(0),adjNode);
				}
				currentNode.setParent(adjNode);
				if (debug)
					log.info("parent of leaf: " + adjNode);

				if (adjNode.getHeight() < currentNode.getHeight() + 1)
					adjNode.setHeight(currentNode.getHeight() + 1);

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
					if (debug)
						log.info("Current adjacent: " + adj);
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

				if (debug){
					log.info("not set num: " + notSetNum);
					log.info("left child: " + lChild);
					log.info("right child:" + rChild);
					log.info("parent " + parent);
					log.info("not set node: " + notSet);
					log.info("not adjacent vertex: " + notSetAdjacent);
				}

				if (notSetNum == 1){
					if (notSet == null){
						notSet = new BinaryTreeNode<V>(notSetAdjacent);
						vertexNodesMap.put(notSetAdjacent, notSet);
					}

					//set parent, but not if it already has both children set
					if ((rChild != null || (adjacent.size() == 2 && lChild != null)
							&& !(notSet.getLeft() != null && notSet.getRight() != null))){ 

						//if the second condition is met
						//there is still a chance that the node is the root
						//and therefore doesn't have a parent

						if (debug)
							log.info("Setting the parent of " + currentNode + " to " + notSet );

						//height of the node should reflect it's max distance from a leaf
						if (notSet.getHeight() < currentNode.getHeight() + 1)
							notSet.setHeight(currentNode.getHeight() + 1);

						if (notSet.getParent() == currentNode)
							notSet.setParent(null);


						nextLevel.add(notSet);
						if (debug)
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

						if (currentNode.getHeight() < notSet.getHeight() + 1)
							currentNode.setHeight(notSet.getHeight() + 1);

						notSet.setParent(currentNode);
					}
				}

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

							if (currentNode.getHeight() < adjNode.getHeight() + 1)
								currentNode.setHeight(adjNode.getHeight() + 1);

						}
					}
				}
			}

			if (adjacent.size() == 2 && currentNode.getLeft() != null && currentNode.getRight() != null)
				root = currentNode;
		}

		if (nextLevel.size() > 0)
			formTree(nextLevel, debug);
	}
	
	/**
	 * Checks if the binary tree is balanced
	 * A height-balanced binary tree is defined as a binary tree in
	 *  which the depth of the two subtrees of every node
	 *  never differ by more than 1.
	 * @return {@code true} if tree is balanced, {@code false} otherwise
	 */
	public boolean isBalanced(){
		if (root == null)
			return true;
 
		if (getHeight(root) == -1)
			return false;
 
		return true;
	}
	
	private int getHeight(BinaryTreeNode<V> root){
		if (root == null)
			return 0;
 
		int left = getHeight(root.getLeft());
		int right = getHeight(root.getRight());
 
		if (left == -1 || right == -1)
			return -1;
 
		if (Math.abs(left - right) > 1) {
			return -1;
		}
 
		return Math.max(left, right) + 1;
 
	}
	
	/**
	 * @return Tree's height
	 */
	public int height(){
		return root.getHeight();
	}

	/**
	 * @return Tree's root
	 */
	public BinaryTreeNode<V> getRoot() {
		return root;
	}

	/**
	 * @param root The root to set
	 */
	public void setRoot(BinaryTreeNode<V> root) {
		this.root = root;
	}

	/**
	 * @return Nodes of the tree
	 */
	public List<BinaryTreeNode<V>> getNodes() {
		return nodes;
	}

	/**
	 * @param nodes Nodes to set
	 */
	public void setNodes(List<BinaryTreeNode<V>> nodes) {
		this.nodes = nodes;
	}

	@Override
	public String toString() {
		String ret =  "BinaryTree [root=" + root + "\n";
		for (BinaryTreeNode<V> node : nodes)
			ret += node + "\n";
		return ret;
	}

	/**
	 * @return Indicator if the tree can be constructed
	 */
	public boolean isCanBeConstructed() {
		return canBeConstructed;
	}

}




