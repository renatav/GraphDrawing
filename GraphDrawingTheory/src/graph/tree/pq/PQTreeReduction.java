package graph.tree.pq;

import java.util.Queue;

public class PQTreeReduction {

	/**
	 * The number of blocks of blocked nodes during the bubbling up pass
	 */
	private int block_count;
	
	/**
	 * The number of blocked nodes during the bubbling up pass.
	 * This is only needed for the case when a pseudonode is used.
	 * The count at the end of the first pass is exactly the number of pretinent 
	 * children for the pseudonode
	 */
	private int blocked_nodes;
	
	/**
	 * A variable which is either 0 (the initial value) or 1 (if the 
	 * root of the tree has been processed during the first pass). It acts
	 * as a count of the number of virtual nodes which are imagined to be in the queue during
	 * the bubbling up.
	 * 
	 */
	private int off_the_top;
	
	/**
	 * A first-in first-out list which is used during both passes for
	 * sequencing the order in which nodes are processed
	 */
	private Queue queue;
}
