package graph.tree.pq;

import graph.elements.Edge;
import graph.elements.Vertex;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.log4j.Logger;

public class PQTreeReduction<V extends Vertex, E extends Edge<V>> {


	/**
	 * A first-in first-out list which is used during both passes for
	 * sequencing the order in which nodes are processed
	 */
	private Queue<PQTreeNode> queue;

	private Logger log = Logger.getLogger(PQTreeReduction.class);

	private boolean debug = false;

	public PQTreeReduction(){
		queue = new LinkedList<PQTreeNode>();
	}


	/**
	 * @param pqTree
	 * @param S A subset of all nodes
	 */
	/**
	 * @param pqTree
	 * @param S A subset of all nodes
	 */
	public boolean bubble(PQTree<V,E> pqTree, List<PQTreeNode> S){

		if (debug)
			log.info("Bubble");

		setPertinendChildren(pqTree.getRoot(), S);

		if (debug){
			for (PQTreeNode node : pqTree.getVertices())
				log.info("Pertinent children count of " + node + " = " +  node.getPertinendChildCount() );
		}
		return true;
	}

	private void setPertinendChildren(PQTreeNode node, List<PQTreeNode> S){
		node.setPertinendChildCount(0);
		if (node.getType() == PQNodeType.LEAF){
			if (S.contains(node)){
				node.setPertinendChildCount(1);
			}
		}
		else{

			for (PQTreeNode child : node.getChildren()){
				setPertinendChildren(child, S);
				if (child.getPertinendChildCount() >= 1){
					node.incrementPertinentChildCount();
				}
			}
		}
	}

	public boolean reduce(PQTree<V,E> pqTree, List<PQTreeNode> S, PQTreeNode pertRoot){

		if (debug)
			log.info("Reduction");

		//initialize queue to be empty
		queue.clear();
		//for each leaf x in S
		for (PQTreeNode x : S){
			if (x.getType() == PQNodeType.LEAF){
				queue.add(x);
			}
		}
		while (queue.size() > 0){
			//remove x from the from the front of the queue
			PQTreeNode x = queue.remove();
			if (debug)
				log.info("Current x " + x);
			if (x != pertRoot){
				//X is not root(T,S)
				PQTreeNode y = x.getParent();
				if (debug){
					log.info("Y (parent of x): " + y); 
					log.info("Y pertinent child count " + y.getPertinendChildCount());
				}
				y.decrementPertinentChildCount();
				if (y.getPertinendChildCount() == 0){
					queue.add(y);
					if (debug)
						log.info("Adding " + y + "  to queue");
				}
				//try templates
				//the order is very important
				if (debug)
					log.info("Trying templates");
				if (!templateL1(x, S))
					if (!templateP1(x))
						if (!templateP3(x, pqTree))
							if (!templateP5(x, pqTree))
								if (!templateQ1(x))
									if (!templateQ2(x, pqTree)){
										return false;
									}
			}
			else{
				//x is root(T,S)
				//try templates a bit differently
				if (!templateL1(x, S))
					if (!templateP1(x))
						if (!templateP2(x, pqTree))
							if (!templateP4(x, pqTree))
								if (!templateP6(x, pqTree))
									if (!templateQ1(x))
										if (!templateQ2(x, pqTree))
											if (!templateQ3(x, pqTree)){
												return false;
											}
			}
		}

		if (debug){
			log.info("Tree reduced");
			log.info(pqTree);
		}
		return true;
	}


	/**
	 * Template matching for leaves. The simplest case, the node is simply
	 * marked as empty or full depending on if it belongs to S or not 
	 * @param node
	 * @return
	 */
	private boolean templateL1(PQTreeNode node, List<PQTreeNode> S){
		if (debug)
			log.info("Trying template L1 for node " + node);
		if (node.getType() != PQNodeType.LEAF)
			return false;

		//label node
		if (S.contains(node))
			node.labelAsFull();
		else
			node.labelAsEmpty();

		if (debug)
			log.info("Template matched. Labeled the leaf as " + node.getLabel());
		return true;
	}


