package graph.tree.pq;

import graph.elements.Edge;
import graph.elements.Vertex;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.log4j.Logger;

public class PQTreeReduction<V extends Vertex, E extends Edge<V>> {

	/**
	 * The number of blocks of blocked nodes during the bubbling up pass
	 */
	private int blockCount;

	/**
	 * The number of blocked nodes during the bubbling up pass.
	 * This is only needed for the case when a pseudonode is used.
	 * The count at the end of the first pass is exactly the number of pretinent 
	 * children for the pseudonode
	 */
	private int blockedNodes;

	/**
	 * A variable which is either 0 (the initial value) or 1 (if the 
	 * root of the tree has been processed during the first pass). It acts
	 * as a count of the number of virtual nodes which are imagined to be in the queue during
	 * the bubbling up.
	 * 
	 */
	private int offTheTop;

	/**
	 * A first-in first-out list which is used during both passes for
	 * sequencing the order in which nodes are processed
	 */
	private Queue<PQTreeNode> queue;

	private Logger log = Logger.getLogger(PQTreeReduction.class);

	public PQTreeReduction(){
		queue = new LinkedList<PQTreeNode>();
	}
	
	
	/**
	 * @param pqTree
	 * @param S A subset of all nodes
	 */
	public boolean bubble(PQTree<V,E> pqTree, List<PQTreeNode> S){
		//initialize queue to be empty
		queue.clear();
		blockCount = 0;
		blockedNodes = 0;
		offTheTop = 0;
		List<PQTreeNode> BS = new ArrayList<PQTreeNode>();
		List<PQTreeNode> US = new ArrayList<PQTreeNode>();
		List<PQTreeNode> list = new ArrayList<PQTreeNode>();
		List<PQTreeNode> right = new ArrayList<PQTreeNode>();
		List<PQTreeNode> left = new ArrayList<PQTreeNode>();
		//for x in s place x onto queue
		for (PQTreeNode x : S)
			queue.add(x);
		//while size of queue + block count + off the top > 1
		while (queue.size() + blockCount + offTheTop > 1){
			if (queue.size() > 0){
				//input tree is a null tree
				return false;
			}
			//remove x from the from of queue
			PQTreeNode x = queue.remove();
			//mark x blocked
			x.setMark(PQNodeMark.BLOCKED);
			//bs = {y in immediate siblings of x, y is blocked
			//us = {y in immediate siblings of x, y is unblocked
			for (PQTreeNode y : x.getImmediateSimblings()) 
				if (y.getMark() == PQNodeMark.BLOCKED)
					BS.add(y);
				else if (y.getMark() == PQNodeMark.UNBLOCKED)
					US.add(y);
			if (US.size() > 0){
				//choose any Y in US
				//parent(x) = parent(Y)
				//mark(x) unblocked
				PQTreeNode y = US.get(0);
				x.setParent(y.getParent());
				x.setMark(PQNodeMark.UNBLOCKED);
			}
			else if (x.getImmediateSimblings().size() < 2)
				x.setMark(PQNodeMark.UNBLOCKED);
			if (x.getMark() == PQNodeMark.UNBLOCKED){
				PQTreeNode y = x.getParent();
				if (BS.size() > 0){
					//TODO to check
					list.clear();
					//list is the maximal consecutive set of blocked siblings
					//adjacent to x
					//since x is unblocked, the next on or the previous one 
					//could be blocked, thus starting the chain
					//check the left and right side 
					List<PQTreeNode> siblings = y.getChildren();
					int xIndex = siblings.indexOf(x);
					left.clear();
					right.clear();
					
					for (int i = xIndex + 1; i < siblings.size(); i++)
						if (siblings.get(i).getMark() == PQNodeMark.BLOCKED)
							right.add(siblings.get(i));
						else
							break;
					
					for (int i = xIndex - 1; i >= 0; i--)
						if (siblings.get(i).getMark() == PQNodeMark.BLOCKED)
							left.add(siblings.get(i));
						else
							break;
					
					if (left.size() > right.size())
						list.addAll(left);
					else
						list.addAll(right);
					
			
					System.out.println("LIST: " + list);
					for (PQTreeNode z : list){
						//mark z unblocked
						//parent z = y
						//increase pertinent child count of y
						z.setMark(PQNodeMark.UNBLOCKED);
						z.setParent(y);
						y.incrementPertinentChildCount();
					}
				}
				if (y == null)
					offTheTop = 1;
				else{
					y.incrementPertinentChildCount();
					if (y.getMark() == PQNodeMark.UNMARKED){
						//place y onto queue
						//mark y queued
						queue.add(y);
						y.setMark(PQNodeMark.QUEUED);
					}
				}
				blockCount -= BS.size();
				blockedNodes -= list.size();
			}
			else{
				blockCount = blockCount + 1 - BS.size();
				blockedNodes++;
			}
		}
		//return tree received as the input parameter
		//naturally, no need to do it here since the object itself 
		//is modified
		return true;
	}

