package graph.algorithms.lrpartition;

import graph.elements.Edge;
import graph.elements.Vertex;
import graph.trees.DFSTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

public class LRPartitionSet<V extends Vertex, E extends Edge<V>> {


	//lists of elements that should be in the same partition
	private List<List<E>> same;
	//list of elements that should be in different partition
	private List<ExclusivePair> exclusivePairs;
	private DFSTree<V,E> tree;
	private Logger log = Logger.getLogger(LRPartitionSet.class);
	private boolean debug = false;


	public LRPartitionSet(DFSTree<V,E> tree){
		same = new ArrayList<List<E>>();
		exclusivePairs = new ArrayList<ExclusivePair>();
		this.tree = tree;
	}

	public boolean add(List<E> class1, List<E> class2){

		//make sure that there are no same edges in the two lists
		for (E e : class1)
			if (class2.contains(e))
				return false;

		if (class1.size() > 0)
			same.add(class1);

		if (class2.size() > 0)
			same.add(class2);

		//add exclusive pair
		if (class1.size() > 0 && class2.size() > 0)
			exclusivePairs.add(new ExclusivePair(class1, class2));

		return true;
	}


	public boolean organizePartitions(){
		
		if (debug)
			log.info("organizing partitions started");
		
		//firstly, join all classes that contain the same edge
		boolean shouldContinue = true;
		while (shouldContinue){
			int index1 = -1, index2 = -1;

			for (int i = 0; i < same.size(); i++){
				List<E> class1 = same.get(i);
				for (int j = i + 1; j < same.size(); j++){
					List<E> class2 = same.get(j);
					if (containSame(class1, class2)){
						index1 = i;
						index2 = j;

						break;
					}
					if (index1 != -1)
						break;
				}
			}

			if (index1 != -1){
				exclusiveJoin(same.get(index1), same.get(index2));
				same.remove(index2);
			}
			else 
				shouldContinue = false;
		}

		//now see if there are elements in the same partitions that have to be in different ones
		for (List<E> list : same){
			for (E e1 : list)
				for (E e2 : list){
					if (e1 == e2)
						continue;
					for (ExclusivePair ep : exclusivePairs){
						if ((ep.getClass1().contains(e1) && ep.getClass2().contains(e2)) || 
								(ep.getClass1().contains(e2) && ep.getClass2().contains(e1))){
							return false;
						}
					}
				}
		}

		joinPartitions();
		
		//if there are more than two partitions after joining them all
		//a LR partition cannot be created
		if (same.size() > 2)
			return false;
		//add empty partitions if there aren't two already
		else if (same.size() < 2){
			int numToAdd = 2 - same.size();
			for (int i = 0; i < numToAdd; i++)
				same.add(new ArrayList<E>());
		}
		
		//System.out.println("Same size: " + same.size());
		addRemainingEdges();
		if (debug)
			log.info("organizing partitions ended");
		return true;
	}

	/**join all partitions that aren't in conflict
	 * in order to minimize their number*/
	private void joinPartitions(){

		boolean modifications = true;
		while (modifications){

			List<E> joinList1 = null, joinList2 = null;
			modifications = false;
			for (List<E> list1 : same){
				for (List<E> list2 : same){
					if (list1 == list2)
						continue;

					boolean ok = true;
					for (E e1 : list1){
						for (E e2 : list2){
							for (ExclusivePair ep : exclusivePairs){
								if ((ep.getClass1().contains(e1) && ep.getClass2().contains(e2)) || 
										(ep.getClass1().contains(e2) && ep.getClass2().contains(e1))){
									ok = false;
									break;
								}
							}
							if (!ok)
								break;
						}
						if (!ok)
							break;
					}
					if (ok){
						joinList1 = list1;
						joinList2 = list2;
						modifications = true;
						break;
					}
				}
				if (modifications)
					break;
			}

			if (modifications){
				joinList1.addAll(joinList2);
				same.remove(joinList2);
			}
		}
	}
	
	private void addRemainingEdges(){

		Random rand = new Random();
		for (E e : tree.getBackEdges()){
			boolean contains = false;
			for (List<E> list : same)
				if (list.contains(e)){
					contains = true;
					break;
				}
			if (!contains){
				int partition = rand.nextInt(2);
				same.get(partition).add(e);
			}
		}
		
		//now add tree edges
		for (E e : tree.getTreeEdges()){
			E highestReturningEdge = tree.getHighestReturningEdge(e);
			
			//add e to the same partition where highest returning edge is
			List<E> partitionList = null;
			for (List<E> list : same)
				if (list.contains(highestReturningEdge)){
					partitionList = list;
					break;
				}
			if (partitionList != null)
				partitionList.add(e);
			else{
				int partition = rand.nextInt(2);
				same.get(partition).add(e);
			}
				
		}
		
	}

	private boolean containSame(List<E> list1, List<E> list2){
		List<E> iterList, otherList;
		iterList = list1.size() < list2.size() ? list1 : list2;
		otherList = iterList == list1 ? list2 : list1;

		for (E e : iterList)
			if (otherList.contains(e))
				return true;

		return false;

	}

	private void exclusiveJoin(List<E> list1, List<E> list2){
		for (E e : list2)
			if (!list1.contains(e))
				list1.add(e);
	}



	public void printPartitions(){
		System.out.println(same.size());
		for (List<E> list : same){
			for (E e : list)
				System.out.print(e);
			System.out.println("kraj");
		}

	}



	class ExclusivePair {
		List<E> class1 = new ArrayList<E>();
		List<E> class2 = new ArrayList<E>();

		public ExclusivePair(List<E> class1, List<E> class2) {
			super();
			this.class1 = class1;
			this.class2 = class2;
		}

		public List<E> getClass1() {
			return class1;
		}
		public void setClass1(List<E> class1) {
			this.class1 = class1;
		}
		public List<E> getClass2() {
			return class2;
		}
		public void setClass2(List<E> class2) {
			this.class2 = class2;
		}

	}



}