	/**
	 * The simplest case for a P-node. If all children of the node
	 * are labeled empty, then the node can be labeled empty.
	 * @param node
	 * @return
	 */
	@SuppressWarnings("unused")
	private boolean templateP0(PQTreeNode node){
		if (debug)
			log.info("Trying template P0 for node " + node);
		if (node.getType() != PQNodeType.P)
			return false;

		//all children are empty
		if (node.emptyChildrenCount() == node.childrenCount()){
			node.labelAsEmpty();
			if (debug)
				log.info("Templated matched. Labeled the P node as empty");
			return true;
		}

		return false;
	}

	/**
	 * The simplest case for a P-node. If all children of the node
	 * are labeled full, then the node can be labeled full.
	 * @param node
	 * @return
	 */
	private boolean templateP1(PQTreeNode node){
		if (debug)
			log.info("Trying template P1 for node " + node);
		if (node.getType() != PQNodeType.P)
			return false;

		//all children are full
		if (node.fullChildrenCount() == node.childrenCount()){
			node.labelAsFull();
			if (debug)
				log.info("Templated matched. Labeled the P node as full");
			return true;
		}
		return false;
	}

	/**
	 * The P-node should be the root of the pertinent subtree
	 * The children of the node have different labels
	 * The full children are grouped under a new P-node
	 * which is made a child of the root node, which is left unlabeled
	 * @param node
	 * @return
	 */
	private boolean templateP2(PQTreeNode node, PQTree<V,E> tree){
		if (debug)
			log.info("Trying template P2 for node " + node);

		if (node.getType() != PQNodeType.P)
			return false;

		//also update the tree

		//checking if there are children with different labels
		//and that there are full children
		if (node.fullChildrenCount() > 0 &&  node.emptyChildrenCount() > 0
				&& node.partialChildrenCount() == 0){

			//if there is only one child, just place it on one of the ends
			//else create a new P-node and group the full children

			if (node.fullChildrenCount() > 1){

				PQTreeNode newNode = new PQTreeNode(PQNodeType.P);
				tree.addVertex(newNode);
				for (PQTreeNode fullChild : node.getFullChildren()){
					newNode.addChild(fullChild);
					node.getChildren().remove(fullChild);
					tree.removeEdge(tree.edgeBetween(node, fullChild));
					tree.addEdge(new PQTreeEdge(newNode, fullChild));
				}
				node.getFullChildren().clear();
				newNode.setLabel(PQNodeLabel.FULL);

				//this also sets the parent of the new node
				//and updates the list of full nodes
				node.addChild(newNode);
				tree.addEdge(new PQTreeEdge(node, newNode));
				if (debug)
					log.info("Created the node: " + newNode);
			}

			else{
				PQTreeNode fullNode = node.getFullChildren().get(0);
				node.getChildren().remove(fullNode);
				node.getChildren().add(fullNode);
			}

			if (debug){
				log.info("Template matched");
				log.info("Node after the change: " + node);
			}
			return true;
		}

		return false;
	}

