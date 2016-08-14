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
	public void bubblet(PQTree<V,E> pqTree, List<PQTreeNode> S){
		//initialize queue to be empty
		queue.clear();
		blockCount = 0;
		blockedNodes = 0;
		offTheTop = 0;
		List<PQTreeNode> BS = new ArrayList<PQTreeNode>();
		List<PQTreeNode> US = new ArrayList<PQTreeNode>();
		List<PQTreeNode> list = new ArrayList<PQTreeNode>();
		List<PQTreeNode> listCurrent = new ArrayList<PQTreeNode>();
		//for x in s place x onto queue
		for (PQTreeNode x : S)
			queue.add(x);
		//while size of queue + block count + off the top > 1
		while (queue.size() + blockCount + offTheTop > 1){
			if (queue.size() > 0){
				//input tree is a null tree
				pqTree = null;
				break;
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
					//list can be computed using immediate siblings
					//starting at any unblocked node x
					//we may traverse its chain of siblings in either direction
					//siblings are added to list until one is encountered that is not blocked
					PQTreeNode unblocked = null;
					int currentIndex = 0;
					List<PQTreeNode> siblings = x.getImmediateSimblings();
					PQTreeNode currentNode;
					while (currentIndex < siblings.size()){
						currentNode = siblings.get(currentIndex);
						if (currentNode.getMark() == PQNodeMark.BLOCKED){
							if (unblocked == null){
								unblocked = currentNode;
								listCurrent.clear();
							}
							listCurrent.add(currentNode);
						}
						else{
							if (unblocked != null){
								if (listCurrent.size() > list.size()){
									list.clear();
									list.addAll(listCurrent);
								}
								unblocked = null;
							}
						}
						currentIndex++;
					}
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
	}

	public void reduce(PQTree<V,E> pqTree, List<PQTreeNode> S){
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
				if (!templateL1(x))
					if (!templateP1(x))
						if (!templateP3(x))
							if (!templateP5(x))
								if (!templateQ1(x))
									if (!templateQ2(x)){
										pqTree = null;
										break;
									}
			}
			else{
				//x is root(T,S)
				//try templates a bit differently
				if (!templateL1(x))
					if (!templateP1(x))
						if (!templateP2(x))
							if (!templateP4(x))
								if (!templateP6(x))
									if (!templateQ1(x))
										if (!templateQ2(x))
											if (!templateQ3(x)){
												pqTree = null;
												break;
											}
			}
		}
		
		log.info("Tree reduced");
		log.info(pqTree);
	}



private boolean templateL1(PQTreeNode node){
	log.info("Trying template L1 for node " + node);

	return true;
}

private boolean templateP1(PQTreeNode node){
	log.info("Trying template P1 for node " + node);

	return true;
}

private boolean templateP2(PQTreeNode node){
	log.info("Trying template P2 for node " + node);

	return true;
}

private boolean templateP3(PQTreeNode node){
	log.info("Trying template P3 for node " + node);

	return true;
}

private boolean templateP4(PQTreeNode node){
	log.info("Trying template P4 for node " + node);

	return true;
}

private boolean templateP5(PQTreeNode node){
	log.info("Trying template P5 for node " + node);

	return true;
}

private boolean templateP6(PQTreeNode node){
	log.info("Trying template P6 for node " + node);

	return true;
}

private boolean templateQ1(PQTreeNode node){
	log.info("Trying template Q1 for node " + node);

	return true;
}

private boolean templateQ2(PQTreeNode node){
	log.info("Trying template Q2 for node " + node);

	return true;
}

private boolean templateQ3(PQTreeNode node){
	log.info("Trying template Q3 for node " + node);

	return true;
}


}