	public boolean reduce(PQTree<V,E> pqTree, List<PQTreeNode> S){
		//initialize queue to be empty
		queue.clear();
		//for each leaf x in S
		for (PQTreeNode x : S){
			if (x.getType() == PQNodeType.LEAF){
				queue.add(x);
				x.setPertinentLeafCount(1);
			}
		}
		while (queue.size() > 0){
			//remove x from the from the front of the queue
			PQTreeNode x = queue.remove();
			if (x.getPertinentLeafCount() < S.size()){
				//X is not root(T,S)
				PQTreeNode y = x.getParent();
				int yPertinent = y.getPertinentLeafCount();
				int xPertinent = x.getPertinentLeafCount();
				y.setPertinentLeafCount(xPertinent + yPertinent);
				y.decrementPertinentChildCount();
				if (y.getPertinendChildCount() == 0)
					queue.add(y);
				//try templates
				//the order is very important
				if (!templateL1(x, S))
					if (!templateP1(x))
						if (!templateP3(x))
							if (!templateP5(x))
								if (!templateQ1(x))
									if (!templateQ2(x)){
										return false;
									}
			}
			else{
				//x is root(T,S)
				//try templates a bit differently
				if (!templateL1(x, S))
					if (!templateP1(x))
						if (!templateP2(x))
							if (!templateP4(x))
								if (!templateP6(x))
									if (!templateQ1(x))
										if (!templateQ2(x))
											if (!templateQ3(x)){
												return false;
											}
			}
		}

		log.info("Tree reduced");
		log.info(pqTree);
		return true;
	}


	//TODO
	//During the reduction the edges in the tree are not updated
	//only parent references
	//update all edges!