	/**
	 * If the P-node is not the root of the tree and it has both
	 * empty and full children, the empty children are grouped are
	 * children of a new empty p-node, the full as children of 
	 * a new full p-node
	 * The node itself is turned into a singly partial Q-node
	 * If there is only one empty or full child
	 * the additional p-node is not created
	 * @param node
	 * @return
	 */
	private boolean templateP3(PQTreeNode node, PQTree<V,E> tree){
		if (debug)
			log.info("Trying template P3 for node " + node);
		if (node.getType() != PQNodeType.P)
			return false;

		//and that there are full children
		if (node.fullChildrenCount() > 0 && node.emptyChildrenCount() > 0
				&& node.partialChildrenCount() == 0){

			PQTreeNode qNode = new PQTreeNode(PQNodeType.Q);
			qNode.setLabel(PQNodeLabel.SINGLY_PARTIAL);
			PQTreeNode parent = node.getParent();
			int index = parent.getChildren().indexOf(node);
			parent.removeChild(node);
			//add on the same position where the node was
			parent.addChild(qNode, index);

			//update tree
			tree.removeVertex(node);
			tree.addVertex(qNode);
			tree.addEdge(new PQTreeEdge(parent, qNode));
			qNode.setContent(node.getContent());

			//should create a new p-node for the empty children
			if (node.emptyChildrenCount() > 1){
				if (debug)
					log.info("Creating a new p node for the empty children");
				PQTreeNode emptyPNode = new PQTreeNode(PQNodeType.P);
				tree.addVertex(emptyPNode);
				for (PQTreeNode emptyNode : node.getEmptyChildren()){
					emptyPNode.addChild(emptyNode);
					tree.addEdge(new PQTreeEdge(emptyPNode, emptyNode));
				}
				emptyPNode.setLabel(PQNodeLabel.EMPTY);
				qNode.addChild(emptyPNode);
				tree.addEdge(new PQTreeEdge(qNode, emptyPNode));
				if (debug)
					log.info("New node: " + emptyPNode);
			}
			else{
				//add the single empty node to the q-node
				qNode.addChild(node.getEmptyChildren().get(0));
				tree.addEdge(new PQTreeEdge(qNode, node.getEmptyChildren().get(0)));
			}

			//should create a new p-node for the full children
			if (node.fullChildrenCount() > 1){
				if (debug)
					log.info("Creating a new p node for the full children");
				PQTreeNode fullPNode = new PQTreeNode(PQNodeType.P);
				tree.addVertex(fullPNode);
				for (PQTreeNode fullNode : node.getFullChildren()){
					fullPNode.addChild(fullNode);
					tree.addEdge(new PQTreeEdge(fullPNode, fullNode));
				}
				fullPNode.setLabel(PQNodeLabel.FULL);
				qNode.addChild(fullPNode);
				tree.addEdge(new PQTreeEdge(qNode, fullPNode));
				if (debug)
					log.info("New node: " + fullPNode);
			}
			else{
				//add the single full node to the q-node
				qNode.addChild(node.getFullChildren().get(0));
				tree.addEdge(new PQTreeEdge(qNode, node.getFullChildren().get(0)));
			}

			if (debug){
				log.info("Template matched");
				log.info("Partial q-node: " + qNode);
				log.info("Children: " + qNode.getChildren());
			}
			return true;
		}

		return false;

	}

	/**
	 * If a P-node has exactly one partial child, it is
	 * labeled singly partial. Template P4 is tried if
	 * the node is the root of the pertinent subtree
	 * @param node
	 * @return
	 */
	private boolean templateP4(PQTreeNode node, PQTree<V,E> tree){
		if (debug)
			log.info("Trying template P4 for node " + node);
		if (node.getType() != PQNodeType.P)
			return false;

		//if the node has exactly one partial child
		if (node.partialChildrenCount() == 1){
			node.labelAsPartial(PQNodeLabel.SINGLY_PARTIAL);

			if (debug)
				log.info("Labeling node as singly partial ");

			//leave empty children as they are
			//add full children as children of a new p-node
			//which itself is a child of the partial node
			//don't add the p-node is there are less than
			//two full children of the P-node in question

			if (debug){
				log.info("FULL CHILDREN");
				log.info(node.getFullChildren());
			}

			PQTreeNode partialChild = node.getPartialChildren().get(0);

			//add new full children in correct place, next to another full child
			int partialChildFullIndex = -1;
			if (partialChild.fullChildrenCount() > 0){
				partialChildFullIndex = partialChild.getChildren().indexOf(partialChild.getFullChildren().get(partialChild.getFullChildren().size() - 1)) + 1;
			}

			if (node.fullChildrenCount() == 1){
				if (debug)
					log.info("Only one full child");
				PQTreeNode fullChild = node.getFullChildren().get(0);
				node.removeChild(fullChild);
				tree.removeEdge(tree.edgeBetween(node, fullChild));

				if (partialChildFullIndex != -1)
					partialChild.addChild(fullChild, partialChildFullIndex);
				else
					partialChild.addChild(fullChild);

				tree.addEdge(new PQTreeEdge(partialChild, fullChild));
				if (debug){
					log.info("Partial child " + partialChild);
					log.info("Partial child children: " + partialChild.getChildren());
				}
			}
			else if (node.fullChildrenCount() > 1){
				if (debug)
					log.info("Two or more full children, creating a new P-node");
				PQTreeNode newPNode = new PQTreeNode(PQNodeType.P);
				tree.addVertex(newPNode);
				for (PQTreeNode fullChild : node.getFullChildren()){
					newPNode.addChild(fullChild);
					node.removeChild(fullChild);
					tree.removeEdge(tree.edgeBetween(node, fullChild));
					tree.addEdge(new PQTreeEdge(newPNode, fullChild));
				}
				newPNode.setLabel(PQNodeLabel.FULL);
				node.getFullChildren().clear();

				if (partialChildFullIndex != -1)
					partialChild.addChild(newPNode, partialChildFullIndex);
				else
					partialChild.addChild(newPNode);

				tree.addEdge(new PQTreeEdge(partialChild, newPNode));
				if (debug){
					log.info("New p-node: " + newPNode);
					log.info("Partial child " + partialChild);
					log.info("Partial child children: " + partialChild.getChildren());
				}
			}
			if (debug){
				log.info("Template matched");
				log.info("Node " + node);
				log.info("Children: " + node.getChildren());
			}
			return true;
		}

		return false;

	}

