package graph.tree.pq;

import graph.elements.Vertex;
import graph.util.DoubleLinkedList;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PQTreeNode implements Vertex{

	/**
	 * Type of the node - either P or Q or a leaf
	 */
	private PQNodeType type;
	/**
	 * Content of the node. A cut vertex if the type is P or 
	 * a block if the type is Q. Virtual vertex if it is a leaf
	 */
	private Object content;

	/**
	 * A set of links which form the children of a P-node into
	 * a doubly-linked circular list. The order of the of the lists is arbitrary.
	 * The sole purpose f the circular list is to enable a P-node
	 * to find its only empty child when all other children are full or partial
	 * Not used for children of Q nodes
	 */
	private DoubleLinkedList<PQTreeNode> circularLink;

	/**
	 * Set which contains the two endmost children of a Q-node
	 * Only used for Q-nodes
	 */
	private Set<PQTreeNode> endmostChildren; 

	/**
	 * Contains all of the children of a node which are 
	 * currently known to be full. Children are added to the
	 * list after they are matched to a template in the second pass
	 * No special order is necessary
	 */
	private List<PQTreeNode> fullChildren;
	
	/**
	 * Contains all of the children of a node which are currently known
	 * to be partial. It can have at most two elements, since otherwise
	 * the node would not match a legal template 
	 */
	private List<PQTreeNode> partialChildren;

	/**
	 * A set containing 0,1, or 2 other nodes
	 * A child of a P-node has no immediate siblings, the endmost children
	 * of Q-nodes have only one immediate sibling and the interior
	 * children of Q-nodes have two immediate siblings
	 */
	private Set<PQTreeNode> immediateSimblins;

	/**
	 * All children of a node 
	 */
	private List<PQTreeNode> children;
	
	/**
	 * A count of the number of pertinent children currently possessed by the node
	 * The count is initially zero and is incremented by one eah time
	 * a child of the node is processed during the bubbling up. During the 
	 * matching pass the count is decremented by one each time a child is matched
	 * The node is queued for matching when the pertinent child count reaches zero
	 * during the second pass 
	 */
	private int pertinendChildCount;
	
	/**
	 * A count of the number of pertinent leaves which are descendants of this node
	 * The field is built up during the second pass as each child of the node is mathced
	 * It is the sum of the pertinent leaf counts for all of the ertinent children
	 */
	private int pertinentLeafCount;
	
	/**
	 * The node's label. Indicates if it's full, partial or empty
	 */
	private PQNodeLabel label;
	
	/**
	 * A designation used during the first pass. Every node is initially
	 * unmarked. It is marked queued when it is placed onto the queue when it 
	 * is placed onto queue during the bubbling up. It is marked either blocked or unblockedwhen it is processed
	 * Blocked nodes can become unblocked if their sibling become
	 * unblocked. 
	 */
	private PQNodeMark mark;
	
	/**
	 * The immediate ancestor of the node. The field os always valid for children
	 * of P-nodes and for endmost children of Q--nodes.
	 * It is only valid for interior children of Q-nodes if the child s marked  as unblocked
	 */
	private PQTreeNode parent;
	
	
	public PQTreeNode(PQNodeType type, Object content) {
		super();
		this.type = type;
		this.content = content;
		mark = PQNodeMark.UNMARKED;
		
		//initialize structures specific to certain nodes
		if (type != PQNodeType.LEAF){
			children = new ArrayList<PQTreeNode>();
			fullChildren = new ArrayList<PQTreeNode>();
			partialChildren = new ArrayList<PQTreeNode>();
			immediateSimblins = new HashSet<PQTreeNode>();

			if (type == PQNodeType.P){
				circularLink = new DoubleLinkedList<PQTreeNode>();
			}
			else if (type == PQNodeType.Q){
				endmostChildren = new HashSet<PQTreeNode>();
			}
		}
	}
	
	/**
	 * The number of children currently possessed by the nodes.
	 * Only used for P-nodes
	 */
	public int childrenCount(){
		return children.size();
	}
	
	public void incrementPertinentChildCount(){
		pertinendChildCount++;
	}
	
	public void decrementPertinentChildCount(){
		pertinendChildCount--;
	}

	public void addChild(PQTreeNode node){
		children.add(node);
	}

	public void removeChild(PQTreeNode node){
		children.remove(node);
	}

	/**
	 * @return the type
	 */
	public PQNodeType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(PQNodeType type) {
		this.type = type;
	}

	/**
	 * @return the content
	 */
	public Object getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(Object content) {
		this.content = content;
	}

	@Override
	public Dimension getSize() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSize(Dimension size) {
		// TODO Auto-generated method stub

	}

	@Override
	public String toString() {
		return "PQTreeNode [type=" + type + ", content=" + content + "]";
	}

	public List<PQTreeNode> getChildren() {
		return children;
	}

	public void setChildren(List<PQTreeNode> children) {
		this.children = children;
	}

	public PQTreeNode getParent() {
		return parent;
	}

	public void setParent(PQTreeNode parent) {
		this.parent = parent;
	}

	public DoubleLinkedList<PQTreeNode> getCircularLink() {
		return circularLink;
	}

	public Set<PQTreeNode> getEndmostChildren() {
		return endmostChildren;
	}

	public List<PQTreeNode> getFullChildren() {
		return fullChildren;
	}

	public Set<PQTreeNode> getImmediateSimblins() {
		return immediateSimblins;
	}

	public PQNodeLabel getLabel() {
		return label;
	}

	public void setLabel(PQNodeLabel label) {
		this.label = label;
	}

	public PQNodeMark getMark() {
		return mark;
	}

	public void setMark(PQNodeMark mark) {
		this.mark = mark;
	}

	public List<PQTreeNode> getPartialChildren() {
		return partialChildren;
	}

	public int getPertinendChildCount() {
		return pertinendChildCount;
	}

	public void setPertinendChildCount(int pertinendChildCount) {
		this.pertinendChildCount = pertinendChildCount;
	}

	public int getPertinentLeafCount() {
		return pertinentLeafCount;
	}

	public void setPertinentLeafCount(int pertinentLeafCount) {
		this.pertinentLeafCount = pertinentLeafCount;
	}




}