	/**
	 * Template matching for leaves. The simplest case, the node is simply
	 * marked as empty or full depending on if it belongs to S or not 
	 * @param node
	 * @return
	 */
	private boolean templateL1(PQTreeNode node, List<PQTreeNode> S){
		log.info("Trying template L1 for node " + node);
		if (node.getType() != PQNodeType.LEAF)
			return false;

		//label node
		if (S.contains(node))
			node.labelAsFull();
		else
			node.labelAsEmpty();

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
		log.info("Trying template P0 for node " + node);
		if (node.getType() != PQNodeType.P)
			return false;

		//all children are empty
		if (node.emptyChildrenCount() == node.childrenCount()){
			node.labelAsEmpty();
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
		log.info("Trying template P1 for node " + node);
		if (node.getType() != PQNodeType.P)
			return false;

		//all children are full
		if (node.fullChildrenCount() == node.childrenCount()){
			node.labelAsFull();
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
	private boolean templateP2(PQTreeNode node){
		log.info("Trying template P2 for node " + node);

		if (node.getType() != PQNodeType.P)
			return false;

		//TODO what is there is only one full child?

		//checking if there are children with different labels
		//and that there are full children
		if (node.fullChildrenCount() > 1 &&  node.emptyChildrenCount() > 0
				&& node.partialChildrenCount() == 0){
			PQTreeNode newNode = new PQTreeNode(PQNodeType.P);
			for (PQTreeNode fullChild : node.getFullChildren()){
				newNode.addChild(fullChild);
				node.getChildren().remove(fullChild);
			}
			node.getFullChildren().clear();
			newNode.setLabel(PQNodeLabel.FULL);

			//this also sets the parent of the new node
			//and updates the list of full nodes
			node.addChild(newNode); 
			log.info("Template matched");
			log.info("Created the node: " + newNode);
			log.info("Node after the change: " + node);
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
	private boolean templateP3(PQTreeNode node){
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


			//should create a new p-node for the empty children
			if (node.emptyChildrenCount() > 1){
				log.info("Creating a new p node for the empty children");
				PQTreeNode emptyPNode = new PQTreeNode(PQNodeType.P);
				for (PQTreeNode emptyNode : node.getEmptyChildren()){
					emptyPNode.addChild(emptyNode);
				}
				emptyPNode.setLabel(PQNodeLabel.EMPTY);
				qNode.addChild(emptyPNode);
				log.info("New node: " + emptyPNode);
			}
			else{
				//add the single empty node to the q-node
				qNode.addChild(node.getEmptyChildren().get(0));
			}

			//should create a new p-node for the full children
			if (node.fullChildrenCount() > 1){
				log.info("Creating a new p node for the full children");
				PQTreeNode fullPNode = new PQTreeNode(PQNodeType.P);
				for (PQTreeNode fullNode : node.getFullChildren()){
					fullPNode.addChild(fullNode);
				}
				fullPNode.setLabel(PQNodeLabel.FULL);
				qNode.addChild(fullPNode);
				log.info("New node: " + fullPNode);
			}
			else{
				//add the single full node to the q-node
				qNode.addChild(node.getFullChildren().get(0));
			}


			log.info("Template matched");
			log.info("Partial q-node: " + qNode);
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
	private boolean templateP4(PQTreeNode node){
		log.info("Trying template P4 for node " + node);
		if (node.getType() != PQNodeType.P)
			return false;

		//if the node has exactly one partial child
		if (node.partialChildrenCount() == 1){
			node.labelAsPartial(PQNodeLabel.SINGLY_PARTIAL);

			log.info("Labeling node as singly partial ");

			//leave empty children as they are
			//add full children as children of a new p-node
			//which itself is a child of the partial node
			//don't add the p-node is there are less than
			//two full children of the P-node in question

			PQTreeNode partialChild = node.getPartialChildren().get(0);
			if (node.fullChildrenCount() == 1){
				log.info("Only one full child");
				PQTreeNode fullChild = node.getFullChildren().get(0);
				node.removeChild(fullChild);
				partialChild.addChild(fullChild);
				log.info("Partial child " + partialChild);
			}
			else if (node.fullChildrenCount() > 1){
				log.info("Two or more full children, creating a new P-node");
				PQTreeNode newPNode = new PQTreeNode(PQNodeType.P);
				for (PQTreeNode fullChild : node.getFullChildren()){
					newPNode.addChild(fullChild);
					node.getChildren().remove(fullChild);
				}
				newPNode.setLabel(PQNodeLabel.FULL);
				node.getFullChildren().clear();
				partialChild.addChild(newPNode);
				log.info("New p-node: " + newPNode);
				log.info("Partial child " + partialChild);
			}
			log.info("Template mathced");
			log.info("Node " + node);
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
	private boolean templateP5(PQTreeNode node){
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


			//should create a new p-node for the empty children
			if (node.emptyChildrenCount() > 1){
				log.info("Creating a new p node for the empty children");
				PQTreeNode emptyPNode = new PQTreeNode(PQNodeType.P);
				for (PQTreeNode emptyNode : node.getEmptyChildren()){
					emptyPNode.addChild(emptyNode);
				}
				emptyPNode.setLabel(PQNodeLabel.EMPTY);
				qNode.addChild(emptyPNode);
				log.info("New node: " + emptyPNode);
			}
			else{
				//add the single empty node to the q-node
				qNode.addChild(node.getEmptyChildren().get(0));
			}

			//now add the empty children of the partial node 
			//followed my the full children of the partial node
			PQTreeNode partialChild = node.getPartialChildren().get(0);
			for (PQTreeNode emptyChild : partialChild.getEmptyChildren())
				qNode.addChild(emptyChild);
			for (PQTreeNode fullChild : partialChild.getFullChildren())
				qNode.addChild(fullChild);

			//should create a new p-node for the full children
			if (node.fullChildrenCount() > 1){
				log.info("Creating a new p node for the full children");
				PQTreeNode fullPNode = new PQTreeNode(PQNodeType.P);
				for (PQTreeNode fullNode : node.getFullChildren()){
					fullPNode.addChild(fullNode);
				}
				fullPNode.setLabel(PQNodeLabel.FULL);
				qNode.addChild(fullPNode);
				log.info("New node: " + fullPNode);
			}
			else{
				//add the single full node to the q-node
				qNode.addChild(node.getFullChildren().get(0));
			}

			log.info("Template matched");
			log.info("Partial q-node: " + qNode);
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
	private boolean templateP6(PQTreeNode node){
		log.info("Trying template P6 for node " + node);
		//if the node has exactly one partial child
		if (node.partialChildrenCount() == 2){
			node.labelAsPartial(PQNodeLabel.DOUBLY_PARTIAL);

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
			newPartialChild.setLabel(PQNodeLabel.SINGLY_PARTIAL);

			PQTreeNode firstPartialChild = node.getPartialChildren().get(0);
			for (PQTreeNode emptyChild : firstPartialChild.getEmptyChildren())
				newPartialChild.addChild(emptyChild);
			for (PQTreeNode fullChild : firstPartialChild.getFullChildren())
				newPartialChild.addChild(fullChild);
			node.removeChild(firstPartialChild);


			if (node.fullChildrenCount() == 1){
				log.info("Only one full child");
				PQTreeNode fullChild = node.getFullChildren().get(0);
				node.removeChild(fullChild);
				newPartialChild.addChild(fullChild);
				log.info("Partial child " + newPartialChild);
			}
			else if (node.fullChildrenCount() > 1){
				log.info("Two or more full children, creating a new P-node");
				PQTreeNode newPNode = new PQTreeNode(PQNodeType.P);
				for (PQTreeNode fullChild : node.getFullChildren()){
					newPNode.addChild(fullChild);
					node.getChildren().remove(fullChild);
				}
				newPNode.setLabel(PQNodeLabel.FULL);
				node.getFullChildren().clear();
				newPartialChild.addChild(newPNode);
				log.info("New p-node: " + newPNode);
				log.info("Partial child " + newPartialChild);
			}

			//already removed the first one
			PQTreeNode secondPartialChild = node.getPartialChildren().get(0);
			for (PQTreeNode fullChild : secondPartialChild.getFullChildren())
				newPartialChild.addChild(fullChild);
			for (PQTreeNode emptyChild : secondPartialChild.getEmptyChildren())
				newPartialChild.addChild(emptyChild);
			node.removeChild(secondPartialChild);

			node.addChild(newPartialChild);

			log.info("Template mathced");
			log.info("Node " + node);
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
		log.info("Trying template Q0 for node " + node);
		if (node.getType() != PQNodeType.Q)
			return false;

		//all children are empty
		if (node.emptyChildrenCount() == node.childrenCount()){
			node.labelAsEmpty();
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
		log.info("Trying template Q1 for node " + node);
		if (node.getType() != PQNodeType.Q)
			return false;

		//all children are full
		if (node.fullChildrenCount() == node.childrenCount()){
			node.labelAsFull();
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
	private boolean templateQ2(PQTreeNode node){
		log.info("Trying template Q2 for node " + node);

		if (node.fullChildrenCount() < node.childrenCount() && 
				node.emptyChildrenCount() < node.childrenCount() && 
				node.partialChildrenCount() <= 1){

			//check the order
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
					index++;
				}
				for (PQTreeNode partialEmpty : partialChild.getFullChildren()){
					node.addChild(partialEmpty, index);
					index++;
				}

				node.removeChild(partialChild);
			}

			node.setLabel(PQNodeLabel.SINGLY_PARTIAL);
			log.info("Template matched");
			log.info("Node: " + node);

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
	private boolean templateQ3(PQTreeNode node){
		log.info("Trying template Q3 for node " + node);

		if (node.fullChildrenCount() < node.childrenCount() && 
				node.emptyChildrenCount() < node.childrenCount() && 
				node.partialChildrenCount() <= 2){

			if (!node.orderValidUpToTwoPartial())
				return false;


			List<PQTreeNode> children = node.getChildren();

			//process the first group of empty nodes
			//the beginning
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
					index++;
				}
				for (PQTreeNode partialEmpty : partialChild.getFullChildren()){
					node.addChild(partialEmpty, index);
					index++;
				}
				
				node.removeChild(partialChild);
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
				
				for (PQTreeNode partialEmpty : partialChild.getEmptyChildren()){
					node.addChild(partialEmpty, index);
					index--;
				}
				
				for (PQTreeNode partialEmpty : partialChild.getFullChildren()){
					node.addChild(partialEmpty, index);
					index--;
				}
				
				node.removeChild(partialChild);
			
			}
			
			node.setLabel(PQNodeLabel.DOUBLY_PARTIAL);
			log.info("Template matched");
			log.info("Node: " + node);

		}
		return true;
	}


}