	/**
	 * If a P-node has exactly one partial child, it is
	 * labeled singly partial. Template P5 is tried if
	 * the node is not the root of the pertinent subtree
	 * A new singly partial node is created, as well as the p-nodes
	 * for empty and full nodes, similarly to the template P3
	 * The new partial node has these p nodes as well as the 
	 * empty and full nodes of the child partial node as its children
	 * That original partial node is removed from the list of children
	 * @param node
	 * @return
	 */
	private boolean templateP5(PQTreeNode node, PQTree<V,E> tree){
		if (debug)
			log.info("Trying template P5 for node " + node);
		if (node.getType() != PQNodeType.P)
			return false;

		if (node.partialChildrenCount() == 1){

			PQTreeNode qNode = new PQTreeNode(PQNodeType.Q);
			qNode.setLabel(PQNodeLabel.SINGLY_PARTIAL);
			PQTreeNode parent = node.getParent();
			int index = parent.getChildren().indexOf(node);
			parent.removeChild(node);
			parent.addChild(qNode, index);

			tree.removeVertex(node);
			tree.addVertex(qNode);
			tree.addEdge(new PQTreeEdge(parent, qNode));
			qNode.setContent(node.getContent());


			//should create a new p-node for the empty children
			if (node.emptyChildrenCount() > 1){
				if (debug)
					log.info("Creating a new p node for the empty children");
				PQTreeNode emptyPNode = new PQTreeNode(PQNodeType.P);
				for (PQTreeNode emptyNode : node.getEmptyChildren()){
					emptyPNode.addChild(emptyNode);
					tree.addEdge(new PQTreeEdge(emptyPNode, emptyNode));
				}
				emptyPNode.setLabel(PQNodeLabel.EMPTY);
				qNode.addChild(emptyPNode);
				tree.addEdge(new PQTreeEdge(qNode, emptyPNode));
				if (debug)
					log.info("New node: " + emptyPNode);
			}
			else{
				//add the single empty node to the q-node
				//there could be no empty children as well
				if (node.emptyChildrenCount() == 1){
					qNode.addChild(node.getEmptyChildren().get(0));
					tree.addEdge(new PQTreeEdge(qNode, node.getEmptyChildren().get(0)));
				}
			}

			//now add the empty children of the partial node 
			//followed my the full children of the partial node
			PQTreeNode partialChild = node.getPartialChildren().get(0);
			for (PQTreeNode emptyChild : partialChild.getEmptyChildren()){
				qNode.addChild(emptyChild);
				tree.removeEdge(tree.edgeBetween(partialChild, emptyChild));
				tree.addEdge(new PQTreeEdge(qNode, emptyChild));
			}
			for (PQTreeNode fullChild : partialChild.getFullChildren()){
				qNode.addChild(fullChild);
				tree.removeEdge(tree.edgeBetween(partialChild, fullChild));
				tree.addEdge(new PQTreeEdge(qNode, fullChild));
			}

			tree.removeVertex(partialChild);


			//should create a new p-node for the full children
			if (node.fullChildrenCount() > 1){
				if (debug)
					log.info("Creating a new p node for the full children");
				PQTreeNode fullPNode = new PQTreeNode(PQNodeType.P);
				for (PQTreeNode fullNode : node.getFullChildren()){
					fullPNode.addChild(fullNode);
					tree.addEdge(new PQTreeEdge(fullPNode, fullNode));
				}
				fullPNode.setLabel(PQNodeLabel.FULL);
				qNode.addChild(fullPNode);
				tree.addEdge(new PQTreeEdge(qNode, fullPNode));
				if (debug)
					log.info("New node: " + fullPNode);
			}
			else{
				//add the single full node to the q-node
				//there could be no full children
				if (node.fullChildrenCount() == 1){
					qNode.addChild(node.getFullChildren().get(0));
					tree.addEdge(new PQTreeEdge(qNode, node.getFullChildren().get(0)));
				}
			}

			if (debug){
				log.info("Template matched");
				log.info("Partial q-node: " + qNode);
			}
			return true;
		}

		return false;
	}

	/**
	 * If a P-node has two partial children
	 * It must be the root of the pertinent subtree
	 * The two partial children are joined and a p-node
	 * is created for the full nodes of the root
	 * and added as the child of the newly created partial child
	 * @param node
	 * @return
	 */
	private boolean templateP6(PQTreeNode node, PQTree<V,E> tree){
		if (debug)
			log.info("Trying template P6 for node " + node);
		//if the node has exactly one partial child
		if (node.partialChildrenCount() == 2){
			node.labelAsPartial(PQNodeLabel.DOUBLY_PARTIAL);

			if (debug)
				log.info("Labeling node as doubly partial ");

			//leave empty children as they are
			//join the partial children and full children
			//create a new partial child and add to it:
			//all empty children of the first partial child
			//all full children of the first partial child
			//full children (using the new P-node if there is more than one)
			//all full children of the second partial child
			//all empty children of the second partial child

			PQTreeNode newPartialChild = new PQTreeNode(PQNodeType.Q);
			tree.addVertex(newPartialChild);
			newPartialChild.setLabel(PQNodeLabel.SINGLY_PARTIAL);

			PQTreeNode firstPartialChild = node.getPartialChildren().get(0);


			for (PQTreeNode emptyChild : firstPartialChild.getEmptyChildren()){
				newPartialChild.addChild(emptyChild);
				tree.addEdge(new PQTreeEdge(newPartialChild, emptyChild));
			}
			for (PQTreeNode fullChild : firstPartialChild.getFullChildren()){
				newPartialChild.addChild(fullChild);
				tree.addEdge(new PQTreeEdge(newPartialChild, fullChild));
			}

			node.removeChild(firstPartialChild);
			tree.removeVertex(firstPartialChild);


			if (node.fullChildrenCount() == 1){
				if (debug)
					log.info("Only one full child");
				PQTreeNode fullChild = node.getFullChildren().get(0);
				node.removeChild(fullChild);
				newPartialChild.addChild(fullChild);
				tree.removeEdge(tree.edgeBetween(node, fullChild));
				tree.addEdge(new PQTreeEdge(newPartialChild, fullChild));
				if (debug)
					log.info("Partial child " + newPartialChild);
			}
			else if (node.fullChildrenCount() > 1){
				if (debug)
					log.info("Two or more full children, creating a new P-node");
				PQTreeNode newPNode = new PQTreeNode(PQNodeType.P);
				for (PQTreeNode fullChild : node.getFullChildren()){
					newPNode.addChild(fullChild);
					node.getChildren().remove(fullChild);
					tree.removeEdge(tree.edgeBetween(node, fullChild));
					tree.addEdge(new PQTreeEdge(newPNode, fullChild));
				}
				newPNode.setLabel(PQNodeLabel.FULL);
				node.getFullChildren().clear();
				newPartialChild.addChild(newPNode);
				tree.addEdge(new PQTreeEdge(newPartialChild, newPNode));
				if (debug){
					log.info("New p-node: " + newPNode);
					log.info("Partial child " + newPartialChild);
				}
			}

			//already removed the first one
			PQTreeNode secondPartialChild = node.getPartialChildren().get(0);
			for (PQTreeNode fullChild : secondPartialChild.getFullChildren()){
				newPartialChild.addChild(fullChild);
				tree.addEdge(new PQTreeEdge(newPartialChild, fullChild));
			}
			for (PQTreeNode emptyChild : secondPartialChild.getEmptyChildren()){
				newPartialChild.addChild(emptyChild);
				tree.addEdge(new PQTreeEdge(newPartialChild, emptyChild));
			}

			node.removeChild(secondPartialChild);
			tree.removeVertex(secondPartialChild);
			node.addChild(newPartialChild);
			tree.addEdge(new PQTreeEdge(node, newPartialChild));

			if (debug){
				log.info("Template mathced");
				log.info("Node " + node);
			}
			return true;
		}

		return false;
	}


	/**
	 * Simplest case for Q-nodes. If all of its children are empty
	 * it is also labeled as empty
	 * @param node
	 * @return
	 */
	@SuppressWarnings("unused")
	private boolean templateQ0(PQTreeNode node){
		if (debug)
			log.info("Trying template Q0 for node " + node);
		if (node.getType() != PQNodeType.Q)
			return false;

		//all children are empty
		if (node.emptyChildrenCount() == node.childrenCount()){
			node.labelAsEmpty();
			if (debug)
				log.info("Templated matched. Labeled the Q node as empty");
			return true;
		}

		return false;
	}

	/**
	 * Simplest case for Q-nodes. If all of its children are full
	 * it is also labeled as full
	 * @param node
	 * @return
	 */
	private boolean templateQ1(PQTreeNode node){
		if (debug)
			log.info("Trying template Q1 for node " + node);
		if (node.getType() != PQNodeType.Q)
			return false;

		//all children are full
		if (node.fullChildrenCount() == node.childrenCount()){
			node.labelAsFull();
			if (debug)
				log.info("Templated matched. Labeled the Q node as full");
			return true;
		}

		return false;
	}

	/**
	 * Not all children of the node are labeled identically
	 * and it shouldn't have more than one partial child
	 * The empty children should be first, then a partial one
	 * and then the full ones
	 * Some may be missing and the order of the children can be reversed 
	 * @param node
	 * @return
	 */
	private boolean templateQ2(PQTreeNode node, PQTree<V,E> tree){
		if (debug)
			log.info("Trying template Q2 for node " + node);

		if (node.getType() != PQNodeType.Q)
			return false;

		if (node.fullChildrenCount() < node.childrenCount() && 
				node.emptyChildrenCount() < node.childrenCount() && 
				node.partialChildrenCount() <= 1){


			//check the order
			//only looks for singly partial
			//so if the partial child is doubly partial, that should be
			//recognized as invalid
			if (!node.orderValidUpToOnePartial())
				return false;

			//if an invalid situation wasn't detected
			//eliminate the partial child and add its children to the parent
			//keep the order empty-full
			if (node.partialChildrenCount() == 1){

				PQTreeNode partialChild = node.getPartialChildren().get(0);
				//check the partial child
				//are its children mixed
				if (!partialChild.orderValidUpToOnePartial())
					return false;

				int index = node.getChildren().indexOf(partialChild);

				for (PQTreeNode partialEmpty : partialChild.getEmptyChildren()){
					node.addChild(partialEmpty, index);
					tree.addEdge(new PQTreeEdge(node, partialEmpty));
					index++;
				}
				for (PQTreeNode partialFull : partialChild.getFullChildren()){
					node.addChild(partialFull, index);
					tree.addEdge(new PQTreeEdge(node, partialFull));
					index++;
				}

				node.removeChild(partialChild);
				tree.removeVertex(partialChild);
			}

			node.setLabel(PQNodeLabel.SINGLY_PARTIAL);
			if (debug){
				log.info("Template matched");
				log.info("Node: " + node);
			}

			return true;
		}

		return false;
	}

	/**
	 * Up to two children may be partial
	 * Node is root of the pertinent subtree
	 * Doubly partial case
	 * The order should be empty, partial, full, partial, empty
	 * @param node
	 * @return
	 */
	private boolean templateQ3(PQTreeNode node, PQTree<V,E> tree){
		if (debug)
			log.info("Trying template Q3 for node " + node);

		if (node.getType() != PQNodeType.Q)
			return false;

		//node must be doubly partial
		if (node.getLabel() != PQNodeLabel.DOUBLY_PARTIAL)
			return false;

		if (node.fullChildrenCount() < node.childrenCount() && 
				node.emptyChildrenCount() < node.childrenCount() && 
				node.partialChildrenCount() <= 2){

			if (!node.orderValidUpToTwoPartial())
				return false;


			List<PQTreeNode> children = node.getChildren();

			//process the first group of empty nodes
			//the beginning
			//firstEmpty counts how many empty nodes are at the beginning
			int firstEmpty = 0;

			for (PQTreeNode child : children){
				if (child.getLabel() == PQNodeLabel.EMPTY)
					firstEmpty++;
				else
					break;
			}

			if (children.get(firstEmpty).getLabel() == PQNodeLabel.SINGLY_PARTIAL){
				PQTreeNode partialChild = children.get(firstEmpty);
				if (!partialChild.orderValidUpToOnePartial())
					return false;

				int index = firstEmpty;

				for (PQTreeNode partialEmpty : partialChild.getEmptyChildren()){
					node.addChild(partialEmpty, index);
					tree.addEdge(new PQTreeEdge(node, partialEmpty));
					index++;
				}
				for (PQTreeNode partialFull : partialChild.getFullChildren()){
					node.addChild(partialFull, index);
					tree.addEdge(new PQTreeEdge(node, partialFull));
					index++;
				}

				node.removeChild(partialChild);
				tree.removeVertex(partialChild);
			}

			//process the other end

			int lastEmpty = children.size() - 1;

			for (int i = children.size() - 1; i >= 0; i--){
				PQTreeNode child = children.get(i);
				if (child.getLabel() != PQNodeLabel.EMPTY){
					lastEmpty = i;
					break;
				}
			}

			if (children.get(lastEmpty).getLabel() == PQNodeLabel.SINGLY_PARTIAL){

				PQTreeNode partialChild = children.get(lastEmpty);

				if (!partialChild.orderValidUpToOnePartial())
					return false;

				int index = lastEmpty;

				//not decreasing the index here
				//if nodes are added at that position, it'll always be the
				//beginning of the part where nodes of the second
				//child are added

				for (PQTreeNode partialEmpty : partialChild.getEmptyChildren()){
					node.addChild(partialEmpty, index);
					tree.addEdge(new PQTreeEdge(node, partialEmpty));
					//index--;
				}

				for (PQTreeNode partialFull : partialChild.getFullChildren()){
					node.addChild(partialFull, index);
					tree.addEdge(new PQTreeEdge(node, partialFull));
					//index--;
				}

				node.removeChild(partialChild);
				tree.removeVertex(partialChild);

			}

			node.setLabel(PQNodeLabel.DOUBLY_PARTIAL);
			if (debug){
				log.info("Template matched");
				log.info("Node: " + node);
			}

		}
		return true;
	}


}
